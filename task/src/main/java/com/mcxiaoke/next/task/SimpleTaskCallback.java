package com.mcxiaoke.next.task;

import android.os.Bundle;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:42
 */
public class SimpleTaskCallback<Result> implements TaskCallback<Result> {

    @Override
    public void onTaskStarted(String name, Bundle extras) {

    }

    @Override
    public void onTaskFinished(String name, Bundle extras) {

    }

    @Override
    public void onTaskCancelled(String name, Bundle extras) {

    }

    @Override
    public void onTaskSuccess(Result result, Bundle extras) {

    }

    @Override
    public void onTaskFailure(Throwable ex, Bundle extras) {

    }
}
