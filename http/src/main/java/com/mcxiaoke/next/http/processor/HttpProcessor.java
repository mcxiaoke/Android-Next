package com.mcxiaoke.next.http.processor;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 11:05
 * F from
 * T to
 */
public interface HttpProcessor<T> {

    void process(T response);
}
