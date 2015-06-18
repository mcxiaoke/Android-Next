## 集成方法

```groovy
    // v1.1.0 新增
    // task 异步任务库，格式:jar和aar
    compile 'com.mcxiaoke.next:task:1.+'
```

## 异步任务

包含异步任务执行模块相关的类，详细的使用见后面的说明
    * TaskQueue 核心类，对外接口，支持单例使用
    * Task 核心类，表示单个异步任务对象
    * TaskTag Task的唯一不变表示
    * TaskStatus Task的状态
    * TaskBuilder 对外接口，链式调用
    * TaskCallback 任务回调接口

#### TaskQueue

```java

        // TaskQueue的接口定义见 com.mcxiaoke.next.task.ITaskQueue

        // 使用默认TaskQueue
        final TaskQueue taskQueue=TaskQueue.getDefault();
        // 使用新的自定义TaskQueue
        final TaskQueue taskQueue2=TaskQueue.createNew();
        taskQueue2.setDebug(true);
        taskQueue2.setExecutor(Executors.newCachedThreadPool());

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
            public void onTaskStarted(final TaskStatus<String> status, final Bundle extras) {
                // task started, on task execute thread
            }

            @Override
            public void onTaskFinished(final TaskStatus<String> status, final Bundle extras) {
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
        // execute task
        taskQueue.execute(callable,callback,caller,serial);
         taskQueue
        // add task, execute concurrently
        taskQueue.add(callable,callback,caller);
        // add task, execute serially
        taskQueue.addSerially(callable, callback, caller);
        // set custom task executor
        taskQueue.setExecutor(executor);
        // save task name for cancel the task
        final String name=taskQueue.add(callable,callback,caller);
        taskQueue.cancel(name);
        // save task tag for cancel the task
        final TaskTag tag=TaskBuilder.create(callable).with(caller).start();
        taskQueue.cancel(tag);
        // cancel the task by caller
        taskQueue.cancelAll(caller);
        // cancel all task
        taskQueue.cancelAll();

```

#### Task/TaskBuilder

```java

        // Task的接口定义见 com.mcxiaoke.next.task.ITask
        // TaskBuilder的定义见 com.mcxiaoke.next.task.TaskBuilder

        // sample for Task helper class
        final String testUrl = "https://api.github.com/users/mcxiaoke";

        TaskBuilder.create(new Callable<JSONObject>() {
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

        TaskBuilder.create(new Callable<JSONObject>() {
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

#### TaskStatus/TaskTag

```java

        // TaskStatus的定义见 com.mcxiaoke.next.task.TaskStatus

        // TaskStatus用法
        TaskStatus<Result> taskStatus=xxx;
        // 获取Task的唯一名字，可用于TaskQueue.cancel(name);
        taskStatus.getName();
        // 获取Task当前状态，取值见下面
        taskStatus.getStatus();
        // 获取Task的线程执行时长，任务结束后可用
        taskStatus.getDuration();
        // 获取Task线程的异常，任务结束后可用
        taskStatus.getError();
        // 获取Task产生的结果，任务结束后可用
        taskStatus.getData();

        // status取值
        public static final int IDLE = 0; // 空闲，初始化
        public static final int RUNNING = 1;  // 线程正在运行
        public static final int CANCELLED = 2;  // 任务已取消
        public static final int FAILURE = 3; // 任务已失败
        public static final int SUCCESS = 4; // 任务已成功

        // TaskTag的定义见 com.mcxiaoke.next.task.TaskTag

        // TaskTag用法
        // Task.start()返回TaskTag对象
        TaskTag tag=Task.start();
        // 获取Task的调用对象的字符串表示
        tag.getGroup();
        // 获取Task的唯一名字
        // 等同taskStatus.getName()
        tag.getName();
        // 获取Task的创建时间
        tag.getCreatedAt();
        // 获取Task的顺序号
        tag.getSequence();
        // 可用于取消Task
        TaskQueue.cancel(tag);

```

#### TaskCallback

```java

/**
 * 任务回调接口
 *
 * @param <Result> 类型参数，任务执行结果
 */
public interface TaskCallback<Result> {
    /**
     * 任务开始
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK STATUS
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskStarted(final TaskStatus<Result> status, final Bundle extras);

    /**
     * 任务完成
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK STATUS
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskFinished(final TaskStatus<Result> status, final Bundle extras);

    /**
     * 任务取消
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK STATUS
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskCancelled(final TaskStatus<Result> status, final Bundle extras);

    /**
     * 回调，任务执行完成
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param result 执行结果
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskSuccess(Result result, final Bundle extras);

    /**
     * 回调，任务执行失败
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param ex     失败原因，异常
     * @param extras 附加结果，需要返回额外的信息时会用到
     */
    void onTaskFailure(Throwable ex, final Bundle extras);
}

```

#### Success/Failure

```java

    // 任务成功的回调接口
    public interface Success<Result> {
        void onSuccess(final Result result, final Bundle extras);
    }

    // 任务失败的回调接口
    public interface Failure {
        void onFailure(Throwable ex, final Bundle extras);
    }

```


