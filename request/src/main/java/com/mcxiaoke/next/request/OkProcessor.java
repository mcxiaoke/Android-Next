package com.mcxiaoke.next.request;

import okhttp3.Response;

/**
 * User: mcxiaoke
 * Date: 16/12/2
 * Time: 11:15
 */

public interface OkProcessor<T> {

    void process(Response response);
}
