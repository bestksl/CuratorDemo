package com.bestksl.tools;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ZkTools2 {

    private String zkAddr = "192.168.1.101:2181,192.168.1.102:2181,192.168.1.103:2181";

    private int connectTimeout = 10000;

    public CuratorFramework getCurator() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(connectTimeout, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddr, retryPolicy);
        client.start();
        return client;
    }



}
