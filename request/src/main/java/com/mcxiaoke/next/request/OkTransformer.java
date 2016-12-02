package com.mcxiaoke.next.request;

import okhttp3.Response;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 16/12/2
 * Time: 11:15
 */
public interface OkTransformer<T> {

    T transform(Response response) throws IOException;
}
