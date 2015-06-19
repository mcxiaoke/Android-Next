package com.mcxiaoke.next.task;

import android.os.Bundle;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:42
 */
public class SimpleTaskCallback<Result> implements TaskCallback<Result> {

    @Override
    public void onTaskStarted(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskFinished(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskCancelled(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskSuccess(final Result result, final Bundle extras) {

    }

    @Override
    public void onTaskFailure(final Throwable ex, final Bundle extras) {

    }
}
