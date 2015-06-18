package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.next.utils.AndroidUtils;

import java.lang.ref.WeakReference;

/**
 * 表示一个异步任务
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:16
 */
class TaskImpl<Result> extends Task<Result> {
    /**
     * 内部使用的TaskCallback
     */
    private final TaskCallback<Result> mCb;
    /**
     * 回调接口执行的线程Handler，默认是主线程
     */
    private final Handler mHandler;
    /**
     * 执行任务的队列，默认是 TaskQueue.getDefault()
     */
    private final TaskQueue mQueue;
    /**
     * 任务的调用者的弱引用
     */
    private final WeakReference<Object> mCallerRef;
    /**
     * 任务的回调接口
     */
    private final TaskCallback<Result> mCallback;
    /**
     * 任务的Callable对象
     */
    private final TaskCallable<Result> mCallable;
    /**
     * 任务成功的回调
     */
    private final Success<Result> mSuccess;
    /**
     * 任务失败的回调
     */
    private final Failure mFailure;
    /**
     * 是否检查调用者
     */
    private final boolean mCheck;

    /**
     * 延迟执行的毫秒数
     */
    private final long mDelayMillis;
    /**
     * 是否按顺序执行
     */
    private final boolean mSerial;
    /**
     * 此任务的唯一TAG
     */
    private final TaskTag mTag;
    /**
     * 任务是否已启动
     */
    private volatile boolean mConsumed;

    /**
     * 任务是否已取消
     */
    private volatile boolean mCancelled;


    public TaskImpl(final TaskBuilder<Result> builder) {
        if (builder.caller == null) {
            throw new NullPointerException("caller can not be null.");
        }
        if (builder.callable == null) {
            throw new NullPointerException("callable can not be null.");
        }
        if (builder.handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = builder.handler;
        }
        if (builder.queue == null) {
            this.mQueue = TaskQueue.getDefault();
        } else {
            this.mQueue = builder.queue;
        }
        this.mCallerRef = new WeakReference<Object>(builder.caller);
        this.mCallable = builder.callable;
        this.mCallback = builder.callback;
        this.mSuccess = builder.success;
        this.mFailure = builder.failure;
        this.mCheck = builder.check;
        this.mDelayMillis = builder.delayMillis;
        this.mSerial = builder.serial;
        this.mTag = new TaskTag(builder.caller);
        this.mCb = new Callbacks<Result>(this);

        if (builder.extras != null) {
            this.mCallable.putExtras(builder.extras);
        }
    }

    /**
     * 任务是否顺序执行
     *
     * @return 是否顺序执行
     */
    @Override
    public boolean isSerial() {
        return mSerial;
    }

    @Override
    public TaskTag getTag() {
        return mTag;
    }

    @Override
    public String getGroup() {
        return mTag.getGroup();
    }

    @Override
    public String getName() {
        return mTag.getName();
    }

    /**
     * 将任务添加到线程池，开始执行
     *
     * @return TAG
     */
    @Override
    public TaskTag start() {
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
        if (mDelayMillis > 0) {
            mHandler.postDelayed(runnable, mDelayMillis);
        } else {
            runnable.run();
        }
        return mTag;
    }

    /**
     * 取消执行当前任务，从线程池移除
     *
     * @return 是否成功取消
     */
    @Override
    public boolean cancel() {
        mCancelled = true;
        return mQueue.cancel(mTag.getName());
    }

    @Override
    public String toString() {
        return "Task{" +
                "mTag='" + mTag + '\'' +
                ", mCheck=" + mCheck +
                ", mSerial=" + mSerial +
                '}';
    }

    private void execute() {
        mQueue.execute(this);
    }

    @Override
    Result call() throws Exception {
        return mCallable.call();
    }

    @Override
    void onDone(final TaskStatus<Result> status) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mQueue.remove(mTag);
            }
        };
        mHandler.post(runnable);

    }

    @Override
    void onStarted(final TaskStatus<Result> future) {
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mCb != null) {
                    mCb.onTaskStarted(future, mCallable.getExtras());
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    void onCancelled(final TaskStatus<Result> future) {
        if (isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mCb != null) {
                    mCb.onTaskCancelled(future, mCallable.getExtras());
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    void onFinished(final TaskStatus<Result> future) {
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mCb != null) {
                    mCb.onTaskFinished(future, mCallable.getExtras());
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    void onSuccess(final TaskStatus<Result> future) {
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mCb != null) {
                    mCb.onTaskSuccess(future.data, mCallable.getExtras());
                }
            }
        };
        mHandler.post(runnable);
    }

    @Override
    void onFailure(final TaskStatus<Result> future) {
        if (mCancelled || isInvalidCaller()) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mCb != null) {
                    mCb.onTaskFailure(future.error, mCallable.getExtras());
                }
            }
        };
        mHandler.post(runnable);
    }

    boolean isInvalidCaller() {
        final Object caller = mCallerRef.get();
        return caller == null || mCheck && !AndroidUtils.isActive(mCallerRef);
    }


    /**
     * 内部使用的TaskCallback，转发结果用
     *
     * @param <Result> 任务结果的类型参数
     */
    static class Callbacks<Result> implements TaskCallback<Result> {
        private WeakReference<TaskImpl<Result>> taskRef;

        public Callbacks(final TaskImpl<Result> task) {
            this.taskRef = new WeakReference<TaskImpl<Result>>(task);
        }

        @Override
        public void onTaskStarted(final TaskStatus<Result> status, final Bundle extras) {
            final TaskImpl<Result> task = taskRef.get();
            if (task != null) {
                if (task.mCallback != null) {
                    task.mCallback.onTaskStarted(status, extras);
                }
            }
        }

        @Override
        public void onTaskFinished(final TaskStatus<Result> status, final Bundle extras) {
            final TaskImpl<Result> task = taskRef.get();
            if (task != null) {
                if (task.mCallback != null) {
                    task.mCallback.onTaskFinished(status, extras);
                }
            }
        }

        @Override
        public void onTaskCancelled(final TaskStatus<Result> status, final Bundle extras) {
            final TaskImpl<Result> task = taskRef.get();
            if (task != null) {
                if (task.mCallback != null) {
                    task.mCallback.onTaskCancelled(status, extras);
                }
            }
        }

        @Override
        public void onTaskSuccess(final Result result, final Bundle extras) {
            final TaskImpl<Result> task = taskRef.get();
            if (task != null) {
                if (task.mSuccess != null) {
                    task.mSuccess.onSuccess(result, extras);
                } else if (task.mCallback != null) {
                    task.mCallback.onTaskSuccess(result, extras);
                }
            }
        }

        @Override
        public void onTaskFailure(final Throwable ex, final Bundle extras) {
            final TaskImpl<Result> task = taskRef.get();
            if (task != null) {
                if (task.mFailure != null) {
                    task.mFailure.onFailure(ex, extras);
                } else if (task.mCallback != null) {
                    task.mCallback.onTaskFailure(ex, extras);
                }
            }
        }
    }

}
