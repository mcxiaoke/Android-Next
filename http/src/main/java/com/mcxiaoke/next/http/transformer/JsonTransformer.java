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
public class JsonTransformer<T> implements HttpTransformer<T> {
    private Gson gson;
    private Type type;

    public JsonTransformer(final Gson gson, final Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T transform(final NextResponse response) throws IOException {
        return gson.fromJson(response.reader(), type);
    }
}
