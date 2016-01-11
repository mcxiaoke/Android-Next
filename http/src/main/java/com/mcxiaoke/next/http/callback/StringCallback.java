package com.mcxiaoke.next.http.callback;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:51
 */
public abstract class StringCallback implements HttpCallback<String> {

    @Override
    public void onSuccess(final String response) {

    }

    @Override
    public void onError(final Throwable error) {

    }
}
