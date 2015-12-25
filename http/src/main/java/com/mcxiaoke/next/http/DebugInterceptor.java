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
public class DebugInterceptor implements Interceptor {
    private final boolean mDumpHeaders;
    private final boolean mDumpBody;

    public DebugInterceptor() {
        this(true, false);
    }

    public DebugInterceptor(final boolean dumpHeaders) {
        this(dumpHeaders, false);
    }

    public DebugInterceptor(final boolean dumpHeaders, final boolean dumpBody) {
        mDumpHeaders = dumpHeaders;
        mDumpBody = dumpBody;
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        long t1 = System.nanoTime();
        final Request request = chain.request();
        Log.v(NextClient.TAG, String.format("[OkHttp Request] %s %s on %s%n [%s]",
                request.method(), request.url(), chain.connection()
                , mDumpHeaders ? request.headers() : ""));
        final Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        Log.v(NextClient.TAG, String.format("[OkHttp Response] (%s:%s) %s %s  in %.1fms%n [%s] [%s]"
                , response.code(), response.message(), request.method(), request.url()
                , (t2 - t1) / 1e6d
                , mDumpHeaders ? response.headers() : ""
                , mDumpBody ? responseToText(response) : ""));
        return response;
    }

    private static String responseToText(final Response response)
            throws IOException {
        return StringUtils.safeSubString(IOUtils.readString(response.body().charStream()), 8196);
    }
}
