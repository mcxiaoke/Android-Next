package com.mcxiaoke.next.http;

import com.squareup.okhttp.OkHttpClient;

/**
 * User: mcxiaoke
 * Date: 15/7/13
 * Time: 13:48
 */
public interface OkClientInterceptor {

    void intercept(OkHttpClient client);
}
