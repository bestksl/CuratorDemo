package com.bestksl.watchers;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

@Slf4j
public class Watcher01 implements CuratorWatcher {
    @Override
    public void process(WatchedEvent watchedEvent) throws Exception {
        log.info(watchedEvent.toString());
    }
}
