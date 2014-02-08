package com.mcxiaoke.commons.http;

import com.mcxiaoke.commons.utils.StringUtils;
import org.apache.http.HttpEntity;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class HttpRequest implements HttpConsts {

    public enum Method {
        GET, POST, PUT, DELETE, HEAD
    }

    private final String url;
    private final Method method;
    private HttpParams params;
    private Map<String, String> headers;
    private HttpURLConnection connection;
    private String charset;
    private boolean useCaches;
    private boolean keepAlive;
    private boolean trustAllCerts;
    private boolean trustAllHosts;
    private boolean followRedirects;
    private int connectTimeout;
    private int readTimeout;
    private Proxy proxy;
    private CookieManager cookieManager;
    private RequestInterceptor interceptor;

    private HttpEntity httpEntity;

    private static SSLSocketFactory TRUSTED_FACTORY;

    private static HostnameVerifier TRUSTED_VERIFIER;


    public static HttpRequest head(String url) {
        return new HttpRequest(Method.HEAD, url);
    }

    public static HttpRequest get(String url) {
        return new HttpRequest(Method.GET, url);
    }

    public static HttpRequest get(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(Method.GET, url);
        request.addParameters(params);
        return request;
    }

    public static HttpRequest delete(String url) {
        return new HttpRequest(Method.DELETE, url);
    }

    public static HttpRequest delete(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(Method.DELETE, url);
        request.addParameters(params);
        return request;
    }

    public static HttpRequest post(String url) {
        return new HttpRequest(Method.POST, url);
    }

    public static HttpRequest post(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(Method.POST, url);
        request.addParameters(params);
        return request;
    }

    public static HttpRequest put(String url) {
        return new HttpRequest(Method.PUT, url);
    }

    public static HttpRequest put(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(Method.PUT, url);
        request.addParameters(params);
        return request;
    }


    /**
     * Creates a new Http Request
     *
     * @param method Http method (GET, POST, etc)
     * @param url    url with optional queryString parameters.
     */
    public HttpRequest(Method method, String url) {
        this.method = method;
        this.url = url;
        initDefaults();
    }

    private void initDefaults() {
        this.charset = Encoder.ENCODING_UTF8;
        this.useCaches = false;
        this.keepAlive = true;
        this.trustAllCerts = false;
        this.trustAllHosts = false;
        this.followRedirects = false;
        this.connectTimeout = CONNECT_TIMEOUT;
        this.readTimeout = READ_TIMEOUT;
        this.proxy = Proxy.NO_PROXY;
        this.keepAlive = false;
        this.headers = new HashMap<String, String>();
        this.params = new HttpParams();

    }

    private static SSLSocketFactory getTrustedFactory()
            throws IOException {
        if (TRUSTED_FACTORY == null) {
            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    // Intentionally left blank
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    // Intentionally left blank
                }
            }};
            try {
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(null, trustAllCerts, new SecureRandom());
                TRUSTED_FACTORY = context.getSocketFactory();
            } catch (GeneralSecurityException e) {
                IOException ioException = new IOException(
                        "Security exception configuring SSL context");
                ioException.initCause(e);
                throw ioException;
            }
        }

        return TRUSTED_FACTORY;
    }

    private static HostnameVerifier getTrustedVerifier() {
        if (TRUSTED_VERIFIER == null)
            TRUSTED_VERIFIER = new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

        return TRUSTED_VERIFIER;
    }

    private HttpURLConnection createConnection() throws IOException {
        String completeUrl = getCompleteUrl();
        if (proxy == null || Proxy.NO_PROXY.equals(proxy)) {
            connection = (HttpURLConnection) new URL(completeUrl)
                    .openConnection();
        } else {
            connection = (HttpURLConnection) new URL(completeUrl)
                    .openConnection(proxy);
        }

        connection.setRequestMethod(this.method.name());

        return connection;
    }

    private HttpURLConnection getConnection() throws IOException {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }

    /**
     * Execute the request and return a {@link HttpResponse}
     *
     * @return Http Response
     * @throws RuntimeException if the connection cannot be created.
     */
    public HttpResponse execute() throws IOException {
        return doExecute();
    }

    private HttpResponse doExecute() throws IOException {
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection conn = getConnection();
        conn.setUseCaches(useCaches);
        conn.setInstanceFollowRedirects(followRedirects);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);

        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            if (trustAllCerts) {
                httpsConn.setSSLSocketFactory(getTrustedFactory());
            }
            if (trustAllHosts) {
                httpsConn.setHostnameVerifier(getTrustedVerifier());
            }
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        intercept();
        checkWriteBody(conn);
        conn.connect();
        return handleResponse(conn);

    }

    private HttpResponse handleResponse(HttpURLConnection conn) throws IOException {
        int code = conn.getResponseCode();
        String message = conn.getResponseMessage();
        int contentLength = conn.getContentLength();
        String contentType = conn.getContentType();
        String contentEncoding = conn.getContentEncoding();

        boolean isGzip = ENCODING_GZIP.equalsIgnoreCase(contentEncoding);

        Map<String, List<String>> rawHeaders = conn.getHeaderFields();

        InputStream httpStream;
        if (isSuccess(code)) {
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

        HttpResponse response = HttpResponse.create(code, message);
        response.setContentLength(contentLength).setContentType(contentType);
        response.setHeaders(rawHeaders).setStream(stream);
        return response;
    }

    private void checkWriteBody(HttpURLConnection conn) throws IOException {
        if (Method.POST.equals(this.method) || Method.PUT.equals(this.method)) {
            writeBody(conn);
        }
    }

    private void writeBody(HttpURLConnection conn) throws IOException {
        conn.setDoOutput(true);
        if (httpEntity != null) {
            conn.setRequestProperty(CONTENT_TYPE, httpEntity.getContentType().getValue());
            conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(httpEntity.getContentLength()));
            httpEntity.writeTo(conn.getOutputStream());
        } else if (params != null) {
            params.writeTo(conn);
        }
    }

    private boolean isSuccess(int code) {
        return code >= HttpURLConnection.HTTP_OK && code < HttpURLConnection.HTTP_BAD_REQUEST;
    }

    private void intercept() {
        if (interceptor != null) {
            interceptor.intercept(this);
        }
    }

    /**
     * Returns the complete url (host + resource + encoded queryString
     * parameters).
     *
     * @return the complete url.
     */
    public String getCompleteUrl() {
        if (Method.POST.equals(method) || Method.PUT.equals(method)) {
            return url;
        } else {
            return appendTo(url);
        }
    }

    private String appendTo(String url) {
        return params.appendQueryString(url);
    }

    /**
     * Add an HTTP Header to the Request
     *
     * @param key   the header name
     * @param value the header value
     */
    public HttpRequest addHeader(String key, String value) {
        if (!headers.containsKey(key)) {
            this.headers.put(key, value);
        }
        return this;
    }

    public HttpRequest addParam(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public HttpRequest addParameters(Map<String, String> map) {
        this.params.put(map);
        return this;
    }

    public HttpRequest addPart(String key, File file, String contentType) throws FileNotFoundException {
        this.params.put(key, file, contentType);
        return this;
    }

    public HttpRequest addPart(String key, byte[] bytes, String contentType) {
        this.params.put(key, bytes, contentType);
        return this;
    }

    public HttpRequest addPart(String key, byte[] bytes, String fileName, String contentType) {
        this.params.put(key, bytes, contentType, fileName);
        return this;
    }

    public HttpRequest addPart(String key, InputStream stream, String contentType) {
        this.params.put(key, stream, contentType);
        return this;
    }

    public HttpRequest addPart(String key, InputStream stream, String contentType, String fileName) {
        this.params.put(key, stream, contentType, fileName);
        return this;
    }

    /**
     * Obtains the body parameters.
     *
     * @return containing the body parameters.
     */
    public Map<String, String> getParams() {
        return this.params.getParams();
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    private boolean hasStream() {
        return this.params.hasStream();
    }

    /**
     * Obtains the URL of the HTTP Request.
     *
     * @return the original URL of the HTTP Request
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the HTTP method
     *
     * @return the method
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Returns the connection charset. Defaults to {@link java.nio.charset.Charset}
     * defaultCharset if not set
     *
     * @return charset
     */
    public String getCharset() {
        return charset == null ? Charset.defaultCharset().name() : charset;
    }

    /**
     * Sets the doExecute timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public void setConnectTimeout(int millis) {
        this.connectTimeout = millis;
    }

    /**
     * Sets the read timeout for the underlying {@link java.net.HttpURLConnection}
     *
     * @param duration duration of the timeout
     * @param unit     unit of time (milliseconds, seconds, etc)
     */
    public void setReadTimeout(int millis) {
        this.readTimeout = millis;
    }

    /**
     * Set the charset of the body of the request
     *
     * @param charsetName name of the charset of the request
     */
    public void setCharset(String charsetName) {
        this.charset = charsetName;
    }

    public HttpRequest setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
        return this;
    }

    /**
     * Sets whether the underlying Http Connection is persistent or not.
     */
    public HttpRequest setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    /*
     * We need this in order to stub the connection object for test cases
     */
    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public HttpRequest setProxy(String host, int port) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        setProxy(proxy);
        return this;
    }

    public HttpRequest setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpRequest setCookieStore(CookieStore cookieStore) {
        this.cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        return this;
    }

    public HttpRequest setInterceptor(RequestInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public HttpRequest acceptGzipEncoding() {
        addHeader(ACCEPT_ENCODING, ENCODING_GZIP);
        return this;
    }

    public HttpRequest setFollowRedirects(final boolean value) {
        followRedirects = value;
        return this;
    }

    public HttpRequest setUserAgent(final String userAgent) {
        if (userAgent != null) {
            addHeader(USER_AGENT, userAgent);
        }
        return this;
    }

    public HttpRequest setReferer(final String referer) {
        addHeader(REFERER, referer);
        return this;
    }

    /**
     * 信任所有证书
     *
     * @return
     */
    public HttpRequest setTrustAllCerts(boolean enable) {
        trustAllCerts = true;
        return this;
    }

    /**
     * 信任所有hosts
     *
     * @return
     */
    public HttpRequest setTrustAllHosts() {
        trustAllHosts = true;
        return this;
    }

    public HttpRequest setHttpEntity(HttpEntity entity) {
        httpEntity = entity;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpRequest{");
        sb.append("url='").append(url).append('\'');
        sb.append(", method=").append(method);
        sb.append(", params=").append(params);
        sb.append(", headers=").append(StringUtils.getPrintString(headers));
        sb.append(", charset='").append(charset).append('\'');
        sb.append(", proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }
}
