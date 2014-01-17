package com.mcxiaoke.commons.utils;

import com.douban.ui.BuildConfig;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * User: mcxiaoke
 * Date: 13-6-5
 * Time: 下午6:17
 */
public final class DateUtils {
    public static final String TAG = DateUtils.class.getSimpleName();

    // API时间的默认时区，计算时间间隔时需要考虑
    public static final TimeZone TIME_ZONE_CHINA = TimeZone.getTimeZone("GMT+8");

    // API返回的时间格式，默认东八区
    private static final String DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";

    private static final String DATE_FORMAT_SHORT_STRING = "yyyy-MM-dd";
    private static final String DATE_FORMAT_DAY_DISPLAY_STRING = "yyyy-MM-dd";
    private static final String DATE_FORMAT_STATUS_STRING_A = "HH:mm";
    private static final String DATE_FORMAT_STATUS_STRING_B = "MM-dd HH:mm";
    private static final String DATE_FORMAT_STATUS_STRING_C = "yyyy-MM-dd HH:mm";

    public static final boolean DEBUG = BuildConfig.DEBUG;

    private DateUtils() {

    }

    // DateFormat wrapped in a ThreadLocal
    // API的时间格式，默认东八区
    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING, Locale.US);
            df.setTimeZone(TIME_ZONE_CHINA);
            return df;
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMAT_SHORT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_SHORT_STRING, Locale.US);
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMAT_DAY_DISPLAY = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_DAY_DISPLAY_STRING, Locale.US);
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMAT_STATUS_A = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_STATUS_STRING_A, Locale.US);
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMAT_STATUS_B = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_STATUS_STRING_B, Locale.US);
        }
    };

    private static final ThreadLocal<DateFormat> DATE_FORMAT_STATUS_C = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT_STATUS_STRING_C, Locale.US);
        }
    };


    /**
     * 以秒为单位计算时间间隔
     */
    private static final long MIN = 60;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = MONTH * 12;

    /**
     * 1分钟内——刚刚
     * 1分钟到59分钟——x分钟前
     * 1小时前到当天00:00——今天 xx:xx
     * 当天0点前到当年1月1日0点——8-29  xx:xx
     * 当年1月1日0点前——2012-12-31 xx:xx
     *
     * @param dateStr
     * @return
     */
    public static String getTimelineDateStr(String dateStr) {
        if (dateStr == null) {
            return "";
        }
        Date date = parseDate(dateStr);

        if (date == null) {
            return dateStr;
        }

        Calendar postCal = Calendar.getInstance();
        Calendar currentCal = Calendar.getInstance();
        postCal.setTime(date);

        long seconds = (System.currentTimeMillis() - date.getTime()) / 1000;

        if (DEBUG) {
            LogUtils.v(TAG, "=====================================");
            LogUtils.v(TAG, "timezone=" + TimeZone.getDefault().getDisplayName());
            LogUtils.v(TAG, "original=" + dateStr);
            LogUtils.v(TAG, "time=" + date);
            LogUtils.v(TAG, "now=" + new Date());
            LogUtils.v(TAG, "seconds=" + seconds);
            LogUtils.v(TAG, "post cal=" + postCal.getTime());
            LogUtils.v(TAG, "current cal=" + currentCal.getTime());
        }

        if (seconds < MIN) {
            return "刚刚";
        } else if (seconds < HOUR) {
            return seconds / MIN + "分钟前";
        } else {
            if (isSameDay(postCal, currentCal)) {
                return String.format("今天 %1$s", formatDate(DATE_FORMAT_STATUS_A.get(), date));
            } else if (isSameYear(postCal, currentCal)) {
                return formatDate(DATE_FORMAT_STATUS_B.get(), date);
            } else {
                return formatDate(DATE_FORMAT_STATUS_C.get(), date);
            }
        }
    }

    // format yyyy-MM-dd HH:mm:ss to HH:mm
    public static String getShortTimeStr(String dateStr) {
        if (dateStr == null) {
            return "";
        }
        Date date = parseDate(dateStr);
        return String.format(formatDate(DATE_FORMAT_STATUS_A.get(), date));
    }

    // format yyyy-MM-dd HH:mm:ss to yyyy-MM-dd
    public static String getShortDateStr(String dateText) {
        if (dateText == null) {
            return "";
        }
        Date date = parseDate(dateText);
        // null check, fix monkey npe
        if (date != null) {
            return formatShortDate(date);
        }
        return dateText;
    }

    /**
     * 返回指定时间与当前时区时间的间隔，单位为秒
     *
     * @param date 指定日期
     * @return 返回时间间隔，单位为秒
     */
    private static long interval(Date date) {
        return (Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
                .getTimeInMillis() - date.getTime()) / 1000;
    }

    /**
     * @param s 代表饭否日期和时间的字符串
     * @return 字符串解析为对应的Date对象
     */
    public static Date parseDate(String s) {
        final ParsePosition position = new ParsePosition(0);
        return DATE_FORMAT.get().parse(s, position);
    }

    public static String formatDate(DateFormat format, Date date) {
        return format.format(date);
    }

    public static String formatDate(long time) {
        Date date = new Date(time);
        return formatDate(date);
    }

    public static String formatDate(Date date) {
        return DATE_FORMAT.get().format(date);
    }

    public static String formatShortDate(Date date) {
        return DATE_FORMAT_SHORT.get().format(date);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }


}
