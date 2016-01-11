## 集成方法

```groovy
    // v1.1.0 新增
    // task 异步任务库，格式:jar和aar
    compile 'com.mcxiaoke.next:task:1.2.+'
```

## 异步任务

包含异步任务执行模块相关的类，详细的使用见后面的说明

    * TaskQueue 对外接口，支持单例使用
    * TaskFuture 表示单个异步任务对象
    * TaskBuilder 对外接口，链式调用
    * TaskCallback 任务回调接口

#### TaskBuilder

```java

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

        // 创建TaskBuilder
        TaskBuilder.create(resultType|callable|callback)
         .with(caller) // 设置Caller
         .action(callable|runnable) // 设置Callable|Runnable
         .callback(callback) // 设置TaskCallback
         .success(success) //设置任务成功Callback
         .failure(failure) //设置任务失败Callback

         // caller==null，此时认为caller已被系统回收，忽略Callback
         // Activity.isFinishing==true，此时认为caller已死，忽略Callback
         // Fragment.isAdded==false，此时认为caller已死，忽略Callback
         .check(true) //默认为true，设置是否检查Caller

         .dispatch(handler)// 回调方法所在线程，默认是主线程
         .serial(false) // 是否按顺序依次执行
         .on(queue) // 设置自定义的TaskQueue
         .delay(millis) // 设置任务延迟开始
         .extras(bundle); // 添加额外参数，会通过callback返回
         .build() // 生成Task对象
         .start() //开始任务
         .cancel()// 取消任务

```

#### TaskFuture

```java

        // TaskFuture的接口定义见 com.mcxiaoke.next.task.TaskFuture
        // TaskBuilder.build()返回一个TaskFuture对象，支持以下方法
        // 获取Task的调用者
        public String getGroup();
        // 获取Task的名字，可用于 TaskQueue.cancel(name)
        public String getName();
        // 启动Task，开始执行
        public String start();
        // 取消Task，停止执行
        public boolean cancel();
        // Task是否是顺序执行
        public boolean isSerial();
        // Task是否已完成
        public boolean isFinished();
        // Task是否已取消
        public boolean isCancelled();
        // 获取Task执行时长
        public long getDuration();
        // 获取Task状态
        public int getStatus();

        // status取值
        public static final int IDLE = 0; // 空闲，初始化
        public static final int RUNNING = 1;  // 线程正在运行
        public static final int CANCELLED = 2;  // 任务已取消
        public static final int FAILURE = 3; // 任务已失败
        public static final int SUCCESS = 4; // 任务已成功

```


#### TaskQueue

TaskQueue的接口

```java
    // 设置ExecutorService
    void setExecutor(ExecutorService executor);
    // 添加任务
    <Result> String add(Callable<Result> callable,
                        TaskCallback<Result> callback,
                        Object caller);
    // 添加任务
    <Result> String add(Callable<Result> callable,
                        TaskCallback<Result> callback);
    // 添加任务
    <Result> String add(Callable<Result> callable, Object caller);
    // 添加任务
    <Result> String add(final Callable<Result> callable);
    // 添加任务
    String add(final Runnable runnable);
    // 取消任务
    boolean cancel(String name);
    // 取消任务
    int cancelAll(Object caller);
    // 取消所有任务
    void cancelAll();
```

TaskQueue的用法

```java

        // TaskQueue的接口定义见 com.mcxiaoke.next.task.ITaskQueue

        // 使用默认TaskQueue，线程数无限制
        final TaskQueue taskQueue=TaskQueue.getDefault();
        // 自定义TaskQueue，线程数无限制
        TaskQueue cached = TaskQueue.concurrent();
        // 自定义TaskQueue，线程数无限制
        TaskQueue p0 = TaskQueue.concurrent(0);
        // 自定义TaskQueue，最大同时执行线程10
        TaskQueue p10 = TaskQueue.concurrent(10);
        // 单线程模式，顺序执行
        TaskQueue p1 = TaskQueue.concurrent(1);
        TaskQueue singleThread = TaskQueue.singleThread();

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
            public void onTaskStarted(final String name, final Bundle extras) {
                // task started, default on main thread
            }

            @Override
            public void onTaskFinished(final String name, final Bundle extras) {
                // task finished, default on main thread
            }

            @Override
            public void onTaskCancelled(final String name, final Bundle extras) {
                // task cancelled, default on main thread
            }

            @Override
            public void onTaskSuccess(final String result, final Bundle extras) {
                // task success, default on main thread
                mTextView.setText(result);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                // task failure, default on main thread
            }
        };
        // execute task
        taskQueue.add(callable,callback,caller);
         taskQueue
        // add task, execute concurrently
        taskQueue.add(callable,callback,caller);
        // set custom task executor
        taskQueue.setExecutor(executor);
        // save task name for cancel the task
        final String name=taskQueue.add(callable,callback,caller);
        taskQueue.cancel(name);
        // save task name for cancel the task
        final String name=TaskBuilder.create(callable).with(caller).start();
        taskQueue.cancel(name);
        // cancel the task by caller
        taskQueue.cancelAll(caller);
        // cancel all task
        taskQueue.cancelAll();

```


#### TaskCallback

```java

/**
 * 任务回调接口
 *
 * @param <Result> 类型参数，任务执行结果
 */
public interface TaskCallback<Result> {

    // 第二个参数保证不为null，extras里包含以下信息
    // 除onTaskStarted()外，以下值在其它回调方法里均可用
    final String group=extras.getString(TASK_GROUP);
    final String name=extras.getString(TASK_NAME);
    final int sequence=extras.getInt(TASK_SEQUENCE);
    final long delay=extras.getLong(TASK_DELAY);
    final long duration=extras.getLong(TASK_DURATION);

    // 这几个回调方法的执行顺序：
    // onTaskStarted -> (onTaskFinished|onTaskCancelled) -> (onTaskSuccess|onTaskFailure)

    /**
     * 任务开始
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskStarted(final String name, final Bundle extras);

    /**
     * 任务完成，无论是成功还是失败此方法都会调用，任务取消则不会调用
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskFinished(final String name, final Bundle extras);

    /**
     * 任务取消时调用
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param status TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskCancelled(final String name, final Bundle extras);

    /**
     * 回调，任务执行成功，无异常
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param result 执行结果
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskSuccess(Result result, final Bundle extras);

    /**
     * 回调，任务执行失败，有异常
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

    // 任务成功的回调接口，先于TaskCallback.onTaskSuccess执行
    public interface Success<Result> {
        void onSuccess(final Result result, final Bundle extras);
    }

    // 任务失败的回调接口，先于TaskCallback.onTaskFailure执行
    public interface Failure {
        void onFailure(Throwable ex, final Bundle extras);
    }

```

#### Async 异步执行工具类

```java
// 一行代码即可，最简单的异步执行方法
        Async.start(new Runnable() {
            @Override
            public void run() {
                // do your work here
            }
        });
        Async.start(new Callable<String>() {
            @Override
            public String call() throws Exception {
                // do your work here
                return "Your Result";
            }
        });
        Async.start(new Runnable() {
            @Override
            public void run() {
                // job1, do your work here
            }
        }, new Runnable() {
            @Override
            public void run() {
                // job2, do your work here
            }
        }, new Runnable() {
            @Override
            public void run() {
                // job3, do your work here
            }
        });
```

