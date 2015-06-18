
## 异步任务

包含异步任务执行模块相关的类，详细的使用见后面的说明
    * TaskQueue 核心类，对外接口，支持单例使用
    * Task 核心类，表示单个异步任务对象
    * TaskBuilder 对外接口，链式调用
    * TaskCallback 任务回调接口

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

        TaskBuilder.create(resultType|callable|callback) // 创建TaskBuilder
         .with(caller) // 设置Caller
         .action(callable|runnable) // 设置Callable|Runnable
         .callback(callback) // 设置TaskCallback
         .success(success) //设置任务成功回调
         .failure(failure) //设置任务失败回调
         .check(false) //设置是否检查Caller
         .dispatch(handler)// 回调方法所在线程，默认是主线程
         .serial(false) // 是否按顺序依次执行
         .on(queue) // 设置自定义的TaskQueue
         .delay(millis) // 设置任务延迟开始
         .extras(bundle); // 添加额外参数，会通过callback返回
         .build() // 生成Task对象
         .start() //开始任务
         .cancel()// 取消任务

```

