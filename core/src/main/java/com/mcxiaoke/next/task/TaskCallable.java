package com.mcxiaoke.next.task;

import android.os.Bundle;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public abstract class TaskCallable<V> implements Callable<V> {
    private String mName;
    private Bundle mExtras;

    public TaskCallable(String name) {
        mName = name;
    }

    public TaskCallable(String name, Bundle extras) {
        mName = name;
        mExtras = extras;
    }

    public void setName(final String name) {
        mName = name;
    }

    public void setExtras(final Bundle extras) {
        mExtras = extras;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    public String getName() {
        return mName;
    }
}
