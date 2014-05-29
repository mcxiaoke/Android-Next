package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public class TaskCallableWrapper<V> extends TaskCallable<V> {
    private static final String TAG = TaskCallableWrapper.class.getSimpleName();

    private Callable<V> mCallable;

    public TaskCallableWrapper(Callable<V> callable) {
        this(TAG, callable);
    }

    public TaskCallableWrapper(String name, Callable<V> callable) {
        super(name);
        mCallable = callable;
    }

    @Override
    public V call() throws Exception {
        return mCallable.call();
    }
}
