package com.bestksl.demo01;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/")
public class ZkApiTest {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CuratorFramework zkClient;

    @GetMapping("/aaa")
    public void testAlive() {
        logger.info("is alive ? {}", zkClient.isStarted());
        logger.info("info ? {}", zkClient.getConfig());

    }
}
