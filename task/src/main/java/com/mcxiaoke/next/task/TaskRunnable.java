package com.mcxiaoke.next.task;

import android.util.Log;

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
    private ITaskCallback<Result> mCallback;
    private boolean mCancelled;

    TaskRunnable(final ITaskCallback<Result> task) {
        this(task, false);
    }

    TaskRunnable(final ITaskCallback<Result> task, final boolean debug) {
        mCallback = task;
        mDebug = debug;
        if (mDebug) {
            Log.v(TAG, "TaskRunnable() task=" + task);
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
        onTaskStarted();
        Result result = null;
        Throwable error = null;
        if (!isTaskCancelled()) {
            try {
                result = mCallback.onExecute();
            } catch (Exception e) {
                error = e;
            }
        }
        if (isTaskCancelled()) {
            onTaskCancelled();
        } else {
            if (error != null) {
                onTaskFailure(error);
            } else {
                onTaskSuccess(result);
            }
            onTaskFinished();
        }
        onDone();
    }

    @Override
    public boolean cancel() {
        mCancelled = true;
        if (mDebug) {
            Log.v(TAG, "cancel()");
        }
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
        return mCancelled;
    }

    private boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    private void onTaskStarted() {
        if (mDebug) {
            Log.v(TAG, "onTaskStarted()");
        }
        mCallback.onStarted();
    }

    private void onTaskCancelled() {
        if (mDebug) {
            Log.v(TAG, "onTaskCancelled()");
        }
        mCallback.onCancelled();
    }


    private void onTaskFinished() {
        if (mDebug) {
            Log.v(TAG, "onTaskFinished()");
        }
        mCallback.onFinished();
    }

    /**
     * 回调，任务执行成功
     *
     * @param result 任务执行结果
     */
    private void onTaskSuccess(final Result result) {
        if (mDebug) {
            Log.v(TAG, "onTaskSuccess()");
        }
        mCallback.onSuccess(result);
    }

    /**
     * 回调，任务执行失败
     *
     * @param ex 失败原因，异常
     */
    private void onTaskFailure(final Throwable ex) {
        if (mDebug) {
            Log.e(TAG, "onTaskFailure() error=" + ex);
        }
        mCallback.onFailure(ex);
    }

    private void onDone() {
        mCallback.onDone();
    }
}
