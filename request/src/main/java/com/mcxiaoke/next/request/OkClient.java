package com.mcxiaoke.next.request;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 2016/12/8
 * Time: 15:27
 */

public class OkClient {
    private OkHttpClient client;

    Response execute(final OkRequest request) throws IOException {
        return client.newCall(request.toOkRequest()).execute();
    }

    void enqueue(final OkRequest request) {

    }

    <T> void execute(final RequestJob<T> job) {

    }

    <T> void enqueue(final RequestJob<T> job) {

    }
}
