package com.mcxiaoke.commons.os;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.commons.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 一个用于执行异步任务的类，单例，支持检查Caller，支持按照Caller和Tag取消对应的任务
 * User: mcxiaoke
 * Date: 2013-7-1 2013-7-25 2014-03-04
 */
public final class NextExecutor {
    public static final String SEPARATOR = "$$$$";
    public static final String TAG = NextExecutor.class.getSimpleName();

    private final Object mLock = new Object();

    private ExecutorService mExecutor;
    private Handler mUiHandler;
    private Map<String, NextDispatcher> mDispatchers;

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
        mDispatchers = new WeakHashMap<String, NextDispatcher>();
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
     * @return 返回内部生成的此次任务的TAG，可用于取消任务
     */
    public <Result, Caller> String add(final Callable<Result> callable, final ResultCallback<Result> callback, final Caller caller) {
        checkArguments(callable, caller);
        // 保存Caller对象的WeakReference，用于后面检查Caller是否存在
        final WeakReference<Caller> weakTarget = new WeakReference<Caller>(caller);
        final String tag = buildTag(caller);
        if (mDebug) {
            LogUtils.v(TAG, "add() callable=" + callable + " callback=" + callback + " caller=" + caller);
        }
        final NextDispatcher dispatcher = new NextDispatcher(tag) {
            @Override
            public void run() {
                try {
                    if (mDebug) {
                        LogUtils.v(TAG, "add() start");
                    }
                    Result result = callable.call();

                    if (isCancelled()) {
                        if (mDebug) {
                            LogUtils.v(TAG, "add() isCancelled, return");
                        }
                        return;
                    }
                    if (isInterrupted()) {
                        if (mDebug) {
                            LogUtils.v(TAG, "add() isInterrupted, return");
                        }
                        return;
                    }

                    // Caller不存在了，不需要执行回调函数
                    // 典型情况如View/Fragment/Activity已经销毁了
                    if (weakTarget.get() == null) {
                        if (mDebug) {
                            LogUtils.v(TAG, "add() caller is null, return");
                        }
                        return;
                    }
                    dispatchTaskSuccess(result, callback);
                } catch (Exception e) {
                    if (mDebug) {
                        e.printStackTrace();
                        LogUtils.e(TAG, "add() error: " + e);
                    }
                    if (isCancelled()) {
                        return;
                    }
                    if (isInterrupted()) {
                        return;
                    }
                    if (weakTarget.get() == null) {
                        return;
                    }
                    dispatchTaskFailure(e, callback);
                } finally {
                    handleFinally(tag);
                }
                if (mDebug) {
                    LogUtils.v(TAG, "add() end");
                }
            }
        };
        return execute(tag, dispatcher);
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
        return add(callable, new SimpleResultCallback<Result>() {
        }, caller);
    }

    /**
     * 没有回调
     *
     * @param runnable Runnable
     * @param caller   Caller
     * @param <Caller> Caller
     * @return Tag
     */
    public <Caller> String add(final Runnable runnable, final Caller caller) {
        final Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        };
        return add(callable, caller);
    }

    /**
     * 不带Caller，无法终止
     *
     * @param callable Callable
     * @param <Result> Result
     * @return Future
     */
    public <Result> Future<Result> add(final Callable<Result> callable) {
        return submit(callable);
    }

    /**
     * 不带Caller，无法终止
     *
     * @param runnable Runnable
     * @return Future
     */
    public Future<?> add(final Runnable runnable) {
        return submit(runnable);
    }


    /**
     * 检查某个任务是否正在运行
     *
     * @param tag 任务的TAG
     * @return 是否正在运行
     */
    public boolean isActive(String tag) {
        NextDispatcher nr = mDispatchers.get(tag);
        return nr.isActive();
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
    // for循环不能修改内容，多线程时会有问题，故使用Iterator
    private void cancelAllInternal() {
        Set<Map.Entry<String, NextDispatcher>> taskEntrySet = mDispatchers.entrySet();
        for (Map.Entry<String, NextDispatcher> entry : taskEntrySet) {
            NextDispatcher runnable = entry.getValue();
            if (runnable != null) {
                runnable.cancel();
            }
        }
        synchronized (mLock) {
            mDispatchers.clear();
        }
    }


    /**
     * 取消一组Runnable对应的任务
     *
     * @param filterTags 过滤TAG列表
     */
    // for循环不能修改内容，多线程时会有问题，故使用Iterator
    private void cancelByTags(Collection<String> filterTags) {
        if (filterTags == null || filterTags.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, NextDispatcher>> taskEntrySet = mDispatchers.entrySet();
        Iterator<Map.Entry<String, NextDispatcher>> taskIterator = taskEntrySet.iterator();
        while (taskIterator.hasNext()) {
            Map.Entry<String, NextDispatcher> entry = taskIterator.next();
            String tag = entry.getKey();
            if (filterTags.contains(tag)) {
                NextDispatcher dispatcher = entry.getValue();
                if (dispatcher != null) {
                    dispatcher.cancel();
                }
                synchronized (mLock) {
                    taskIterator.remove();
                }
            }
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
            LogUtils.v(TAG, "cancelAll() tag=" + tag);
        }
        NextDispatcher dispatcher = mDispatchers.remove(tag);
        if (dispatcher != null) {
            dispatcher.cancel();
            return true;
        }
        return false;
    }

    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    public <Caller> int cancelAll(Caller caller) {
        int cancelledCount = 0;
        String tagPrefix = buildTagPrefix(caller).toString();
        if (mDebug) {
            LogUtils.v(TAG, "cancelAll() caller=" + caller);
        }
//        List<String> filterTags = new ArrayList<String>();
        Set<String> keySet = mDispatchers.keySet();
        for (String tag : keySet) {
            if (tag.startsWith(tagPrefix)) {
//                filterTags.add(tag);
                cancel(tag);
                ++cancelledCount;
            }
        }
//        cancelByTags(filterTags);
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
            mDispatchers.remove(tag);
        }
    }

    /**
     * 执行某个任务
     *
     * @param tag      任务TAG
     * @param runnable 任务Runnable
     * @return 任务TAG
     */
    private String execute(final String tag, final NextDispatcher runnable) {
        if (mDebug) {
            LogUtils.v(TAG, "add() tag=" + tag + " runnable=" + runnable);
        }
        synchronized (mLock) {
            Future<?> future = submit(runnable);
            runnable.setFuture(future);
            mDispatchers.put(tag, runnable);
        }
        return tag;
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
     * 回调，任务执行成功
     * 注意：回调函数在UI线程运行
     *
     * @param result   任务执行结果
     * @param callback 任务回调接口
     * @param <Result> 类型参数，任务结果类型
     */
    private <Result> void dispatchTaskSuccess(final Result result, final ResultCallback<Result> callback) {
        if (mDebug) {
            LogUtils.v(TAG, "dispatchTaskSuccess() result=" + result + " callback=" + callback);
        }
        dispatchOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResultSuccess(result, null, null);
                }
            }
        });
    }

    /**
     * 回调，任务执行失败
     * 注意：回调函数在UI线程运行
     *
     * @param exception 失败原因，异常
     * @param callback  任务回调接口
     * @param <Result>  类型参数，任务结果类型
     */
    private <Result> void dispatchTaskFailure(final Exception exception, final ResultCallback<Result> callback) {
        if (mDebug) {
            LogUtils.v(TAG, "dispatchTaskFailure() exception=" + exception + " callback=" + callback);
        }
        dispatchOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResultFailure(exception, null);
                }
            }
        });
    }

    private void dispatchOnUiThread(final Runnable runnable) {
        ensureHandler();
        mUiHandler.post(runnable);
    }

    /**
     * 资源清理函数，在每个任务执行完成后从队列移除
     * 注意：此函数在UI线程运行，是为了避免多线程问题
     *
     * @param tag 任务TAG
     */
    private void handleFinally(final String tag) {
        if (mDebug) {
            LogUtils.v(TAG, "handleFinally() tag=" + tag);
        }

        dispatchOnUiThread(new Runnable() {
            @Override
            public void run() {
                remove(tag);
            }
        });
    }

    /**
     * 检查并初始化ExecutorService
     *
     * @return ExecutorService
     */
    private ExecutorService ensureExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
        return mExecutor;
    }

    /**
     * 检查并初始化Handler
     */
    private void ensureHandler() {
        if (mDebug) {
            LogUtils.v(TAG, "ensureHandler()");
        }
        if (mUiHandler == null) {
            mUiHandler = new Handler(Looper.getMainLooper());
        }
    }

    /**
     * 关闭Executor
     */
    private void destroyExecutor() {
        if (mDebug) {
            LogUtils.v(TAG, "destroyExecutor()");
        }
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
    }

    /**
     * 关闭Handler
     */
    private void destroyHandler() {
        if (mDebug) {
            LogUtils.v(TAG, "destroyHandler()");
        }
        synchronized (mLock) {
            if (mUiHandler != null) {
                mUiHandler.removeCallbacksAndMessages(null);
                mUiHandler = null;
            }
        }
    }

    /**
     * 根据Caller生成对应的TAG，完整类名+hashcode+timestamp
     *
     * @param caller 调用对象
     * @return 任务的TAG
     */
    private static <Caller> String buildTag(Caller caller) {
        long timestamp = System.currentTimeMillis();
        return buildTagPrefix(caller).append(timestamp).toString();
    }

    /**
     * 根据Caller生成TAG前缀，方法是完整类名+hashcode
     *
     * @param caller   调用对象
     * @param <Caller> 类型参数，调用对象类型
     * @return TAG前缀（StringBuilder对象）
     */
    private static <Caller> StringBuilder buildTagPrefix(Caller caller) {
        String className = caller.getClass().getName();
        long hashCode = System.identityHashCode(caller);
        StringBuilder builder = new StringBuilder();
        builder.append(hashCode).append(SEPARATOR).append(className).append(SEPARATOR);
        return builder;
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

    abstract static class NextDispatcher implements Runnable {

        public static final String TAG = NextDispatcher.class.getSimpleName();

        private String mName;
        private Callable<?> mCallable;
        private Future<?> mFuture;
        private WeakReference<?> mWeakTarget;
        private boolean mCancelled;

        public NextDispatcher() {
            this(TAG + ":" + System.currentTimeMillis());
        }

        public NextDispatcher(String name) {
            this.mName = name;
        }

        public boolean cancel() {
            mCancelled = true;
            boolean result = false;
            if (mFuture != null) {
                result = mFuture.cancel(true);
            }
            return result;
        }

        public boolean isActive() {
            return !isInactive();
        }

        private boolean isInactive() {
            return mFuture == null ||
                    mFuture.isCancelled() ||
                    mFuture.isDone();
        }

        public boolean isCancelled() {
            return mCancelled;
        }

        public boolean isInterrupted() {
            return Thread.currentThread().isInterrupted();
        }

        public String getName() {
            return mName;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public void setFuture(Future<?> mFuture) {
            this.mFuture = mFuture;
        }

    }

    /**
     * 任务回调接口
     *
     * @param <Result> 类型参数，任务执行结果
     */
    public static interface ResultCallback<Result> {

        /**
         * 回调，任务执行完成
         *
         * @param result 执行结果
         * @param extras 附加结果，需要返回多种结果时会用到
         * @param object 附加结果，需要返回多种结果时会用到
         */
        public void onResultSuccess(Result result, Bundle extras, Object object);

        /**
         * 回调，任务执行失败
         *
         * @param e      失败原因，异常
         * @param extras 附加结果，需要返回额外的信息时会用到
         */
        public void onResultFailure(Throwable e, Bundle extras);

    }

    /**
     * 回调接口抽象类
     *
     * @param <Result> 类型参数，执行结果类型
     */
    public static abstract class SimpleResultCallback<Result> implements ResultCallback<Result> {

        @Override
        public void onResultSuccess(Result result, Bundle extras, Object object) {
        }

        @Override
        public void onResultFailure(Throwable e, Bundle extras) {
        }

    }

}
