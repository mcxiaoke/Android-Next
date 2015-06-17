package com.mcxiaoke.next.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.mcxiaoke.next.task.TaskQueue.TaskStatus;
import com.mcxiaoke.next.utils.AndroidUtils;
import com.mcxiaoke.next.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
final class RunnableImplA<Result> implements TaskRunnable {

    public static final String CLASS_TAG = RunnableImplA.class.getSimpleName();
    public static final String TAG = TaskQueue.TAG + "." + CLASS_TAG;
    private Handler mHandler;
    private TaskCallable<Result> mCallable;
    private TaskCallback<Result> mCallback;
    private Future<?> mFuture;
    private WeakReference<Object> mWeakCaller;

    private Result mResult;
    private Throwable mThrowable;

    private int mHashCode;
    private String mTag;

    private boolean mCheckCaller;
    private boolean mSerial;
    private boolean mCancelled;
    private boolean mDebug;

    private TaskStatus mStatus;

    private long mStartTime;
    private long mEndTime;

    RunnableImplA(final Handler handler, final boolean checkCaller, final boolean serial,
                  final TaskCallable<Result> callable,
                  final TaskCallback<Result> callback,
                  final Object caller, final boolean debug) {
        mHandler = handler;
        mCheckCaller = checkCaller;
        mSerial = serial;
        mCallable = callable;
        mCallback = callback;
        mWeakCaller = new WeakReference<Object>(caller);
        mHashCode = System.identityHashCode(caller);
        mTag = TaskHelper.buildTag(caller);
        mStatus = TaskStatus.IDLE;
        mDebug = debug;
        if (mDebug) {
            LogUtils.v(TAG, "TaskRunnable() tag=" + mTag + " serial=" + serial);
        }
    }

    // 重置所有字段
    private void reset() {
//        if (mDebug) {
//            LogUtils.v(TAG, "reset()");
//        }
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

    /**
     * 由于各种原因导致任务被取消
     * 原因：手动取消，线程中断，调用者不存在，回调接口不存在
     *
     * @return cancelled
     */
    private boolean isTaskCancelled() {
//        if (mDebug) {
//            final boolean cancelled = isCancelled();
//            final boolean interrupted = isInterrupted();
//            final boolean noCaller = mWeakCaller.get() == null;
//            final boolean noCallback = mCallback == null;
//            LogUtils.v(TAG, "isTaskCancelled() cancelled=" + cancelled
//                    + " interrupted=" + interrupted + " noCaller="
//                    + noCaller + " noCallback=" + noCallback + " tag=" + getTag());
//        }
        return isCancelled() || isInterrupted()
                || mWeakCaller == null || mCallback == null;
    }

    /**
     * 检查Caller的生命周期，是否Alive
     *
     * @return is active
     */
    @SuppressLint("NewApi")
    private boolean isCallerActive() {
        return !mCheckCaller || AndroidUtils.isActive(mWeakCaller.get());
    }

    @Override
    public void run() {
        mStatus = TaskStatus.RUNNING;
        if (mDebug) {
            LogUtils.v(TAG, "run() start, thread=" + Thread.currentThread().getName()
                    + " tag=" + getTag());
            mStartTime = SystemClock.elapsedRealtime();
        }
        preProcess();
        final Callable<Result> callable = mCallable;
        Result result = null;
        Throwable throwable = null;


        // check  task cancelled before execute
        boolean taskCancelled = isTaskCancelled();

        if (!taskCancelled) {
            try {
                result = callable.call();
            } catch (Throwable e) {
                throwable = e;
            }
        } else {
            if (mDebug) {
                LogUtils.v(TAG, "run() task is cancelled, ignore task, thread="
                        + Thread.currentThread().getName() + " tag=" + getTag());
            }
        }

        // check task cancelled after task execute
        if (!taskCancelled) {
            taskCancelled = isTaskCancelled();
        }

        mThrowable = throwable;
        mResult = result;
        postProcess();
        notifyDone();

        // if task not cancelled and caller alive, notify callback
        final boolean callerAlive = isCallerActive();
        if (!taskCancelled && callerAlive) {
            if (throwable != null) {
                notifyFailure(throwable);
            } else {
                notifySuccess(result);
            }
        }

        onFinally();
        if (mDebug) {
            mEndTime = SystemClock.elapsedRealtime();
            LogUtils.v(TAG, "run() end taskCancelled=" + taskCancelled
                    + " callerAlive=" + callerAlive
                    + " thread=" + Thread.currentThread().getName());
            LogUtils.v(TAG, "run() end duration:" + getDuration() + "ms tag=" + getTag());
        }
        mStatus = throwable == null ? TaskStatus.SUCCESS : TaskStatus.FAILURE;
    }

    @Override
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

    @Override
    public void setFuture(Future<?> future) {
        mFuture = future;
    }

    public Result getResult() {
        return mResult;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    @Override
    public int getHashCode() {
        return mHashCode;
    }

    @Override
    public String getTag() {
        return mTag;
    }

    public long getDuration() {
        return mEndTime - mStartTime;
    }

    public String getStatus() {
        return mStatus.name();
    }

    @Override
    public boolean isRunning() {
        return mStatus == TaskStatus.RUNNING;
    }

    @Override
    public boolean isSerial() {
        return mSerial;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    private void preProcess() {
        final String tag = getTag();
        if (mDebug) {
            LogUtils.v(TAG, "preProcess() tag=" + tag);
        }
        final TaskCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        if (callback != null) {
            callback.onTaskStarted(tag, callable.getExtras());
        }
    }

    private void postProcess() {
        final String tag = getTag();
        if (mDebug) {
            LogUtils.v(TAG, "postProcess() tag=" + tag);
        }
        final TaskCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        final Result result = mResult;
        if (callback != null) {
            callback.onTaskFinished(result, callable.getExtras());
        }
    }

    /**
     * 回调，任务执行成功
     * 注意：回调函数在UI线程运行
     *
     * @param result 任务执行结果
     */
    private void notifySuccess(final Result result) {
        if (mDebug) {
            LogUtils.v(TAG, "notifySuccess() tag=" + getTag());
        }
        final TaskCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTaskSuccess(result, callable.getExtras());
                }
            }
        });
    }

    /**
     * 回调，任务执行失败
     * 注意：回调函数在UI线程运行
     *
     * @param exception 失败原因，异常
     */
    private void notifyFailure(final Throwable exception) {
        if (mDebug) {
            LogUtils.e(TAG, "notifyFailure() exception=" + exception + " tag=" + getTag());
        }
        final TaskCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTaskFailure(exception, callable.getExtras());
                }
            }
        });
    }

    private void notifyDone() {
//        if (mDebug) {
//            LogUtils.v(TAG, "notifyDone() tag=" + getTag());
//        }
        final Handler handler = mHandler;
        final String tag = mTag;
        if (handler != null) {
            Message message = handler.obtainMessage(TaskQueue.MSG_TASK_DONE, tag);
            handler.sendMessage(message);
        }
    }

    private void onFinally() {
//        if (mDebug) {
//            LogUtils.v(TAG, "onFinally()");
//        }
        reset();
    }

    private void postRunnable(final Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    @Override
    public String toString() {
        return "TaskRunnable{" +
                "mFuture=" + mFuture +
                ", mResult=" + mResult +
                ", mThrowable=" + mThrowable +
                ", mHashCode=" + mHashCode +
                ", mTag='" + mTag + '\'' +
                ", mCheckCaller=" + mCheckCaller +
                ", mSerial=" + mSerial +
                ", mCancelled=" + mCancelled +
                ", mDebug=" + mDebug +
                ", mStatus=" + mStatus +
                ", mStartTime=" + mStartTime +
                ", mEndTime=" + mEndTime +
                '}';
    }

}
