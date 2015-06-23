package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
final class WrappedCallable<Result> extends TaskCallable<Result> {
    private static final String TAG = WrappedCallable.class.getSimpleName();

    private Callable<Result> callable;

    public WrappedCallable(Callable<Result> callable) {
        this(TAG, callable);
    }

    public WrappedCallable(String name, Callable<Result> callable) {
        super(name);
        this.callable = callable;
    }

    @Override
    public Result call() throws Exception {
        return callable.call();
    }
}
