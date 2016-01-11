package com.mcxiaoke.next.async;

import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.task.TaskQueue;
import com.squareup.okhttp.OkHttpClient;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 11:42
 */
public final class HttpAsync {

    public static HttpQueue newHttpQueue() {
        return new HttpQueue();
    }

    public static HttpQueue newHttpQueue(final OkHttpClient client) {
        return new HttpQueue(client);
    }

    public static HttpQueue newHttpQueue(final TaskQueue queue, final NextClient client) {
        return new HttpQueue(queue, client);
    }

}
