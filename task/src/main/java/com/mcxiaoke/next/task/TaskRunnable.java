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
    private ITaskCallbacks<Result> mTask;
    private String mName;
    private boolean mCancelled;

    TaskRunnable(final ITaskCallbacks<Result> task, final String name) {
        mTask = task;
        mName = name;
        if (Config.DEBUG) {
            Log.v(TAG, "TaskRunnable() task=" + name);
        }
    }

    /**
     * 由于各种原因导致任务被取消
     * 原因：手动取消，线程中断
     *
     * @return cancelled
     */
    private boolean isCancelled() {
        return mCancelled || isInterrupted();
    }

    @Override
    public void run() {
        onTaskStarted();
        Result result = null;
        Exception error = null;
        if (!isCancelled()) {
            try {
                if (Config.DEBUG) {
                    Log.v(TAG, "run() task:" + mName + " thread:" + Thread.currentThread());
                }
                result = mTask.onExecute();
            } catch (Exception ex) {
                error = ex;
            }
        }
        onTaskDone();
        if (!isCancelled()) {
            onTaskFinished();
            if (error != null) {
                onTaskFailure(error);
            } else {
                onTaskSuccess(result);
            }
        }
    }

    @Override
    public boolean cancel() {
        mCancelled = true;
        if (Config.DEBUG) {
            Log.d(TAG, "cancel() " + mName);
        }
        boolean result = false;
        if (mFuture != null) {
            result = mFuture.cancel(true);
        }
        onTaskCancelled();
        return result;
    }

    @Override
    public void setFuture(final Future<?> future) {
        mFuture = future;
    }

    private boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * 任务线程开始
     */
    private void onTaskStarted() {
        mTask.onStarted();
    }

    /**
     * 任务取消
     */
    private void onTaskCancelled() {
        mTask.onCancelled();
    }


    /**
     * 任务完成
     */
    private void onTaskFinished() {
        mTask.onFinished();
    }

    /**
     * 回调，任务执行成功
     *
     * @param result 任务执行结果
     */
    private void onTaskSuccess(final Result result) {
        mTask.onSuccess(result);
    }

    /**
     * 回调，任务执行失败
     *
     * @param ex 失败原因，异常
     */
    private void onTaskFailure(final Throwable ex) {
        mTask.onFailure(ex);
    }

    /**
     * 任务已执行
     */
    private void onTaskDone() {
        mTask.onDone();
    }
}
