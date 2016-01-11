package com.mcxiaoke.next.async.callback;

import android.os.Bundle;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:51
 */
public abstract class FileCallback implements AsyncCallback<File> {
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
    public void onTaskSuccess(final File file, final Bundle extras) {

    }

    @Override
    public void onTaskStarted(final String name, final Bundle extras) {

    }
}
