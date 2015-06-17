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
public class Task<Result> {
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
    final boolean mSerial;
    /**
     * 调用者的hashcode
     */
    final int mHashCode;
    /**
     * 此任务的唯一TAG
     */
    final String mTag;
    /**
     * 任务线程是否已启动
     */
    private boolean mStarted;


    public Task(final TaskBuilder<Result> builder) {
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
        this.mHashCode = System.identityHashCode(builder.caller);
        this.mTag = TaskHelper.buildTag(builder.caller);
        this.mCb = new Callbacks<Result>(this);
    }

    /**
     * 获取任务TAG
     *
     * @return TAG
     */
    public String getTag() {
        return mTag;
    }

    /**
     * 任务是否顺序执行
     *
     * @return 是否顺序执行
     */
    public boolean isSerial() {
        return mSerial;
    }

    /**
     * 获取任务调用者hashcode
     *
     * @return caller hashcode
     */
    public int getHashCode() {
        return mHashCode;
    }

    /**
     * 将任务添加到线程池，开始执行
     *
     * @return TAG
     */
    public String start() {
        if (mStarted) {
            throw new IllegalStateException("task has been executed already");
        }
        final Task<Result> task = this;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mQueue.enqueue(task);
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
    public boolean cancel() {
        return mQueue.cancel(this);
    }

    @Override
    public String toString() {
        return "Task{" +
                "mTag='" + mTag + '\'' +
                ", mHashCode=" + mHashCode +
                ", mCheck=" + mCheck +
                ", mCallback=" + mCallback +
                ", mQueue=" + mQueue +
                ", mSerial=" + mSerial +
                '}';
    }

    Result call() throws Exception {
        return mCallable.call();
    }


    void onDone(final TaskStatus<Result> status) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mQueue.remove(mTag, mHashCode);
            }
        };
        mHandler.post(runnable);

    }

    void onStarted(final TaskStatus<Result> future) {
        mStarted = true;
        if (!isInvalidCaller()) {
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
    }

    void onCancelled(final TaskStatus<Result> future) {
        if (!isInvalidCaller()) {
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
    }

    void onFinished(final TaskStatus<Result> future) {
        if (!isInvalidCaller()) {
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
    }

    void onSuccess(final TaskStatus<Result> future) {
        if (!isInvalidCaller()) {
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
    }

    void onFailure(final TaskStatus<Result> future) {
        if (!isInvalidCaller()) {
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
        private WeakReference<Task<Result>> taskRef;

        public Callbacks(final Task<Result> task) {
            this.taskRef = new WeakReference<Task<Result>>(task);
        }

        @Override
        public void onTaskStarted(final TaskStatus<Result> status, final Bundle extras) {
            final Task<Result> taskInfo = taskRef.get();
            if (taskInfo != null) {
                if (taskInfo.mCallback != null) {
                    taskInfo.mCallback.onTaskStarted(status, extras);
                }
            }
        }

        @Override
        public void onTaskFinished(final TaskStatus<Result> status, final Bundle extras) {
            final Task<Result> taskInfo = taskRef.get();
            if (taskInfo != null) {
                if (taskInfo.mCallback != null) {
                    taskInfo.mCallback.onTaskFinished(status, extras);
                }
            }
        }

        @Override
        public void onTaskCancelled(final TaskStatus<Result> status, final Bundle extras) {
            final Task<Result> taskInfo = taskRef.get();
            if (taskInfo != null) {
                if (taskInfo.mCallback != null) {
                    taskInfo.mCallback.onTaskCancelled(status, extras);
                }
            }
        }

        @Override
        public void onTaskSuccess(final Result result, final Bundle extras) {
            final Task<Result> taskInfo = taskRef.get();
            if (taskInfo != null) {
                if (taskInfo.mSuccess != null) {
                    taskInfo.mSuccess.onSuccess(result, extras);
                } else if (taskInfo.mCallback != null) {
                    taskInfo.mCallback.onTaskSuccess(result, extras);
                }
            }
        }

        @Override
        public void onTaskFailure(final Throwable ex, final Bundle extras) {
            final Task<Result> taskInfo = taskRef.get();
            if (taskInfo != null) {
                if (taskInfo.mFailure != null) {
                    taskInfo.mFailure.onFailure(ex, extras);
                } else if (taskInfo.mCallback != null) {
                    taskInfo.mCallback.onTaskFailure(ex, extras);
                }
            }
        }
    }

}
