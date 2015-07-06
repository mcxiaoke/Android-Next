## 集成方法

```groovy
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.+'
```

## HTTP

包含一个经过简单封装的HTTP操作模块，简化常用的网络请求操作

 - **NextClient** 网络组件的核心类
 - **NextParams** HTTP参数封装和处理
 - **NextRequest** HTTP Request
 - **NextResponse** HTTP Response

```java

        final String url = "https://github.com/mcxiaoke/Android-Next/raw/master/README.md";
        try {
            // simple use
            // get next client
            // final NextClient client=NextClient.getDefault();
            final NextClient client = new NextClient();
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
                    .progress(new ProgressListener() {
                        @Override
                        public void update(final long bytesRead, final long contentLength,
                                           final boolean done) {
                            Log.v("HTTP", "http progress: " + bytesRead * 100 / contentLength);
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

            // NextClient usage
            // head method
            client.head(url);
            client.head(url, queries);
            client.head(url, queries, headers);
            // get method
            client.get(url);
            client.get(url, queries);
            client.get(url, queries, headers);
            client.get(url, params);
            // delete method
            client.delete(url);
            client.delete(url, queries);
            client.delete(url, queries, headers);
            client.delete(url, params);
            // delete with body
            client.delete2(url, forms);
            client.delete2(url, forms, headers);
            // post method
            client.post(url, forms);
            client.post(url, forms, headers);
            client.post(url, params);
            // put method
            client.put(url, forms);
            client.put(url, forms, headers);
            client.put(url, params);
        } catch (IOException e) {
            e.printStackTrace();
        }

```
