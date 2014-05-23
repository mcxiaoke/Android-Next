package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:42
 */
public class SimpleTaskCallback<Result> implements TaskCallback<Result> {

    @Override
    public void onTaskStarted(final String tag) {

    }

    @Override
    public void onTaskSuccess(final Result result, final TaskMessage message) {

    }

    @Override
    public void onTaskFailure(final Throwable ex, final TaskMessage message) {

    }
}
