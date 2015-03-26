package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public interface RequestInterceptor {
    void intercept(final NextRequest request);
}