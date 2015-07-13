package com.mcxiaoke.next.http;

import android.util.Log;
import com.mcxiaoke.next.utils.AssertUtils;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final OkHttpClient mClient;
    private OkClientInterceptor mInterceptor;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;

    public NextClient() {
        mClient = new OkHttpClient();
        mClient.setFollowRedirects(true);
        mParams = new NoEmptyValuesHashMap();
        mHeaders = new NoEmptyValuesHashMap();
    }

    public NextClient setDebug(final boolean debug) {
        mDebug = debug;
        return this;
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

    public NextClient setInterceptor(final OkClientInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    public NextClient setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mClient.setHostnameVerifier(hostnameVerifier);
        return this;
    }

    public NextClient setSocketFactory(SocketFactory socketFactory) {
        mClient.setSocketFactory(socketFactory);
        return this;
    }

    public NextClient setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        mClient.setSslSocketFactory(sslSocketFactory);
        return this;
    }

    public NextClient setFollowRedirects(boolean followRedirects) {
        mClient.setFollowRedirects(followRedirects);
        return this;
    }

    public NextClient setFollowSslRedirects(boolean followProtocolRedirects) {
        mClient.setFollowSslRedirects(followProtocolRedirects);
        return this;
    }

    public NextClient setRetryOnConnectionFailure(boolean retryOnConnectionFailure) {
        mClient.setRetryOnConnectionFailure(retryOnConnectionFailure);
        return this;
    }

    public int getConnectTimeout() {
        return mClient.getConnectTimeout();
    }

    public int getReadTimeout() {
        return mClient.getReadTimeout();
    }

    public int getWriteTimeout() {
        return mClient.getWriteTimeout();
    }

    public NextClient setConnectTimeout(long timeout, TimeUnit unit) {
        mClient.setConnectTimeout(timeout, unit);
        return this;
    }

    public NextClient setReadTimeout(long timeout, TimeUnit unit) {
        mClient.setReadTimeout(timeout, unit);
        return this;
    }

    public NextClient setWriteTimeout(long timeout, TimeUnit unit) {
        mClient.setWriteTimeout(timeout, unit);
        return this;
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
        return executeInternal(createRequest(method, url, queries, forms, headers));
    }

    public NextResponse request(final HttpMethod method, final String url,
                                final NextParams params)
            throws IOException {
        return executeInternal(createRequest(method, url, params));
    }


    protected NextRequest createRequest(final HttpMethod method, final String url,
                                        final NextParams params) {
        final NextRequest request = new NextRequest(method, url)
                .headers(mHeaders);
        if (request.supportBody()) {
            request.forms(mParams);
        } else {
            request.queries(mParams);
        }
        return request.params(params);
    }

    protected NextRequest createRequest(final HttpMethod method, final String url,
                                        final Map<String, String> queries,
                                        final Map<String, String> forms,
                                        final Map<String, String> headers) {
        final NextRequest request = new NextRequest(method, url)
                .headers(mHeaders);
        if (request.supportBody()) {
            request.forms(mParams);
            request.forms(forms);
        } else {
            request.queries(mParams);
        }
        return request.headers(headers).queries(queries);
    }

    public NextResponse execute(final NextRequest req)
            throws IOException {
        // add client params and headers to request
        final NextRequest request = new NextRequest(req.method(), req.url().toString());
        if (request.supportBody()) {
            request.forms(mParams);
        } else {
            request.queries(mParams);
        }
        request.copy(req);
        return executeInternal(request);
    }

    protected NextResponse executeInternal(final NextRequest request)
            throws IOException {
        return new NextResponse(sendRequest(request));
    }

    protected Response sendRequest(final NextRequest nr)
            throws IOException {

        final Request request = createOkRequest(nr);
        final OkHttpClient client = mClient.clone();
        if (mDebug || nr.debug()) {
            Log.v(NextClient.TAG, "execute() " + nr.dump());
            // intercept for logging
            client.networkInterceptors().add(new LoggingInterceptor());
        }
        // intercept for progress callback
        if (nr.listener() != null) {
            client.interceptors().add(new ProgressInterceptor(nr.listener()));
        }
        if (mInterceptor != null) {
            mInterceptor.intercept(client);
        }
        return client.newCall(request).execute();

    }

    static Request createOkRequest(final NextRequest nr) throws IOException {
        return new Request.Builder()
                .url(nr.url())
                .headers(Headers.of(nr.headers()))
                .method(nr.method().name(), nr.getRequestBody()).build();
    }

}
