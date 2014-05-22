package com.mcxiaoke.next.os;

import android.os.Handler;
import android.os.SystemClock;
import com.mcxiaoke.next.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
class NextRunnable<Result, Caller> implements Runnable {

    public static final String TAG = NextRunnable.class.getSimpleName();
    public static final String SEPARATOR = "::";

    private Handler mHandler;
    private NextCallable<Result> mCallable;
    private TaskCallback<Result> mCallback;
    private Future<?> mFuture;
    private WeakReference<Caller> mWeakCaller;

    private Result mResult;
    private Throwable mThrowable;

    private int mHashCode;
    private String mTag;

    private boolean mSerial;
    private boolean mCancelled;
    private boolean mDebug;

    public NextRunnable(final Handler handler, final boolean serial,
                        final NextCallable<Result> callable,
                        final TaskCallback<Result> callback,
                        final Caller caller) {
        mHandler = handler;
        mSerial = serial;
        mCallable = callable;
        mCallback = callback;
        mWeakCaller = new WeakReference<Caller>(caller);
        mHashCode = System.identityHashCode(caller);
        mTag = buildTag(caller);
        if (mDebug) {
            LogUtils.v(TAG, "NextRunnable() hashCode=" + mHashCode + " tag=" + mTag);
        }
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
    }


    // 重置所有字段
    private void reset() {
        if (mDebug) {
            LogUtils.v(TAG, "reset()");
        }
        mHandler = null;
        mCallback = null;
        mCallable = null;
        mFuture = null;
        mWeakCaller = null;
        mResult = null;
        mThrowable = null;
//            mHashCode=0;
//            mTag=null;
    }

    private boolean isDiscard() {
        return isCancelled() || isInterrupted()
                || mWeakCaller.get() == null || mCallback == null;
    }

    @Override
    public void run() {
        final Callable<Result> callable = mCallable;
        Result result = null;
        Throwable throwable = null;

        if (isDiscard()) {
            if (mDebug) {
                LogUtils.v(TAG, "discard result, return");
            }
            return;
        }

        try {
            result = callable.call();
        } catch (Throwable e) {
            throwable = e;
        }

        if (isDiscard()) {
            if (mDebug) {
                LogUtils.v(TAG, "discard result, return");
            }
            return;
        }

        mResult = result;
        mThrowable = throwable;

        onDone();
        if (throwable != null) {
            onFailure(throwable);
        } else {
            onSuccess(result);
        }

        onFinally();
    }

    public boolean cancel() {
        if (mDebug) {
            LogUtils.v(TAG, "cancel()");
        }
        mCancelled = true;
        boolean result = false;
        if (mFuture != null) {
            result = mFuture.cancel(true);
        }
        return result;
    }

    public Future<?> getFuture() {
        return mFuture;
    }

    public Result getResult() {
        return mResult;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public int getHashCode() {
        return mHashCode;
    }

    public String getTag() {
        return mTag;
    }

    public void setFuture(Future<?> future) {
        mFuture = future;
    }

    public boolean isActive() {
        return !isInactive();
    }

    private boolean isInactive() {
        return mFuture == null ||
                mFuture.isCancelled() ||
                mFuture.isDone();
    }

    public boolean isSerial() {
        return mSerial;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * 回调，任务执行成功
     * 注意：回调函数在UI线程运行
     *
     * @param result   任务执行结果
     * @param callback 任务回调接口
     * @param <Result> 类型参数，任务结果类型
     */
    private void onSuccess(final Result result) {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskSuccess()");
        }
        final NextCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTaskSuccess(result, callable.mMessage);
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
    private void onFailure(final Throwable exception) {
        if (mDebug) {
            LogUtils.e(TAG, "onTaskFailure() exception=" + exception);
        }
        final NextCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTaskFailure(exception, callable.mMessage);
                }
            }
        });
    }

    private void onDone() {
        if (mDebug) {
            LogUtils.v(TAG, "onDone()");
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(NextExecutor.MSG_REMOVE_TASK_BY_TAG);
        }
    }

    private void onFinally() {
        reset();
    }

    private void postRunnable(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("NextRunnable{");
        builder.append("mResult=").append(mResult);
        builder.append(", mThrowable=").append(mThrowable);
        builder.append(", mHashCode=").append(mHashCode);
        builder.append(", mTag='").append(mTag).append('\'');
        builder.append(", mSerial=").append(mSerial);
        builder.append(", mCancelled=").append(mCancelled);
        builder.append(", mDebug=").append(mDebug);
        builder.append(", mCallback=").append(mCallback);
        builder.append('}');
        return builder.toString();
    }


    private static AtomicInteger mSequenceNumber = new AtomicInteger(0);

    static int getSequenceNumber() {
        return mSequenceNumber.getAndIncrement();
    }

    /**
     * 根据Caller生成对应的TAG，完整类名+hashcode+timestamp
     *
     * @param caller 调用对象
     * @return 任务的TAG
     */
    static <Caller> String buildTag(Caller caller) {
        // caller的key是hashcode
        // tag的组成:className+hashcode+sequenceNumber+timestamp
        final int hashCode = System.identityHashCode(caller);
        final String className = caller.getClass().getName();
        final int sequenceNumber = getSequenceNumber();
        final long timestamp = SystemClock.elapsedRealtime();

        StringBuilder builder = new StringBuilder();
        builder.append(System.identityHashCode(caller)).append(SEPARATOR);
        builder.append(className).append(SEPARATOR);
        builder.append(timestamp).append(SEPARATOR);
        builder.append(sequenceNumber).append(SEPARATOR);
        return builder.toString();
    }
}
