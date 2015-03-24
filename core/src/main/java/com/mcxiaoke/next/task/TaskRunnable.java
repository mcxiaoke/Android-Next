package com.mcxiaoke.next.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.mcxiaoke.next.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
final class TaskRunnable<Result> implements Runnable {

    public static final String CLASS_TAG = TaskRunnable.class.getSimpleName();
    public static final String TAG = TaskQueue.TAG + "." + CLASS_TAG;
    public static final String SEPARATOR = "::";
    private static volatile int sSequence = 0;
    private Handler mHandler;
    private TaskCallable<Result> mCallable;
    private TaskCallback<Result> mCallback;
    private Future<?> mFuture;
    private WeakReference<Object> mWeakCaller;

    private Result mResult;
    private Throwable mThrowable;

    private int mSequence;
    private int mHashCode;
    private String mTag;

    private boolean mCheckCaller;
    private boolean mSerial;
    private boolean mCancelled;
    private boolean mDebug;

    private TaskStatus mStatus;

    private long mStartTime;
    private long mEndTime;

    TaskRunnable(final Handler handler, final boolean checkCaller, final boolean serial,
                 final TaskCallable<Result> callable,
                 final TaskCallback<Result> callback,
                 final Object caller) {
        mHandler = handler;
        mCheckCaller = checkCaller;
        mSerial = serial;
        mCallable = callable;
        mCallback = callback;
        mWeakCaller = new WeakReference<Object>(caller);
        mSequence = incSequence();
        mHashCode = System.identityHashCode(caller);
        mTag = buildTag(caller);
        mStatus = TaskStatus.IDLE;
        if (mDebug) {
            LogUtils.v(TAG, "TaskRunnable() tag=" + mTag + " serial=" + serial);
        }
    }

    static int incSequence() {
        return ++sSequence;
    }

    public void setDebug(boolean debug) {
        mDebug = debug;
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
    private boolean isCallerAlive() {
        if (!mCheckCaller) {
            return true;
        }
        final Object caller = mWeakCaller.get();
        if (caller == null) {
            return false;
        }
        if (caller instanceof Activity) {
            return !((Activity) caller).isFinishing();
        }

        if (caller instanceof Fragment) {
            return ((Fragment) caller).isAdded();
        }

        return isAddedCompat(caller);
    }

    private boolean isAddedCompat(final Object caller) {
        try {
            final Class<?> fragmentClass = Class.forName("android.support.v4.app.Fragment");
            final Class<?> clazz = caller.getClass();
            if (caller == fragmentClass) {
                final Method method = clazz.getMethod("isAdded", clazz);
                return (boolean) method.invoke(caller);
            }
        } catch (InvocationTargetException e) {
            if (mDebug) {
                LogUtils.e(TAG, "isFragmentAdded() ex=" + e);
            }
        } catch (NoSuchMethodException e) {
            if (mDebug) {
                LogUtils.e(TAG, "isFragmentAdded() ex=" + e);
            }
        } catch (IllegalAccessException e) {
            if (mDebug) {
                LogUtils.e(TAG, "isFragmentAdded() ex=" + e);
            }
        } catch (ClassNotFoundException e) {
            if (mDebug) {
                LogUtils.e(TAG, "isFragmentAdded() ex=" + e);
            }
        }

        return false;
    }

    @Override
    public void run() {
        mStatus = TaskStatus.RUNNING;
        if (mDebug) {
            LogUtils.v(TAG, "run() start seq=" + getSequence()
                    + " thread=" + Thread.currentThread().getName() + " tag=" + getTag());
            mStartTime = SystemClock.elapsedRealtime();
        }

        notifyStarted();

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
                LogUtils.v(TAG, "run() task is cancelled, ignore task, seq="
                        + getSequence() + " thread="
                        + Thread.currentThread().getName() + " tag=" + getTag());
            }
        }

        // check task cancelled after task execute
        if (!taskCancelled) {
            taskCancelled = isTaskCancelled();
        }

        mResult = result;
        mThrowable = throwable;


        notifyDone();

        // if task not cancelled and caller alive, notify callback
        final boolean callerAlive = isCallerAlive();
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
                    + " seq=" + getSequence() + " callerAlive=" + callerAlive
                    + " thread=" + Thread.currentThread().getName());
            LogUtils.v(TAG, "run() end duration:" + getDuration() + "ms tag=" + getTag());
        }
        mStatus = TaskStatus.DONE;
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

    public void setFuture(Future<?> future) {
        mFuture = future;
    }

    public Result getResult() {
        return mResult;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public int getSequence() {
        return mSequence;
    }

    public int getHashCode() {
        return mHashCode;
    }

    public String getTag() {
        return mTag;
    }

    public long getDuration() {
        return mEndTime - mStartTime;
    }

    public String getStatus() {
        return mStatus.name();
    }

    public boolean isRunning() {
        return mStatus == TaskStatus.RUNNING;
    }

    private boolean isIdle() {
        return mStatus == TaskStatus.IDLE;
    }

    private boolean isDone() {
        return mStatus == TaskStatus.DONE;
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

    private void notifyStarted() {
        if (mDebug) {
            LogUtils.v(TAG, "notifyStarted() tag=" + getTag());
        }
        final String tag = getTag();
        final TaskCallable<Result> callable = mCallable;
        final TaskCallback<Result> callback = mCallback;
        postRunnable(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTaskStarted(tag, callable.getExtras());
                }
            }
        });
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
                ", mSequence=" + mSequence +
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

    /**
     * 根据Caller生成对应的TAG，hashcode+类名+timestamp+seq
     *
     * @param caller 调用对象
     * @return 任务的TAG
     */
    private String buildTag(final Object caller) {
        // caller的key是hashcode
        // tag的组成:className+hashcode+timestamp+seq
        final int sequence = mSequence;
        final int hashCode = mHashCode;
        final String className = caller.getClass().getSimpleName();
        final long timestamp = SystemClock.elapsedRealtime();

//        if (mDebug) {
//            LogUtils.v(TAG, "buildTag() class=" + className + " seq=" + sequence);
//        }

        StringBuilder builder = new StringBuilder();
        builder.append(className).append(SEPARATOR);
        builder.append(hashCode).append(SEPARATOR);
        builder.append(timestamp).append(SEPARATOR);
        builder.append(sequence);
        return builder.toString();
    }

    static enum TaskStatus {
        IDLE, RUNNING, DONE
    }
}
