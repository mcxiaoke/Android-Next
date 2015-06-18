package com.mcxiaoke.next.task;

import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.next.utils.LogUtils;
import com.mcxiaoke.next.utils.StringUtils;
import com.mcxiaoke.next.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 一个用于执行异步任务的类，单例，支持检查Caller，支持按照Caller和Tag取消对应的任务
 * User: mcxiaoke
 * Date: 2013-7-1 2013-7-25 2014-03-04 2014-03-25
 * Date: 2014-05-14 2014-05-29 2015-06-15
 */
final class TaskQueueImpl extends TaskQueue {

    public static final String TAG = "TaskQueue";
    private final Object mLock = new Object();
    private ThreadPoolExecutor mExecutor;
    private ThreadPoolExecutor mSerialExecutor;
    private Handler mUiHandler;
    private Map<String, List<String>> mGroups;
    private Map<String, ITaskRunnable> mNames;
    private boolean mDebug;

    static final class SingletonHolder {
        static final TaskQueueImpl INSTANCE = new TaskQueueImpl();
    }

    public static TaskQueueImpl getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public TaskQueueImpl() {
        LogUtils.v(TAG, "TaskQueue()");
        init();
        checkThread();
        checkExecutor();
    }

    private void init() {
        mNames = new ConcurrentHashMap<String, ITaskRunnable>();
        mGroups = new ConcurrentHashMap<String, List<String>>();
        mUiHandler = new Handler();
    }

    /********************************************************************
     *
     * PUBLIC METHODS
     *
     *******************************************************************/

    /**
     * debug开关
     *
     * @param debug 是否开启DEBUG模式
     */
    @Override
    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * 执行异步任务，回调时会检查Caller是否存在，如果不存在就不执行回调函数
     *
     * @param callable Callable对象，任务的实际操作
     * @param callback 回调接口
     * @param caller   调用方，一般为Fragment或Activity
     * @param serial   是否按顺序执行任务
     * @param <Result> 类型参数，异步任务执行结果
     * @return 返回内部生成的此次任务的TAG
     */
    @Override
    public <Result> TaskTag execute(final Callable<Result> callable,
                                    final TaskCallback<Result> callback,
                                    final Object caller, final boolean serial) {
        return execute(TaskBuilder.create(callable).with(caller).callback(callback)
                .serial(serial).build());
    }

    @Override
    public <Result> String add(final Callable<Result> callable,
                               final TaskCallback<Result> callback,
                               final Object caller) {
        if (mDebug) {
            LogUtils.v(TAG, "execute()");
        }
        return execute(callable, callback, caller, false).getName();
    }

    /**
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @return Tag
     */
    @Override
    public <Result> String add(final Callable<Result> callable, final Object caller) {
        return add(callable, null, caller);
    }

    @Override
    public <Result> String addSerially(final Callable<Result> callable,
                                       final TaskCallback<Result> callback, final Object caller) {
        if (mDebug) {
            LogUtils.v(TAG, "addSerially()");
        }
        return execute(callable, callback, caller, true).getName();
    }

    /**
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @return Tag
     */
    @Override
    public <Result> String addSerially(final Callable<Result> callable, final Object caller) {
        return addSerially(callable, null, caller);
    }

    /**
     * 取消NAME对应的任务
     *
     * @param name 任务NAME
     * @return 任务是否存在
     */
    @Override
    public boolean cancel(String name) {
        return cancelByName(name);
    }

    @Override
    public boolean cancel(final TaskTag tag) {
        return cancelByName(tag.getName());
    }

    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    @Override
    public int cancelAll(Object caller) {
        return cancelByCaller(caller);
    }

    /**
     * 设置自定义的ExecutorService
     *
     * @param executor ExecutorService
     */
    @Override
    public void setExecutor(final ThreadPoolExecutor executor) {
        if (executor == null) {
            throw new NullPointerException("executor must not be null.");
        }
        mExecutor = executor;
    }

    /**
     * 便利任务列表，取消所有任务
     */
    @Override
    public void cancelAll() {
        if (mDebug) {
            LogUtils.v(TAG, "cancelAll()");
        }
        cancelAllInQueue();
    }

    /**
     * 获取当前实例的详细信息
     *
     * @param logcat 是否输出到logcat
     * @return dump output
     */
    @Override
    public String dump(final boolean logcat) {
        final StringBuilder builder = new StringBuilder();

        final ThreadPoolExecutor executor = mExecutor;

        // thread pool info
        final int corePoolSize = executor.getCorePoolSize();
        final int poolSize = executor.getPoolSize();
        final int activeCount = executor.getActiveCount();
        final long taskCount = executor.getTaskCount();
        final long completedCount = executor.getCompletedTaskCount();
        final boolean isShutdown = executor.isShutdown();
        final boolean isTerminated = executor.isTerminated();
        builder.append(TAG).append("[ ");
        builder.append("ThreadPool:{")
                .append(" coreSize:").append(corePoolSize).append(";")
                .append(" poolSize:").append(poolSize).append(";")
                .append(" isShutdown:").append(isShutdown).append(";")
                .append(" isTerminated:").append(isTerminated).append(";")
                .append(" activeCount:").append(activeCount).append(";")
                .append(" taskCount:").append(taskCount).append(";")
                .append(" completedCount:").append(completedCount).append(";")
                .append("}\n");
        // caller map
        final Map<String, List<String>> callerMap = mGroups;
        builder.append("Groups:{");
        for (Map.Entry<String, List<String>> entry : callerMap.entrySet()) {
            builder.append(" group:").append(entry.getKey())
                    .append(", tags:").append(StringUtils.toString(entry.getValue())).append(";");
        }
        builder.append("}\n");
        builder.append("]");

        // task map
        Map<String, ITaskRunnable> taskMap = mNames;
        builder.append("Tasks:{");
        for (Map.Entry<String, ITaskRunnable> entry : taskMap.entrySet()) {
            builder.append(" tag:").append(entry.getKey())
                    .append(", runnable:").append(entry.getValue()).append(";");
        }
        builder.append("}\n");

        final String info = builder.toString();

        if (logcat) {
            LogUtils.d(TAG, info);
        }
        return info;
    }

    /********************************************************************
     *
     * PRIVATE METHODS
     *
     *******************************************************************/

    /**
     * 执行异步任务
     *
     * @param task     任务对象
     * @param <Result> 类型参数，异步任务执行结果
     * @return 返回内部生成的此次任务的TAG
     */
    @Override
    <Result> TaskTag execute(final Task<Result> task) {
        if (mDebug) {
            LogUtils.v(TAG, "execute() task=" + task);
        }
        final ITaskRunnable runnable = new TaskRunnable<Result>
                (task, mDebug);

        final boolean serial = task.isSerial();
        final TaskTag tag = task.getTag();
        addTagToTaskMap(tag.getName(), runnable);
        addTagToCallerMap(tag);
        smartSubmit(runnable, serial);
        return tag;
    }


    private void addTagToTaskMap(final String tag, final ITaskRunnable runnable) {
        synchronized (mLock) {
            mNames.put(tag, runnable);
        }
    }

    private void addTagToCallerMap(final TaskTag taskTag) {
        final String group = taskTag.getGroup();
        List<String> tags = mGroups.get(taskTag.getGroup());
        if (tags == null) {
            tags = new ArrayList<String>();
            synchronized (mLock) {
                mGroups.put(group, tags);
            }
        }
        synchronized (mLock) {
            tags.add(taskTag.getName());
        }
    }

    /**
     * 取消所有的Runnable对应的任务
     */
    private void cancelAllInQueue() {
        if (mDebug) {
            LogUtils.v(TAG, "cancelAllInQueue()");
        }
        final Collection<ITaskRunnable> tasks = mNames.values();
        for (ITaskRunnable task : tasks) {
            if (task != null) {
                task.cancel();
            }
        }
        synchronized (mLock) {
            mNames.clear();
            mGroups.clear();
        }
    }

    int cancelByCaller(final Object caller) {
        if (mDebug) {
            LogUtils.v(TAG, "cancelByCaller() caller=" + caller);
        }
        return cancelByGroup(TaskTag.getGroup(caller));
    }

    int cancelByGroup(final String group) {
        if (mDebug) {
            LogUtils.v(TAG, "cancelByGroup() group=" + group);
        }
        int count = 0;
        final List<String> tags;
        synchronized (mLock) {
            tags = mGroups.remove(group);
        }
        if (tags == null || tags.isEmpty()) {
            return count;
        }
        for (String tag : tags) {
            cancel(tag);
            ++count;
        }
        if (mDebug) {
            LogUtils.v(TAG, "cancelByGroup() count=" + count);
        }
        return count;
    }

    boolean cancelByName(final String name) {
        if (mDebug) {
            LogUtils.v(TAG, "cancel() name=" + name);
        }
        boolean result = false;
        final ITaskRunnable runnable;
        synchronized (mLock) {
            runnable = mNames.remove(name);
        }
        if (runnable != null) {
            result = runnable.cancel();
        }
        return result;
    }

    @Override
    void remove(final TaskTag tag) {
        if (mDebug) {
            LogUtils.v(TAG, "remove " + tag + " at thread:" + Thread.currentThread().getName());
        }
        synchronized (mLock) {
            mNames.remove(tag.getName());
        }
        List<String> tags = mGroups.get(tag.getGroup());
        if (tags != null) {
            synchronized (mLock) {
                tags.remove(tag.getName());
            }
        }
    }

    /**
     * 将任务添加到线程池执行
     *
     * @param runnable 任务Runnable
     */
    private void smartSubmit(final ITaskRunnable runnable, final boolean serial) {
        checkExecutor();
        final Future<?> future;
        if (serial) {
            future = mSerialExecutor.submit(runnable);
        } else {
            future = mExecutor.submit(runnable);
        }
        runnable.setFuture(future);
    }

    /**
     * 检查并初始化ExecutorService
     */
    private void checkExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = ThreadUtils.newCachedThreadPool("task-default");
        }
        if (mSerialExecutor == null || mSerialExecutor.isShutdown()) {
            mSerialExecutor = ThreadUtils.newSingleThreadExecutor("task-serial");
        }
    }

    /**
     * 关闭Executor
     */
    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
        if (mSerialExecutor != null) {
            mSerialExecutor.shutdown();
            mSerialExecutor = null;
        }
    }

    /**
     * 关闭Handler
     */
    private void destroyHandler() {
        synchronized (mLock) {
            if (mUiHandler != null) {
                mUiHandler.removeCallbacksAndMessages(null);
                mUiHandler = null;
            }
        }
    }

    /********************************************************************
     * STATIC METHODS
     *******************************************************************/

    private static void checkThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("TaskQueue instance must be created on main thread");
        }
    }

    /**
     * 检查参数非空
     *
     * @param args 参数列表
     */
    private static void checkArguments(final Object... args) {
        for (Object o : args) {
            if (o == null) {
                throw new NullPointerException("argument can not be null " + Arrays.toString(args));
            }
        }
    }

    private static void logExecutor(final String name, final ThreadPoolExecutor executor) {
        final int corePoolSize = executor.getCorePoolSize();
        final int poolSize = executor.getPoolSize();
        final int activeCount = executor.getActiveCount();
        final long taskCount = executor.getTaskCount();
        final long completedCount = executor.getCompletedTaskCount();
        final boolean isShutdown = executor.isShutdown();
        final boolean isTerminated = executor.isTerminated();
        LogUtils.v(TAG, name + " CorePoolSize:" + corePoolSize + " PoolSize:" + poolSize);
        LogUtils.v(TAG, name + " isShutdown:" + isShutdown + " isTerminated:" + isTerminated);
        LogUtils.v(TAG, name + " activeCount:" + activeCount + " taskCount:" + taskCount
                + " completedCount:" + completedCount);
    }

}
