package com.mcxiaoke.next.http;

import android.os.Bundle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcxiaoke.next.http.callback.BitmapCallback;
import com.mcxiaoke.next.http.callback.FileCallback;
import com.mcxiaoke.next.http.callback.HttpCallback;
import com.mcxiaoke.next.http.callback.JsonCallback;
import com.mcxiaoke.next.http.callback.ResponseCallback;
import com.mcxiaoke.next.http.callback.StringCallback;
import com.mcxiaoke.next.http.job.HttpJob;
import com.mcxiaoke.next.http.processor.HttpProcessor;
import com.mcxiaoke.next.http.transformer.BitmapTransformer;
import com.mcxiaoke.next.http.transformer.FileTransformer;
import com.mcxiaoke.next.http.transformer.HttpTransformer;
import com.mcxiaoke.next.http.transformer.JsonTransformer;
import com.mcxiaoke.next.http.transformer.ResponseTransformer;
import com.mcxiaoke.next.http.transformer.StringTransformer;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.LogUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private final Map<Integer, String> mJobs;
    private final List<String> mRequests;
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
        mJobs = new ConcurrentHashMap<Integer, String>();
        mRequests = new CopyOnWriteArrayList<String>();
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
        synchronized (mJobs) {
            mRequests.clear();
            mJobs.clear();
        }
    }

    public List<String> getRequests() {
        return new ArrayList<>(mRequests);
    }

    public <T> String add(final HttpJob<T> job) {
        return enqueue(job);
    }

    public <T> String add(final NextRequest request,
                          final HttpTransformer<T> transformer,
                          final HttpCallback<T> callback,
                          final Object caller,
                          final HttpProcessor<NextRequest> requestProcessor,
                          final HttpProcessor<NextResponse> preProcessor,
                          final HttpProcessor<T> postProcessor) {
        final HttpJob<T> job = new HttpJob<T>(request, transformer, callback, caller);
        job.addRequestProcessor(requestProcessor)
                .addPreProcessor(preProcessor)
                .addPostProcessor(postProcessor);
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
                          final JsonCallback<T> callback,
                          Object caller) {
        return add(request, new JsonTransformer<T>(mGson, callback.getType()), callback, caller);
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
        final NextRequest request = job.request;
        final HttpTransformer<T> transformer = job.transformer;
        final HttpCallback<T> callback = job.callback;
        final Object caller = job.caller;
        final String url = String.valueOf(request.url());
        if (mDebug) {
            logHttpJob(TAG, "[Enqueue] " + url + " from " + caller);
        }
        ensureClient();
        ensureQueue();
        final int hashCode = System.identityHashCode(request);
        final TaskCallable<T> callable = new TaskCallable<T>() {
            @Override
            public T call() throws Exception {
                // request interceptors
//                NextRequest aReq = request;
                final List<HttpProcessor<NextRequest>> requestProcessors = job.getRequestProcessors();
                if (requestProcessors != null) {
                    for (HttpProcessor<NextRequest> pr : requestProcessors) {
                        pr.process(request);
//                        pr.process(aReq);
//                        aReq = pr.process(aReq);
//                        if (pr.process(aReq)) {
//                            break;
//                        }
                    }
                }
                // response interceptors
                final NextResponse nextResponse = mClient.execute(request);
                final List<HttpProcessor<NextResponse>> preProcessors = job.getPreProcessors();
                if (preProcessors != null) {
                    for (HttpProcessor<NextResponse> pr : preProcessors) {
                        pr.process(nextResponse);
//                        pr.process(aRes);
//                        aRes = pr.process(aRes);
//                        if (pr.process(aRes)) {
//                            break;
//                        }
                    }
                }
                //  model interceptors
                final T response = transformer.transform(nextResponse);
                final List<HttpProcessor<T>> postProcessors = job.getPostProcessors();
                if (postProcessors != null) {
                    for (HttpProcessor<T> pr : postProcessors) {
                        pr.process(response);
//                        pr.process(model);
//                        model = pr.process(model);
//                        if (pr.process(model)) {
//                            break;
//                        }
                    }
                }
                return response;
            }
        };
        final TaskCallback<T> taskCallback = new SimpleTaskCallback<T>() {

            @Override
            public void onTaskCancelled(final String name, final Bundle extras) {
                if (mDebug) {
                    logHttpJob(TAG, "[Cancelled] (" + name + ") " + url);
                }
            }

            @Override
            public void onTaskFinished(final String name, final Bundle extras) {
                super.onTaskFinished(name, extras);
                synchronized (mJobs) {
                    mJobs.remove(hashCode);
                    mRequests.remove(url);
                }
            }

            @Override
            public void onTaskSuccess(final T t, final Bundle extras) {
                if (mDebug) {
                    logHttpJob(TAG, "[Success] Type:" + t.getClass() + " " + url);
                }
                if (callback != null) {
                    callback.onSuccess(t);
                }
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                if (mDebug) {
                    logHttpJob(TAG, "[Failure] Error:" + ex + " " + url);
                }
                if (callback != null) {
                    callback.onError(ex);
                }
            }
        };
        final String tag = mQueue.add(callable, taskCallback, caller);
        synchronized (mJobs) {
            mJobs.put(hashCode, tag);
            mRequests.add(url);
        }
        return tag;
    }

    private synchronized void ensureClient() {
        if (mClient == null) {
            mClient = new NextClient();
        }
    }

    private synchronized void ensureQueue() {
        if (mQueue == null) {
            mQueue = createQueue();
        }
    }


    private static TaskQueue createQueue() {
        return TaskQueue.concurrent(NUM_THREADS_DEFAULT);
    }

    private void logHttpJob(final String tag, final String message) {
        LogUtils.v(tag, "[HttpJob]" + message + " thread:" + Thread.currentThread().getName());
    }

}
