package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public class TaskCallableWrapper<V> extends TaskCallable<V> {

    private Callable<V> mCallable;

    public TaskCallableWrapper(Callable<V> callable) {
        mCallable = callable;
    }

    @Override
    public V call() throws Exception {
        return mCallable.call();
    }
}
