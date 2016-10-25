package com.mcxiaoke.next.http.job;

import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.callback.HttpCallback;
import com.mcxiaoke.next.http.processor.HttpProcessor;
import com.mcxiaoke.next.http.transformer.HttpTransformer;

import java.util.ArrayList;
import java.util.List;

public class HttpJobBuilder<T> {
    private NextRequest mRequest;
    private HttpTransformer<T> mTransformer;
    private HttpCallback<T> mCallback;
    private Object mCaller;
    public List<HttpProcessor<T>> mProcessors;

    public HttpJobBuilder<T> request(final NextRequest request) {
        mRequest = request;
        return this;
    }

    public HttpJobBuilder<T> transformer(final HttpTransformer<T> transformer) {
        mTransformer = transformer;
        return this;
    }

    public HttpJobBuilder<T> callback(final HttpCallback<T> callback) {
        mCallback = callback;
        return this;
    }

    public HttpJobBuilder<T> caller(final Object caller) {
        mCaller = caller;
        return this;
    }

    public HttpJobBuilder<T> processor(HttpProcessor<T> processor) {
        if (processor != null) {
            if (mProcessors == null) {
                mProcessors = new ArrayList<HttpProcessor<T>>(2);
            }
            mProcessors.add(processor);
        }
        return this;
    }

    public HttpJob<T> create() {
        return new HttpJob<T>(mRequest, mTransformer, mCallback, mProcessors, mCaller);
    }
}