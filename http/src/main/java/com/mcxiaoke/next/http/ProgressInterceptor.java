package com.mcxiaoke.next.http;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/2
 * Time: 15:17
 */
class ProgressInterceptor implements Interceptor {
    private ProgressListener listener;

    public ProgressInterceptor(final ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new ProgressResponseBody(response.body(), listener))
                .build();
    }
}
