package com.mcxiaoke.commons.os;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.commons.utils.LogUtils;
import com.mcxiaoke.commons.utils.ThreadUtils;

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
    private Handler mUiHandler;
    private Map<Integer, List<String>> mCallerMap;
    private Map<String, NextRunnable> mRunnableMap;

    private boolean mDebug;

    // 延迟加载
    private static final class SingletonHolder {
        static final NextExecutor INSTANCE = new NextExecutor();
    }

    public static NextExecutor getDefault() {
        return SingletonHolder.INSTANCE;
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
        mCallerMap = new ConcurrentHashMap<Integer, List<String>>();
        mRunnableMap = new ConcurrentHashMap<String, NextRunnable>();
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
    public <Result, Caller> NextRunnable<Result, Caller> execute(
            final Callable<Result> callable, final TaskCallback<Result> callback, final Caller caller) {
        checkArguments(callable, caller);
        final Map<String, NextRunnable> runnableMap = mRunnableMap;
        final Handler handler = mUiHandler;

        final RunnableCallback nextCallback = new RunnableCallback() {
            @Override
            public void onDone(final int hashCode, final String tag) {
                remove(tag);
            }
        };

        final NextCallable<Result> nextCallable;
        if (callable instanceof NextCallable) {
            nextCallable = (NextCallable<Result>) callable;
        } else {
            nextCallable = new NextCallableWrapper<Result>(callable);
        }

        final NextRunnable<Result, Caller> runnable = new NextRunnable<Result, Caller>
                (handler, nextCallback, nextCallable, callback, caller);

        synchronized (mLock) {
            Future<?> future = submit(runnable);
            runnable.setFuture(future);
            runnableMap.put(runnable.getTag(), runnable);
        }

        putToRunnableMap(runnable);
        putToCallerMap(runnable);
        return runnable;
    }

    public <Result, Caller> String add(final Callable<Result> callable,
                                       final TaskCallback<Result> callback, final Caller caller) {
        final NextRunnable<Result, Caller> runnable = execute(callable, callback, caller);
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
    public <Result, Caller> String add(final Callable<Result> callable, final Caller caller) {
        return add(callable, new SimpleTaskCallback<Result>() {
        }, caller);
    }

    /**
     * 不带Caller，无法终止
     *
     * @param callable Callable
     * @param <Result> Result
     * @return Future
     */
    public <Result> Future<Result> addDirect(final Callable<Result> callable) {
        return submit(callable);
    }

    /**
     * 不带Caller，无法终止
     *
     * @param runnable Runnable
     * @return Future
     */
    public Future<?> addDirect(final Runnable runnable) {
        return submit(runnable);
    }


    /**
     * 检查某个任务是否正在运行
     *
     * @param tag 任务的TAG
     * @return 是否正在运行
     */
    public boolean isActive(String tag) {
        NextRunnable nr = mRunnableMap.get(tag);
        return nr.isActive();
    }

    private <Result, Caller> void putToRunnableMap(final NextRunnable<Result, Caller> runnable) {
        final String tag = runnable.getTag();
        Future<?> future = submit(runnable);
        runnable.setFuture(future);
        synchronized (mLock) {
            mRunnableMap.put(tag, runnable);
        }
    }

    private <Result, Caller> void putToCallerMap(final NextRunnable<Result, Caller> runnable) {
        // caller的key是hashcode
        // tag的组成:className+hashcode+sequenceNumber+timestamp
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
        Collection<NextRunnable> runnables = mRunnableMap.values();
        for (NextRunnable runnable : runnables) {
            if (runnable != null) {
                runnable.cancel();
                runnable.reset();
            }
        }
        synchronized (mLock) {
            mRunnableMap.clear();
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
        NextRunnable runnable = mRunnableMap.remove(tag);
        if (runnable != null) {
            result = runnable.cancel();
            runnable.reset();
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
            mRunnableMap.remove(tag);
        }
    }

    /**
     * 将任务添加到线程池执行
     *
     * @param runnable 任务Runnable
     * @return 返回任务对应的Future对象
     */
    private Future<?> submit(final Runnable runnable) {
        ensureHandler();
        ensureExecutor();
        return mExecutor.submit(runnable);
    }

    /**
     * 将任务添加到线程池执行
     *
     * @param runnable 任务Runnable
     * @return 返回任务对应的Future对象
     */
    private <Result> Future<Result> submit(final Callable<Result> callable) {
        ensureHandler();
        ensureExecutor();
        return mExecutor.submit(callable);
    }

    /**
     * 检查并初始化ExecutorService
     *
     * @return ExecutorService
     */
    private ExecutorService ensureExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = ThreadUtils.newCachedThreadPool(TAG);
        }
        return mExecutor;
    }

    /**
     * 检查并初始化Handler
     */
    private void ensureHandler() {
        if (mUiHandler == null) {
            mUiHandler = new Handler(Looper.getMainLooper());
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

    /**
     * 任务回调接口
     *
     * @param <Result> 类型参数，任务执行结果
     */
    public static interface TaskCallback<Result> {

        /**
         * 回调，任务执行完成
         *
         * @param result 执行结果
         * @param extras 附加结果，需要返回多种结果时会用到
         * @param object 附加结果，需要返回多种结果时会用到
         */
        public void onTaskSuccess(Result result, Bundle extras, Object object);

        /**
         * 回调，任务执行失败
         *
         * @param e      失败原因，异常
         * @param extras 附加结果，需要返回额外的信息时会用到
         */
        public void onTaskFailure(Throwable e, Bundle extras, Object object);

    }

    /**
     * 回调接口抽象类
     *
     * @param <Result> 类型参数，执行结果类型
     */
    public static abstract class SimpleTaskCallback<Result> implements TaskCallback<Result> {

        @Override
        public void onTaskSuccess(Result result, Bundle extras, Object object) {
        }

        @Override
        public void onTaskFailure(Throwable e, Bundle extras, Object object) {
        }

    }

}
