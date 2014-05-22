package com.mcxiaoke.next.os;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.mcxiaoke.next.utils.LogUtils;
import com.mcxiaoke.next.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 一个用于执行异步任务的类，单例，支持检查Caller，支持按照Caller和Tag取消对应的任务
 * User: mcxiaoke
 * Date: 2013-7-1 2013-7-25 2014-03-04 2014-03-25 2014-05-14
 */
public final class NextExecutor {
    public static final String SEPARATOR = "::";
    public static final String TAG = NextExecutor.class.getSimpleName();

    private final Object mLock = new Object();

    private ExecutorService mExecutor;
    private ExecutorService mSerialExecutor;
    private Handler mUiHandler;
    private Map<Integer, List<String>> mCallerMap;
    private Map<String, NextRunnable> mTaskMap;

    private boolean mDebug;

    // 延迟加载
    private static final class SingletonHolder {
        static final NextExecutor DEFAULT = new NextExecutor();
    }

    public static NextExecutor getDefault() {
        return SingletonHolder.DEFAULT;
    }

    public NextExecutor() {
        if (mDebug) {
            LogUtils.v(TAG, "NextExecutor()");
        }
        ensureData();
        ensureHandler();
        ensureExecutor();
    }

    private void ensureData() {
        if (mDebug) {
            LogUtils.v(TAG, "ensureData()");
        }
        if (mTaskMap == null) {
            mTaskMap = new ConcurrentHashMap<String, NextRunnable>();
        }
        if (mCallerMap == null) {
            mCallerMap = new ConcurrentHashMap<Integer, List<String>>();
        }
    }


    /**
     * debug开关
     *
     * @param debug 是否开启DEBUG模式
     */
    public void setDebug(boolean debug) {
        mDebug = debug;
    }


    /**
     * 执行异步任务，回调时会检查Caller是否存在，如果不存在就不执行回调函数
     *
     * @param callable Callable对象，任务的实际操作
     * @param callback 回调接口
     * @param caller   调用方，一般为Fragment或Activity
     * @param <Result> 类型参数，异步任务执行结果
     * @param <Caller> 类型参数，调用对象
     * @return 返回内部生成的此次任务的NextRunnable
     */
    private <Result, Caller> NextRunnable<Result, Caller> addToQueue(
            final boolean serial, final Callable<Result> callable,
            final TaskCallback<Result> callback, final Caller caller) {

        checkArguments(callable, caller);
        ensureData();
        ensureHandler();
        ensureExecutor();

        final Handler handler = mUiHandler;

        final NextCallable<Result> nextCallable;
        if (callable instanceof NextCallable) {
            nextCallable = (NextCallable<Result>) callable;
        } else {
            nextCallable = new NextCallableWrapper<Result>(callable);
        }

        final NextRunnable<Result, Caller> unit = new NextRunnable<Result, Caller>
                (handler, serial, nextCallable, callback, caller);

        addToTaskMap(unit);
        addToCallerMap(unit);
        return unit;
    }

    public <Result, Caller> String execute(final Callable<Result> callable,
                                           final TaskCallback<Result> callback,
                                           final Caller caller) {
        final NextRunnable<Result, Caller> runnable = addToQueue(false, callable, callback, caller);
        return runnable.getTag();
    }

    /**
     * 没有回调
     *
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @param <Caller> Caller
     * @return Tag
     */
    public <Result, Caller> String execute(final Callable<Result> callable, final Caller caller) {
        return execute(callable, null, caller);
    }

    public <Result, Caller> String executeSerially(final Callable<Result> callable,
                                                   final TaskCallback<Result> callback, final Caller caller) {
        final NextRunnable<Result, Caller> runnable = addToQueue(true, callable, callback, caller);
        return runnable.getTag();
    }

    /**
     * 没有回调
     *
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @param <Caller> Caller
     * @return Tag
     */
    public <Result, Caller> String executeSerially(final Callable<Result> callable, final Caller caller) {
        return executeSerially(callable, null, caller);
    }

    /**
     * 检查某个任务是否正在运行
     *
     * @param tag 任务的TAG
     * @return 是否正在运行
     */
    public boolean isActive(String tag) {
        NextRunnable nr = mTaskMap.get(tag);
        return nr != null && nr.isActive();
    }

    private <Result, Caller> void addToTaskMap(final NextRunnable<Result, Caller> unit) {

        final String tag = unit.getTag();
        Future<?> future = smartSubmit(unit);
        unit.setFuture(future);
        synchronized (mLock) {
            mTaskMap.put(tag, unit);
        }
    }

    private <Result, Caller> void addToCallerMap(final NextRunnable<Result, Caller> runnable) {
        // caller的key是hashcode
        // tag的组成:className+hashcode+timestamp+sequenceNumber
        final int hashCode = runnable.getHashCode();
        final String tag = runnable.getTag();
        List<String> tags = mCallerMap.get(hashCode);
        if (tags == null) {
            tags = new ArrayList<String>();
            synchronized (mLock) {
                mCallerMap.put(hashCode, tags);
            }
        }
        synchronized (mLock) {
            tags.add(tag);
        }

    }


    /**
     * 便利任务列表，取消所有任务
     */
    public void cancelAll() {
        if (mDebug) {
            LogUtils.v(TAG, "cancelAll()");
        }
        cancelAllInternal();
    }

    /**
     * 取消所有的Runnable对应的任务
     */
    private void cancelAllInternal() {
        Collection<NextRunnable> runnables = mTaskMap.values();
        for (NextRunnable runnable : runnables) {
            if (runnable != null) {
                runnable.cancel();
            }
        }
        synchronized (mLock) {
            mTaskMap.clear();
        }
    }

    /**
     * 取消TAG对应的任务
     *
     * @param tag 任务TAG
     * @return 任务是否存在
     */
    public boolean cancel(String tag) {
        if (mDebug) {
            LogUtils.v(TAG, "cancel() tag=" + tag);
        }
        boolean result = false;
        NextRunnable runnable = mTaskMap.remove(tag);
        if (runnable != null) {
            result = runnable.cancel();
        }
        return result;
    }

    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    public <Caller> int cancelAll(Caller caller) {
        if (mDebug) {
            LogUtils.v(TAG, "cancelAll() caller=" + caller.getClass().getSimpleName());
        }
        int cancelledCount = 0;
        final int hashCode = System.identityHashCode(caller);
        final List<String> tags = mCallerMap.remove(hashCode);
        if (tags == null || tags.isEmpty()) {
            return cancelledCount;
        }

        for (String tag : tags) {
            cancel(tag);
            ++cancelledCount;
        }

        if (mDebug) {
            LogUtils.v(TAG, "cancelAll() cancelledCount=" + cancelledCount);
        }

        return cancelledCount;
    }

    /**
     * 设置自定义的ExecutorService
     *
     * @param executor ExecutorService
     */
    public void setExecutor(ExecutorService executor) {
        mExecutor = executor;
    }

    /**
     * 取消所有任务，关闭TaskExecutor
     */
    public void destroy() {
        if (mDebug) {
            LogUtils.v(TAG, "destroy()");
        }
        cancelAll();
        destroyHandler();
        destroyExecutor();
    }

    /**
     * 从队列移除某个任务
     *
     * @param tag 任务TAG
     */
    private void remove(String tag) {
        if (mDebug) {
            LogUtils.v(TAG, "remove() tag=" + tag);
        }
        synchronized (mLock) {
            mTaskMap.remove(tag);
        }
    }

    /**
     * 将任务添加到线程池执行
     *
     * @param runnable 任务Runnable
     * @return 返回任务对应的Future对象
     */
    private Future<?> smartSubmit(final NextRunnable unit) {
        if (unit.isSerial()) {
            return submitSerial(unit);
        } else {
            return submit(unit);
        }
    }

    private Future<?> submit(final Runnable runnable) {
        ensureHandler();
        ensureExecutor();
        return mExecutor.submit(runnable);
    }

    private <T> Future<T> submit(final Callable<T> callable) {
        ensureHandler();
        ensureExecutor();
        return mExecutor.submit(callable);
    }

    private Future<?> submitSerial(final Runnable runnable) {
        ensureHandler();
        ensureExecutor();
        return mSerialExecutor.submit(runnable);
    }


    private <T> Future<T> submitSerial(final Callable<T> callable) {
        ensureHandler();
        ensureExecutor();
        return mSerialExecutor.submit(callable);
    }

    /**
     * 检查并初始化ExecutorService
     */
    private void ensureExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = ThreadUtils.newCachedThreadPool(TAG);
        }
        if (mSerialExecutor == null || mSerialExecutor.isShutdown()) {
            mSerialExecutor = ThreadUtils.newSingleThreadExecutor(TAG + " serial");
        }
    }


    // 某一个线程运行结束时需要从TaskMap里移除
    public static final int MSG_REMOVE_TASK_BY_TAG = 4001;

    /**
     * 检查并初始化Handler，主线程处理消息
     * TODO 考虑把所有的操作都放到一个单独的线程，避免cancelAll等操作堵塞住线程
     */
    private void ensureHandler() {
        if (mUiHandler == null) {
            mUiHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(final Message msg) {
                    super.handleMessage(msg);
                    if (mDebug) {
                        LogUtils.v(TAG, "handleMessage() what=" + msg.what);
                    }
                    switch (msg.what) {
                        case MSG_REMOVE_TASK_BY_TAG: {
                            final String tag = (String) msg.obj;
                            remove(tag);
                        }
                        break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    /**
     * 关闭Executor
     */
    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
        if (mSerialExecutor != null) {
            mSerialExecutor.shutdownNow();
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

    /**
     * 检查参数非空
     *
     * @param args 参数列表
     */
    private static void checkArguments(final Object... args) {
        for (Object o : args) {
            if (o == null) {
                throw new NullPointerException("argument can not be null.");
            }
        }
    }

}
