package com.mcxiaoke.next.task;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Build.VERSION_CODES;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 15:30
 */
public class ThreadUtils {

    public static String toString(Collection<?> coll) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static boolean isActive(final Object caller) {
        if (caller == null) {
            return false;
        }
        if (caller instanceof Activity) {
            return !((Activity) caller).isFinishing();
        }

        if (caller instanceof Fragment) {
            return ((Fragment) caller).isAdded();
        }
        return isAddedCompat(caller);
    }

    private static boolean isAddedCompat(final Object caller) {
        try {
            final String className = "android.support.v4.app.Fragment";
            final Class<?> clazz = caller.getClass();
            final String callerClassName = clazz.getName();
            if (className.equals(callerClassName)) {
                final Method method = clazz.getMethod("isAdded", clazz);
                if (method != null) {
                    return (boolean) method.invoke(caller);
                }
            }
        } catch (Exception ex) {
            if (Config.DEBUG) {
                Log.w("TaskQueue", "isAddedCompat() ", ex);
            }
        }

        return true;
    }

    public static ExecutorService newCachedThreadPool(final String name) {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new CounterThreadFactory(name));
    }

    public static ExecutorService newFixedThreadPool(final String name, int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name));
    }

    public static ExecutorService newSingleThreadExecutor(final String name) {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new CounterThreadFactory(name));
    }


    static class CounterThreadFactory implements ThreadFactory {
        private int count;
        private String name;

        public CounterThreadFactory(String name) {
            this.name = (name == null ? "task" : name);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + ":thread-" + count++);
            return thread;
        }
    }
}
