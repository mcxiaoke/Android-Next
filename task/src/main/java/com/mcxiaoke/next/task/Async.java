package com.mcxiaoke.next.task;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 15/8/5
 * Time: 15:45
 */
public class Async {
    private static final String TAG = Async.class.getSimpleName();

    private static ExecutorService mExecutor = Executors.newCachedThreadPool();

    public static Future<?> run(final Runnable task) {
        return mExecutor.submit(new SafeRunnable(task));
    }

    public static <T> Future<T> run(final Callable<T> task) {
        return mExecutor.submit(new SafeCallable<T>(task));
    }

    public static List<Future<?>> run(final Runnable... tasks) {
        final List<Future<?>> futures = new ArrayList<Future<?>>();
        for (Runnable task : tasks) {
            futures.add(run(task));
        }
        return futures;
    }

    public static <T> List<Future<T>> run(final Callable<T>... tasks) {
        final List<Future<T>> futures = new ArrayList<Future<T>>();
        for (Callable<T> task : tasks) {
            futures.add(run(task));
        }
        return futures;
    }

    public void reset() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = Executors.newCachedThreadPool();
        }
    }


    static class SafeRunnable implements Runnable {
        private Runnable wrapped;

        public SafeRunnable(final Runnable task) {
            this.wrapped = task;
        }

        @Override
        public void run() {
            try {
                this.wrapped.run();
            } catch (Throwable ex) {
                Log.e(TAG, "task failed, error: " + ex);
            }
        }
    }

    static class SafeCallable<V> implements Callable<V> {
        private Callable<V> wrapped;

        public SafeCallable(final Callable<V> task) {
            this.wrapped = task;
        }

        @Override
        public V call() throws Exception {
            V result = null;
            try {
                result = this.wrapped.call();
            } catch (Throwable ex) {
                Log.e(TAG, "task failed, error: " + ex);
            }
            return result;
        }
    }

}
