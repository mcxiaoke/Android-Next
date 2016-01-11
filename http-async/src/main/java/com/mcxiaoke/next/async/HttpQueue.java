package com.mcxiaoke.next.async;

import com.google.gson.Gson;
import com.mcxiaoke.next.async.callback.FileCallback;
import com.mcxiaoke.next.async.callback.GsonCallback;
import com.mcxiaoke.next.async.callback.ResponseCallback;
import com.mcxiaoke.next.async.callback.StringCallback;
import com.mcxiaoke.next.async.converter.GsonConverter;
import com.mcxiaoke.next.http.HttpMethod;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextParams;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.converter.FileConverter;
import com.mcxiaoke.next.http.converter.ResponseConverter;
import com.mcxiaoke.next.http.converter.StringConverter;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.AssertUtils;

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

    private Map<Integer, String> mRequests;
    private TaskQueue mQueue;
    private NextClient mClient;
    private Gson mGson;

    public HttpQueue() {
        mRequests = new ConcurrentHashMap<Integer, String>();
        mQueue = TaskQueue.concurrent();
        mClient = new NextClient();
        mGson = new Gson();
    }

    private Map<String, String> emptyMap() {
        return null;
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

    public String head(final String url, final ResponseCallback callback,
                       Object caller) {
        return head(url, emptyMap(), callback, caller);
    }

    public String head(final String url, final Map<String, String> queries,
                       final ResponseCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.HEAD, url, queries, null, callback, caller);
    }

    public String head(final String url, final NextParams params,
                       final ResponseCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.HEAD, url, params, callback, caller);
    }

    public String get(final String url, final ResponseCallback callback,
                      Object caller) {
        return get(url, emptyMap(), callback, caller);
    }

    public String get(final String url, final Map<String, String> queries,
                      final ResponseCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.GET, url, queries, null, callback, caller);
    }

    public String get(final String url, final NextParams params,
                      final ResponseCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.GET, url, params, callback, caller);
    }

    public String get(final String url, final StringCallback callback,
                      Object caller) {
        return get(url, emptyMap(), callback, caller);
    }

    public String get(final String url, final Map<String, String> queries,
                      final StringCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.GET, url, queries, null, callback, caller);
    }

    public String get(final String url, final NextParams params,
                      final StringCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.GET, url, params, callback, caller);
    }

    public <T> String get(final String url,
                          final GsonCallback<T> callback,
                          Object caller) {
        return get(url, emptyMap(), callback, caller);
    }

    public <T> String get(final String url, final Map<String, String> queries,
                          final GsonCallback<T> callback,
                          Object caller) {
        return addRequest(HttpMethod.GET, url, queries, null, callback, caller);
    }

    public <T> String get(final String url, final NextParams params,
                          final GsonCallback<T> callback,
                          Object caller) {
        return addRequest(HttpMethod.GET, url, params, callback, caller);
    }

    public String delete(final String url, final ResponseCallback callback,
                         Object caller) {
        return delete(url, emptyMap(), callback, caller);
    }

    public String delete(final String url, final Map<String, String> queries,
                         final ResponseCallback callback,
                         Object caller) {
        return addRequest(HttpMethod.DELETE, url, queries, null, callback, caller);
    }

    public String delete(final String url, final NextParams params,
                         final ResponseCallback callback,
                         Object caller) {
        return addRequest(HttpMethod.DELETE, url, params, callback, caller);
    }

    public String delete(final String url, final StringCallback callback,
                         Object caller) {
        return delete(url, emptyMap(), callback, caller);
    }

    public String delete(final String url, final Map<String, String> queries,
                         final StringCallback callback,
                         Object caller) {
        return addRequest(HttpMethod.DELETE, url, queries, null, callback, caller);
    }

    public String delete(final String url, final NextParams params,
                         final StringCallback callback,
                         Object caller) {
        return addRequest(HttpMethod.DELETE, url, params, callback, caller);
    }


    public <T> String delete(final String url,
                             final GsonCallback<T> callback,
                             Object caller) {
        return delete(url, emptyMap(), callback, caller);
    }

    public <T> String delete(final String url,
                             final Map<String, String> queries,
                             final GsonCallback<T> callback,
                             Object caller) {
        return addRequest(HttpMethod.DELETE, url, queries, null, callback, caller);
    }

    public <T> String delete(final String url,
                             final NextParams params,
                             final GsonCallback<T> callback,
                             Object caller) {
        return addRequest(HttpMethod.DELETE, url, params, callback, caller);
    }

    public String post(final String url,
                       final ResponseCallback callback,
                       Object caller) {
        return post(url, emptyMap(), callback, caller);
    }

    public String post(final String url,
                       final Map<String, String> forms,
                       final ResponseCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.POST, url, null, forms, callback, caller);
    }

    public String post(final String url, final NextParams params,
                       final StringCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.POST, url, params, callback, caller);
    }

    public String post(final String url,
                       final StringCallback callback,
                       Object caller) {
        return post(url, emptyMap(), callback, caller);
    }

    public String post(final String url,
                       final Map<String, String> forms,
                       final StringCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.POST, url, null, forms, callback, caller);
    }

    public String post(final String url, final NextParams params,
                       final ResponseCallback callback,
                       Object caller) {
        return addRequest(HttpMethod.POST, url, params, callback, caller);
    }

    public <T> String post(final String url,
                           final GsonCallback<T> callback,
                           Object caller) {
        return post(url, emptyMap(), callback, caller);
    }

    public <T> String post(final String url, final Map<String, String> forms,
                           final GsonCallback<T> callback,
                           Object caller) {
        return addRequest(HttpMethod.POST, url, null, forms, callback, caller);
    }

    public <T> String post(final String url, final NextParams params,
                           final GsonCallback<T> callback,
                           Object caller) {
        return addRequest(HttpMethod.POST, url, params, callback, caller);
    }


    public String put(final String url, final ResponseCallback callback,
                      Object caller) {
        return put(url, emptyMap(), callback, caller);
    }

    public String put(final String url, final Map<String, String> forms,
                      final ResponseCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.PUT, url, null, forms, callback, caller);
    }

    public String put(final String url, final NextParams params,
                      final ResponseCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.PUT, url, params, callback, caller);
    }

    public String put(final String url, final StringCallback callback,
                      Object caller) {
        return put(url, emptyMap(), callback, caller);
    }

    public String put(final String url, final Map<String, String> forms,
                      final StringCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.PUT, url, null, forms, callback, caller);
    }

    public String put(final String url, final NextParams params,
                      final StringCallback callback,
                      Object caller) {
        return addRequest(HttpMethod.PUT, url, params, callback, caller);
    }

    public <T> String put(final String url, final GsonCallback<T> callback,
                          Object caller) {
        return put(url, emptyMap(), callback, caller);
    }

    public <T> String put(final String url, final Map<String, String> forms,
                          final GsonCallback<T> callback,
                          Object caller) {
        return addRequest(HttpMethod.PUT, url, null, forms, callback, caller);
    }

    public <T> String put(final String url, final NextParams params,
                          final GsonCallback<T> callback,
                          Object caller) {
        return addRequest(HttpMethod.PUT, url, params, callback, caller);
    }


    public String download(final String url, final File file, final FileCallback callback,
                           Object caller) {
        return download(url, file, emptyMap(), callback, caller);
    }

    public String download(final String url, final File file, final Map<String, String> queries,
                           final FileCallback callback,
                           Object caller) {
        final NextRequest request = new NextRequest(HttpMethod.GET, url).queries(queries);
        return addRequest(request, file, callback, caller);
    }

    public String download(final String url, final File file, final NextParams params,
                           final FileCallback callback,
                           Object caller) {
        final NextRequest request = new NextRequest(HttpMethod.GET, url, params);
        return addRequest(request, file, callback, caller);
    }

    public String addRequest(final NextRequest request, final File file,
                             final FileCallback callback,
                             Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(file, "file must not be null.");
        AssertUtils.notNull(callback, "callback must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        final NextClient client = mClient;
        final TaskCallable<File> callable = new TaskCallable<File>() {
            @Override
            public File call() throws Exception {
                return client.execute(request, new FileConverter(file));
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public String addRequest(final NextRequest request,
                             final ResponseCallback callback,
                             Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(callback, "callback must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        final TaskQueue queue = mQueue;
        final NextClient client = mClient;
        final TaskCallable<NextResponse> callable = new TaskCallable<NextResponse>() {
            @Override
            public NextResponse call() throws Exception {
                return client.execute(request);
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public String addRequest(final NextRequest request,
                             final StringCallback callback,
                             Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(callback, "callback must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        final TaskQueue queue = mQueue;
        final NextClient client = mClient;
        final Gson gson = mGson;
        final TaskCallable<String> callable = new TaskCallable<String>() {
            @Override
            public String call() throws Exception {
                return client.execute(request, new StringConverter());
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public <T> String addRequest(final NextRequest request,
                                 final GsonCallback<T> callback,
                                 Object caller) {
        AssertUtils.notNull(request, "request must not be null.");
        AssertUtils.notNull(callback, "callback must not be null.");
        AssertUtils.notNull(caller, "caller must not be null.");
        final TaskQueue queue = mQueue;
        final NextClient client = mClient;
        final Gson gson = mGson;
        final TaskCallable<T> callable = new TaskCallable<T>() {
            @Override
            public T call() throws Exception {
                final ResponseConverter<T> converter = new GsonConverter<>(gson, callback.type());
                return client.execute(request, converter);
            }
        };
        return enqueue(request, callable, callback, caller);
    }

    public String addRequest(final HttpMethod method,
                             final String url,
                             final NextParams params,
                             final ResponseCallback callback,
                             Object caller) {
        final NextRequest request = new NextRequest(method, url).params(params);
        return addRequest(request, callback, caller);
    }

    public String addRequest(final HttpMethod method,
                             final String url,
                             final NextParams params,
                             final StringCallback callback,
                             Object caller) {
        final NextRequest request = new NextRequest(method, url).params(params);
        return addRequest(request, callback, caller);
    }

    public <T> String addRequest(final HttpMethod method,
                                 final String url,
                                 final NextParams params,
                                 final GsonCallback<T> callback,
                                 Object caller) {
        final NextRequest request = new NextRequest(method, url).params(params);
        return addRequest(request, callback, caller);
    }

    public String addRequest(final HttpMethod method, final String url,
                             final Map<String, String> queries,
                             final Map<String, String> forms,
                             final ResponseCallback callback,
                             Object caller) {
        return addRequest(method, url, queries, forms, null, callback, caller);
    }

    public String addRequest(final HttpMethod method, final String url,
                             final Map<String, String> queries,
                             final Map<String, String> forms,
                             final StringCallback callback,
                             Object caller) {
        return addRequest(method, url, queries, forms, null, callback, caller);
    }

    public <T> String addRequest(final HttpMethod method, final String url,
                                 final Map<String, String> queries,
                                 final Map<String, String> forms,
                                 final GsonCallback<T> callback,
                                 Object caller) {
        return addRequest(method, url, queries, forms, null, callback, caller);
    }

    public String addRequest(final HttpMethod method, final String url,
                             final Map<String, String> queries,
                             final Map<String, String> forms,
                             final Map<String, String> headers,
                             final ResponseCallback callback,
                             Object caller) {
        final NextRequest request = new NextRequest(method, url).queries(queries).
                forms(forms).headers(headers);
        return addRequest(request, callback, caller);
    }

    public String addRequest(final HttpMethod method, final String url,
                             final Map<String, String> queries,
                             final Map<String, String> forms,
                             final Map<String, String> headers,
                             final StringCallback callback,
                             Object caller) {
        final NextRequest request = new NextRequest(method, url).queries(queries).
                forms(forms).headers(headers);
        return addRequest(request, callback, caller);
    }

    public <T> String addRequest(final HttpMethod method, final String url,
                                 final Map<String, String> queries,
                                 final Map<String, String> forms,
                                 final Map<String, String> headers,
                                 final GsonCallback<T> callback,
                                 Object caller) {
        final NextRequest request = new NextRequest(method, url).queries(queries).
                forms(forms).headers(headers);
        return addRequest(request, callback, caller);
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
                               final TaskCallback<T> callback,
                               final Object caller) {
        final int hashCode = System.identityHashCode(request);
        final String tag = mQueue.add(callable, callback, caller);
        mRequests.put(hashCode, tag);
        return tag;
    }
}
