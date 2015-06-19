package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.SystemClock;

import java.lang.ref.WeakReference;

/**
 * 表示一个异步任务
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:16
 */
class TaskImpl<Result> implements Task<Result> {
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
     * 任务是否已取消
     */
    private volatile boolean mCancelled;

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
    }

    private void execute() {
        mInfo.queue.execute(this);
    }

    private void remove() {
        mInfo.queue.remove(this);
    }

    /**
     * 任务是否顺序执行
     *
     * @return 是否顺序执行
     */
    @Override
    public boolean isSerial() {
        return mInfo.serial;
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
        mCancelled = true;
        return mInfo.queue.cancel(mInfo.tag.getName());
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
        return mInfo.callable.call();
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
        mInfo.handler.post(runnable);

    }

    @Override
    public void onStarted() {
        mStatus = TaskFuture.RUNNING;
        mStartTime = SystemClock.elapsedRealtime();
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskStarted(getName(), mInfo.callable.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onCancelled() {
        mStatus = TaskFuture.CANCELLED;
        if (isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskCancelled(getName(), mInfo.callable.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onFinished() {
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskFinished(getName(), mInfo.callable.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onSuccess(final Result result) {
        mStatus = TaskFuture.SUCCESS;
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskSuccess(result, mInfo.callable.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    @Override
    public void onFailure(final Throwable error) {
        mStatus = TaskFuture.FAILURE;
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCb.onTaskFailure(error, mInfo.callable.getExtras());
            }
        };
        mInfo.handler.post(runnable);
    }

    boolean isInvalidCaller() {
        final Object caller = mInfo.callerRef.get();
        return caller == null || mInfo.check && !Utils.isActive(caller);
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
