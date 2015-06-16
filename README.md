Android Next 公共组件库
======
Tasks, Views, Widgets, Http, Utils
------

## 项目介绍


这个库是我在日常开发过程中积累下来的一些可复用组件，有一些是原创的，有一些是修改开源项目的，有一些则完全是从Android源码/Apache-Commons源码中复制过来的，大部分都在我的工作项目和个人项目中有实际使用案例，具体就不一一说明了


## 最新版本

* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:core:1.0.8-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Ccore%7C1.0.9%7Cjar)
* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:http:1.0.8-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Chttp%7C1.0.9%7Cjar)
* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:ui:1.0.8-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Cui%7C1.0.9%7Cjar)

已经部署到Maven Central，可以直接使用

- **1.0.9** 2015.06.16
    * core: 优化TaskQueue，调整接口，添加新的辅助类Task，支持链式调用
    * core: 重构MemoryCache，精简结构，缓存对象支持设置过期时间
    * core: 添加一些工具类，如PackageUtils和TrafficUtils
    * 细节调整，更新示例和说明文档

- **1.0.8** 2015.05.18
    * ui: 移除所有的ic_launcher.png，修复appt报错问题

- **1.0.7** 2015.03.24
    * core: 优化TaskQueue，去掉对support-v4的依赖
    * core: 微调LogUtils，其它细节调整
    * ui: 微调EndlessListView，新增ListViewExtend
    
- **1.0.6** 2015.03.20
    * 细节调整
    
- **1.0.5** 2014.12.22
    * 补充完整的文档
    
- **1.0.4** 2014.09.15
    * 发布到github

------

## 开始使用

Gradle集成方法：

```groovy

    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.0.+'
    
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.0.+'
    
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.0.+'
    
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extras-abc:1.0.+'
    
    
```

------


## 模块结构

分为以下几个模块：

#### core 核心组件

包含异步任务组件，缓存组件，基础Activity和Service，还有一些工具类，按Java包介绍如下：

- **com.mcxiaoke.next.annotation** 两个简单的Annotation，标注是否线程安全，纯标注用

- **com.mcxiaoke.next.app** 基础类，包含:
    * NextBaseActivity 基础Activity，添加了一些ActionBar相关的封装方法
    * NextBaseFragment 基础Fragment，添加了ActionBar和Activity相关的一些封装方法
    * MultiIntentService 类似于IntentService，但是可以多个异步任务并发执行，可以在所有任务执行完成后自动stopSelf()，具体请看源码
    

- **com.mcxiaoke.next.cache** 简单缓存类，包含内存缓存MemoryCache和磁盘缓存DiscCache，使用非常简单，可定制

- **com.mcxiaoke.next.collection** 几个常用的集合类，包含：NoDuplicatesArrayList, NoDuplicatesCopyOnWriteArrayList, NoDuplicatesLinkedList和WeakFastHashMap

- **com.mcxiaoke.next.common** 包含NextMessage，类似于Android系统的Message类，但是使用更方便，能支持更多数据类型

- **com.mcxiaoke.next.db** 包含两个简单的数据库相关的工具类

- **com.mcxiaoke.next.geo**  包含LastLocationFinder，用于快速获取上次定位位置

- **com.mcxiaoke.next.io** 包含CountingInputStream, CountingOutputStream, StringBuilderWriter, BoundedInputStream等IO数据流相关的封装类，方便使用

- **com.mcxiaoke.next.task** 包含异步任务执行模块相关的类，详细的使用见后面的说明
    * TaskQueue 核心类，对外接口，支持单例使用
    * Task 辅助类，对外接口，方便链式调用
    * TaskCallback 任务回调接口


- **com.mcxiaoke.next.utils** 包含了很多使用简单但又非常有用的小工具类:
    * AndroidUtils Android系统相关的一些工具类，包括文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息等
    * AssertUtils Assert类，Null检查，对象检查，数组检查等
    * BitmapUtils Bitmap缩放，旋转，圆角，阴影，裁剪等方法
    * CryptoUtils 加密算法相关的工具方法，支持MD5/SHA1/SHA256/AES/HEX等
    * IOUtils IO操作工具类，包含常用的文件复制/字符串/数组/列表/数据流读写方法
    * MimeUtils MIME工具类，支持根据文件扩展名获取MIME类型
    * NetworkUtils 网络工具类，支持获取网络类型，设置代理等
    * ReflectionUtils Java反射相关的工具类
    * StringUtils 字符串工具类，支持常用的字符串合并/分割/比较/转换/判断等操作
    * ViewUtils View相关的几个工具方法，例如getScreenRawSize/getActionBarHeightInDp/getStatusBarHeightInDp/getResourceValue等
    * ZipUtils 支持ZIP文件压缩/解压缩
    * PackageUtils Package相关的工具类，App是否安装，是否运行，启用和禁用组件等
    * TrafficUtils App流量使用统计工具类
    
    

------    


#### http HTTP组件

包含一个经过简单封装的HTTP操作模块，简化常用的网络请求操作

 - **NextClient** 网络组件的核心类
 - **NextParams** HTTP参数封装和处理
 - **NextRequest** HTTP Request
 - **NextResponse** HTTP Response

详细的使用方法见后面的介绍

------


#### ui UI组件

一些常用的UI控件，可简化日常开发，包含：

- **AlertDialogFragment和ProgressDialogFragment** 封装好的DialogFragmen，接口简单，同时有4.0版本和使用support-v4的版本

- **EndlessListView** 封装的ListView，添加了支持加载更多数据的接口和FooterView展示

- **CheckableFrameLayout** Checkable布局系列，包含几种常用布局的Checkable封装，附带一个很有用的CheckableImageView

- **NoPressStateFrameLayout** NoPress布局系统，包含几种常用布局的NoPressState封装

- **SquaredFrameLayout** Squared布局系列，包含几个常用布局的Squared封装，正方形布局

- **typeface** typeface包里包含一些支持字体设置的常用控件，如Button/TextView/CheckBox等

- **AspectRatioImageView** 定制的ImageView，缩放时会保持长宽比

- **CircularImageView** 圆形ImageView，一般用作头像等显示，不建议使用，github有更好的项目

- **FixedRatioImageView** 强制高宽比的ImageView，可选以水平或垂直方向为基准，另外还有一个强制保持正方形的SquaredImageView

- **AdvancedShareActionProvider** 高级版的ShareActionProvider，支持自定义分享菜单列表项，自定图标和分享内容等，包含4.0的版本和使用AppCompat的版本，具体可以看源码和示例

- **ArrayAdapterCompat** 增强版的ArrayAdapter，支持2.3以上版本，增加很多实用方法


------

#### samples 示例

包含几个常用模块的示例，主要是TaskQueue/NextClient/AdvancedShareActionProvider的示例


------

## 使用说明

#### 高级分享模块

```java

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem share = menu.findItem(R.id.menu_share);
        final AdvancedShareActionProvider provider = (AdvancedShareActionProvider) share.getActionProvider();
        final MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.v(TAG, "Share Target, onMenuItemClicked");
                return true;
            }
        };
        ShareTarget target = new ShareTarget("ShareTarget",
                getResources().getDrawable(android.R.drawable.ic_menu_share), listener);
        provider.addShareTarget(target);
        final String pkg = getPackageName();
        provider.addCustomPackage("com.twitter.android");
        provider.addCustomPackage(pkg);
        provider.addCustomPackage("com.twitter.android");
        provider.removePackage("com.google.android.apps.plus");
        provider.setDefaultLength(3);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "I am some text for sharing!");
        provider.setShareIntent(intent);
        return true;
    }


```

#### 异步任务模块

```java

        // you can use TaskCallable or just Callable
        final TaskCallable<String> callable=new TaskCallable<String>("name") {
            @Override
            public String call() throws Exception {
                final String url="https://github.com/mcxiaoke/Android-Next/raw/master/README.md";
                final NextResponse response=NextClient.get(url);
                return response.string();
            }
        };
        // task callback
        final TaskCallback<String> callback=new SimpleTaskCallback<String>() {
            @Override
            public void onTaskStarted(final String tag, final Bundle extras) {
                // task started, on task execute thread
            }
            
            @Override
            public void onTaskFinished(final String result, final Bundle extras) {
                // task started, on task execute thread
            }

            @Override
            public void onTaskSuccess(final String result, final Bundle extras) {
                // task success, on main thread
                mTextView.setText(result);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                // task failure, on main thread
            }
        };
        // add task, execute concurrently
        TaskQueue.getDefault().add(callable,callback,this);
        // add task, execute serially
        TaskQueue.getDefault().addSerially(callable, callback, this);

        // set custom task executor
        TaskQueue.getDefault().setExecutor(executor);
        //  set yes/no check activity/fragment lifecycle
        TaskQueue.getDefault().setEnableCallerAliveCheck(true);
        // save task tag for cancel the task
        final String tag=TaskQueue.getDefault().add(callable,callback,this);
        TaskQueue.getDefault().cancel(tag);
        // cancel the task by caller
        TaskQueue.getDefault().cancelAll(this);
        // cancel all task
        TaskQueue.getDefault().cancelAll();

        // sample for Task helper class
        final String testUrl = "https://api.github.com/users/mcxiaoke";

        Task.create(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.get(testUrl).string();
                return new JSONObject(response);
            }
        }).callback(new SimpleTaskCallback<JSONObject>() {
            @Override
            public void onTaskSuccess(final JSONObject result, final Bundle extras) {
                super.onTaskSuccess(result, extras);
                Log.v("Task", "onTaskSuccess() result=" + result);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                super.onTaskFailure(ex, extras);
                Log.e("Task", "onTaskFailure() error=" + ex);
            }
        }).with(this).serial(false).start();

        Task.create(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.get(testUrl).string();
                return new JSONObject(response);
            }
        }).success(new Success<JSONObject>() {
            @Override
            public void onSuccess(final JSONObject result, final Bundle extras) {
                Log.v("Task", "onSuccess() result=" + result);
            }
        }).failure(new Failure() {
            @Override
            public void onFailure(final Throwable ex, final Bundle extras) {
                Log.e("Task", "onFailure() error=" + ex);
            }
        }).with(this).start();

        Task.create(callable) // 设置Task Callable
                .callback(callback) // 设置TaskCallback
                .with(caller) // 设置Task Caller
                .serial(serially) // 设置是否顺序执行
                .success(success) // 设置任务成功回调，如果callback!=null，忽略
                .failure(failure) // 设置任务失败回调，如果callback!=null，忽略
                .start(); // 开始执行异步任务

```


#### HTTP模块

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



#### 缓存模块


```java

        // create memory cache, internally using map
        final IMemoryCache<String,String> memoryCache= MemoryCache.mapCache();
        // create memory cache, internally using lru cache
        // final IMemoryCache<String,String> memoryCache= MemoryCache.lruCache(100);
        memoryCache.put("strKey", "value");
        memoryCache.put("intKey", 123);
        memoryCache.put("boolKey", false);
        memoryCache.put("objKey", new TaskQueue());
        memoryCache.put("boolKey", false);
        // expires in 300 seconds
        memoryCache.put("canExpireKey", "hello, world", 300*1000L);
        final String value = memoryCache.get("strKey");

        final Context context = mockContext();
        // default disc cache, use /data/data/package-name/cache/.disc/ dir
        final DiscCache discCache = new DiscCache(context);
        // use custom /data/data/package-name/cache/json-cache/ dir
        //final DiscCache discCache=new DiscCache(context,"json-cache");
        // use custom /sdcard/Android/data/package-name/cache/json-cache/ dir
        //final DiscCache discCache=new DiscCache(context,"json-cache",DiscCache.MODE_EXTERNAL);
        discCache.setCacheDir("dirName");
        discCache.setCharset("UTF-8");
        discCache.setFileNameGenerator(nameGenerator);
        final byte[] bytes = new byte[100];
        discCache.put("bytes", bytes);
        discCache.put("stream", new ByteArrayInputStream(bytes));
        discCache.put("text", "some text for cache");
        final byte[] bytesValue = discCache.getBytes("bytes");
        final File file = discCache.getFile("stream");
        final String stringValue = discCache.get("text");
        discCache.remove("cacheKey");
        discCache.clear();
        discCache.delete(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return false;
            }
        });


```

------


##其它问题

发现任何问题可以提issue


------


##License


    Copyright 2013 - 2014 Xiaoke Zhang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





