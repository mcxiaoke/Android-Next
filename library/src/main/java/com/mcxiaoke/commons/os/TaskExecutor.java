package com.mcxiaoke.commons.os;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.commons.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 一个用于执行异步任务的类，单例，支持检查Caller，支持按照Caller和Tag取消对应的任务
 * User: mcxiaoke
 * Date: 2013-7-1 - 2013-7-25
 * Time: 下午10:24
 */
public final class TaskExecutor {
    public static final String SEPARATOR = "@@";
    public static final String TAG = TaskExecutor.class.getSimpleName();

    private final Object mLock = new Object();

    private ExecutorService mExecutor;
    private Handler mUiHandler;
    private Map<String, ExtendedRunnable> mTasks;
    private Map<String, Future<?>> mFutures;

    private boolean mDebug;

    // 延迟加载
    private static final class SingletonHolder {
        static final TaskExecutor INSTANCE = new TaskExecutor();
    }

    public static TaskExecutor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private TaskExecutor() {
        if (mDebug) {
            LogUtils.v(TAG, "TaskExecutor()");
        }
        mTasks = new ConcurrentHashMap<String, ExtendedRunnable>();
        mFutures = new ConcurrentHashMap<String, Future<?>>();
        ensureHandler();
        ensureExecutor();
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
    public <Result, Caller> String execute(final Callable<Result> callable, final TaskCallback<Result> callback, final Caller caller) {
        checkArguments(callable, caller);
        // 保存Caller对象的WeakReference，用于后面检查Caller是否存在
        final WeakReference<Caller> weakTarget = new WeakReference<Caller>(caller);
        final String tag = buildTag(caller);
        if (mDebug) {
            LogUtils.v(TAG, "execute() callable=" + callable + " callback=" + callback + " caller=" + caller);
        }
        final ExtendedRunnable runnable = new ExtendedRunnable(tag) {
            @Override
            public void run() {
                try {
                    if (mDebug) {
                        LogUtils.v(TAG, "execute() start");
                    }
                    Result result = callable.call();

                    if (isCancelled()) {
                        if (mDebug) {
                            LogUtils.v(TAG, "execute() isCancelled, return");
                        }
                        return;
                    }
                    if (isInterrupted()) {
                        if (mDebug) {
                            LogUtils.v(TAG, "execute() isInterrupted, return");
                        }
                        return;
                    }

                    // Caller不存在了，不需要执行回调函数
                    // 典型情况如View/Fragment/Activity已经销毁了
                    if (weakTarget.get() == null) {
                        if (mDebug) {
                            LogUtils.v(TAG, "execute() caller is null, return");
                        }
                        return;
                    }
                    onTaskSuccess(result, callback);
                } catch (Exception e) {
                    if (mDebug) {
                        e.printStackTrace();
                        LogUtils.e(TAG, "execute() error: " + e);
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
                    onTaskFailure(e, callback);
                } finally {
                    onFinally(tag);
                }
                if (mDebug) {
                    LogUtils.v(TAG, "execute() end");
                }
            }
        };
        return doExecute(tag, runnable);
    }


    /**
     * 检查某个任务是否正在运行
     *
     * @param tag 任务的TAG
     * @return 是否正在运行
     */
    public boolean isRunning(String tag) {
        Future<?> future = mFutures.get(tag);
        return future != null && !future.isDone() && !future.isCancelled();
    }

    /**
     * 便利任务列表，取消所有任务
     */
    public void cancelAll() {
        if (mDebug) {
            LogUtils.v(TAG, "cancelAll()");
        }
        cancelAllRunnables();
        cancelAllFutures();
    }

    /**
     * 取消所有的Runnable对应的任务
     */
    // for循环不能修改内容，多线程时会有问题，故使用Iterator
    private void cancelAllRunnables() {
        Set<Map.Entry<String, ExtendedRunnable>> taskEntrySet = mTasks.entrySet();
        Iterator<Map.Entry<String, ExtendedRunnable>> taskIterator = taskEntrySet.iterator();
        while (taskIterator.hasNext()) {
            Map.Entry<String, ExtendedRunnable> entry = taskIterator.next();
            ExtendedRunnable runnable = entry.getValue();
            if (runnable != null) {
                runnable.cancel();
            }
        }
        mTasks.clear();
    }

    /**
     * 取消所有的Future的对应的任务
     */
    private void cancelAllFutures() {
        Set<Map.Entry<String, Future<?>>> futureEntrySet = mFutures.entrySet();
        Iterator<Map.Entry<String, Future<?>>> futureIterator = futureEntrySet.iterator();
        while (futureIterator.hasNext()) {
            Map.Entry<String, Future<?>> entry = futureIterator.next();
            Future<?> future = entry.getValue();
            if (future != null) {
                future.cancel(true);
            }
        }
        mFutures.clear();
    }


    /**
     * 取消一组Runnable对应的任务
     *
     * @param filterTags 过滤TAG列表
     */
    // for循环不能修改内容，多线程时会有问题，故使用Iterator
    private void cancelRunnablesByTags(Collection<String> filterTags) {
        if (filterTags == null || filterTags.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, ExtendedRunnable>> taskEntrySet = mTasks.entrySet();
        Iterator<Map.Entry<String, ExtendedRunnable>> taskIterator = taskEntrySet.iterator();
        while (taskIterator.hasNext()) {
            Map.Entry<String, ExtendedRunnable> entry = taskIterator.next();
            String tag = entry.getKey();
            if (filterTags.contains(tag)) {
                ExtendedRunnable runnable = entry.getValue();
                if (runnable != null) {
                    runnable.cancel();
                }
                taskIterator.remove();
            }
        }
    }

    /**
     * 取消一组Future的对应的任务
     *
     * @param filterTags 过滤TAG列表
     */
    private void cancelFuturesByTags(Collection<String> filterTags) {
        if (filterTags == null || filterTags.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, Future<?>>> futureEntrySet = mFutures.entrySet();
        Iterator<Map.Entry<String, Future<?>>> futureIterator = futureEntrySet.iterator();
        while (futureIterator.hasNext()) {
            Map.Entry<String, Future<?>> entry = futureIterator.next();
            String tag = entry.getKey();
            if (filterTags.contains(tag)) {
                Future<?> future = entry.getValue();
                if (future != null) {
                    future.cancel(true);
                }
                futureIterator.remove();
            }
        }
    }


    /**
     * 取消TAG对应的任务
     *
     * @param tag 任务TAG
     * @return 任务是否存在
     */
    public boolean cancelByTag(String tag) {
        if (mDebug) {
            LogUtils.v(TAG, "cancelByCaller() tag=" + tag);
        }
        ExtendedRunnable runnable = mTasks.remove(tag);
        if (runnable != null) {
            runnable.cancel();
        }
        Future<?> future = mFutures.remove(tag);
        if (future != null) {
            future.cancel(true);
            return true;
        }
        return false;
    }

    /**
     * 取消TAGS对应的任务列表
     *
     * @param tags TAGS
     * @return 任务数量
     */
    public int cancelByTags(Collection<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return 0;
        }
        cancelRunnablesByTags(tags);
        cancelFuturesByTags(tags);
        return tags.size();
    }

    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    public <Caller> int cancelByCaller(Caller caller) {
        int cancelledCount = 0;
        String tagPrefix = buildTagPrefix(caller).toString();
        if (mDebug) {
            LogUtils.v(TAG, "cancelByCaller() caller=" + caller);
        }
        List<String> filterTags = new ArrayList<String>();
        Set<String> keySet = mFutures.keySet();
        for (String tag : keySet) {
            if (tag.startsWith(tagPrefix)) {
                filterTags.add(tag);
                ++cancelledCount;
            }
        }

        cancelRunnablesByTags(filterTags);
        cancelFuturesByTags(filterTags);

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
            mTasks.remove(tag);
            mFutures.remove(tag);
        }
    }

    /**
     * 执行某个任务
     *
     * @param tag      任务TAG
     * @param runnable 任务Runnable
     * @return 任务TAG
     */
    private String doExecute(final String tag, final ExtendedRunnable runnable) {
        if (mDebug) {
            LogUtils.v(TAG, "doExecute() tag=" + tag + " runnable=" + runnable);
        }
        synchronized (mLock) {
            Future<?> future = doSubmit(runnable);
            mTasks.put(tag, runnable);
            mFutures.put(tag, future);
        }
        return tag;
    }

    /**
     * 将任务添加到线程池执行
     *
     * @param runnable 任务Runnable
     * @return 返回任务对应的Future对象
     */
    private Future<?> doSubmit(final Runnable runnable) {
        if (mDebug) {
            String name = "Runnable";
            if (runnable instanceof ExtendedRunnable) {
                name = ((ExtendedRunnable) runnable).getName();
            }
            LogUtils.v(TAG, "submit() name=" + name);
        }
        ensureHandler();
        ensureExecutor();
        return mExecutor.submit(runnable);
    }

    /**
     * 回调，任务执行成功
     * 注意：回调函数在UI线程运行
     *
     * @param result   任务执行结果
     * @param callback 任务回调接口
     * @param <Result> 类型参数，任务结果类型
     */
    private <Result> void onTaskSuccess(final Result result, final TaskCallback<Result> callback) {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskComplete() result=" + result + " callback=" + callback);
        }
        if (callback != null) {
            ensureHandler();
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onTaskSuccess(result, null, null);
                }
            });
        }
    }

    /**
     * 回调，任务执行失败
     * 注意：回调函数在UI线程运行
     *
     * @param exception 失败原因，异常
     * @param callback  任务回调接口
     * @param <Result>  类型参数，任务结果类型
     */
    private <Result> void onTaskFailure(final Exception exception, final TaskCallback<Result> callback) {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskComplete() exception=" + exception + " callback=" + callback);
        }
        if (callback != null) {
            ensureHandler();
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onTaskFailure(exception, null);
                }
            });
        }
    }

    /**
     * 资源清理函数，在每个任务执行完成后从队列移除
     * 注意：此函数在UI线程运行，是为了避免多线程问题
     *
     * @param tag 任务TAG
     */
    private void onFinally(final String tag) {
        if (mDebug) {
            LogUtils.v(TAG, "onFinally() tag=" + tag);
        }

        ensureHandler();
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mLock) {
                    remove(tag);
                }
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
     * 根据Caller生成对应的TAG
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

    /**
     * 扩展版的Runnable类，添加了名字定义和cancelled标志
     */
    public static abstract class ExtendedRunnable implements Runnable {
        public static final String TAG = ExtendedRunnable.class.getSimpleName();
        private String mName;
        private boolean mCancelled;

        public ExtendedRunnable() {
            this(TAG);
        }

        public ExtendedRunnable(String name) {
            mName = name;
            mCancelled = false;
        }

        public boolean isInterrupted() {
            return Thread.currentThread().isInterrupted();
        }

        public void cancel() {
            mCancelled = true;
        }

        public boolean isCancelled() {
            return mCancelled;
        }

        public String getName() {
            return mName;
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
        public void onTaskFailure(Throwable e, Bundle extras);

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
        public void onTaskFailure(Throwable e, Bundle extras) {
        }

    }

    /**
     * 辅助类，可直接使用
     */
    public static class BooleanTaskCallback extends SimpleTaskCallback<Boolean> {

    }

    /**
     * 辅助类，可直接使用
     */
    public static class StringTaskCallback extends SimpleTaskCallback<String> {

    }

    /**
     * 辅助类，可直接使用
     */
    public static class BitmapTaskCallback extends SimpleTaskCallback<Bitmap> {

    }

}
