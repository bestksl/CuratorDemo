package com.bestksl.configFileWatcherDemo;

import com.bestksl.tools.ZkTools2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

public class SetNode {
    static String json1 = "{\"type\":\"add\",\"url\":\"ftp://110:110:110:110/config/redis.xml\",\"remark\":\"666\"}";
    static String json2 = "{\"type\":\"delete\",\"url\":\"ftp://110:110:110:110/config/redis.xml\",\"remark\":\"666\"}";
    static String json3 = "{\"type\":\"update\",\"url\":\"ftp://110:110:110:110/config/redis.xml\",\"remark\":\"666\"}";

    public static void main(String[] args) throws Exception {
        ZkTools2 zkTools = new ZkTools2();
        CuratorFramework zkCli = zkTools.getCurator();
        zkCli.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/config/redis", json1.getBytes());
        Thread.sleep(1000 * 3600);
    }
}