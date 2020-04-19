package com.bestksl.configFileWatcherDemo.client;

import com.bestksl.configFileWatcherDemo.Path.MyPath;
import com.bestksl.configFileWatcherDemo.configWatcher.ConfigWatcher;
import com.bestksl.tools.ZkTools;
import com.bestksl.tools.ZkTools2;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class Client3 {
    static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZkTools2 zkTools = new ZkTools2();
        CuratorFramework zkCli = zkTools.getCurator();

        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkCli, MyPath.CONFIG_ROOT , true);
        pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        if (pathChildrenCache.getListenable().size() > 0) {
            log.info("wrong number of listener");
            return;
        }
        pathChildrenCache.getListenable().addListener(new ConfigWatcher());

        countDownLatch.await();

    }
}
