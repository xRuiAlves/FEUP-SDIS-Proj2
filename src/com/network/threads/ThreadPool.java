package com.network.threads;



import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPool {
    public static final int POOL_SIZE = 128;
    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(POOL_SIZE);

    private static ThreadPool ourInstance = new ThreadPool();
    public static ScheduledThreadPoolExecutor getInstance() {
        return ourInstance.scheduler;
    }
}
