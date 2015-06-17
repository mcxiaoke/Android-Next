
## HTTP模块

#### http HTTP组件

包含一个经过简单封装的HTTP操作模块，简化常用的网络请求操作

 - **NextClient** 网络组件的核心类
 - **NextParams** HTTP参数封装和处理
 - **NextRequest** HTTP Request
 - **NextResponse** HTTP Response

```java

            final String url = "https://github.com/mcxiaoke/Android-Next/raw/master/README.md";

            // simple use
            // NextResponse response = NextClient.get(url);

            // advanced use
            final NextClient client = new NextClient();
            final NextRequest request = NextRequest.post(url)
                    .encoding("UTF-8")
                    .method("POST")
                    .header("X-UDID", "cxgdg4543gd64tgdgs2tgdgst4")
                    .param("image", new File("IMG_20141222.jpg"), "image/jpeg")
                    .param("param1", "value1")
                            // http progress callback
                            // for monitor upload/download file progress
                    .callback(new ProgressCallback() {
                        @Override
                        public void onProgress(final long currentSize, final long totalSize) {
                            Log.v("HTTP", "http progress: " + currentSize * 100 / totalSize);
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
            final InputStreamReader reader = response.reader(Charsets.UTF_8);

```
