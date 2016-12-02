package com.mcxiaoke.next.request;

/**
 * User: mcxiaoke
 * Date: 2016/12/8
 * Time: 15:19
 */

public class RequestJob<T> {
    protected OkRequest request;
    protected OkTransformer<T> transformer;
    protected OkProcessor<T> processor;

    RequestJob(final JobBuilder<T> builder) {
        this.request = builder.request;
        this.transformer = builder.transformer;
        this.processor = builder.processor;
    }
}
