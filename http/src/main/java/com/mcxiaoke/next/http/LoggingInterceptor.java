package com.mcxiaoke.next.http;

import android.util.Log;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/2
 * Time: 16:01
 */
public class LoggingInterceptor implements Interceptor {
    private final boolean mHeaders;
    private final boolean mBody;

    public LoggingInterceptor() {
        this(false, false);
    }

    public LoggingInterceptor(final boolean headers) {
        this(headers, false);
    }

    public LoggingInterceptor(final boolean headers, final boolean body) {
        mHeaders = headers;
        mBody = body;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        long t1 = System.nanoTime();
        final Request request = chain.request();
        Log.v(NextClient.TAG, String.format("[Request] %s %s on %s%n",
                request.method(), request.url(), chain.connection()));
        if (mHeaders) {
            Log.v(NextClient.TAG, "[Request Headers] " + request.headers());
        }
        final Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.v(NextClient.TAG, String.format("[Response] %s %s (%s:%s) in %.1fms%n "
                , request.method(), request.url()
                , response.code(), response.message()
                , (t2 - t1) / 1e6d));
        if (mHeaders) {
            Log.v(NextClient.TAG, "[Response Headers] " + response.headers());
        }
        if (mBody) {
            Log.v(NextClient.TAG, "[Response] " + responseToText(response));
        }
        return response;
    }

    private static String responseToText(final Response response)
            throws IOException {
        return StringUtils.safeSubString(IOUtils.readString(response.body().charStream()), 8196);
    }

}
