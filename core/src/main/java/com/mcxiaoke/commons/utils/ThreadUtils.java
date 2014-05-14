package com.mcxiaoke.commons.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: mcxiaoke
 * Date: 14-5-10
 * Time: 12:02
 */
public final class ThreadUtils {
    private static final String TAG = ThreadUtils.class.getSimpleName();

    public static ThreadPoolExecutor newCachedThreadPool(final String name) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new CounterThreadFactory(name),
                new LogDiscardPolicy());
    }

    public static ThreadPoolExecutor newFixedThreadPool(final String name, int nThreads) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.setThreadFactory(new CounterThreadFactory(name));
        executor.setRejectedExecutionHandler(new LogDiscardPolicy());
        return executor;
    }

    public static class LogDiscardPolicy implements RejectedExecutionHandler {

        public LogDiscardPolicy() {
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            LogUtils.v(TAG, "rejectedExecution() " + r + " is discard.");
        }
    }

    public static class CounterThreadFactory implements ThreadFactory {
        private int count;
        private String name;

        public CounterThreadFactory(String name) {
            this.name = (name == null ? "Android" : name);

        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + "-thread #" + count++);
            LogUtils.v(TAG, "newThread() thread=" + thread.getName());
            return thread;
        }
    }
}
