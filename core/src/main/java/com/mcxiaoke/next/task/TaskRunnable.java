package com.mcxiaoke.next.task;

import android.os.SystemClock;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
final class TaskRunnable<Result> implements Runnable {
    private static final String CLASS_TAG = TaskRunnable.class.getSimpleName();
    private static final String TAG = TaskQueue.TAG;

    private Future<?> mFuture;
    private boolean mDebug;

    private Task<Result> mTaskInfo;
    private TaskStatus<Result> mStatus;

    TaskRunnable(final Task<Result> task, final boolean debug) {
        mTaskInfo = task;
        mStatus = new TaskStatus<Result>(task.mTag);
        mDebug = debug;
        if (mDebug) {
            LogUtils.v(TAG, "TaskRunnable() task=" + task);
        }
    }

    void execute(final ExecutorService executor) {
        mFuture = executor.submit(this);
    }

    /**
     * 由于各种原因导致任务被取消
     * 原因：手动取消，线程中断
     *
     * @return cancelled
     */
    private boolean isTaskCancelled() {
        return isCancelled() || isInterrupted();
    }

    @Override
    public void run() {
        mStatus.status = TaskStatus.RUNNING;
        mStatus.startTime = SystemClock.elapsedRealtime();
        if (mDebug) {
            LogUtils.v(TAG, "run() start, thread=" + Thread.currentThread().getName()
                    + " tag=" + mStatus.tag);
        }
        onTaskStarted();
        Result result = null;
        Throwable error = null;
        if (!isTaskCancelled()) {
            try {
                result = mTaskInfo.call();
            } catch (Exception e) {
                error = e;
            }
        } else {
            if (mDebug) {
                LogUtils.v(TAG, "run() task is cancelled, ignore task, thread="
                        + Thread.currentThread().getName() + " tag=" + mStatus.tag);
            }
        }
        mStatus.endTime = SystemClock.elapsedRealtime();
        mStatus.data = result;
        mStatus.error = error;

        if (isTaskCancelled()) {
            onTaskCancelled();
        } else {
            onTaskFinished();
            if (error != null) {
                onTaskFailure(error);
            } else {
                onTaskSuccess(result);
            }
        }
        if (!mStatus.isCancelled()) {
            mStatus.status = error == null ? TaskStatus.SUCCESS : TaskStatus.FAILURE;
        }
        if (mDebug) {
            LogUtils.v(TAG, "run() end duration:" + mStatus.getDuration() + "ms tag=" + mStatus.tag);
        }
        onDone();
    }

    public boolean cancel() {
        if (mDebug) {
            LogUtils.v(TAG, "cancel()");
        }
        mStatus.status = TaskStatus.CANCELLED;
        boolean result = false;
        if (mFuture != null) {
            result = mFuture.cancel(true);
        }
        return result;
    }

    public boolean isSerial() {
        return mTaskInfo.mSerial;
    }

    public boolean isRunning() {
        return mStatus.isRunning();
    }

    public boolean isCancelled() {
        return mStatus.isCancelled();
    }

    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    private void onTaskStarted() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskStarted()");
        }
        mTaskInfo.onStarted(mStatus);
    }

    private void onTaskCancelled() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskCancelled()");
        }
        mTaskInfo.onCancelled(mStatus);
    }


    private void onTaskFinished() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskFinished()");
        }
        mTaskInfo.onFinished(mStatus);
    }

    /**
     * 回调，任务执行成功
     *
     * @param result 任务执行结果
     */
    private void onTaskSuccess(final Result result) {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskSuccess()");
        }
        mTaskInfo.onSuccess(mStatus);
    }

    /**
     * 回调，任务执行失败
     *
     * @param ex 失败原因，异常
     */
    private void onTaskFailure(final Throwable ex) {
        if (mDebug) {
            LogUtils.e(TAG, "onTaskFailure() error=" + ex);
        }
        mTaskInfo.onFailure(mStatus);
    }

    private void onDone() {
        mTaskInfo.onDone(mStatus);
    }
}
