package com.mcxiaoke.commons.http;

/**
 * User: mcxiaoke
 * Date: 13-10-10
 * Time: 下午1:17
 */

import java.net.HttpURLConnection;

/**
 * PreProcessor for HttpURLConnection
 * 在HTTP请求发送前对HttpURLConnection和HttpRequest进行额外处理
 */
public interface RequestInterceptor {

    /**
     * Processes a request.
     * On the client side, this step is performed before the request is
     * sent to the server.
     *
     * @param connection the url connection to preprocess
     * @param request    the request to preprocess
     */
    void process(HttpURLConnection connection, HttpRequest request);

    public static final RequestInterceptor DEFAULT = new RequestInterceptor() {
        @Override
        public void process(HttpURLConnection connection, HttpRequest request) {

        }
    };

}
