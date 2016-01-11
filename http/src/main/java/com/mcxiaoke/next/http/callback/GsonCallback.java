package com.mcxiaoke.next.http.callback;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 11:25
 */
public abstract class GsonCallback<T> implements HttpCallback<T> {
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
    public void onSuccess(final T response) {

    }

    @Override
    public void onError(final Throwable error) {

    }
}
