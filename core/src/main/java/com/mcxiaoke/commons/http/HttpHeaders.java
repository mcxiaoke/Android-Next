package com.mcxiaoke.commons.http;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

class HttpHeaders {
    private Date servedDate;
    private Date lastModified;
    private Date expires;
    private String cacheControl;
    private boolean noCache;
    private int maxAgeSeconds = -1;
    private int sMaxAgeSeconds = -1;
    private String etag;
    private int ageSeconds = -1;
    private String contentEncoding;
    private String transferEncoding;
    private int contentLength = -1;
    private String contentType;
    private List<String> rawCookies;
    private final Map<String, List<String>> rawHeaders;

    public HttpHeaders(Map<String, List<String>> rawHeaders) {
        this.rawHeaders = rawHeaders;

        for (String key : rawHeaders.keySet()) {
            String value = rawHeaders.get(key).get(0);
            if (CACHE_CONTROL.equalsIgnoreCase(key)) {
                cacheControl = value;
            } else if (DATE.equalsIgnoreCase(key)) {
                servedDate = DateParser.parse(value);
            } else if (EXPIRES.equalsIgnoreCase(key)) {
                expires = DateParser.parse(value);
            } else if (LAST_MODIFIED.equalsIgnoreCase(key)) {
                lastModified = DateParser.parse(value);
            } else if (ETAG.equalsIgnoreCase(key)) {
                etag = value;
            } else if (PRAGMA.equalsIgnoreCase(key)) {
                if ("no-cache".equalsIgnoreCase(value)) {
                    noCache = true;
                }
            } else if (SET_COOKIE.equals(key)) {
                rawCookies = rawHeaders.get(key);
            } else if ("Age".equalsIgnoreCase(key)) {
                ageSeconds = parseSeconds(value);
            } else if (CONTENT_TYPE.equalsIgnoreCase(key)) {
                contentType = value;
            } else if (CONTENT_ENCODING.equalsIgnoreCase(key)) {
                contentEncoding = value;
            } else if (TRANSFER_ENCODING.equalsIgnoreCase(key)) {
                transferEncoding = value;
            } else if (CONTENT_LENGTH.equalsIgnoreCase(key)) {
                contentLength = parseInt(value);
            }
        }
    }

    private static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static int parseSeconds(String value) {
        try {
            long seconds = Long.parseLong(value);
            if (seconds > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else if (seconds < 0) {
                return 0;
            } else {
                return (int) seconds;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public Date getServedDate() {
        return servedDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getExpires() {
        return expires;
    }

    public String getCacheControl() {
        return cacheControl;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public int getMaxAgeSeconds() {
        return maxAgeSeconds;
    }

    public int getsMaxAgeSeconds() {
        return sMaxAgeSeconds;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public int getAgeSeconds() {
        return ageSeconds;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getTransferEncoding() {
        return transferEncoding;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public List<String> getRawCookies() {
        return rawCookies;
    }

    public int size() {
        return rawHeaders.size();
    }

    public String getHeader(String filedName) {
        List<String> headers = rawHeaders.get(filedName);
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        return rawHeaders.get(filedName).get(0);
    }

    public int getHeaderInt(String filedName, int defaultValue) {
        List<String> headers = rawHeaders.get(filedName);
        if (headers == null || headers.isEmpty()) {
            return defaultValue;
        }
        String value = rawHeaders.get(filedName).get(0);
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }


    static class DateParser {

        /**
         * Most websites serve cookies in the blessed format. Eagerly create the
         * parser to ensure such cookies are on the fast path.
         */
        private static final ThreadLocal<DateFormat> STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                DateFormat rfc1123 = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                rfc1123.setTimeZone(TimeZone.getTimeZone("UTC"));
                return rfc1123;
            }
        };

        /**
         * If we fail to parse a date in a non-standard format, try each of these
         * formats in sequence.
         */
        private static final String[] BROWSER_COMPATIBLE_DATE_FORMATS = new String[]{
            /*
             * This list comes from {@code
			 * org.apache.http.impl.cookie.BrowserCompatSpec}.
			 */
                "EEEE, dd-MMM-yy HH:mm:ss zzz", // RFC 1036
                "EEE MMM d HH:mm:ss yyyy", // ANSI C asctime()
                "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z",
                "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z",
                "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z",
                "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z",
                "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z",
                "EEE, dd-MM-yyyy HH:mm:ss z",

			/*
             * RI bug 6641315 claims a cookie of this format was once served by
			 * www.yahoo.com
			 */
                "EEE MMM d yyyy HH:mm:ss z",};

        /**
         * Returns the date for {@code value}. Returns null if the value couldn't be
         * parsed.
         */
        public static Date parse(String value) {
            try {
                return STANDARD_DATE_FORMAT.get().parse(value);
            } catch (ParseException ignore) {
            }
            for (String formatString : BROWSER_COMPATIBLE_DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(formatString, Locale.US)
                            .parse(value);
                } catch (ParseException ignore) {
                }
            }
            return null;
        }

        /**
         * Returns the string for {@code value}.
         */
        public static String format(Date value) {
            return STANDARD_DATE_FORMAT.get().format(value);
        }
    }

    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String DATE = "Date";
    public static final String ETAG = "ETag";
    public static final String EXPIRES = "Expires";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LOCATION = "Location";
    public static final String PRAGMA = "Pragma";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String USER_AGENT = "User-Agent";
    public static final String VARY = "Vary";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
}
