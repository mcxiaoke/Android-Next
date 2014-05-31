package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-5-31
 * Time: 18:14
 */
public class HttpException extends RuntimeException {

    public HttpException() {
        super();
    }

    public HttpException(final String detailMessage) {
        super(detailMessage);
    }

    public HttpException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
    }

    public HttpException(final Throwable throwable) {
        super(throwable);
    }
}
