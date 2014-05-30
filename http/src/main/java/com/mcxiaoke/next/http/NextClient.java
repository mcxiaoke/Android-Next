package com.mcxiaoke.next.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class NextClient implements NextConsts {
    public static final String TAG = NextClient.class.getSimpleName();

    private static boolean sDebug;
    private final String url;
    private final Method method;
    private NextParams httpParams;
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
    private NextInterceptor interceptor;

    private HttpEntity httpEntity;
    private NextResponse response;

    private ProgressCallback progressCallback = ProgressCallback.DEFAULT;

    private static SSLSocketFactory sTrustedSocketFactory;

    private static HostnameVerifier sTrustedHostnameVerifier;

    private static ConnectionFactory sConnectionFactory = ConnectionFactory.DEFAULT;

    public static void setDebug(boolean debug) {
        NextClient.sDebug = debug;
    }

    public static NextClient head(String url) {
        return create(url, Method.HEAD);
    }

    public static NextClient get(String url) {
        return create(url, Method.GET);
    }

    public static NextClient get(String url, Map<String, String> params) {
        return get(url).addParams(params);
    }

    public static NextClient delete(String url) {
        return create(url, Method.DELETE);
    }

    public static NextClient delete(String url, Map<String, String> params) {
        return delete(url).addParams(params);
    }

    public static NextClient post(String url) {
        return create(url, Method.POST);
    }

    public static NextClient post(String url, Map<String, String> params) {
        return post(url).addParams(params);
    }

    public static NextClient put(String url) {
        return create(url, Method.PUT);
    }

    public static NextClient put(String url, Map<String, String> params) {
        return put(url).addParams(params);
    }

    public static NextClient create(String url, Method method) {
        return new NextClient(url, method);
    }


    /**
     * Creates a new Http Request
     *
     * @param method Http method (GET, POST, etc)
     * @param url    url with optional queryString parameters.
     */
    public NextClient(String url, Method method) {
        this.url = url;
        this.method = method;
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
        this.httpParams = new NextParams();

    }

    private static SSLSocketFactory getTrustedFactory()
            throws IOException {
        if (sTrustedSocketFactory == null) {
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
                sTrustedSocketFactory = context.getSocketFactory();
            } catch (GeneralSecurityException e) {
                IOException ioException = new IOException(
                        "Security exception configuring SSL context");
                ioException.initCause(e);
                throw ioException;
            }
        }

        return sTrustedSocketFactory;
    }

    private static HostnameVerifier getTrustedVerifier() {
        if (sTrustedHostnameVerifier == null)
            sTrustedHostnameVerifier = new HostnameVerifier() {

                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

        return sTrustedHostnameVerifier;
    }

    public static void setConnectionFactory(final ConnectionFactory cf) {
        if (cf == null) {
            sConnectionFactory = ConnectionFactory.DEFAULT;
        } else {
            sConnectionFactory = cf;
        }
    }

    private HttpURLConnection createConnection() throws IOException {
        String completeUrl = getCompleteUrl();
        URL url = new URL(completeUrl);
        if (proxy == null || Proxy.NO_PROXY.equals(proxy)) {
            connection = sConnectionFactory.create(url);
        } else {
            connection = sConnectionFactory.create(url, proxy);
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
     * Execute the request and return a {@link com.mcxiaoke.next.http.NextResponse}
     *
     * @return Http Response
     * @throws RuntimeException if the connection cannot be created.
     */
    public NextResponse getResponse() throws IOException {
        if (response == null) {
            response = execute();
        }
        return response;
    }

    public int getCode() throws IOException {
        return getResponse().getCode();
    }

    public String getMessage() throws IOException {
        return getResponse().getMessage();
    }

    public InputStream asStream() throws IOException {
        return getResponse().getAsStream();
    }

    public byte[] asBytes() throws IOException {
        return getResponse().getAsBytes();
    }

    public String asString() throws IOException {
        return getResponse().getAsAsString();
    }

    public File asBinaryFile(File file) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            IOUtils.copy(asStream(), fos);
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return file;
    }

    public File asTextFile(File file, String encoding) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            IOUtils.copy(asStream(), writer, encoding);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return file;
    }

    public Bitmap asBitmap() throws IOException {
        return BitmapFactory.decodeStream(asStream());
    }

    public JSONObject asJSONObject() throws IOException, JSONException {
        return new JSONObject(asString());
    }

    public <T> T as(NextResponseHandler<T> handler) throws IOException {
        return handler.process(getResponse());
    }

    private NextResponse execute() throws IOException {
        intercept();
        CookieHandler.setDefault(cookieManager);
        HttpURLConnection conn = getConnection();
        checkConfig(conn);
        checkHttps(conn);
        checkHeaders(conn);
        if (sDebug) {
            Log.v(TAG, "[Request] " + toString());
        }
        checkWriteBody(conn);
        conn.connect();
        return handleResponse(conn);
    }

    private void checkConfig(HttpURLConnection conn) {
        conn.setUseCaches(useCaches);
        conn.setInstanceFollowRedirects(followRedirects);
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
    }

    private void checkHttps(HttpURLConnection conn) throws IOException {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            if (trustAllCerts) {
                httpsConn.setSSLSocketFactory(getTrustedFactory());
            }
            if (trustAllHosts) {
                httpsConn.setHostnameVerifier(getTrustedVerifier());
            }
        }
    }

    private void checkHeaders(HttpURLConnection conn) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private NextResponse handleResponse(HttpURLConnection conn) throws IOException {
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

        NextResponse response = NextResponse.create(code, message);
        response.setContentLength(contentLength).setContentType(contentType);
        response.setHeaders(rawHeaders).setStream(stream);

        if (sDebug) {
            Log.v(TAG, "[Response] " + response);
        }

        return response;
    }

    private void checkWriteBody(HttpURLConnection conn) throws IOException {
        if (Method.POST.equals(this.method) || Method.PUT.equals(this.method)) {
            writeBody(conn);
        }
    }

    private void writeBody(HttpURLConnection conn) throws IOException {
        if (httpEntity == null) {
            httpEntity = httpParams.getHttpEntity();
        }

        if (httpEntity != null) {
            String contentType = httpEntity.getContentType().getValue();
            long contentLength = httpEntity.getContentLength();
            if (contentType != null) {
                conn.setRequestProperty(CONTENT_TYPE, contentType);
            }
            if (contentLength > 0) {
                conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(contentLength));
                conn.setFixedLengthStreamingMode((int) contentLength);
            } else {
                conn.setChunkedStreamingMode(0);
            }

            conn.setDoOutput(true);
            final ProgressCallback proxyCallback = new ProgressCallback() {
                @Override
                public void onProgress(long currentSize, long totalSize) {
                    Log.e(TAG, "Progress: " + currentSize + ", Total: " + totalSize);
                    progressCallback.onProgress(currentSize, totalSize);
                }
            };
            OutputStream outputStream = null;
            try {
                outputStream = new ProgressOutputStream(conn.getOutputStream(), proxyCallback, contentLength);
                httpEntity.writeTo(outputStream);
                outputStream.flush();
            } finally {
                IOUtils.closeQuietly(outputStream);
            }
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
        return httpParams.appendQueryString(url);
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

    public NextClient addParam(String key, String value) {
        this.httpParams.put(key, value);
        return this;
    }

    public NextClient addParams(Map<String, String> map) {
        this.httpParams.put(map);
        return this;
    }

    public NextClient addBody(String key, File file, String mimeType) {
        this.httpParams.put(key, file, mimeType);
        return this;
    }

    public NextClient addBody(String key, File file, String mimeType, String fileName) {
        this.httpParams.put(key, file, mimeType, fileName);
        return this;
    }

    public NextClient addBody(String key, byte[] bytes, String mimeType) {
        this.httpParams.put(key, bytes, mimeType);
        return this;
    }

    public NextClient addBody(String key, byte[] bytes, String mimeType, String fileName) {
        this.httpParams.put(key, bytes, mimeType, fileName);
        return this;
    }

    public NextClient addBody(String key, InputStream stream, String mimeType) {
        this.httpParams.put(key, stream, mimeType);
        return this;
    }

    public NextClient addBody(String key, InputStream stream, String mimeType, String fileName) {
        this.httpParams.put(key, stream, mimeType, fileName);
        return this;
    }

    /**
     * Obtains the body parameters.
     *
     * @return containing the body parameters.
     */
    public List<NameValuePair> getHttpParams() {
        return this.httpParams.getParams();
    }

    public Map<String, String> getHeaders() {
        return this.headers;
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

    /**
     * Set the charset of the body of the request
     *
     * @param charsetName name of the charset of the request
     */
    public NextClient setCharset(String charsetName) {
        this.charset = charsetName;
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

    public void setProgressCallback(final ProgressCallback callback) {
        if (callback == null) {
            progressCallback = ProgressCallback.DEFAULT;
        } else {
            progressCallback = callback;
        }
    }

    public NextClient setHttpEntity(HttpEntity entity) {
        httpEntity = entity;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpRequest{");
        sb.append("url='").append(url).append('\'');
        sb.append(", method=").append(method);
        sb.append(", httpParams=").append(httpParams);
        sb.append(", headers=").append(StringUtils.toString(headers));
        sb.append(", charset='").append(charset).append('\'');
        sb.append(", proxy=").append(proxy);
        sb.append('}');
        return sb.toString();
    }


    /*******************************************************************
     *******************************************************************
     *******************************************************************
     *
     * Interfaces,  Inter Classes
     */


    /**
     * HTTP Method
     */
    public enum Method {
        GET, POST, PUT, DELETE, HEAD
    }

    /**
     * Creates {@link java.net.HttpURLConnection HTTP connections} for
     * {@link java.net.URL urls}.
     */
    public interface ConnectionFactory {
        /**
         * Open an {@link java.net.HttpURLConnection} for the specified {@link java.net.URL}.
         *
         * @throws java.io.IOException
         */
        HttpURLConnection create(URL url) throws IOException;

        /**
         * Open an {@link java.net.HttpURLConnection} for the specified {@link java.net.URL}
         * and {@link java.net.Proxy}.
         *
         * @throws java.io.IOException
         */
        HttpURLConnection create(URL url, Proxy proxy) throws IOException;

        /**
         * A {@link ConnectionFactory} which uses the built-in
         * {@link java.net.URL#openConnection()}
         */
        ConnectionFactory DEFAULT = new ConnectionFactory() {
            public HttpURLConnection create(URL url) throws IOException {
                return (HttpURLConnection) url.openConnection();
            }

            public HttpURLConnection create(URL url, Proxy proxy) throws IOException {
                return (HttpURLConnection) url.openConnection(proxy);
            }
        };
    }

    /**
     * POST/PUT write data progress callback
     */
    public interface ProgressCallback {
        void onProgress(long currentSize, long totalSize);

        ProgressCallback DEFAULT = new ProgressCallback() {
            @Override
            public void onProgress(long currentSize, long totalSize) {

            }
        };
    }

    /**
     * Progress OutputStream, internal use only.
     */
    static class ProgressOutputStream extends BufferedOutputStream {
        private ProgressCallback callback;
        private long totalSize;
        private long currentSize;

        public ProgressOutputStream(OutputStream out, ProgressCallback callback, long totalSize) {
            super(out);
            this.callback = callback;
            this.totalSize = totalSize;
        }

        @Override
        public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
            super.write(buffer, offset, length);
            currentSize += length;
            callback.onProgress(currentSize, totalSize);
        }

    }

}
