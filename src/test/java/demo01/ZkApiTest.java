package demo01;

import com.bestksl.CuratorDemoMain;
import com.bestksl.watchers.Watcher01;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CuratorDemoMain.class)
public class ZkApiTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    @Value("${zookeeper.addr}")
    private String zkAddr;

    @Value("${zookeeper.connectTimeout}")
    private int connectTimeout;

    @Test
    public void testAlive() {
        logger.info("is alive ? {}", zkClient.isStarted());
        logger.info("info ? {}  {}", zkAddr, connectTimeout);

    }

    @Test
    public void testCreate() throws Exception {
        String nodePath = "/test777";
        zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(nodePath, "dsada222".getBytes());
    }

    @Test
    public void testUpdate() throws Exception {
        String nodePath = "/ksl1/ksl2/ksl3";
        zkClient.setData()
                .withVersion(3)
                .forPath(nodePath, "dddsasdasdadd".getBytes());
    }

    @Test
    public void testWatcher() throws Exception {
        zkClient.getData()
                .usingWatcher(new Watcher01())
                .forPath("/test777");
        Thread.sleep(10000);
    }

    @Test
    public void testPermanentWatcher() throws Exception {
        NodeCache nodeCache = new NodeCache(zkClient, "/test777");
        nodeCache.start(true);
        logger.info("init data is {}", nodeCache.getCurrentData().getData());
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                logger.info("data changed!!! current data is {}", new String(nodeCache.getCurrentData().getData()));
            }
        });


        Thread.sleep(100000);
    }

    @Test
    public void testDelete() throws Exception {
        zkClient.delete()
                .deletingChildrenIfNeeded()
                .forPath("/lining/ec/order");
    }

    @Test
    public void testCreateOrder() throws Exception {
        String rootPath = "/lining/ec/order";
        zkClient.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                .forPath(rootPath);

        for (int i = 0; i < 300; i++) {
            zkClient.create()
                    .creatingParentsIfNeeded()
                    //.withMode(CreateMode.EPHEMERAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(rootPath + "/" + i);
            Thread.sleep(200);
        }
        Thread.sleep(3600 * 1000);
    }

    @Test
    public void testChildWatcher() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(30);
        for (int i = 0; i < 20; i++) {
            final int orderId = i;
            executor.execute(() -> {
                try {
                    testPermanentChildWatcher(orderId);
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
            });
        }
        Thread.sleep(1000 * 3600);
    }


    public void testPermanentChildWatcher(int orderId) throws Exception {
        String orderFullNode = "/lining/ec/order/" + orderId;
        logger.info("");
        PathChildrenCache orderCache = new PathChildrenCache(zkClient, orderFullNode, true);
        orderCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);

        orderCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case INITIALIZED:
                        if (orderCache.getListenable().size() > 1) {
                            logger.info("order node {} watcher exists", orderId);
                            return;
                        } else if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                            logger.info("Watcher created ==> OrderId {}", orderId);
                        }
                        break;
                    case CHILD_REMOVED:
                        logger.info("order node removed ==> {}", orderId);
                        break;
                    case CHILD_UPDATED:
                        logger.info("order node updated ==> {}", orderId);
                        break;
                    case CONNECTION_LOST:
                    case CONNECTION_SUSPENDED:
                    case CONNECTION_RECONNECTED:
                        break;
                }
            }
        });
        //Thread.sleep(1000 * 3600);

    }

    @After
    public void close() {
        zkClient.close();
    }
}