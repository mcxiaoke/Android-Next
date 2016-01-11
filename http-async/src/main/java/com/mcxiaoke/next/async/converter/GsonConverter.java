package com.mcxiaoke.next.async.converter;

import com.google.gson.Gson;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.converter.ResponseConverter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:42
 */
public class GsonConverter<T> implements ResponseConverter<T> {
    private Gson gson;
    private Type type;

    public GsonConverter(final Gson gson, final Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(final NextResponse response) throws IOException {
        return gson.fromJson(response.reader(), type);
    }
}
