package com.mcxiaoke.commons.http;

/**
 * User: mcxiaoke
 * Date: 14-2-10
 * Time: 11:41
 */
public interface ResponseHandler<T> {

    T process(HttpResponse response);
}
