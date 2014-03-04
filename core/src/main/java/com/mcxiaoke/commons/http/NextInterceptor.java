package com.mcxiaoke.commons.http;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public interface NextInterceptor {
    void intercept(NextRequest request);
}