package com.mcxiaoke.next.http.job;

import com.mcxiaoke.next.http.HttpQueue;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.callback.HttpCallback;
import com.mcxiaoke.next.http.processor.HttpProcessor;
import com.mcxiaoke.next.http.transformer.HttpTransformer;
import com.mcxiaoke.next.utils.AssertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 14:51
 */
public class HttpJob<T> {

    public final NextRequest request;
    public final HttpTransformer<T> transformer;
    public final HttpCallback<T> callback;
    public final Object caller;
    private List<HttpProcessor<NextRequest>> requestProcessors;
    private List<HttpProcessor<NextResponse>> preProcessors;
    private List<HttpProcessor<T>> postProcessors;

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final Object caller) {
        this(request, transformer, callback, caller, null, null, null);
    }

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final Object caller,
                   List<HttpProcessor<NextRequest>> requestProcessors,
                   final List<HttpProcessor<NextResponse>> preProcessors,
                   final List<HttpProcessor<T>> postProcessors) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(transformer, "transformer must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        this.request = request;
        this.transformer = transformer;
        this.callback = callback;
        this.caller = caller;
        if (requestProcessors != null) {
            this.requestProcessors = requestProcessors;
        } else {
            this.requestProcessors = new ArrayList<HttpProcessor<NextRequest>>(2);
        }
        if (preProcessors != null) {
            this.preProcessors = preProcessors;
        } else {
            this.preProcessors = new ArrayList<HttpProcessor<NextResponse>>(2);
        }
        if (postProcessors != null) {
            this.postProcessors = postProcessors;
        } else {
            this.postProcessors = new ArrayList<HttpProcessor<T>>(2);
        }
    }


    public HttpJob<T> addRequestProcessor(HttpProcessor<NextRequest> processor) {
        if (processor != null) {
            this.requestProcessors.add(processor);
        }
        return this;
    }

    public HttpJob<T> addPreProcessor(HttpProcessor<NextResponse> processor) {
        if (processor != null) {
            this.preProcessors.add(processor);
        }
        return this;
    }

    public HttpJob<T> addPostProcessor(HttpProcessor<T> processor) {
        if (processor != null) {
            this.postProcessors.add(processor);
        }
        return this;
    }

    public List<HttpProcessor<NextRequest>> getRequestProcessors() {
        return requestProcessors;
    }

    public List<HttpProcessor<NextResponse>> getPreProcessors() {
        return preProcessors;
    }

    public List<HttpProcessor<T>> getPostProcessors() {
        return postProcessors;
    }

    public String execute(final HttpQueue queue) {
        return queue.add(this);
    }

    public String execute() {
        return HttpQueue.getDefault().add(this);
    }

    @Override
    public String toString() {
        return "HttpJob{" +
                "request=" + request +
                ", transformer=" + transformer +
                ", callback=" + callback +
                ", requestProcessors=" + requestProcessors +
                ", preProcessors=" + preProcessors +
                ", postProcessors=" + postProcessors +
                ", caller=" + caller +
                '}';
    }
}
