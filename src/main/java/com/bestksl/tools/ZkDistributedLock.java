package com.bestksl.tools;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.ZooDefs;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
public class ZkDistributedLock {

    CuratorFramework zkCli = null; // zk客户端

    private final String ZK_Lock_PROJECT = "ZK_DEMO_PROJECT";


    private final String DISTRIBUTED_LOCK = "DISTRIBUTED_LOCK";

    // 用于挂起当前请求, 等待上一个锁的释放
    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZkDistributedLock(CuratorFramework curatorFramework) {
        this.zkCli = curatorFramework;
    }

    /*
    初始化锁
     */
    public void init() {
        try {
            if (zkCli.checkExists().forPath("/" + ZK_Lock_PROJECT) == null) {
                zkCli.create()
                        .creatingParentsIfNeeded()
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                        .forPath("/" + ZK_Lock_PROJECT);
            }
            addWatherToLock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    监控锁节点
     */
    private void addWatherToLock() throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkCli, ZK_Lock_PROJECT, true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
                    String path = event.getData().getPath();
                    log.info("连接断开 {}", path);
                    if (path.contains(DISTRIBUTED_LOCK)) {
                        countDownLatch.countDown();
                    }
                }
            }
        });
    }

    /*
    释放锁
     */
    private boolean releaseLock() {
        try {
            if (zkCli.checkExists().forPath("/" + ZK_Lock_PROJECT + "/" + DISTRIBUTED_LOCK) != null) {
                zkCli.delete().forPath("/" + ZK_Lock_PROJECT + "/" + DISTRIBUTED_LOCK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        log.info("释放zk锁");
        return true;
    }

}

