package com.mcxiaoke.next.async;

import android.os.Bundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.mcxiaoke.next.async.callback.FileCallback;
import com.mcxiaoke.next.async.callback.GsonCallback;
import com.mcxiaoke.next.async.callback.HttpCallback;
import com.mcxiaoke.next.async.callback.ResponseCallback;
import com.mcxiaoke.next.async.callback.StringCallback;
import com.mcxiaoke.next.async.converter.GsonTransformer;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.transformer.FileTransformer;
import com.mcxiaoke.next.http.transformer.ResponseTransformer;
import com.mcxiaoke.next.http.transformer.StringTransformer;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.LogUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:42
 */
public class HttpQueue {
    static class SingletonHolder {
        public static final HttpQueue INSTANCE = new HttpQueue();
    }

    public static HttpQueue getDefault() {
        return SingletonHolder.INSTANCE;
    }

    private static final String TAG = HttpQueue.class.getSimpleName();

    private boolean mDebug;
    private Map<Integer, String> mRequests;
    private TaskQueue mQueue;
    private NextClient mClient;
    private Gson mGson;

    HttpQueue() {
        this(TaskQueue.concurrent(), new NextClient());
    }

    HttpQueue(final OkHttpClient client) {
        this(TaskQueue.concurrent(), new NextClient(client));
    }

    HttpQueue(final TaskQueue queue, final NextClient client) {
        mRequests = new ConcurrentHashMap<Integer, String>();
        mGson = new GsonBuilder().setPrettyPrinting().create();
        mQueue = queue;
        mClient = client;
    }

    public void setDebug(final boolean debug) {
        mDebug = debug;
        mClient.setDebug(debug);
    }

    public void setQueue(final TaskQueue queue) {
        mQueue = queue;
    }

    public void setClient(final NextClient client) {
        mClient = client;
    }

    public void setGson(final Gson gson) {
        mGson = gson;
    }

    public NextClient getClient() {
        return mClient;
    }

    public TaskQueue getQueue() {
        return mQueue;
    }

    public Gson getGson() {
        return mGson;
    }

    public String add(final NextRequest request, final File file,
                      final FileCallback callback,
                      Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(file, "file must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        ensureClient();
        final NextClient client = mClient;
        final TaskCallable<File> callable = new TaskCallable<File>() {
            @Override
            public File call() throws Exception {
                return client.execute(request, new FileTransformer(file));
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public String add(final NextRequest request,
                      final ResponseCallback callback,
                      Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        ensureClient();
        final NextClient client = mClient;
        final TaskCallable<NextResponse> callable = new TaskCallable<NextResponse>() {
            @Override
            public NextResponse call() throws Exception {
                return client.execute(request);
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public String add(final NextRequest request,
                      final StringCallback callback,
                      Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        ensureClient();
        final NextClient client = mClient;
        final TaskCallable<String> callable = new TaskCallable<String>() {
            @Override
            public String call() throws Exception {
                return client.execute(request, new StringTransformer());
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public <T> String add(final NextRequest request,
                          final GsonCallback<T> callback,
                          Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        ensureClient();
        final NextClient client = mClient;
        final Gson gson = mGson;
        final TaskCallable<T> callable = new TaskCallable<T>() {
            @Override
            public T call() throws Exception {
                final ResponseTransformer<T> converter = new GsonTransformer<>(gson, callback.type());
                return client.execute(request, converter);
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public void cancelAll(Object caller) {
        mQueue.cancelAll(caller);
    }

    public void cancel(String name) {
        if (name != null) {
            mQueue.cancel(name);
        }
    }

    public void close() {
        mQueue.cancelAll();
        mRequests.clear();
    }

    private void cancelByHashCode(final int hashCode) {
        final String name = mRequests.remove(hashCode);
        cancel(name);
    }

    private <T> String enqueue(final NextRequest request,
                               final TaskCallable<T> callable,
                               final HttpCallback<T> callback,
                               final Object caller) {
        if (mDebug) {
            LogUtils.v(TAG, "[Enqueue] " + request + " from " + caller);
        }
        ensureQueue();
        final int hashCode = System.identityHashCode(request);
        final TaskCallback<T> taskCallback = new TaskCallback<T>() {
            @Override
            public void onTaskStarted(final String name, final Bundle extras) {
                if (mDebug) {
                    LogUtils.v(TAG, "[Started] (" + name + ") " + request.url());
                }
            }

            @Override
            public void onTaskFinished(final String name, final Bundle extras) {
                if (mDebug) {
                    LogUtils.v(TAG, "[Finished] (" + name + ") " + request.url());
                }
            }

            @Override
            public void onTaskCancelled(final String name, final Bundle extras) {
                if (mDebug) {
                    LogUtils.v(TAG, "[Cancelled] (" + name + ") " + request.url());
                }
            }

            @Override
            public void onTaskSuccess(final T t, final Bundle extras) {
                if (mDebug) {
                    LogUtils.v(TAG, "[Success] (" + t.getClass() + ") " + request.url());
                }
                if (callback != null) {
                    callback.onSuccess(t);
                }
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                if (mDebug) {
                    LogUtils.v(TAG, "[Failure] (" + ex + ") " + request.url());
                }
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        };
        final String tag = mQueue.add(callable, taskCallback, caller);
        mRequests.put(hashCode, tag);
        return tag;
    }

    private synchronized void ensureClient() {
        if (mClient == null) {
            mClient = new NextClient();
        }
    }

    private synchronized void ensureQueue() {
        if (mQueue == null) {
            mQueue = TaskQueue.concurrent();
        }
    }


    public static String prettyPrintJson(final String rawJson) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(new JsonParser().parse(rawJson));
    }
}
