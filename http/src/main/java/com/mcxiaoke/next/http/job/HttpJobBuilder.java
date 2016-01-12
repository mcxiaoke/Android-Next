package com.mcxiaoke.next.http.job;

import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
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
    public List<HttpProcessor<NextRequest>> mRequestProcessors;
    public List<HttpProcessor<NextResponse>> mPreProcessors;
    public List<HttpProcessor<T>> mPostProcessors;

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

    public HttpJobBuilder<T> requestProcessor(HttpProcessor<NextRequest> processor) {
        if (processor != null) {
            if (mRequestProcessors == null) {
                mRequestProcessors = new ArrayList<HttpProcessor<NextRequest>>(2);
            }
            mRequestProcessors.add(processor);
        }
        return this;
    }

    public HttpJobBuilder<T> preProcessor(HttpProcessor<NextResponse> processor) {
        if (processor != null) {
            if (mPreProcessors == null) {
                mPreProcessors = new ArrayList<HttpProcessor<NextResponse>>(2);
            }
            mPreProcessors.add(processor);
        }
        return this;
    }

    public HttpJobBuilder<T> postProcessor(HttpProcessor<T> processor) {
        if (processor != null) {
            if (mPostProcessors == null) {
                mPostProcessors = new ArrayList<HttpProcessor<T>>(2);
            }
            mPostProcessors.add(processor);
        }
        return this;
    }

    public HttpJob<T> create() {
        return new HttpJob<T>(mRequest, mTransformer, mCallback, mCaller,
                mRequestProcessors, mPreProcessors, mPostProcessors);
    }
}