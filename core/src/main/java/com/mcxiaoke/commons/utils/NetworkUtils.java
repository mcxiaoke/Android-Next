package com.mcxiaoke.commons.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.params.HttpParams;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;

/**
 * 网络状态工具类
 *
 * @author mcxiaoke
 * @version 2.0 2014.02.12
 */
public final class NetworkUtils {
    public static final String TAG = NetworkUtils.class.getSimpleName();
    public static final String MOBILE_CTWAP = "ctwap";
    public static final String MOBILE_CMWAP = "cmwap";
    public static final String MOBILE_3GWAP = "3gwap";
    public static final String MOBILE_UNIWAP = "uniwap";

    private NetworkUtils() {
    }

    public static enum NetworkType {
        WIFI, MOBILE, OTHER, NONE
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
        int type = info.getType();
        if (ConnectivityManager.TYPE_WIFI == type) {
            return NetworkType.WIFI;
        } else if (ConnectivityManager.TYPE_MOBILE == type) {
            return NetworkType.MOBILE;
        } else {
            return NetworkType.OTHER;
        }
    }

    /**
     * 网络连接是否断开
     *
     * @param context Context
     * @return 是否断开s
     */
    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    /**
     * 是否有网络连接
     *
     * @param context Context
     * @return 是否连接
     */
    public static boolean isConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * 当前是否是WIFI网络
     *
     * @param context Context
     * @return 是否WIFI
     */
    public static boolean isWifi(Context context) {
        return NetworkType.WIFI.equals(getNetworkType(context));
    }

    /**
     * 当前是否移动网络
     *
     * @param context Context
     * @return 是否移动网络
     */
    public static boolean isMobile(Context context) {
        return NetworkType.MOBILE.equals(getNetworkType(context));
    }

    /**
     * 根据当前网络状态获取代理
     *
     * @param context Context
     * @return 代理
     */
    public static Proxy getProxyChina(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() != ConnectivityManager.TYPE_MOBILE
                || networkInfo.getExtraInfo() == null) {
            return null;
        }
        String typeName = networkInfo.getExtraInfo();
        if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
            InetSocketAddress address = new InetSocketAddress("10.0.0.200", 80);
            return new Proxy(Type.HTTP, address);
        } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
            InetSocketAddress address = new InetSocketAddress("10.0.0.172", 80);
            return new Proxy(Type.HTTP, address);
        } else {
            return null;
        }
    }

    /**
     * 获取系统代理
     *
     * @param context Context
     * @return 代理
     */
    public static Proxy getProxy(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            return null;
        }

        boolean isMobile = isMobile(context);
        if (isMobile) {
            String defaultProxyHost = android.net.Proxy.getDefaultHost();
            int defaultProxyPort = android.net.Proxy.getDefaultPort();
            if (defaultProxyHost != null && defaultProxyHost.length() > 0
                    && defaultProxyPort > 0) {
                SocketAddress address = new InetSocketAddress(defaultProxyHost, defaultProxyPort);
                return new Proxy(Type.HTTP, address);
            }
        }
        return null;

    }

    /**
     * 根据当前网络状态设置代理
     *
     * @param context    Context
     * @param httpParams HttpParams
     * @return 是否使用了代理
     */
    public static boolean setProxyChina(final Context context,
                                        final HttpParams httpParams) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() != ConnectivityManager.TYPE_MOBILE
                || networkInfo.getExtraInfo() == null) {
            return false;
        }

        boolean hasProxy = false;
        String typeName = networkInfo.getExtraInfo();
        if (MOBILE_CTWAP.equalsIgnoreCase(typeName)) {
            HttpHost proxy = new HttpHost("10.0.0.200", 80);
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            hasProxy = true;
        } else if (MOBILE_CMWAP.equalsIgnoreCase(typeName)
                || MOBILE_UNIWAP.equalsIgnoreCase(typeName)
                || MOBILE_3GWAP.equalsIgnoreCase(typeName)) {
            HttpHost proxy = new HttpHost("10.0.0.172", 80);
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            hasProxy = true;
        }
        return hasProxy;
    }

    /**
     * 根据系统代理设置代理
     *
     * @param context    Context
     * @param httpParams HttpParams
     * @return 是否使用了代理
     */
    public static boolean setProxy(final Context context, HttpParams httpParams) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null
                || networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            return false;
        }

        boolean isMobile = isMobile(context);
        boolean hasProxy = false;
        if (isMobile) {
            String defaultProxyHost = android.net.Proxy.getDefaultHost();
            int defaultProxyPort = android.net.Proxy.getDefaultPort();
            if (defaultProxyHost != null && defaultProxyHost.length() > 0
                    && defaultProxyPort > 0) {
                HttpHost proxy = new HttpHost(defaultProxyHost, defaultProxyPort);
                httpParams.setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
                hasProxy = true;
            }
        }
        return hasProxy;
    }

}
