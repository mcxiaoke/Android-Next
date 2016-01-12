package com.mcxiaoke.next.http.transformer;

import com.google.gson.Gson;
import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:42
 */
public class GsonTransformer<T> implements HttpTransformer<T> {
    private Gson gson;
    private Class<T> clazz;
    private Type type;

    private GsonTransformer(final Gson gson, final Class<T> clazz, final Type type) {
        this.gson = gson;
        this.clazz = clazz;
        this.type = type;
    }

    public GsonTransformer(final Gson gson, final Class<T> clazz) {
        this(gson, clazz, null);
    }

    public GsonTransformer(final Gson gson, final Type type) {
        this(gson, null, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T transform(final NextResponse response) throws IOException {
        // ugly hack for runtime type for gson
        if (type instanceof Class) {
            return gson.fromJson(response.reader(), (Class<T>) type);
        } else {
            return gson.fromJson(response.reader(), type);
        }
    }
}
