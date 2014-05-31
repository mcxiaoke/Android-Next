package com.mcxiaoke.next.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class NextClient implements Consts, Cloneable {
    public static final String TAG = NextClient.class.getSimpleName();
    private boolean mDebug;
    private String mUserAgent;
    private Map<String, String> mHeaders;
    private boolean mUseCaches;
    private boolean mTrustAllCerts;
    private boolean mTrustAllHosts;
    private boolean mFollowRedirects;
    private int mConnectTimeout;
    private int mReadTimeout;
    private Proxy mProxy;
    private CookieManager mCookieManager;
    private NextInterceptor mInterceptor;
    private SSLSocketFactory mSSLSocketFactory;
    private HostnameVerifier mHostnameVerifier;

    /**
     * Creates a new Http Client
     */
    public NextClient() {
        initDefaults();
    }

    public static NextClient getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public static NextResponse head(String url) throws IOException {
        return NextRequest.newBuilder().head(url).build().execute();
    }

    public static NextResponse head(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().head(url).params(params).build().execute();
    }

    public static NextResponse get(String url) throws IOException {
        return NextRequest.newBuilder().get(url).build().execute();
    }

    public static NextResponse get(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().get(url).params(params).build().execute();
    }

    public static NextResponse delete(String url) throws IOException {
        return NextRequest.newBuilder().delete(url).build().execute();
    }

    public static NextResponse delete(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().delete(url).params(params).build().execute();
    }

    public static NextResponse post(String url) throws IOException {
        return NextRequest.newBuilder().post(url).build().execute();
    }

    public static NextResponse post(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().post(url).params(params).build().execute();
    }

    public static NextResponse put(String url) throws IOException {
        return NextRequest.newBuilder().put(url).build().execute();
    }

    public static NextResponse put(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().put(url).params(params).build().execute();
    }

    public static NextResponse patch(String url) throws IOException {
        return NextRequest.newBuilder().patch(url).build().execute();
    }

    public static NextResponse patch(String url, NextParams params) throws IOException {
        return NextRequest.newBuilder().patch(url).params(params).build().execute();
    }

    private void initDefaults() {
        this.mUseCaches = true; // default
        this.mTrustAllCerts = false;
        this.mTrustAllHosts = false;
        this.mFollowRedirects = true; //default
        this.mConnectTimeout = CONNECT_TIMEOUT;
        this.mReadTimeout = READ_TIMEOUT;
        this.mProxy = Proxy.NO_PROXY;
        this.mHeaders = new HashMap<String, String>();
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    public String getUserAgent() {
        return mUserAgent;
    }

    public CookieManager getCookieManager() {
        return mCookieManager;
    }

    public void setCookieManager(final CookieManager cm) {
        this.mCookieManager = cm;
    }

    public SSLSocketFactory getTrustedSSLSocketFactory() {
        return mSSLSocketFactory;
    }

    public HostnameVerifier getTrustedHostnameVerifier() {
        return mHostnameVerifier;
    }

    public void setSSLSocketFactory(final SSLSocketFactory SSLSocketFactory) {
        mSSLSocketFactory = SSLSocketFactory;
    }

    public void setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
    }

    /**
     * Add an HTTP Header to the Request
     *
     * @param key   the header name
     * @param value the header value
     */
    public NextClient addHeader(String key, String value) {
        if (!mHeaders.containsKey(key)) {
            this.mHeaders.put(key, value);
        }
        return this;
    }

    public NextClient addHeaders(Map<String, String> map) {
        this.mHeaders.putAll(map);
        return this;
    }

    public Map<String, String> getHeaders() {
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
        addHeader(ACCEPT_ENCODING, ENCODING_GZIP);
        return this;
    }

    public NextClient setUserAgent(final String userAgent) {
        mUserAgent = userAgent;
        if (userAgent != null) {
            addHeader(USER_AGENT, userAgent);
        }
        return this;
    }

    public NextClient setReferer(final String referer) {
        addHeader(REFERER, referer);
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
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
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
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public NextClient setReadTimeout(int millis) {
        this.mReadTimeout = millis;
        return this;
    }


    public NextInterceptor getInterceptor() {
        return mInterceptor;
    }

    public NextClient setInterceptor(NextInterceptor interceptor) {
        this.mInterceptor = interceptor;
        return this;
    }

    void configConnection(HttpURLConnection conn) {
        applyClientConfig(conn);
        applyHttpsConfig(conn);
        applyClientHeaders(conn);
    }

    private void applyClientConfig(HttpURLConnection conn) {
        conn.setUseCaches(mUseCaches);
        conn.setInstanceFollowRedirects(mFollowRedirects);
        conn.setConnectTimeout(mConnectTimeout);
        conn.setReadTimeout(mReadTimeout);
    }

    private void applyHttpsConfig(HttpURLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            final HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            final SSLSocketFactory sslSocketFactory = mSSLSocketFactory;
            final HostnameVerifier hostnameVerifier = mHostnameVerifier;
            if (sslSocketFactory != null) {
                httpsConn.setSSLSocketFactory(sslSocketFactory);
            }
            if (hostnameVerifier != null) {
                httpsConn.setHostnameVerifier(hostnameVerifier);
            }
        }
    }

    private void applyClientHeaders(HttpURLConnection conn) {
        final Map<String, String> clientHeaders = mHeaders;
        for (Map.Entry<String, String> entry : clientHeaders.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }


    @Override
    public NextClient clone() {
        try {
            return (NextClient) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private NextClient copyDefaults() {
        NextClient client = clone();
        return client;
    }

    Caller newCaller(final NextRequest request) {
        return new Caller(copyDefaults(), request);
    }

    public NextResponse execute(final NextRequest request) throws IOException {
        return newCaller(request).execute();
    }

    static final class SingletonHolder {
        public static final NextClient INSTANCE = new NextClient();
    }

}
