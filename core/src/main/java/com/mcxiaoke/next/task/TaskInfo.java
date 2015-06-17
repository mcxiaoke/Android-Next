package com.mcxiaoke.next.task;

import android.os.Handler;
import android.os.Looper;
import com.mcxiaoke.next.task.TaskQueue.Failure;
import com.mcxiaoke.next.task.TaskQueue.Success;

/**
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:16
 */
public class TaskInfo<Result> {
    public final Handler handler;
    public final Object caller;
    public final TaskCallback<Result> callback;
    public final TaskCallable<Result> callable;
    public final Success<Result> success;
    public final Failure failure;
    public final boolean serial;
    public final boolean check;
    public final int hashCode;
    public final String tag;

    public TaskInfo(final TaskBuilder<Result> builder) {
        if (builder.caller == null) {
            throw new NullPointerException("caller can not be null.");
        }
        if (builder.callable == null) {
            throw new NullPointerException("callable can not be null.");
        }
        if (builder.handler == null) {
            this.handler = new Handler(Looper.getMainLooper());
        } else {
            this.handler = builder.handler;
        }
        this.caller = builder.caller;
        this.callable = builder.callable;
        this.callback = builder.callback;
        this.success = builder.success;
        this.failure = builder.failure;
        this.serial = builder.serial;
        this.check = builder.check;
        this.hashCode = System.identityHashCode(this.caller);
        this.tag = TaskHelper.buildTag(this.caller);
    }


    public String start() {
        return TaskQueue.getDefault().enqueue(this);
    }

    public boolean cancel() {
        return TaskQueue.getDefault().cancel(this);
    }

}
