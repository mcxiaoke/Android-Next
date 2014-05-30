package com.mcxiaoke.next.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
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
public class NextClient implements Consts {
    public static final String TAG = NextClient.class.getSimpleName();

    static final class SingletonHolder {
        public static final NextClient INSTANCE = new NextClient();
    }

    public static NextClient getDefault() {
        return SingletonHolder.INSTANCE;
    }

    private boolean mDebug;
    private Map<String, String> headers;
    private HttpURLConnection connection;
    private boolean useCaches;
    private boolean keepAlive;
    private boolean trustAllCerts;
    private boolean trustAllHosts;
    private boolean followRedirects;
    private int connectTimeout;
    private int readTimeout;
    private Proxy proxy;
    private CookieManager cookieManager;
    private NextInterceptor interceptor;

    private SSLSocketFactory mSSLSocketFactory;
    private HostnameVerifier mHostnameVerifier;
    private ConnectionFactory mConnectionFactory;


    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * Creates a new Http Client
     */
    public NextClient() {
        initDefaults();
    }

    private void initDefaults() {
        this.useCaches = true; // default
        this.keepAlive = true; // default
        this.trustAllCerts = false;
        this.trustAllHosts = false;
        this.followRedirects = true; //default
        this.connectTimeout = CONNECT_TIMEOUT;
        this.readTimeout = READ_TIMEOUT;
        this.proxy = Proxy.NO_PROXY;
        this.headers = new HashMap<String, String>();
        this.mSSLSocketFactory = Utils.createTrustedAllSslSocketFactory();
        this.mHostnameVerifier = Utils.createTrustAllHostNameVerifier();
        this.mConnectionFactory = ConnectionFactory.DEFAULT;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public void setCookieManager(final CookieManager cm) {
        this.cookieManager = cm;
    }

    public CookieManager getCookieManager() {
        return cookieManager;
    }

    public SSLSocketFactory getTrustedSSLSocketFactory() {
        if (mSSLSocketFactory == null) {
            mSSLSocketFactory = Utils.createTrustedAllSslSocketFactory();
        }
        return mSSLSocketFactory;
    }

    public HostnameVerifier getTrustedHostnameVerifier() {
        if (mHostnameVerifier == null) {
            mHostnameVerifier = Utils.createTrustAllHostNameVerifier();
        }
        return mHostnameVerifier;
    }

    public void setConnectionFactory(final ConnectionFactory cf) {
        if (cf == null) {
            mConnectionFactory = ConnectionFactory.DEFAULT;
        } else {
            mConnectionFactory = cf;
        }
    }

    public ConnectionFactory getConnectionFactory() {
        return mConnectionFactory;
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
        if (!headers.containsKey(key)) {
            this.headers.put(key, value);
        }
        return this;
    }

    public NextClient addHeaders(Map<String, String> map) {
        this.headers.putAll(map);
        return this;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    /**
     * Sets the add timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public NextClient setConnectTimeout(int millis) {
        this.connectTimeout = millis;
        return this;
    }

    /**
     * Sets the read timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public NextClient setReadTimeout(int millis) {
        this.readTimeout = millis;
        return this;
    }

    public NextClient setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    /**
     * Sets whether the underlying Http Connection is persistent or not.
     */
    public NextClient setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    /*
     * We need this in order to stub the connection object for test cases
     */
    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public NextClient setProxy(String host, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        setProxy(proxy);
        return this;
    }

    public NextClient setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public NextClient setCookieStore(CookieStore cookieStore) {
        this.cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        return this;
    }

    public NextClient setInterceptor(NextInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public NextClient acceptGzipEncoding() {
        addHeader(ACCEPT_ENCODING, ENCODING_GZIP);
        return this;
    }

    public NextClient setFollowRedirects(final boolean value) {
        followRedirects = value;
        return this;
    }

    public NextClient setUserAgent(final String userAgent) {
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
     * 信任所有证书
     *
     * @return
     */
    public NextClient setTrustAllCerts(boolean enable) {
        trustAllCerts = true;
        return this;
    }

    /**
     * 信任所有hosts
     *
     * @return
     */
    public NextClient setTrustAllHosts() {
        trustAllHosts = true;
        return this;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public boolean isUseCaches() {
        return useCaches;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public boolean isTrustAllCerts() {
        return trustAllCerts;
    }

    public boolean isTrustAllHosts() {
        return trustAllHosts;
    }

    public void setTrustAllHosts(final boolean trustAllHosts) {
        this.trustAllHosts = trustAllHosts;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public NextInterceptor getInterceptor() {
        return interceptor;
    }


    Caller newCaller(final NextRequest request) {
        return new Caller(this, request);
    }

    public NextResponse execute(final NextRequest request) throws IOException {
        return new Caller(this, request).execute();
    }

    /**
     * ****************************************************
     * <p/>
     * UTIL METHODS
     * <p/>
     * ****************************************************
     */

    public static NextResponse head(String url) throws IOException {
        return NextRequest.newBuilder().head(url).build().execute();
    }

    public static NextResponse head(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().head(url).params(params).build().execute();
    }

    public static NextResponse get(String url) throws IOException {
        return NextRequest.newBuilder().get(url).build().execute();
    }

    public static NextResponse get(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().get(url).params(params).build().execute();
    }

    public static NextResponse delete(String url) throws IOException {
        return NextRequest.newBuilder().delete(url).build().execute();
    }

    public static NextResponse delete(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().delete(url).params(params).build().execute();
    }

    public static NextResponse post(String url) throws IOException {
        return NextRequest.newBuilder().post(url).build().execute();
    }

    public static NextResponse post(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().post(url).params(params).build().execute();
    }

    public static NextResponse put(String url) throws IOException {
        return NextRequest.newBuilder().put(url).build().execute();
    }

    public static NextResponse put(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().put(url).params(params).build().execute();
    }

    public static NextResponse patch(String url) throws IOException {
        return NextRequest.newBuilder().patch(url).build().execute();
    }

    public static NextResponse patch(String url, Map<String, String> params) throws IOException {
        return NextRequest.newBuilder().patch(url).params(params).build().execute();
    }

}
