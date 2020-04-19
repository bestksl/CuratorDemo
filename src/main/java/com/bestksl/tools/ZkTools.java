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

@Configuration
public class ZkTools {

    @Value("${zookeeper.addr}")
    private String zkAddr;

    @Value("${zookeeper.connectTimeout}")
    private int connectTimeout;

    @Bean
    public CuratorFramework getCurator() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(connectTimeout, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddr, retryPolicy);
        client.start();
        return client;
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ZkTools.class);

        logger.info("{}", new ZkTools().zkAddr);

        System.out.println();
    }


}
