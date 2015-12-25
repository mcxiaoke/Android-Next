package com.mcxiaoke.next.task;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

/**
 * 任务信息
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:16
 */
class TaskInfo<Result> {
    /**
     * 回调接口执行的线程Handler，默认是主线程
     */
    public final Handler handler;
    /**
     * 执行任务的队列，默认是 TaskQueue.getDefault()
     */
    public final TaskQueue queue;
    /**
     * 任务的调用者的弱引用
     */
    public final WeakReference<Object> callerRef;
    /**
     * 任务的回调接口
     */
    public final TaskCallback<Result> callback;
    /**
     * 任务的Callable对象
     */
    public final TaskCallable<Result> action;
    /**
     * 任务成功的回调
     */
    public final Success<Result> success;
    /**
     * 任务失败的回调
     */
    public final Failure failure;
    /**
     * 是否检查调用者
     */
    public final boolean check;

    /**
     * 延迟执行的毫秒数
     */
    public final long delayMillis;
    /**
     * 此任务的唯一TAG
     */
    public final TaskTag tag;


    public TaskInfo(final TaskBuilder<Result> builder) {
        if (builder.caller == null) {
            throw new NullPointerException("caller can not be null.");
        }
        if (builder.action == null) {
            throw new NullPointerException("action can not be null.");
        }
        if (builder.handler == null) {
            this.handler = new Handler(Looper.getMainLooper());
        } else {
            this.handler = builder.handler;
        }
        if (builder.queue == null) {
            this.queue = TaskQueue.getDefault();
        } else {
            this.queue = builder.queue;
        }
        this.callerRef = new WeakReference<Object>(builder.caller);
        this.action = builder.action;
        this.callback = builder.callback;
        this.success = builder.success;
        this.failure = builder.failure;
        this.check = builder.check;
        this.delayMillis = builder.delayMillis;
        this.tag = new TaskTag(builder.caller);
        if (builder.extras != null) {
            this.action.putExtras(builder.extras);
        }
    }

    @Override
    public String toString() {
        return "{" +
                "tag=" + tag +
                ", delay=" + delayMillis +
                ", check=" + check +
                '}';
    }
}
