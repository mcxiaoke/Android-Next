package com.mcxiaoke.next.http.callback;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 11:25
 */
public abstract class JsonCallback<T> implements HttpCallback<T> {
    private Type type;
    private Gson gson;

    public JsonCallback(final Class<T> clazz) {
        this.type = clazz;
    }

    public JsonCallback(final Class<T> clazz, final Gson gson) {
        this.type = clazz;
        this.gson = gson;
    }


    public JsonCallback(final Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Gson getGson() {
        return gson;
    }
}
