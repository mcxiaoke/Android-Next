package com.mcxiaoke.next.http.callback;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 11:25
 */
public abstract class GsonCallback<T> implements HttpCallback<T> {
    public final Type type;
    public final Gson gson;

    public GsonCallback(final Class<T> clazz) {
        this(clazz, null);
    }

    public GsonCallback(final Class<T> clazz, final Gson gson) {
        this.type = clazz;
        this.gson = gson;
    }

    public GsonCallback(final Type type) {
        this(type, null);
    }

    public GsonCallback(Type type, final Gson gson) {
        this.type = type;
        this.gson = gson;
    }

}
