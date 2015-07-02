package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.LogUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/2
 * Time: 16:01
 */
class LoggingInterceptor implements Interceptor {

    @Override
    public Response intercept(final Chain chain) throws IOException {
        long t1 = System.nanoTime();
        Request request = chain.request();
        LogUtils.v(NextClient.TAG, String.format("Sending http request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));
        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        LogUtils.v(NextClient.TAG, String.format("Received http response for %s (%s:%s) in %.1fms%n%s",
                request.url(), response.code(), response.message()
                , (t2 - t1) / 1e6d, response.headers()));
        return response;
    }
}
