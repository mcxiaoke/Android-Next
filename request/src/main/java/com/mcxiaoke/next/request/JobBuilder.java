package com.mcxiaoke.next.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 2016/12/2
 * Time: 11:44
 */

public class JobBuilder<T> {
    private Type type;
    private Gson gson;
    protected OkRequest request;
    protected OkTransformer<T> transformer;
    protected OkProcessor<T> processor;

    public JobBuilder() {

    }

    public JobBuilder(OkTransformer<T> transformer) {
        this.transformer = transformer;
    }

    public JobBuilder(TypeToken<T> typeToken) {
        this.type = typeToken.getType();
    }

    public JobBuilder(Class<T> clazz) {
        this.type = clazz;
    }

    public JobBuilder<T> gson(final Gson gson) {
        this.gson = gson;
        return this;
    }

    public JobBuilder<T> transformer(final OkTransformer<T> transformer) {
        this.transformer = transformer;
        return this;
    }

    public JobBuilder<T> processor(final OkProcessor<T> processor) {
        this.processor = processor;
        return this;
    }
}
