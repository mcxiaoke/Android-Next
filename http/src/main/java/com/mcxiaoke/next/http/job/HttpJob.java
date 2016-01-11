package com.mcxiaoke.next.http.job;

import com.mcxiaoke.next.http.HttpQueue;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.callback.HttpCallback;
import com.mcxiaoke.next.http.processor.ResponseProcessor;
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
    public final List<ResponseProcessor<T>> processors;

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final Object caller) {
        this(request, transformer, callback, null, caller);
    }

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final List<ResponseProcessor<T>> processors,
                   final Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(transformer, "transformer must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        this.request = request;
        this.transformer = transformer;
        this.callback = callback;
        this.caller = caller;
        if (processors != null) {
            this.processors = processors;
        } else {
            this.processors = new ArrayList<ResponseProcessor<T>>(4);
        }
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
                ", processors=" + processors +
                ", caller=" + caller +
                '}';
    }
}
