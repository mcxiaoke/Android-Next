package com.mcxiaoke.next.http;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcxiaoke.next.http.callback.BitmapCallback;
import com.mcxiaoke.next.http.callback.FileCallback;
import com.mcxiaoke.next.http.callback.GsonCallback;
import com.mcxiaoke.next.http.callback.HttpCallback;
import com.mcxiaoke.next.http.callback.ResponseCallback;
import com.mcxiaoke.next.http.callback.StringCallback;
import com.mcxiaoke.next.http.exception.HttpException;
import com.mcxiaoke.next.http.job.HttpJob;
import com.mcxiaoke.next.http.processor.HttpProcessor;
import com.mcxiaoke.next.http.transformer.BitmapTransformer;
import com.mcxiaoke.next.http.transformer.FileTransformer;
import com.mcxiaoke.next.http.transformer.GsonTransformer;
import com.mcxiaoke.next.http.transformer.HttpTransformer;
import com.mcxiaoke.next.http.transformer.ResponseTransformer;
import com.mcxiaoke.next.http.transformer.StringTransformer;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.LogUtils;
import okhttp3.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
    private static final int NUM_THREADS_DEFAULT = 0;

    private TaskQueue mQueue;
    private NextClient mClient;
    private Gson mGson;
    private boolean mDebug;

    public HttpQueue() {
        this(createQueue(), new NextClient());
    }

    public HttpQueue(final OkHttpClient client) {
        this(createQueue(), new NextClient(client));
    }

    public HttpQueue(final NextClient client) {
        this(createQueue(), client);
    }

    public HttpQueue(final TaskQueue queue) {
        this(queue, new NextClient());
    }

    public HttpQueue(final TaskQueue queue, final NextClient client) {
        mGson = new GsonBuilder().setPrettyPrinting().create();
        mQueue = queue;
        mClient = client;
    }

    public void setDebug(final boolean debug) {
        mDebug = debug;
        mClient.setDebug(debug);
        TaskQueue.setDebug(true);
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

    public void cancelAll(Object caller) {
        mQueue.cancelAll(caller);
    }

    public void cancel(String name) {
        if (name != null) {
            mQueue.cancel(name);
        }
    }

    public void cancelAll() {
        mQueue.cancelAll();
    }

    public <T> String add(final HttpJob<T> job) {
        return enqueue(job);
    }

    public <T> String add(final NextRequest request,
                          final HttpTransformer<T> transformer,
                          final HttpCallback<T> callback,
                          final HttpProcessor<T> processor,
                          final Object caller) {
        final HttpJob<T> job = new HttpJob<T>(request, transformer, callback, caller);
        job.addProcessor(processor);
        return enqueue(job);
    }

    public <T> String add(final NextRequest request,
                          final HttpTransformer<T> transformer,
                          final HttpCallback<T> callback,
                          final Object caller) {
        final HttpJob<T> job = new HttpJob<T>(request, transformer, callback, caller);
        return enqueue(job);
    }

    public String add(final NextRequest request,
                      final ResponseCallback callback,
                      final Object caller) {
        return add(request, new ResponseTransformer(), callback, caller);
    }

    public <T> String add(final NextRequest request,
                          final GsonCallback<T> callback,
                          Object caller) {
        final Gson gson = callback.gson == null ? mGson : callback.gson;
        final GsonTransformer<T> transformer;
        transformer = new GsonTransformer<T>(gson, callback.type);
        return add(request, transformer, callback, caller);
    }

    public String add(final NextRequest request,
                      final StringCallback callback,
                      final Object caller) {
        return add(request, new StringTransformer(), callback, caller);
    }

    public String add(final NextRequest request,
                      final BitmapCallback callback,
                      final Object caller) {
        return add(request, new BitmapTransformer(), callback, caller);
    }

    public String add(final NextRequest request, final File file,
                      final FileCallback callback,
                      final Object caller) {
        return add(request, new FileTransformer(file), callback, caller);
    }


    private <T> String enqueue(final HttpJob<T> job) {
        if (mDebug) {
            LogUtils.v(TAG, "[HttpJob][Enqueue]" + job.request.url() + " " + Thread.currentThread());
        }
        final TaskCallable<Pair<NextResponse, T>> callable =
                new TaskCallable<Pair<NextResponse, T>>() {
                    @Override
                    public Pair<NextResponse, T> call() throws Exception {
                        return performRequest(job);
                    }
                };
        return mQueue.add(callable, createCallback(job.callback), job.caller);
    }

    private <T> Pair<NextResponse, T> performRequest(final HttpJob<T> job)
            throws IOException, HttpException {
        long start = SystemClock.elapsedRealtime();
        final NextResponse nextResponse = mClient.execute(job.request);
        if (!nextResponse.successful()) {
            // not successful, throw http exception
            throw new HttpException(nextResponse);
        }
        // final T response = mClient.execute(job.request,job.transformer);
        final T response = job.transformer.transform(nextResponse);
        invokeProcessors(response, job.getProcessors());
        if (mDebug) {
            LogUtils.v(TAG, "[HttpJob][Perform] in "
                    + (SystemClock.elapsedRealtime() - start) + "ms " + nextResponse);
        }
        return new Pair<NextResponse, T>(nextResponse, response);
    }

    private <T> void invokeProcessors(final T data,
                                      final Collection<HttpProcessor<T>> ps) {
        if (ps != null) {
            for (HttpProcessor<T> p : ps) {
                p.process(data);
            }
        }
    }

    private <T> TaskCallback<Pair<NextResponse, T>> createCallback(
            final HttpCallback<T> callback) {
        return new SimpleTaskCallback<Pair<NextResponse, T>>() {

            @Override
            public void onTaskSuccess(final Pair<NextResponse, T> result,
                                      final Bundle extras) {
                if (mDebug) {
                    LogUtils.d(TAG, "[HttpJob][Success] " + result.first);
                }
                if (callback != null) {
                    callback.handleResponse(result.second);
                }
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                if (mDebug) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[HttpJob][Failure] Error:").append(ex);
                    if (ex.getCause() != null) {
                        builder.append(" Reason:").append(ex.getCause());
                    }
                    LogUtils.w(TAG, builder.toString());
                }
                if (callback != null) {
                    callback.handleException(ex);
                }
            }
        };
    }

    private static TaskQueue createQueue() {
        return TaskQueue.concurrent(NUM_THREADS_DEFAULT);
    }

}
