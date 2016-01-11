package com.mcxiaoke.next.async.callback;

import android.os.Bundle;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:51
 */
public abstract class StringCallback implements AsyncCallback<String> {

    @Override
    public void onTaskCancelled(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskFailure(final Throwable ex, final Bundle extras) {

    }

    @Override
    public void onTaskFinished(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskStarted(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskSuccess(final String s, final Bundle extras) {

    }
}
