package com.tim1.daimlerback.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TaskExecutor {
    public void schedule(Runnable command, long delay, TimeUnit unit) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        executor.schedule(command, delay, unit);
    }
}
