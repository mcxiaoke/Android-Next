## 异步HTTP模块

## 使用指南

### 模块说明

异步HTTP，一个公开接口 `com.mcxiaoke.next.async.HttpQueue`，主要是结合 `http`模块和`task`模块，提供方便的异步网络操作，本模块主要的方法都是异步执行，内部使用 `TaskQeue` 执行异步任务管理，使用 `NextClient` 发送和解析HTTP网络请求，通过回调接口返回数据，网络请求在异步线程执行，回调方法在主线程调用，可用于替代Google的`Volley`库，能极大的减轻应用开发中异步请求数据然后主线程更新UI这一过程的工作量。

### Gradle集成

```groovy
compile 'com.mcxiaoke.next:http-async:1.5.1'
// 依赖http和task：
compile 'com.mcxiaoke.next:http:1.5.1'
compile 'com.mcxiaoke.next:task:1.5.1'
```

### 文档参考

* 有关TaskQueue的使用请参考：[TaskQueue](docs/task.md)
* 有关NextClient的使用请参考 [NextClient](docs/http.md)

### 任务管理HttpQueue
```java
	// 设置内部使用的 TaskQueue
    void setQueue(TaskQueue queue);
	// 设置内部使用的 NextClient
    void setClient(NextClient client);
    // 设置解析用的Gson
    void setGson(Gson gson);
    // 取消某个调用者的全部任务
    // 一般在onDestroy()方法里调用
    void cancelAll(Object caller);
	// 取消某个Task
    void cancel(String name);
```

### 异步执行Request

```java
// 使用StringCallback，返回类型为String
    String addRequest(NextRequest request, StringCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, NextParams params, StringCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, Map<String, String> queries, Map<String, String> forms,
                      StringCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, Map<String, String> queries, Map<String, String> forms,
                      Map<String, String> headers, StringCallback callback, Object caller);

// 使用ResponseCallback，返回类型为NextResponse
    String addRequest(NextRequest request, ResponseCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, NextParams params, ResponseCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, Map<String, String> queries, Map<String, String> forms,
                      ResponseCallback callback, Object caller);
    String addRequest(HttpMethod method, String url, Map<String, String> queries, Map<String, String> forms,
                      Map<String, String> headers, ResponseCallback callback, Object caller);

// 使用FileCallback，保存为文件
    String addRequest(NextRequest request, File file, FileCallback callback, Object caller);

// 使用GsonCallback<T>，返回解析后的Model对象
    <T> String addRequest(NextRequest request, GsonCallback<T> callback, Object caller);
    <T> String addRequest(HttpMethod method, String url, NextParams params, GsonCallback<T> callback, Object caller);
    <T> String addRequest(HttpMethod method, String url, Map<String, String> queries,
                          Map<String, String> forms, GsonCallback<T> callback, Object caller);
    <T> String addRequest(HttpMethod method, String url, Map<String, String> queries, Map<String, String> forms,
                          Map<String, String> headers, GsonCallback<T> callback, Object caller);
```

### 返回String类型的接口

```java
    String get(String url, StringCallback callback, Object caller);
    String get(String url, Map<String, String> queries, StringCallback callback, Object caller);
    String get(String url, NextParams params, StringCallback callback, Object caller);
    String delete(String url, StringCallback callback, Object caller);
    String delete(String url, Map<String, String> queries, StringCallback callback, Object caller);
    String delete(String url, NextParams params, StringCallback callback, Object caller);
    String post(String url, NextParams params, StringCallback callback, Object caller);
    String post(String url, StringCallback callback, Object caller);
    String post(String url, Map<String, String> forms, StringCallback callback, Object caller);
```

### Gson Model的接口

```java
    <T> String get(String url, GsonCallback<T> callback, Object caller);
    <T> String get(String url, Map<String, String> queries, GsonCallback<T> callback, Object caller);
    <T> String get(String url, NextParams params, GsonCallback<T> callback, Object caller);
    <T> String delete(String url, GsonCallback<T> callback, Object caller);
    <T> String delete(String url, Map<String, String> queries, GsonCallback<T> callback, Object caller);
    <T> String delete(String url, NextParams params, GsonCallback<T> callback, Object caller);
    <T> String post(String url, GsonCallback<T> callback, Object caller);
    <T> String post(String url, Map<String, String> forms, GsonCallback<T> callback, Object caller);
    <T> String post(String url, NextParams params, GsonCallback<T> callback, Object caller);
```

### 下载文件的接口

```java
    String download(String url, File file, FileCallback callback, Object caller);
    String download(String url, File file, Map<String, String> queries, FileCallback callback, Object caller);
    String download(String url, File file, NextParams params, FileCallback callback, Object caller);
```

### 通用类型接口

```java
    String get(String url, ResponseCallback callback, Object caller);
    String get(String url, Map<String, String> queries, ResponseCallback callback, Object caller);
    String get(String url, NextParams params, ResponseCallback callback, Object caller);
    String delete(String url, ResponseCallback callback, Object caller);
    String delete(String url, Map<String, String> queries, ResponseCallback callback, Object caller);
    String delete(String url, NextParams params, ResponseCallback callback, Object caller);
    String post(String url, ResponseCallback callback, Object caller);
    String post(String url, Map<String, String> forms, ResponseCallback callback, Object caller);
    String post(String url, NextParams params, ResponseCallback callback, Object caller);
```
