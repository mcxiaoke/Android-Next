# HTTP网络模块

## 集成方法

```groovy
    // http HTTP组件, 格式:jar和aar
    // 依赖 :core :task
    // 1.3.0新增HttpQueue
    compile 'com.mcxiaoke.next:http:1.5.1'
    compile 'com.mcxiaoke.next:task:1.5.1'
    compile 'com.mcxiaoke.next:core:1.5.1'
```

## 同步接口

包含一个经过简单封装的HTTP操作模块，简化常用的网络请求操作

 - **NextClient** 网络组件的核心类，封装全局的配置参数
 - **NextParams** HTTP参数封装和处理
 - **NextRequest** HTTP 请求封装
 - **NextResponse** HTTP 响应数据结构
 - **ProgressListener** HTTP请求数据传输进度回调接口

## 异步接口

异步HTTP，一个公开接口 `com.mcxiaoke.next.http.HttpQueue`，主要是结合 `http`模块和`task`模块，提供方便的异步网络操作，本模块主要的方法都是异步执行，内部使用 `TaskQeue` 执行异步任务管理，使用 `NextClient` 发送和解析HTTP网络请求，通过回调接口返回数据，网络请求在异步线程执行，回调方法在主线程调用，可用于替代Google的`Volley`库，能极大的减轻应用开发中异步请求数据然后主线程更新UI这一过程的工作量。

- ***HttpAsync* 异步HTTP操作辅助类，支持直接的异步HEAD/GET/DELETE/POST/PUT请求
- **HttpQueue** 异步HTTP任务队列，支持添加和取消HTTP异步任务，支持多种形式的Callback和Transformer
- **HttpJob** HTTP任务对象，封装了Request/Callback/Transformer等
- **HttpJobBuilder** 生成HttpJob对象的Builder
- **HttpCallback** 异步HTTP请求回调接口，调用者可以获知HTTP请求的结果是成功还是失败，获取数据和异常对象
- **HttpTransformer** 异步HTTP请求数据类型转换接口，支持Response/String/Gson/File等类型，支持自定义数据类型
- **HttpProcessor** 异步HTTP请求数据处理器，支持多个Processor

## 快速入门（同步接口）

### 最简单的用法

```java

    String url = "https://api.douban.com/v2/user/1000001";
    // http head
    NextClient.getDefault().head(url);
    // http delete
    NextClient.getDefault().delete(url);
    // http get
    String result1 = NextClient.getDefault().get(url).string();
    // http post
    Map<String, String> forms = new HashMap<String, String>();
    forms.put("key1", "value1");
    forms.put("key2", "value2");
    String result2 = NextClient.getDefault().post(url, forms).string();
    // http put
    String result3 = NextClient.getDefault().put(url, forms).string();

```


### 获取API数据

```java
// 用法一
    String userJsonData=NextClient.getDefault().get("https://api.douban.com/v2/user/1000001").string();
    JSONObject json=new JSONObject(userJsonData);
    String userId=json.getString("id");
    String createdAt=json.getString("created");

// 用法二
    Map<String, String> queries = new HashMap<String, String>();
    queries.put("urlquery1", "qvalue2");
    queries.put("device_id", "#RGDGrdgegd");
    Map<String, String> headers = new HashMap<String, String>();
    headers.put("User-Agent", "NextClient 1.x/Android");
    headers.put("X-PLATFORM", "Android");
    String userJsonData = NextClient.getDefault().
            get("https://api.douban.com/v2/user/1000001", queries, headers).string();
    JSONObject json = new JSONObject(userJsonData);
    String userId = json.getString("id");
    String createdAt = json.getString("created");

// 返回JSON数据
    /**
     {
     loc_id: "108288",
     name: "阿北",
     created: "2006-01-09 21:12:47",
     is_banned: false,
     is_suicide: false,
     loc_name: "北京",
     avatar: "http://img3.douban.com/icon/u1000001-30.jpg",
     signature: "less is more",
     uid: "ahbei",
     alt: "http://www.douban.com/people/ahbei/",
     desc: "Less is more",
     type: "user",
     id: "1000001",
     large_avatar: "http://img3.douban.com/icon/up1000001-30.jpg"
     }
     */
```

###  下载图片

```java
    String imageUrl="http://img4.douban.com/view/photo/raw/public/p2249105947.jpg";
    File imageFile=new File("filename.jpg");
    NextClient.getDefault().get(imageUrl).writeTo(imageFile);

```

### 提交表单数据

```java
// 用法一
    Map<String, String> forms = new HashMap<String, String>();
    forms.put("key1", "value1");
    forms.put("key2", "value2");
    NextResponse response = NextClient.getDefault().post("http://httpbin.org/post", forms);

// 用法二
    final NextRequest request = NextRequest.post("http://httpbin.org/post");
    request.query("param1", "value");
    request.form("key1", "value1");
    request.form("key2", "value2");
    NextResponse response = NextClient.getDefault().execute(request);
```

### 上传文件

```java
    final NextRequest request = NextRequest.post("http://httpbin.org/post");
    request.query("param1","value");
    request.form("name", "myfile");
    request.file("image", new File("12345.jpg"), "image/jpeg");
    NextResponse response = NextClient.getDefault().execute(request);
```

### NextClient

```java
    OkHttpClient okHttpClient = new OkHttpClient();
    NextClient client = new NextClient(okHttpClient);
    client.setDebug(true);
    client.setUserAgent("...");
    client.setAuthorization("...");

```

## 快速入门（异步接口）

### HttpQueue

```java

    HttpQueue q1 = new HttpQueue();
    HttpQueue q2 = new HttpQueue(new NextClient());
    HttpQueue q3 = new HttpQueue(new OkHttpClient());
    HttpQueue q4 = new HttpQueue(TaskQueue.concurrent());
    HttpQueue q5 = new HttpQueue(TaskQueue.concurrent(10), new NextClient());
    HttpQueue httpQueue=HttpQueue.getDefault();

    httpQueue.add(httpJob);
    httpQueue.add(request, responseCallback, caller);
    httpQueue.add(request, jsonCallback, caller);
    httpQueue.add(request, stringCallback, caller);
    httpQueue.add(request, fileCallback, caller);
    httpQueue.add(request, bitmapCallback, caller);
	httpQueue.add(request, transformer, callback, caller);
    httpQueue.add(request, transformer, callback, caller,
               requestProcessor, preProcessor, postProcessor);

    httpQueue.cancel(name);
    httpQueue.cancelAll(caller);
    httpQueue.cancelAll();

    httpQueue.setClient(nextClient);
    httpQueue.setGson(gson);
    httpQueue.setQueue(taskQueu);
    httpQueue.setDebug(true);


```

### HttpJob

```java
        final String url = "https://api.github.com/users/mcxiaoke";
        final NextRequest request = NextRequest.get(url);
        final StringCallback callback = new StringCallback() {
            @Override
            public void handleResponse(final String response) {
                // request success
            }

            @Override
            public void handleException(final Throwable error) {
                // request failure
            }
        };
        final HttpProcessor<NextRequest> requestProcessor=new HttpProcessor<NextRequest>() {
            @Override
            public void process(final NextRequest response) {
                // process next request
            }
        } ;
        final HttpProcessor<NextResponse> preProcessor=new HttpProcessor<NextResponse>() {
            @Override
            public void process(final NextResponse response) {
                // process next response
            }
        };
        final HttpProcessor<String> postProcessor = new HttpProcessor<String>() {
            @Override
            public void process(final String response) {
                // process response data
            }
        };
        httpQueue.add(request, callback, this);

        // httpQueue.add(request,new StringTransformer(),callback,this);
        // or using HttpJob
        final HttpJob<String> httpJob = new HttpJobBuilder<String>()
                .request(request)
                .callback(callback)
                .caller(this)
                .transformer(new StringTransformer())
                .requestProcessor(requestProcessor)
                .preProcessor(preProcessor)
                .postProcessor(postProcessor)
                .create();

        httpQueue.add(httpJob);
```

### HttpQueue接口

```java
    void setDebug(boolean debug);
    void setQueue(TaskQueue queue);
    void setClient(NextClient client);
    void setGson(Gson gson);

    void cancelAll(Object caller);
    void cancel(String name);
    void cancelAll();

    <T> String add(HttpJob<T> job);

    <T> String add(NextRequest request,
                   HttpTransformer<T> transformer,
                   HttpCallback<T> callback,
                   Object caller,
                   HttpProcessor<NextRequest> requestProcessor,
                   HttpProcessor<NextResponse> preProcessor,
                   HttpProcessor<T> postProcessor);

    <T> String add(NextRequest request,
                   HttpTransformer<T> transformer,
                   HttpCallback<T> callback,
                   Object caller);

    String add(NextRequest request,
               ResponseCallback callback,
               Object caller);

    <T> String add(NextRequest request,
                   JsonCallback<T> callback,
                   Object caller);

    String add(NextRequest request,
               StringCallback callback,
               Object caller);

    String add(NextRequest request,
               BitmapCallback callback,
               Object caller);

    String add(NextRequest request, File file,
               FileCallback callback,
               Object caller);
```

### HttpJob接口

```java
    HttpJobBuilder<T> request(NextRequest request);
    HttpJobBuilder<T> transformer(HttpTransformer<T> transformer);
    HttpJobBuilder<T> callback(HttpCallback<T> callback);
    HttpJobBuilder<T> caller(Object caller);
    HttpJobBuilder<T> requestProcessor(HttpProcessor<NextRequest> processor);
    HttpJobBuilder<T> preProcessor(HttpProcessor<NextResponse> processor);
    HttpJobBuilder<T> postProcessor(HttpProcessor<T> processor);
    HttpJob<T> create();

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final Object caller);

    public HttpJob(final NextRequest request,
                   final HttpTransformer<T> transformer,
                   final HttpCallback<T> callback,
                   final Object caller,
                   List<HttpProcessor<NextRequest>> requestProcessors,
                   final List<HttpProcessor<NextResponse>> preProcessors,
                   final List<HttpProcessor<T>> postProcessors);
```

## HTTP Response

HTTP请求返回的结果是 `com.mcxiaoke.next.http.NextResponse` 对象，包含下面这些方法：

```java

    File file = null;
    OutputStream outputStream = null;
    String url = "https://api.douban.com/v2/user/1000001";
    NextResponse response = NextClient.getDefault().get(url);
    // 获取HTTP状态码
    int httpStatusCode = response.code();
    // 获取HTTP状态消息
    String httpStatusMessage = response.message();
    // 获取HTTP HEADERS
    Headers httpHeaders = response.headers();
    // 根据名字获取HTTP HEADER
    String dateHeader = response.header("Date");
    // 是否是成功的请求 20x
    response.successful();
    // 是否是重定向 30x
    response.redirect();
    // 获取重定向地址
    response.location();
    // 获取 ContentType
    response.contentType();
    // 获取 Content-Length
    response.contentLength();
    // 获取原始的Response对象
    response.raw();
    // 获取InputStream
    response.stream();
    // 获取Reader
    response.reader();
    // 获取字节数组
    response.bytes();
    // 获取响应文本内容
    response.string();
    // 写入文件
    response.writeTo(file);
    // 写入输出流
    response.writeTo(outputStream);
    // 关闭连接
    response.close();
    // 获取创建时间
    response.createdAt();
    // 获取编码
    response.charset();

```

## HTTP Request

HTTP请求封装为一个 `com.mcxiaoke.next.http.NextRequest` 对象，主要接口如下：

```java
// 快捷工厂方法：
public static NextRequest head(final String url)
public static NextRequest get(final String url)
public static NextRequest delete(final String url)
public static NextRequest post(final String url)
public static NextRequest put(final String url)

// 构造函数
public NextRequest(final NextRequest source)
public NextRequest(final HttpMethod method, String url)
public NextRequest(final HttpMethod method, String url, final NextParams params)

// 实例方法
// 启用调试，输出日志
NextRequest debug(boolean debug);
// 设置进度Listener
NextRequest progress(ProgressListener listener);
// 设置UserAgent
NextRequest userAgent(String userAgent);
// 设置Authorization信息
NextRequest authorization(String authorization);
// 设置Referer头
NextRequest referer(String referer);
// 设置某个Header
NextRequest header(String name, String value);
// 设置多个Header
NextRequest headers(Map<String, String> headers);
// 设置URL参数
NextRequest query(String key, String value);
// 设置多个URL参数
NextRequest queries(Map<String, String> queries);
// 设置POST/PUT参数
NextRequest form(String key, String value);
// 设置多个POST/PUT参数
NextRequest forms(Map<String, String> forms);
// 设置多个MultiPart参数 POST/PUT
NextRequest parts(Collection<BodyPart> parts);
// File参数 POST/PUT
NextRequest file(String key, File file);
// File参数 POST/PUT
NextRequest file(String key, File file, String contentType);
// File参数 POST/PUT
NextRequest file(String key, File file, String contentType, String fileName);
// File参数 POST/PUT
NextRequest file(String key, byte[] bytes);
// File参数 POST/PUT
NextRequest file(String key, byte[] bytes, String contentType);
// 设置POST/PUT的数据
NextRequest body(byte[] body);
// 设置POST/PUT的数据
NextRequest body(String content, Charset charset);
// 设置POST/PUT的数据
NextRequest body(File file) throws IOException;
// 设置POST/PUT的数据
NextRequest body(Reader reader) throws IOException;
// 设置POST/PUT的数据
NextRequest body(InputStream stream) throws IOException;
// 使用NextParams设置参数
NextRequest params(NextParams params);

```

## HTTP Client

这个类用于管理一些全局的参数，如统一的POST/PUT参数，全局的HTTP头，设置超时和SSL配置，执行HTTP请求等，位置是 `com.mcxiaoke.next.http.NextClient` ，主要方法如下：

```java
NextClient addParam(String key, String value);
NextClient addParams(Map<String, String> params);

NextClient addHeader(String key, String value);
NextClient addHeaders(Map<String, String> headers);

NextClient setInterceptor(OkClientInterceptor interceptor);

NextClient setUserAgent(String userAgent);
NextClient setAuthorization(String authorization);
NextClient setReferer(String referer);

NextResponse head(String url) throws IOException;
NextResponse head(String url, Map<String, String> queries)
        throws IOException;
NextResponse head(String url, Map<String, String> queries,
                  Map<String, String> headers)
        throws IOException;

NextResponse get(String url) throws IOException;
NextResponse get(String url, Map<String, String> queries)
        throws IOException;
NextResponse get(String url, Map<String, String> queries,
                 Map<String, String> headers)
        throws IOException;

NextResponse delete(String url) throws IOException;
// put params into url queries
NextResponse delete(String url, Map<String, String> queries)
        throws IOException;
// put params into url queries
NextResponse delete(String url, Map<String, String> queries,
                    Map<String, String> headers)
        throws IOException;

// put params into  http request body
NextResponse delete2(String url, Map<String, String> forms)
        throws IOException;
// put params into  http request body
NextResponse delete2(String url, Map<String, String> forms,
                     Map<String, String> headers)
        throws IOException;

NextResponse post(String url, Map<String, String> forms)
        throws IOException;
NextResponse post(String url, Map<String, String> forms,
                  Map<String, String> headers)
        throws IOException;

NextResponse put(String url, Map<String, String> forms)
        throws IOException;
NextResponse put(String url, Map<String, String> forms,
                 Map<String, String> headers)
        throws IOException;

NextResponse request(HttpMethod method, String url)
        throws IOException;
NextResponse request(HttpMethod method, String url,
                     Map<String, String> queries)
        throws IOException;

NextResponse get(String url, NextParams params) throws IOException;
NextResponse delete(String url, NextParams params) throws IOException;
NextResponse post(String url, NextParams params) throws IOException;
NextResponse put(String url, NextParams params) throws IOException;

NextResponse request(HttpMethod method, String url,
                     Map<String, String> queries,
                     Map<String, String> forms)
        throws IOException;
NextResponse request(HttpMethod method, String url,
                     Map<String, String> queries,
                     Map<String, String> forms,
                     Map<String, String> headers)
        throws IOException;
<T> T request(HttpMethod method, String url,
              Map<String, String> queries,
              Map<String, String> forms,
              Map<String, String> headers,
              ResponseConverter<T> converter)
        throws IOException;
NextResponse request(HttpMethod method, String url,
                     NextParams params)
        throws IOException;

NextResponse execute(NextRequest req)
        throws IOException;
<T> T execute(NextRequest req, ResponseConverter<T> converter)
        throws IOException;
```

## HTTP请求示例


```java

    final String url = "https://github.com/mcxiaoke/Android-Next/raw/master/README.md";
    try {
        // simple use
        // get next client
        // final NextClient client=NextClient.getDefault();
        OkHttpClient okHttpClient = new OkHttpClient();
        final Cache cache = new Cache(getCacheDir(), 100 * 1024 * 1024);
        okHttpClient.setCache(cache);
        final NextClient client = new NextClient(okHttpClient).setDebug(true);
        // http get
        final NextResponse res1 = client.get(url);
        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("uid", "next");
        queries.put("date", "2015-07-02");
        final NextResponse res2 = client.get(url, queries);
        // http post
        final Map<String, String> forms = new HashMap<String, String>();
        queries.put("data", "hello");
        queries.put("date", "2015-07-02");
        final NextResponse res3 = client.post(url, forms);


        // using NextParams
        final NextParams params = new NextParams();
        params.query("uid", "next");
        params.query("date", "2015-07-02");
        params.form("text", "hello");
        params.form("test", "wahahah");
        params.file("image", new File("IMG_20141222.jpg"), "image/jpeg");
        final NextResponse res = client.post(url, params);

        // advanced use
        final NextRequest request = NextRequest.post(url)
                .debug(true)
                .charset(HttpConsts.CHARSET_UTF8)
                .method(HttpMethod.POST)
                .header("X-UDID", "cxgdg4543gd64tgdgs2tgdgst4")
                .file("image", new File("IMG_20141222.jpg"), "image/jpeg")
                .query("debug_mode", "true")
                .form("param1", "value1")
                        // http progress callback
                        // for monitor upload/download file progress
                .authorization("Bearer %your access token here")
                .userAgent("com.mcxiaoke.next/1.1.5 Android/19")
                .listener(new ProgressListener() {
                    @Override
                    public void update(final long bytesRead, final long contentLength,
                                       final boolean done) {
                        Log.v("HTTP", "http progress: " + bytesRead * 100 / contentLength);
                    }
                })
                .interceptor(new OkClientInterceptor() {
                    @Override
                    public void intercept(final OkHttpClient client) {
                        // config client here
                    }
                });

        final NextResponse response = client.execute(request);
        // get response meta-data
        Log.v(TAG, "http response successful: " + response.successful());
        Log.v(TAG, "http response statusCode: " + response.code());
        Log.v(TAG, "http response statusMessage: " + response.message());
        Log.v(TAG, "http response contentLength: " + response.contentLength());
        Log.v(TAG, "http response contentType: " + response.contentType());
        // get 301/302/30x location header
        Log.v(TAG, "http response location: " + response.location());
        Log.v(TAG, "http response Server:" + response.header("Server"));
        Log.v(TAG, "http response Connection: " + response.header("Connection"));
        // get body as string
        Log.v(TAG, "http response content: " + response.string());
        // get body as  bytes
        final byte[] bytes = response.bytes();
        final Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        // get body as  stream
        final InputStream stream = response.stream();
        final Bitmap bitmap2 = BitmapFactory.decodeStream(stream);
        // get body as reader
        final Reader reader = response.reader();

    } catch (IOException e) {
        e.printStackTrace();
    }

```
