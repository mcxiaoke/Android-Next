package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:42
 */
public class SimpleTaskCallback<V> implements TaskCallback<V> {

    @Override
    public void onTaskSuccess(final V result, final TaskMessage message) {

    }

    @Override
    public void onTaskFailure(final Throwable ex, final TaskMessage message) {

    }
}
