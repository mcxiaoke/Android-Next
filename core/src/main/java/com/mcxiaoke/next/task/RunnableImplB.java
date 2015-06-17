package com.mcxiaoke.next.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.mcxiaoke.next.task.TaskQueue.TaskStatus;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:12
 */
final class RunnableImplB<Result> implements TaskRunnable {

    public static final String CLASS_TAG = RunnableImplB.class.getSimpleName();
    public static final String TAG = TaskQueue.TAG + "." + CLASS_TAG;

    private Future<?> mFuture;
    private Handler mQueueHandler;
    private boolean mDebug;

    private TaskInfo<Result> mTaskInfo;
    private TaskResult<Result> mTaskResult;

    RunnableImplB(final TaskInfo<Result> task, final Handler handler, final boolean debug) {
        mTaskInfo = task;
        mTaskResult = new TaskResult<Result>(task.tag, task.hashCode);
        mQueueHandler = handler;
        mDebug = debug;
        if (mDebug) {
            LogUtils.v(TAG, "TaskRunnable() task=" + task);
        }
    }

    // 重置所有字段
    private void reset() {
    }

    /**
     * 由于各种原因导致任务被取消
     * 原因：手动取消，线程中断，调用者不存在，回调接口不存在
     *
     * @return cancelled
     */
    private boolean isTaskCancelled() {
        return isCancelled() || isInterrupted()
                || mTaskInfo.caller == null || mTaskInfo.callback == null;
    }

    /**
     * 检查Caller的生命周期，是否Alive
     *
     * @return is active
     */
    @SuppressLint("NewApi")
    private boolean isCallerActive() {
        return TaskHelper.validCaller(mTaskInfo);
    }

    @Override
    public void run() {
        mTaskResult.status = TaskStatus.RUNNING;
        mTaskResult.startTime = SystemClock.elapsedRealtime();
        if (mDebug) {
            LogUtils.v(TAG, "run() start, thread=" + Thread.currentThread().getName() + " tag=" + getTag());
        }
        onTaskStarted();
        final Callable<Result> callable = mTaskInfo.callable;
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

        mTaskResult.error = throwable;
        mTaskResult.data = result;
        onTaskFinished();
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
        mTaskResult.endTime = SystemClock.elapsedRealtime();
        mTaskResult.status = throwable == null ? TaskStatus.SUCCESS : TaskStatus.FAILURE;
        if (mDebug) {
            LogUtils.v(TAG, "run() end duration:" + mTaskResult.getDuration() + "ms tag=" + getTag());
        }
    }

    @Override
    public boolean cancel() {
        if (mDebug) {
            LogUtils.v(TAG, "cancel()");
        }
        mTaskResult.status = TaskStatus.CANCELLED;
        boolean result = false;
        if (mFuture != null) {
            result = mFuture.cancel(true);
        }
        return result;
    }

    @Override
    public boolean isSerial() {
        return mTaskInfo.serial;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    public Result getResult() {
        return mTaskResult.data;
    }

    public Throwable getThrowable() {
        return mTaskResult.error;
    }

    @Override
    public void setFuture(Future<?> future) {
        mFuture = future;
    }

    @Override
    public int getHashCode() {
        return 0;
    }

    @Override
    public String getTag() {
        return mTaskInfo.tag;
    }

    public boolean isCancelled() {
        return TaskStatus.CANCELLED.equals(mTaskResult.status);
    }

    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    private void onTaskStarted() {
        final String tag = getTag();
        if (mDebug) {
            LogUtils.v(TAG, "preProcess() tag=" + tag);
        }
        final TaskCallable<Result> callable = mTaskInfo.callable;
        final TaskCallback<Result> callback = mTaskInfo.callback;
        if (callback != null) {
            callback.onTaskStarted(tag, callable.getExtras());
        }
    }

    private void onTaskFinished() {
        final String tag = getTag();
        if (mDebug) {
            LogUtils.v(TAG, "postProcess() tag=" + tag);
        }
        final TaskCallable<Result> callable = mTaskInfo.callable;
        final TaskCallback<Result> callback = mTaskInfo.callback;
        final Result result = mTaskResult.data;
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
        final TaskCallable<Result> callable = mTaskInfo.callable;
        final TaskCallback<Result> callback = mTaskInfo.callback;
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
        final TaskCallable<Result> callable = mTaskInfo.callable;
        final TaskCallback<Result> callback = mTaskInfo.callback;
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
        final Handler handler = mQueueHandler;
        final String tag = mTaskInfo.tag;
        Message message = handler.obtainMessage(TaskQueue.MSG_TASK_DONE, tag);
        handler.sendMessage(message);
    }

    private void onFinally() {
//        if (mDebug) {
//            LogUtils.v(TAG, "onFinally()");
//        }
        reset();
    }

    private void postRunnable(final Runnable runnable) {
        mTaskInfo.handler.post(runnable);
    }
}
