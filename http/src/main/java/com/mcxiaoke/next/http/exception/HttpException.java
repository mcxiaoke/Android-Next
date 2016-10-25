package com.mcxiaoke.next.http.exception;

import com.mcxiaoke.next.http.NextResponse;

/**
 * User: mcxiaoke
 * Date: 16/1/12
 * Time: 11:20
 */
public class HttpException extends Exception {

    public final NextResponse response;

    public HttpException(final NextResponse response) {
        super(response.message());
        this.response = response;
    }

    @Override
    public String toString() {
        return super.toString() + " {" + response + '}';
    }
}
