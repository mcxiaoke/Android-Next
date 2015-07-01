package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.LogUtils;
import com.mcxiaoke.next.utils.StringUtils;
import org.apache.http.HttpEntity;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class NextClient implements HttpConsts {
    public static final String TAG = NextClient.class.getSimpleName();
    private boolean mDebug;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;
    private boolean mUseCaches;
    private boolean mTrustAllCerts;
    private boolean mTrustAllHosts;
    private boolean mFollowRedirects;
    private int mConnectTimeout;
    private int mReadTimeout;
    private Proxy mProxy;
    private CookieManager mCookieManager;
    private RequestInterceptor mInterceptor;
    private SSLSocketFactory mSSLSocketFactory;
    private HostnameVerifier mHostnameVerifier;
    private WeakReference<NextRequest> mRequestRef;
    private WeakReference<NextResponse> mResponseRef;

    static final class SingletonHolder {
        public static final NextClient INSTANCE = new NextClient();
    }


    public static NextClient getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public static NextResponse get(String url) throws IOException {
        return NextClient.getDefault().execute(NextRequest.get(url));
    }

    public static NextResponse get(String url, NextParams params) throws IOException {
        return NextClient.getDefault().execute(NextRequest.get(url).params(params));
    }

    public static NextResponse delete(String url) throws IOException {
        return NextClient.getDefault().execute(NextRequest.delete(url));
    }

    public static NextResponse delete(String url, NextParams params) throws IOException {
        return NextClient.getDefault().execute(NextRequest.delete(url).params(params));
    }

    public static NextResponse post(String url) throws IOException {
        return NextClient.getDefault().execute(NextRequest.post(url));
    }

    public static NextResponse post(String url, NextParams params) throws IOException {
        return NextClient.getDefault().execute(NextRequest.post(url).params(params));
    }

    public static NextResponse put(String url) throws IOException {
        return NextClient.getDefault().execute(NextRequest.put(url));
    }

    public static NextResponse put(String url, NextParams params) throws IOException {
        return NextClient.getDefault().execute(NextRequest.put(url).params(params));
    }

    /**
     * Creates a new Http Client
     */
    public NextClient() {
        initDefaults();
    }

    private void initDefaults() {
        this.mUseCaches = true; // default
        this.mTrustAllCerts = false;
        this.mTrustAllHosts = false;
        this.mFollowRedirects = true; //default
        this.mConnectTimeout = CONNECT_TIMEOUT;
        this.mReadTimeout = READ_TIMEOUT;
        this.mProxy = Proxy.NO_PROXY;
        this.mParams = new HashMap<String, String>();
        this.mHeaders = new HashMap<String, String>();
        acceptGzipEncoding();
    }

    public boolean isDebug() {
        return mDebug;
    }

    public NextClient setDebug(boolean debug) {
        mDebug = debug;
        return this;
    }

    public String getClientHeader(final String key) {
        return mHeaders.get(key);
    }

    public String getUserAgent() {
        return getClientHeader(USER_AGENT);
    }

    public String getAuthorization() {
        return getClientHeader(AUTHORIZATION);
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    public NextClient setCookieManager(final CookieManager cm) {
        this.mCookieManager = cm;
        return this;
    }

    public SSLSocketFactory getTrustedSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    public HostnameVerifier getTrustedHostnameVerifier() {
        return mHostnameVerifier;
    }

    public NextClient setSSLSocketFactory(final SSLSocketFactory SSLSocketFactory) {
        mSSLSocketFactory = SSLSocketFactory;
        return this;
    }

    public NextClient setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
        return this;
    }

    public NextClient addClientParam(String key, String value) {
        this.mParams.put(key, value);
        return this;
    }

    public NextClient removeClientParam(String key) {
        this.mParams.remove(key);
        return this;
    }

    public NextClient addClientParams(Map<String, String> map) {
        this.mParams.putAll(map);
        return this;
    }

    public Map<String, String> getClientParams() {
        return this.mParams;
    }

    /**
     * Add an HTTP Header to the Request
     *
     * @param key   the header name
     * @param value the header value
     */
    public NextClient addClientHeader(String key, String value) {
        this.mHeaders.put(key, value);
        return this;
    }

    public NextClient removeClientHeader(String key) {
        this.mHeaders.remove(key);
        return this;
    }

    public NextClient addClientHeaders(Map<String, String> map) {
        this.mHeaders.putAll(map);
        return this;
    }

    public Map<String, String> getClientHeaders() {
        return this.mHeaders;
    }

    public NextClient setProxy(String host, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        setProxy(proxy);
        return this;
    }

    public Proxy getProxy() {
        return mProxy;
    }

    public NextClient setProxy(Proxy proxy) {
        this.mProxy = proxy;
        return this;
    }

    public NextClient acceptGzipEncoding() {
        addClientHeader(ACCEPT_ENCODING, ENCODING_GZIP);
        return this;
    }

    public NextClient setUserAgent(final String userAgent) {
        if (userAgent != null) {
            addClientHeader(USER_AGENT, userAgent);
        }
        return this;
    }

    public NextClient setAuthorization(final String authorization) {
        if (authorization != null) {
            addClientHeader(AUTHORIZATION, authorization);
        }
        return this;
    }

    public NextClient setReferer(final String referer) {
        addClientHeader(REFERER, referer);
        return this;
    }

    /**
     * 信任所有hosts
     */
    public NextClient setTrustAllHosts() {
        mTrustAllHosts = true;
        mHostnameVerifier = Utils.createTrustAllHostNameVerifier();
        return this;
    }

    public boolean isUseCaches() {
        return mUseCaches;
    }

    public NextClient setUseCaches(boolean useCaches) {
        this.mUseCaches = useCaches;
        return this;
    }

    public boolean isTrustAllCerts() {
        return mTrustAllCerts;
    }

    /**
     * 信任所有证书
     *
     * @return
     */
    public NextClient setTrustAllCerts() {
        mTrustAllCerts = true;
        mSSLSocketFactory = Utils.createTrustedAllSslSocketFactory();
        return this;
    }

    public boolean isTrustAllHosts() {
        return mTrustAllHosts;
    }

    public boolean isFollowRedirects() {
        return mFollowRedirects;
    }

    public NextClient setFollowRedirects(final boolean value) {
        mFollowRedirects = value;
        return this;
    }

    public int getConnectTimeout() {
        return mConnectTimeout;
    }

    /**
     * Sets the add timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param millis duration of the timeout
     */
    public NextClient setConnectTimeout(int millis) {
        this.mConnectTimeout = millis;
        return this;
    }

    public int getReadTimeout() {
        return mReadTimeout;
    }

    /**
     * Sets the read timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param millis duration of the timeout
     */
    public NextClient setReadTimeout(int millis) {
        this.mReadTimeout = millis;
        return this;
    }


    public RequestInterceptor getInterceptor() {
        return mInterceptor;
    }

    public NextClient setInterceptor(RequestInterceptor interceptor) {
        this.mInterceptor = interceptor;
        return this;
    }

    public NextResponse execute(final NextRequest request) throws IOException {
        return executeInternal(request);
    }

    private NextResponse executeInternal(final NextRequest request) throws IOException {
        // config request
        final boolean isDebug = mDebug || request.isDebug();
        final RequestInterceptor interceptor = getInterceptor();
        if (interceptor != null) {
            interceptor.intercept(request);
        }

        if (isDebug) {
            LogUtils.v(TAG, "[Request]\n" + request.dump());
        }

        HttpURLConnection conn = openConnection(request);

        final Map<String, String> clientHeaders = mHeaders;
        for (Map.Entry<String, String> entry : clientHeaders.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        final Map<String, String> requestHeaders = request.getHeaders();
        for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
        if (isDebug) {
            LogUtils.v(TAG, "[Request Headers]\n"
                    + StringUtils.toString(conn.getRequestProperties(), "\n"));
        }
        addBodyIfNeeds(request, conn);
        final NextResponse response = getResponse(conn);
        if (isDebug) {
            LogUtils.v(TAG, "[Response Headers]\n"
                    + StringUtils.toString(response.headers(), "\n"));
        }
        return response;
    }

    private HttpURLConnection openConnection(final NextRequest request) throws IOException {
        final HttpURLConnection connection;
        final String method = request.getMethod();
        final Proxy proxy = getProxy();
        final URL url = request.getURL();
        if (proxy == null || Proxy.NO_PROXY.equals(proxy)) {
            connection = (HttpURLConnection) url.openConnection();
        } else {
            connection = (HttpURLConnection) url.openConnection(proxy);
        }
        connection.setRequestMethod(method);
        connection.setUseCaches(mUseCaches);
        connection.setInstanceFollowRedirects(mFollowRedirects);
        connection.setConnectTimeout(mConnectTimeout);
        connection.setReadTimeout(mReadTimeout);

        if (connection instanceof HttpsURLConnection) {
            final HttpsURLConnection httpsConn = (HttpsURLConnection) connection;
            final SSLSocketFactory sslSocketFactory = mSSLSocketFactory;
            final HostnameVerifier hostnameVerifier = mHostnameVerifier;
            if (sslSocketFactory != null) {
                httpsConn.setSSLSocketFactory(sslSocketFactory);
            }
            if (hostnameVerifier != null) {
                httpsConn.setHostnameVerifier(hostnameVerifier);
            }
        }

        if (mDebug) {
            LogUtils.v(TAG, "createConnection() url=" + url + " method=" + method);
        }
        return connection;
    }

    private void addBodyIfNeeds(final NextRequest request,
                                final HttpURLConnection conn) throws IOException {
        if (!request.hasBody()) {
            return;
        }
        HttpEntity entity = new NextParams(request.getParams()).putAll(mParams).entity();
        if (request.isDebug()) {
            LogUtils.v(TAG, "addBodyIfNeeds() entity=" + entity);
        }
        if (entity == null) {
            return;
        }
        long contentLength = -1;
        String contentType;
        if (entity.getContentType() != null) {
            contentType = entity.getContentType().getValue();
        } else {
            contentType = HttpConsts.DEFAULT_CONTENT_TYPE;
        }
        contentLength = entity.getContentLength();
        if (contentType != null) {
            conn.addRequestProperty(HttpConsts.CONTENT_TYPE, contentType);
        }
//            if (contentLength != -1) {
//                conn.addRequestProperty(Consts.CONTENT_LENGTH, Long.toString(contentLength));
//            } else {
//                conn.addRequestProperty(Consts.TRANSFER_ENCODING, "chunked");
//            }

        conn.setDoOutput(true);
        final ProgressCallback callback = request.getCallback();
        final OutputStream os = conn.getOutputStream();
        final long length = entity.getContentLength();
        OutputStream outputStream = null;
        try {
            outputStream = new ProgressOutputStream(os, callback, length);
            entity.writeTo(outputStream);
            outputStream.flush();
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
    }

    private NextResponse getResponse(HttpURLConnection conn) throws IOException {
        final int code = conn.getResponseCode();
        final String message = conn.getResponseMessage();
        final int contentLength = conn.getContentLength();
        final String contentType = conn.getContentType();
        final String contentEncoding = conn.getContentEncoding();

        boolean isGzip = HttpConsts.ENCODING_GZIP.equalsIgnoreCase(contentEncoding);

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
