package com.finago.interview.task.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ScheduleRunner {
    private static final Logger logger = LoggerFactory.getLogger("taskScheduler");

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private FolderListener listener;

    @PostConstruct
    public void init() {
        long period = TimeUnit.MILLISECONDS.toMillis(2000);
        taskScheduler.scheduleWithFixedDelay(listener, new Date(System.currentTimeMillis() + period), period);
    }

}
