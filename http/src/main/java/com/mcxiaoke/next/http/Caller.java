package com.mcxiaoke.next.http;

import com.mcxiaoke.next.http.NextRequest.Builder;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.LogUtils;
import org.apache.http.HttpEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * 执行HTTP请求
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:39
 */
final class Caller {
    public static final String TAG = NextClient.TAG;

    private boolean mDebug;
    private NextClient mClient;
    private NextRequest mRequest;
    private NextResponse mResponse;

    public Caller(final NextClient client, final NextRequest request) {
        this.mClient = client;
        this.mRequest = request;
        this.mDebug = client.isDebug();
    }

    public void setDebug(final boolean debug) {
        mDebug = debug;
    }

    public NextResponse execute() throws IOException {
        return executeInternal();
    }

    private NextResponse executeInternal() throws IOException {
        intercept();
        // re config request
        final NextClient client = mClient;
        final Builder builder = mRequest.copyToBuilder();
        final HttpEntity entity = mRequest.entity();
        long contentLength = -1;
        if (entity != null) {
            String contentType = entity.getContentType().getValue();
            contentLength = entity.getContentLength();
            if (contentType != null) {
                builder.header(Consts.CONTENT_TYPE, contentType);
            }
            if (contentLength != -1) {
                builder.header(Consts.CONTENT_LENGTH, Long.toString(contentLength));
                builder.removeHeader(Consts.TRANSFER_ENCODING);
            } else {
                builder.header(Consts.TRANSFER_ENCODING, "chunked");
                builder.removeHeader(Consts.CONTENT_LENGTH);
            }
            mRequest = builder.build();
        }

        if (mDebug) {
            LogUtils.v(TAG, "[NextRequest] " + mRequest);
        }


        HttpURLConnection conn = createConnection();
        client.configConnection(conn);
        addRequestHeaders(conn);
        writeBody(entity, contentLength, conn);
        final NextResponse response = getResponse(conn);

        mResponse = response;

        if (mDebug) {
            LogUtils.v(TAG, "[NextResponse] " + response);
        }

        return response;
    }

    private void intercept() {
        final NextInterceptor interceptor = mClient.getInterceptor();
        // intercept before create connection
        if (interceptor != null) {
            interceptor.intercept(mRequest);
        }
    }


    private HttpURLConnection createConnection() throws IOException {
        final NextClient client = mClient;
        final NextRequest request = mRequest;
        final HttpURLConnection connection;
        final String method = request.method();
        final Proxy proxy = client.getProxy();
        final URL url = request.url();
        if (proxy == null || Proxy.NO_PROXY.equals(proxy)) {
            connection = (HttpURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection(proxy);
        }
        connection.setRequestMethod(method);

        if (mDebug) {
            LogUtils.v(TAG, "createConnection() url=" + url + " method=" + method);
        }
        return connection;
    }

    public void addRequestHeaders(HttpURLConnection conn) {
        final NextRequest request = mRequest;
        final Map<String, String> clientHeaders = request.headers();
        for (Map.Entry<String, String> entry : clientHeaders.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void writeBody(final HttpEntity entity, final long length,
                           final HttpURLConnection conn) throws IOException {
        final ProgressCallback callback = mRequest.callback();
        if (HttpMethod.hasRequestBody(mRequest.method())) {
            conn.setDoOutput(true);
            final OutputStream os = conn.getOutputStream();
            OutputStream outputStream = null;
            try {
                outputStream = new ProgressOutputStream(os, callback, length);
                entity.writeTo(outputStream);
                outputStream.flush();
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
        }
    }

    private NextResponse getResponse(HttpURLConnection conn) throws IOException {
        final int code = conn.getResponseCode();
        final String message = conn.getResponseMessage();
        final int contentLength = conn.getContentLength();
        final String contentType = conn.getContentType();
        final String contentEncoding = conn.getContentEncoding();

        boolean isGzip = Consts.ENCODING_GZIP.equalsIgnoreCase(contentEncoding);

        Map<String, List<String>> rawHeaders = conn.getHeaderFields();

        InputStream httpStream;
        if (Utils.isSuccess(code)) {
            httpStream = conn.getInputStream();
        } else {
            httpStream = conn.getErrorStream();
        }

        if (httpStream == null) {
            httpStream = new ByteArrayInputStream(new byte[0]);
        }

        InputStream stream;
        if (isGzip) {
            stream = new GZIPInputStream(httpStream);
        } else {
            stream = httpStream;
        }

        return new NextResponse(code, message, contentLength,
                contentType, rawHeaders, stream);
    }

}
