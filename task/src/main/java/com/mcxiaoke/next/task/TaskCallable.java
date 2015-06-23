package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.Parcelable;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public abstract class TaskCallable<V> implements Callable<V> {
    private String mName;
    private Bundle mExtras;

    public TaskCallable() {
        this("task", new Bundle());
    }

    public TaskCallable(String name) {
        this(name, new Bundle());
    }

    public TaskCallable(String name, Bundle extras) {
        mName = name;
        mExtras = extras;
    }

    public String getName() {
        return mName;
    }

    public TaskCallable<V> setName(final String name) {
        mName = name;
        return this;
    }

    public Bundle getExtras() {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        return mExtras;
    }

    public TaskCallable<V> putExtra(String name, boolean value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putBoolean(name, value);
        return this;
    }

    public TaskCallable<V> putExtra(String name, int value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putInt(name, value);
        return this;
    }

    public TaskCallable<V> putExtra(String name, long value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putLong(name, value);
        return this;
    }

    public TaskCallable<V> putExtra(String name, String value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putString(name, value);
        return this;
    }

    public TaskCallable<V> putExtra(String name, Parcelable value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putParcelable(name, value);
        return this;
    }

    public TaskCallable<V> putExtra(String name, byte[] value) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putByteArray(name, value);
        return this;
    }

    public TaskCallable<V> putExtras(Bundle extras) {
        if (mExtras == null) {
            mExtras = new Bundle();
        }
        mExtras.putAll(extras);
        return this;
    }

}
