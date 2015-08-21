package com.mcxiaoke.next.aio.callback;

import android.os.Bundle;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 11:25
 */
public abstract class GsonCallback<T> implements AsyncCallback<T> {
    private Type type;

    public GsonCallback(final Class<T> clazz) {
        this.type = clazz;
    }

    public GsonCallback(final Type type) {
        this.type = type;
    }

    public Type type() {
        return type;
    }

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
    public void onTaskSuccess(final T t, final Bundle extras) {

    }
}
