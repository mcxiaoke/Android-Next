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

    public final int code;
    public final String message;
    public final NextResponse response;

    public HttpException(final NextResponse response) {
        this(response.description(), response);
    }

    public HttpException(final String message, final NextResponse response) {
        super(message);
        this.response = response;
        this.code = response.code();
        this.message = response.message();
    }

    public HttpException(final int code, final Throwable ex) {
        super(ex.getMessage(), ex);
        this.code = code;
        this.message = ex.getMessage();
        this.response = null;
    }

    @Override
    public String toString() {
        return super.toString() + " {" + response + '}';
    }
}
