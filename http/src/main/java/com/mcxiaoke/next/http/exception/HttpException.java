package com.mcxiaoke.next.http.exception;

import com.mcxiaoke.next.http.NextResponse;

/**
 * User: mcxiaoke
 * Date: 16/1/12
 * Time: 11:20
 */
public class HttpException extends Exception {
    public static final int ERROR_IO = -1;
    public static final int ERROR_NETWORK = -2;
    public static final int ERROR_TRANSFORM = -11;
    public static final int ERROR_UNKNOWN = -999;

    public int httpCode;
    public String httpMessage;
    public NextResponse response;

    public HttpException(final NextResponse response) {
        this(response.description(), response);
    }

    public HttpException(final String message, final NextResponse response) {
        super(message);
        this.response = response;
        this.httpCode = response.code();
        this.httpMessage = response.message();
    }

    public HttpException(final int code, final Throwable ex) {
        super(ex.getMessage(), ex);
        this.httpCode = code;
        this.httpMessage = ex.getMessage();
    }
}
