package com.mcxiaoke.next.http;

import okhttp3.OkHttpClient;

/**
 * User: mcxiaoke
 * Date: 15/7/13
 * Time: 13:48
 */
public interface OkClientInterceptor {

    void intercept(OkHttpClient.Builder builder);
}
