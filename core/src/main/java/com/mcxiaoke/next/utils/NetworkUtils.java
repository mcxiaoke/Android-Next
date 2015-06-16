package com.mcxiaoke.next.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

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

    public static String getNetworkTypeName(Context context) {
        String result = "(No Network)";

        try {
            final ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return result;
            }

            final NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnectedOrConnecting()) {
                return result;
            }

            result = info.getTypeName();
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                result += info.getSubtypeName();
//                        + "(" + info.getExtraInfo() + ")";
            } else {
//                result += "(" + info.getExtraInfo() + ")";
            }
        } catch (Throwable ignored) {
        }

        return result;
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

    public static String getOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperator();
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

    public enum NetworkType {
        WIFI, MOBILE, OTHER, NONE
    }

}
