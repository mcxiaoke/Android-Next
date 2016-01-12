package com.mcxiaoke.next.http.callback;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 12:16
 */
public interface HttpCallback<T> {

    void handleResponse(T response);

    boolean handleException(Throwable throwable);
}
