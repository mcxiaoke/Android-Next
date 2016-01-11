package com.mcxiaoke.next.async.callback;

import android.os.Bundle;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:51
 */
public abstract class FileCallback implements HttpCallback<File> {
    @Override
    public void onSuccess(final File response) {

    }

    @Override
    public void onError(final Throwable error) {

    }
}
