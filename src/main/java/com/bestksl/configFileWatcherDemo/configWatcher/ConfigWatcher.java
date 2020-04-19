package com.bestksl.configFileWatcherDemo.configWatcher;

import com.alibaba.fastjson.JSON;
import com.bestksl.configFileWatcherDemo.Path.MyPath;
import com.bestksl.configFileWatcherDemo.configBean.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

@Slf4j
public class ConfigWatcher implements PathChildrenCacheListener {


    @Override
    public void childEvent(CuratorFramework zkCli, PathChildrenCacheEvent event) throws Exception {
        if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {
            if (event.getData().getPath().equals(MyPath.CONFIG_ROOT + MyPath.REDIS_CONFIG)) {
                String configString = new String(event.getData().getData());
                log.info("节点信息为: {}", configString);
                RedisConfig redisConfig = JSON.parseObject(configString, RedisConfig.class);
                if (redisConfig != null) {
                    String type = redisConfig.getType();
                    String url = redisConfig.getUrl();
                    String remark = redisConfig.getRemark();
                    switch (type) {
                        case "add":
                            log.info("监听到新增的配置文件 准备下载" + url);
                            break;
                        case "delete":
                            log.info("监听到删除的配置文件 准备删除" + url);
                            break;
                        case "update":
                            log.info("监听到更新的配置文件 准备更新" + url);
                            break;
                    }
                }
            }

        }
    }
}
