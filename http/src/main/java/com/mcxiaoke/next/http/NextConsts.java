package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:56
 */
interface NextConsts {

    public static final String ENCODING_UTF8 = Charsets.ENCODING_UTF_8;

    public static final int BUFFER_SIZE = 8196;

    public static final int CONNECT_TIMEOUT = 20 * 1000;
    public static final int READ_TIMEOUT = 20 * 1000;

    public static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String EMPTY_STRING = "";

    public static final char QUERY_STRING_SEPARATOR = '?';
    public static final String PARAM_SEPARATOR = "&";
    public static final String PAIR_SEPARATOR = "=";

    /**
     * HEADERS
     */
    public static final String REFERER = "Referer";
    public static final String ENCODING_GZIP = "gzip";
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
