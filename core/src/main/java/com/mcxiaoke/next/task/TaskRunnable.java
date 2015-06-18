package com.mcxiaoke.next.task;

import android.os.SystemClock;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
final class TaskRunnable<Result> implements ITaskRunnable {
    private static final String TAG = "TaskQueue.Runnable";

    private Future<?> mFuture;
    private boolean mDebug;

    private Task<Result> mTask;
    private TaskStatus<Result> mStatus;

    TaskRunnable(final Task<Result> task, final boolean debug) {
        mTask = task;
        mStatus = new TaskStatus<Result>(task.getTag());
        mDebug = debug;
        if (mDebug) {
            LogUtils.v(TAG, "TaskRunnable() task=" + task);
        }
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
            LogUtils.v(TAG, "run() start, thread=" + Thread.currentThread().getName());
        }
        onTaskStarted();
        Result result = null;
        Throwable error = null;
        if (!isTaskCancelled()) {
            try {
                result = mTask.call();
            } catch (Exception e) {
                error = e;
            }
        } else {
            if (mDebug) {
                LogUtils.v(TAG, "run() task is cancelled, ignore task, thread="
                        + Thread.currentThread().getName());
            }
        }
        mStatus.endTime = SystemClock.elapsedRealtime();
        mStatus.data = result;
        mStatus.error = error;
        if (isTaskCancelled()) {
            onTaskCancelled();
        } else {
            mStatus.status = error == null ? TaskStatus.SUCCESS : TaskStatus.FAILURE;
            onTaskFinished();
            if (error != null) {
                onTaskFailure(error);
            } else {
                onTaskSuccess(result);
            }
        }
        if (mDebug) {
            LogUtils.v(TAG, "run() end duration:" + mStatus.getDuration() + "ms");
        }
        onDone();
    }

    @Override
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

    @Override
    public void setFuture(final Future<?> future) {
        mFuture = future;
    }

    private boolean isCancelled() {
        return mStatus.isCancelled();
    }

    private boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    private void onTaskStarted() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskStarted()");
        }
        mTask.onStarted(mStatus);
    }

    private void onTaskCancelled() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskCancelled()");
        }
        mTask.onCancelled(mStatus);
    }


    private void onTaskFinished() {
        if (mDebug) {
            LogUtils.v(TAG, "onTaskFinished()");
        }
        mTask.onFinished(mStatus);
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
        mTask.onSuccess(mStatus);
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
        mTask.onFailure(mStatus);
    }

    private void onDone() {
        mTask.onDone(mStatus);
    }
}
