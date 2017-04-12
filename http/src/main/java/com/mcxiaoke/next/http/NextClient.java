package com.mcxiaoke.next.http;

import android.util.Log;
import com.mcxiaoke.next.http.transformer.HttpTransformer;
import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.LogUtils;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 15/7/1
 * Time: 14:13
 */
public final class NextClient {

    static class SingletonHolder {
        static NextClient INSTANCE = new NextClient();
    }

    public static NextClient getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public static final String TAG = NextClient.class.getSimpleName();
    private boolean mDebug;
    private OkHttpClient mClient;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;

    public NextClient() {
        this(new OkHttpClient());
    }

    public NextClient(OkHttpClient client) {
        mClient = client;
        mParams = new NoEmptyValuesHashMap();
        mHeaders = new NoEmptyValuesHashMap();
    }

    public NextClient setDebug(final boolean debug) {
        mDebug = debug;
        return this;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setClient(final OkHttpClient client) {
        mClient = client;
    }

    public OkHttpClient getClient() {
        return mClient;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    /***********************************************************
     * CLIENT PARAMS AND HEADERS
     * **********************************************************
     */

    public String getParam(final String key) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        return mParams.get(key);
    }

    public NextClient addParam(final String key, final String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        mParams.put(key, value);
        return this;
    }

    public NextClient addParams(final Map<String, String> params) {
        AssertUtils.notNull(params, "params must not be null.");
        mParams.putAll(params);
        return this;
    }

    public NextClient removeParam(final String key) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        mParams.remove(key);
        return this;
    }


    public int getParamsSize() {
        return mParams.size();
    }

    public String getHeader(final String key) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        return mHeaders.get(key);
    }

    public NextClient addHeader(final String key, final String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        mHeaders.put(key, value);
        return this;
    }

    public NextClient addHeaders(final Map<String, String> headers) {
        AssertUtils.notNull(headers, "headers must not be null.");
        mHeaders.putAll(headers);
        return this;
    }

    public NextClient removeHeader(final String key) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        mHeaders.remove(key);
        return this;
    }


    public int getHeadersSize() {
        return mHeaders.size();
    }

    public String getUserAgent() {
        return getHeader(HttpConsts.USER_AGENT);
    }

    public NextClient setUserAgent(final String userAgent) {
        if (userAgent == null) {
            removeHeader(HttpConsts.USER_AGENT);
        } else {
            addHeader(HttpConsts.USER_AGENT, userAgent);
        }
        return this;
    }

    public String getAuthorization() {
        return getHeader(HttpConsts.AUTHORIZATION);
    }

    public NextClient setAuthorization(final String authorization) {
        addHeader(HttpConsts.AUTHORIZATION, authorization);
        return this;
    }

    public NextClient removeAuthorization() {
        removeHeader(HttpConsts.AUTHORIZATION);
        return this;
    }

    public String getRefer() {
        return getHeader(HttpConsts.REFERER);
    }

    public NextClient setReferer(final String referer) {
        addHeader(HttpConsts.REFERER, referer);
        return this;
    }

    /***********************************************************
     * HTTP REQUEST METHODS
     * **********************************************************
     */

    public NextResponse head(final String url) throws IOException {
        return head(url, null);
    }

    public NextResponse head(final String url, final Map<String, String> queries)
            throws IOException {
        return head(url, queries, null);
    }

    public NextResponse head(final String url, final Map<String, String> queries,
                             final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.HEAD, url, queries, null, headers);
    }

    public NextResponse get(final String url) throws IOException {
        return get(url, null, null);
    }

    public NextResponse get(final String url, final Map<String, String> queries)
            throws IOException {
        return get(url, queries, null);
    }

    public NextResponse get(final String url, final Map<String, String> queries,
                            final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.GET, url, queries, null, headers);
    }

    public NextResponse delete(final String url) throws IOException {
        return delete(url, null, null);
    }

    // put params into url queries
    public NextResponse delete(final String url, final Map<String, String> queries)
            throws IOException {
        return delete(url, queries, null);
    }

    // put params into url queries
    public NextResponse delete(final String url, final Map<String, String> queries,
                               final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.DELETE, url, queries, null, headers);
    }

    // put params into  http request body
    public NextResponse delete2(final String url, final Map<String, String> forms)
            throws IOException {
        return delete(url, forms, null);
    }

    // put params into  http request body
    public NextResponse delete2(final String url, final Map<String, String> forms,
                                final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.DELETE, url, null, forms, headers);
    }

    public NextResponse post(final String url, final Map<String, String> forms)
            throws IOException {
        return post(url, forms, null);
    }

    public NextResponse post(final String url, final Map<String, String> forms,
                             final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.POST, url, null, forms, headers);
    }

    public NextResponse put(final String url, final Map<String, String> forms)
            throws IOException {
        return put(url, forms, null);
    }

    public NextResponse put(final String url, final Map<String, String> forms,
                            final Map<String, String> headers)
            throws IOException {
        return request(HttpMethod.PUT, url, null, forms, headers);
    }

    public NextResponse request(final HttpMethod method, final String url)
            throws IOException {
        return request(method, url, null, null, null);
    }

    public NextResponse request(final HttpMethod method, final String url,
                                final Map<String, String> queries)
            throws IOException {
        return request(method, url, queries, null, null);
    }

    public NextResponse get(final String url, final NextParams params) throws IOException {
        return request(HttpMethod.GET, url, params);
    }

    public NextResponse delete(final String url, final NextParams params) throws IOException {
        return request(HttpMethod.DELETE, url, params);
    }

    public NextResponse post(final String url, final NextParams params) throws IOException {
        return request(HttpMethod.POST, url, params);
    }

    public NextResponse put(final String url, final NextParams params) throws IOException {
        return request(HttpMethod.PUT, url, params);
    }

    public NextResponse request(final HttpMethod method, final String url,
                                final Map<String, String> queries,
                                final Map<String, String> forms)
            throws IOException {
        return request(method, url, queries, forms, null);
    }

    public NextResponse request(final HttpMethod method, final String url,
                                final Map<String, String> queries,
                                final Map<String, String> forms,
                                final Map<String, String> headers)
            throws IOException {
        return executeInternal(createRequest(method, url,
                queries, forms, headers));
    }

    public <T> T request(final HttpMethod method, final String url,
                         final Map<String, String> queries,
                         final Map<String, String> forms,
                         final Map<String, String> headers,
                         final HttpTransformer<T> converter)
            throws IOException {
        return executeInternal(createRequest(method, url,
                queries, forms, headers), converter);
    }

    public NextResponse request(final HttpMethod method, final String url,
                                final NextParams params)
            throws IOException {
        return executeInternal(createRequest(method, url, params));
    }

    public NextResponse execute(final NextRequest request)
            throws IOException {
        return executeInternal(request);
    }

    public <T> T execute(final NextRequest req, final HttpTransformer<T> transformer)
            throws IOException {
        return executeInternal(req, transformer);
    }

    public NextResponse execute(Request request)
            throws IOException {
        return new NextResponse(sendRequest(request));
    }

    protected NextResponse executeInternal(final NextRequest request)
            throws IOException {
        return new NextResponse(sendRequest(request));
    }

    protected <T> T executeInternal(final NextRequest request,
                                    final HttpTransformer<T> transformer)
            throws IOException {
        final Response response = sendRequest(request);
        return transformer.transform(new NextResponse(response));
    }

    public Response sendRequest(final NextRequest request)
            throws IOException {
        final OkHttpClient.Builder builder = mClient.newBuilder();
        if (mDebug || request.isDebug()) {
            LogUtils.v(NextClient.TAG, "[sendRequest] " + request);
            logHttpCurl(request);
            builder.addNetworkInterceptor(new LoggingInterceptor());
        }
        final OkHttpClient client = builder.build();
        return sendOkRequest(createOkRequest(request), client, request.isDebug());
    }

    public Response sendRequest(final Request request)
            throws IOException {
        final OkHttpClient client = mClient.newBuilder().build();
        if (mDebug) {
            Log.v(NextClient.TAG, "Sending " + request);
        }
        return sendOkRequest(request, client, false);
    }

    private Response sendOkRequest(final Request request,
                                   final OkHttpClient client,
                                   final boolean debug)
            throws IOException {
        long start = System.nanoTime();
        try {
            final Response response = client.newCall(request).execute();
            if (mDebug || debug) {
                Log.d(NextClient.TAG, "[sendRequest][OK] " + request.url()
                        + " in " + (System.nanoTime() - start) / 1000000
                        + "ms Response:" + response);
            }
            return response;
        } catch (IOException e) {
            if (mDebug || debug) {
                Log.w(NextClient.TAG, "[sendRequest][FAIL] " + request.url()
                        + " in " + (System.nanoTime() - start) / 1000000
                        + "ms  Error:" + e);
            }
            throw e;
        }
    }

    public NextRequest createRequest(final HttpMethod method, final String url,
                                     final NextParams params) {
        final NextRequest request = new NextRequest(method, url);
        if (request.supportBody()) {
            request.forms(mParams);
        } else {
            request.queries(mParams);
        }
        request.headers(mHeaders);
        return request.params(params);
    }

    public NextRequest createRequest(final HttpMethod method, final String url,
                                     final Map<String, String> queries,
                                     final Map<String, String> forms,
                                     final Map<String, String> headers) {
        final NextRequest request = new NextRequest(method, url);
        if (request.supportBody()) {
            request.forms(mParams);
        } else {
            request.queries(mParams);
        }
        request.headers(mHeaders);
        return request.queries(queries)
                .forms(forms).headers(headers);
    }

    public Request createOkRequest(final NextRequest request) throws IOException {
        return new Request.Builder()
                .url(request.url())
                .headers(Headers.of(request.headers()))
                .method(request.method().name(), request.getRequestBody()).build();
    }

    public Request createOkRequest(final HttpMethod method, final String url,
                                   final Map<String, String> queries,
                                   final Map<String, String> forms,
                                   final Map<String, String> headers)
            throws IOException {
        return createOkRequest(createRequest(method, url, queries, forms, headers));
    }

    private void logHttpCurl(final NextRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append("http");
        builder.append(" -f");
        builder.append(" ").append(request.method().name());
        builder.append(" ").append(request.url());
//
//        for (Map.Entry<String, String> entry : request.queries().entrySet()) {
//            builder.append(" ").append(entry.getKey()).append("==").append(entry.getValue());
//        }

        for (KeyValue entry : request.forms()) {
            builder.append(" ").append(entry.first).append("=\"")
                    .append(entry.second).append("\"");
        }

        for (BodyPart part : request.parts()) {
            builder.append(" ").append(part.getName()).append("@").append(part.getFileName());
        }

        for (Map.Entry<String, String> entry : request.headers().entrySet()) {
            builder.append(" ").append(entry.getKey()).append(":\"")
                    .append(entry.getValue()).append("\"");
        }

        LogUtils.i(TAG, "Http Command: [ " + builder.toString() + " ]");

    }

}
