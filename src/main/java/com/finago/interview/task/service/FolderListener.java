package com.finago.interview.task.service;

import com.finago.interview.task.event.FindFilesEvent;
import com.finago.interview.task.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FolderListener implements Runnable, DisposableBean, ApplicationEventPublisherAware {
    private static final Logger logger = LoggerFactory.getLogger("folder.listener");

    private volatile boolean destroyCondition = false;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }

    @Override
    public void run() {
        if (destroyCondition) return;
        logger.info("files search start ... ");
        try (Stream<Path> walk = Files.walk(Paths.get(Utils.IN_FOLDER))) {
            Set<String> result = walk.map(Path::getFileName).map(Path::toString)
                    .filter(f -> f.endsWith(".xml"))
                    .collect(Collectors.toSet());
            publisher.publishEvent(new FindFilesEvent(this, result));
        } catch (IOException e) {
            logger.error("can not scan IN folder {}", e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        destroyCondition = true;
    }

}
