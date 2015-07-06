package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import com.squareup.okhttp.MediaType;

import java.nio.charset.Charset;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:56
 */
public interface HttpConsts {

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


    /**
     * HEADERS
     */
    String HOST = "Host";
    String REFERER = "Referer";
    String ENCODING_GZIP = "gzip";
    String ACCEPT = "Accept";
    String ACCEPT_CHARSET = "Accept-Charset";
    String ACCEPT_ENCODING = "Accept-Encoding";
    String AUTHORIZATION = "Authorization";
    String CONTENT_ENCODING = "Content-Encoding";
    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_TYPE = "Content-Type";
    String LOCATION = "Location";
    String TRANSFER_ENCODING = "Transfer-Encoding";
    String USER_AGENT = "User-Agent";
}
