package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * 表示一个异步任务
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:16
 */
final class TaskImpl<Result> implements Task<Result> {
    private static final String TAG = "TaskQueue.Task";

    final TaskInfo<Result> mInfo;
    /**
     * 内部使用的TaskCallback
     */
    final TaskCallback<Result> mCb;
    /**
     * 任务是否已启动
     */
    private volatile boolean mConsumed;

    /**
     * 任务当前状态
     */
    private int mStatus;
    /**
     * 线程启动时间
     */
    private long mStartTime;
    /**
     * 线程结束时间
     */
    private long mEndTime;


    public TaskImpl(final TaskBuilder<Result> builder) {
        mInfo = new TaskInfo<Result>(builder);
        mCb = new Callbacks<Result>(mInfo);
        mStatus = IDLE;
        mStartTime = 0;
        mEndTime = 0;
        if (Config.DEBUG) {
            Log.v(TAG, "Task() " + this);
        }
    }

    private void execute() {
        mInfo.queue.execute(this);
    }

    private void remove() {
        mInfo.queue.remove(this);
    }

    @Override
    public String getGroup() {
        return mInfo.tag.getGroup();
    }

    @Override
    public String getName() {
        return mInfo.tag.getName();
    }

    /**
     * 将任务添加到线程池，开始执行
     *
     * @return TAG
     */
    @Override
    public String start() {
        if (mConsumed) {
            throw new IllegalStateException("task has been executed already");
        }
        if (Config.DEBUG) {
            Log.v(TAG, "start() " + getName());
        }
        mConsumed = true;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                execute();
            }
        };
        final long delayMillis = mInfo.delayMillis;
        if (delayMillis > 0) {
            mInfo.handler.postDelayed(runnable, delayMillis);
        } else {
            runnable.run();
        }
        return getName();
    }

    /**
     * 取消执行当前任务，从线程池移除
     *
     * @return 是否成功取消
     */
    @Override
    public boolean cancel() {
        if (Config.DEBUG) {
            Log.v(TAG, "cancel() " + getName());
        }
        return mInfo.queue.cancel(getName());
    }

    @Override
    public boolean isFinished() {
        return mStatus == TaskFuture.FAILURE || mStatus == TaskFuture.SUCCESS;
    }

    @Override
    public boolean isCancelled() {
        return mStatus == TaskFuture.CANCELLED;
    }

    @Override
    public long getDuration() {
        if (mEndTime < mStartTime) {
            return 0;
        }
        return mEndTime - mStartTime;
    }

    @Override
    public int getStatus() {
        return mStatus;
    }

    @Override
    public String toString() {
        return "Task{" + mInfo +
                '}';
    }

    @Override
    public Result onExecute() throws Exception {
        if (Config.DEBUG) {
            Log.v(TAG, "onExecute() " + getName());
        }
        return mInfo.action.call();
    }

    @Override
    public void onDone() {
        mEndTime = SystemClock.elapsedRealtime();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                remove();
            }
        };
        if (Config.DEBUG) {
            Log.v(TAG, "onDone() in " + getDuration() + "ms " + getName()
                    + " cancelled=" + isCancelled());
        }
        mInfo.handler.post(runnable);
        addResultExtras();
    }

    private void addResultExtras() {
        final TaskTag tag = mInfo.tag;
        final TaskCallable<Result> action = mInfo.action;
        action.putExtra(TaskCallback.TASK_THREAD, Thread.currentThread().toString());
        action.putExtra(TaskCallback.TASK_GROUP, tag.getGroup());
        action.putExtra(TaskCallback.TASK_NAME, tag.getName());
        action.putExtra(TaskCallback.TASK_SEQUENCE, tag.getSequence());
        action.putExtra(TaskCallback.TASK_DELAY, mInfo.delayMillis);
        action.putExtra(TaskCallback.TASK_DURATION, getDuration());
    }

    @Override
    public void onStarted() {
        if (Config.DEBUG) {
            Log.v(TAG, "onStarted() " + getName() + " cancelled=" + isCancelled());
            dumpCaller();
        }
        mStatus = TaskFuture.RUNNING;
        mStartTime = SystemClock.elapsedRealtime();
        if (isCancelled()) {
            return;
        }
        if (isCallerDead()) {
            if (Config.DEBUG) {
                Log.v(TAG, "onStarted() " + getName() + " caller dead, cancel task");
            }
            cancel();
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskStarted(getName(), mInfo.action.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onCancelled() {
        if (Config.DEBUG) {
            Log.v(TAG, "onCancelled() " + getName());
            dumpCaller();
        }
        mStatus = TaskFuture.CANCELLED;
        if (isCallerDead()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskCancelled(getName(), mInfo.action.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onFinished() {
        if (Config.DEBUG) {
            Log.v(TAG, "onFinished() " + getName() + " cancelled=" + isCancelled());
            dumpCaller();
        }
        if (isCancelled() || isCallerDead()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskFinished(getName(), mInfo.action.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onSuccess(final Result result) {
        if (Config.DEBUG) {
            Log.v(TAG, "onSuccess() " + getName() + " cancelled=" + isCancelled());
        }
        mStatus = TaskFuture.SUCCESS;
        if (isCancelled() || isCallerDead()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskSuccess(result, mInfo.action.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onFailure(final Throwable error) {
        if (Config.DEBUG) {
            Log.v(TAG, "onFailure() " + getName() + " cancelled=" + isCancelled()
                    + " error=" + error);
        }
        mStatus = TaskFuture.FAILURE;
        if (isCancelled() || isCallerDead()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskFailure(error, mInfo.action.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    boolean isCallerDead() {
        final Object caller = mInfo.callerRef.get();
        return caller == null || (mInfo.check && !ThreadUtils.isActive(caller));
    }

    private void dumpCaller() {
        final Object caller = mInfo.callerRef.get();
        if (caller == null) {
            Log.w(TAG, "dump() caller is recycled " + getName());
            return;
        }
        if (!mInfo.check) {
            Log.d(TAG, "dump() caller check is not enabled " + getName());
            return;
        }
        final boolean notActive = !ThreadUtils.isActive(caller);
        if (notActive) {
            Log.w(TAG, "dump() caller is not active " + getName());
        }
    }


    /**
     * 内部使用的TaskCallback，转发结果用
     *
     * @param <Result> 任务结果的类型参数
     */
    static class Callbacks<Result> implements TaskCallback<Result> {
        private WeakReference<TaskInfo<Result>> taskRef;

        public Callbacks(final TaskInfo<Result> task) {
            this.taskRef = new WeakReference<TaskInfo<Result>>(task);
        }

        @Override
        public void onTaskStarted(final String name, final Bundle extras) {
            final TaskInfo<Result> task = taskRef.get();
            if (task != null) {
                if (task.callback != null) {
                    task.callback.onTaskStarted(name, extras);
                }
            }
        }

        @Override
        public void onTaskFinished(final String name, final Bundle extras) {
            final TaskInfo<Result> task = taskRef.get();
            if (task != null) {
                if (task.callback != null) {
                    task.callback.onTaskFinished(name, extras);
                }
            }
        }

        @Override
        public void onTaskCancelled(final String name, final Bundle extras) {
            final TaskInfo<Result> task = taskRef.get();
            if (task != null) {
                if (task.callback != null) {
                    task.callback.onTaskCancelled(name, extras);
                }
            }
        }

        @Override
        public void onTaskSuccess(final Result result, final Bundle extras) {
            final TaskInfo<Result> task = taskRef.get();
            if (task != null) {
                if (task.success != null) {
                    task.success.onSuccess(result, extras);
                }
                if (task.callback != null) {
                    task.callback.onTaskSuccess(result, extras);
                }
            }
        }

        @Override
        public void onTaskFailure(final Throwable ex, final Bundle extras) {
            final TaskInfo<Result> task = taskRef.get();
            if (task != null) {
                if (task.failure != null) {
                    task.failure.onFailure(ex, extras);
                }
                if (task.callback != null) {
                    task.callback.onTaskFailure(ex, extras);
                }
            }
        }
    }

}
