package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public interface NextInterceptor {
    void preIntercept(final NextRequest request);

    void postIntercept(final NextResponse response);
}