package com.mcxiaoke.next.http;

import android.util.Log;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/2
 * Time: 16:01
 */
public class LoggingInterceptor implements Interceptor {
    private final boolean dumpHeaders;
    private final boolean dumpBody;

    public LoggingInterceptor() {
        this(false, false);
    }

    public LoggingInterceptor(final boolean headers) {
        this(headers, false);
    }

    public LoggingInterceptor(final boolean headers, final boolean body) {
        dumpHeaders = headers;
        dumpBody = body;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        long t1 = System.nanoTime();
        final Request request = chain.request();
        Log.v(NextClient.TAG, String.format("[OkHttp Request] %s %s on %s%n",
                request.method(), request.url(), chain.connection()));
        if (dumpHeaders) {
            Log.v(NextClient.TAG, "[Request Headers] " + request.headers());
        }
        final Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.v(NextClient.TAG, String.format("[OkHttp Response] %s %s (%s:%s) in %.1fms%n "
                , request.method(), request.url()
                , response.code(), response.message()
                , (t2 - t1) / 1e6d));
        if (dumpHeaders) {
            Log.v(NextClient.TAG, "[Response Headers] " + response.headers());
        }
        if (dumpBody) {
            Log.v(NextClient.TAG, "[Response Body] " + responseToText(response));
        }
        return response;
    }

    private static String responseToText(final Response response)
            throws IOException {
        return StringUtils.safeSubString(IOUtils.readString(response.body().charStream()), 2048);
    }

}
