package com.mcxiaoke.next.http.util;

import com.mcxiaoke.next.http.NextRequest;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 16:39
 */
public class Usage {

    public static void test(){
        final String url="http://www.douban.com";
        NextRequest.Builder builder=NextRequest.newBuilder();
        builder.get(url);
    }
}
