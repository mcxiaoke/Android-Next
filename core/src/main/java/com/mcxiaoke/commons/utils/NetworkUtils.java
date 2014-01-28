package com.mcxiaoke.commons.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

/**
 * @author mcxiaoke
 * @version 1.4 2013.03.16
 */
public final class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOBILE_CTWAP = "ctwap";
    private static final String MOBILE_CMWAP = "cmwap";
    private static final String MOBILE_3GWAP = "3gwap";
    private static final String MOBILE_UNIWAP = "uniwap";

    public static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    public static final int DEFAULT_READ_TIMEOUT = 15000;

    private NetworkUtils() {
    }

    public static enum NetworkType {
        WIFI, MOBILE, OTHER, NONE
    }

    public static enum ApnType {
        WIFI, NET, WAP, CTWAP, NONE
    }

    private static void debug(String message) {
        LogUtils.v(TAG, message);
    }

    /**
     * 获取当前网络类型
     *
     * @return 返回网络类型
     */
    public static NetworkType getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return NetworkType.NONE;
        }
        if (ConnectivityManager.TYPE_WIFI == info.getType()) {
            return NetworkType.WIFI;
        } else if (ConnectivityManager.TYPE_MOBILE == info.getType()) {
            return NetworkType.MOBILE;
        } else {
            return NetworkType.OTHER;
        }
    }

    /**
     * 获取当前介入点类型
     *
     * @return 返回接入点类型
     */
    public static ApnType getApnType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnectedOrConnecting()) {
            return ApnType.NONE;
        }
        if (ConnectivityManager.TYPE_WIFI == info.getType()) {
            return ApnType.WIFI;
        }
        ApnType type;
        String typeName = info.getExtraInfo();
        if (typeName != null) {
            if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
                type = ApnType.CTWAP;
            } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                    || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                    || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
                type = ApnType.WAP;
            } else {
                type = ApnType.NET;
            }
        } else {
            type = ApnType.NET;
        }
        return type;
    }

    /**
     * 根据当前网络状态获取代理
     *
     * @param context
     */
    public static Proxy getProxy(final Context context) {
        if (context == null) {
            return Proxy.NO_PROXY;
        }
        boolean needCheckProxy = true;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                || networkInfo.getExtraInfo() == null) {
            needCheckProxy = false;
        }
        if (needCheckProxy) {
            String typeName = networkInfo.getExtraInfo();
            if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
                InetSocketAddress address = new InetSocketAddress("10.0.0.200", 80);
                return new Proxy(Type.HTTP, address);
            } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                    || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                    || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
                InetSocketAddress address = new InetSocketAddress("10.0.0.172", 80);
                return new Proxy(Type.HTTP, address);
            }
        }
        return Proxy.NO_PROXY;
    }

    /**
     * 根据当前网络状态填充代理
     *
     * @param context
     * @param httpParams
     */
    public static void setProxy(final Context context,
                                final HttpParams httpParams) {
        if (context == null || httpParams == null) {
            return;
        }
        boolean needCheckProxy = true;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                || networkInfo.getExtraInfo() == null) {
            needCheckProxy = false;
        }
        if (needCheckProxy) {
            String typeName = networkInfo.getExtraInfo();
            if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
                HttpHost proxy = new HttpHost("10.0.0.200", 80);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                    || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                    || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
                HttpHost proxy = new HttpHost("10.0.0.172", 80);
                httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            }
        }

        // String defaultProxyHost = android.net.Proxy.getDefaultHost();
        // int defaultProxyPort = android.net.Proxy.getDefaultPort();
        // if (defaultProxyHost != null && defaultProxyHost.length() > 0
        // && defaultProxyPort > 0) {
        // HttpHost proxy = new HttpHost(defaultProxyHost, defaultProxyPort);
        // httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
        // }
    }

    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    public static boolean isConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isMobile(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * @param originalUrl 链接地址
     * @return URL的域名部分
     */
    public static String getHost(String originalUrl) throws MalformedURLException {
        if (originalUrl == null || originalUrl.length() == 0) {
            return null;
        }
        URL url = new URL(originalUrl);
        return url.getHost();
    }

    /**
     * @param originalUrl 链接地址
     * @return URL的相对链接地址(不包含域名)
     */
    public static String getUrlNoDomain(String originalUrl) throws MalformedURLException {
        if (originalUrl == null || originalUrl.length() == 0) {
            return null;
        }
        URL url = new URL(originalUrl);
        String path = url.getPath();
        String query = url.getQuery();
        return path + "?" + query;
    }

    /**
     * 根据URL获取Connection对象
     *
     * @param url 远程URL
     * @return 返回HttpURLConnection对象
     * @throws java.io.IOException
     */
    public static HttpURLConnection getHttpConnection(String url)
            throws IOException {
        debug("==getConnection() initialize url: " + url);
        URL downUrl = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) downUrl.openConnection();
        conn.setDoInput(true);
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
        conn.setInstanceFollowRedirects(true);
        return conn;
    }

    /**
     * 根据URL获取Connection对象，根据运营商自动设置代理
     *
     * @param context Context参数
     * @param url     远程URL
     * @return 返回HttpURLConnection对象
     * @throws java.io.IOException
     */
    public static HttpURLConnection getHttpConnection(Context context, String url)
            throws IOException {
        debug("==getConnection() initialize url: " + url);
        HttpURLConnection conn = null;
        URL downUrl;
        String domain = getHost(url);
        ApnType type = getApnType(context);
        debug("==getConnection() == " + type.name());
        boolean isCtwap = ApnType.CTWAP.equals(type);
        boolean isWap = ApnType.WAP.equals(type);
        if (isCtwap || isWap) {
            String proxy = isCtwap ? "http://10.0.0.200:80/" : "http://10.0.0.172:80/";
            url = proxy + getUrlNoDomain(url);
            downUrl = new URL(url);
            conn = (HttpURLConnection) downUrl.openConnection();
            conn.setRequestProperty("X-Online-Host", domain);
            conn.setRequestProperty("Host", domain);
        } else {
            downUrl = new URL(url);
            conn = (HttpURLConnection) downUrl.openConnection();
        }
        conn.setDoInput(true);
//        conn.setRequestProperty("User-Agent", "");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "*/*");
        conn.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
        conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
        conn.setInstanceFollowRedirects(true);
        return conn;
    }
}
