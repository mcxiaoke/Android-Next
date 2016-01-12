package com.mcxiaoke.next.utils;

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
        return new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name),
                new LogDiscardPolicy());
    }

    public static ThreadPoolExecutor newSingleThreadExecutor(final String name) {
        return newFixedThreadPool(name, 1);
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
            return thread;
        }
    }
}
