package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
final class WrappedRunnable<Result> extends TaskCallable<Result> {
    private static final String TAG = WrappedRunnable.class.getSimpleName();

    private Runnable runnable;

    public WrappedRunnable(Runnable runnable) {
        this(TAG, runnable);
    }

    public WrappedRunnable(String name, Runnable runnable) {
        super(name);
        this.runnable = runnable;
    }

    @Override
    public Result call() throws Exception {
        runnable.run();
        return null;
    }
}
