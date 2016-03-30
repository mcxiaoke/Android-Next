package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import okhttp3.MediaType;

import java.nio.charset.Charset;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:56
 */
public interface HttpConsts {

    int SLOW_REQUEST_THRESHOLD_MS = 3000;

    String ENCODING_UTF8 = Charsets.ENCODING_UTF_8;
    Charset CHARSET_UTF8 = Charsets.UTF_8;

    int BUFFER_SIZE = 10 * 1024;

    int CONNECT_TIMEOUT = 10 * 1000;
    int READ_TIMEOUT = 10 * 1000;
    int WRITE_TIMEOUT = 10 * 1000;

    String APPLICATION_OCTET_STREAM = "application/octet-stream";
    MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream; charset=utf-8");
    String CONTENT_TYPE_FORM_ENCODED = "application/x-www-form-urlencoded";
    String EMPTY_STRING = "";
    String DEFAULT_NAME = "nofilename";

    byte[] NO_BODY = new byte[0];

    char QUERY_STRING_SEPARATOR = '?';
    String PARAM_SEPARATOR = "&";
    String PAIR_SEPARATOR = "=";

    String REFERER = "Referer";
    String ENCODING_GZIP = "gzip";
    String ACCEPT = "Accept";
    String ACCEPT_CHARSET = "Accept-Charset";
    String ACCEPT_ENCODING = "Accept-Encoding";
    String ACCEPT_LANGUAGE = "Accept-Language";
    String AUTHORIZATION = "Authorization";
    String CACHE_CONTROL = "Cache-Control";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_LANGUAGE = "Content-Language";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_LOCATION = "Content-Location";
    String CONTENT_TYPE = "Content-Type";
    String DATE = "Date";
    String ETAG = "ETag";
    String EXPIRES = "Expires";
    String HOST = "Host";
    String IF_MATCH = "If-Match";
    String IF_MODIFIED_SINCE = "If-Modified-Since";
    String IF_NONE_MATCH = "If-None-Match";
    String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    String LAST_MODIFIED = "Last-Modified";
    String LOCATION = "Location";
    String PRAGMA = "Pragma";
    String TRANSFER_ENCODING = "Transfer-Encoding";
    String USER_AGENT = "User-Agent";
    String VARY = "Vary";
    String WWW_AUTHENTICATE = "WWW-Authenticate";
    String COOKIE = "Cookie";
    String SET_COOKIE = "Set-Cookie";
}
