package com.finago.interview.task;

import com.finago.interview.task.service.FilesChecker;
import com.finago.interview.task.service.FolderListener;
import com.finago.interview.task.service.ScheduleRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * A simple main method as an example.
 */

@SpringBootApplication
public class BatchProcessor {

    @Configuration
    public static class Factory {
        @Bean
        public FolderListener getListener() {
            return new FolderListener();
        }

        @Bean
        public ScheduleRunner getScheduleRunner() {
            return new ScheduleRunner();
        }

        @Bean
        public FilesChecker getTaskRunner() {
            return new FilesChecker();
        }

        @Bean
        public TaskScheduler getTaskScheduler() {
            return new ThreadPoolTaskScheduler();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(BatchProcessor.class, args);
    }

}
