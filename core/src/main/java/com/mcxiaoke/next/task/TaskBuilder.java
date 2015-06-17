package com.mcxiaoke.next.task;

import android.os.Handler;
import com.mcxiaoke.next.task.TaskQueue.Failure;
import com.mcxiaoke.next.task.TaskQueue.Success;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 11:50
 */
public class TaskBuilder<Result> {

    Handler handler;
    Object caller;
    TaskCallable<Result> callable;
    TaskCallback<Result> callback;
    Success<Result> success;
    Failure failure;
    boolean serial;
    boolean check;

    public static <Result> TaskBuilder<Result> create(Callable<Result> callable) {
        return new TaskBuilder<Result>().run(callable);
    }

    private TaskBuilder() {
    }

    TaskInfo<Result> build() {
        return new TaskInfo<Result>(this);
    }


    public TaskInfo<Result> get() {
        return build();
    }

    public <Caller> TaskBuilder<Result> with(final Caller caller) {
        this.caller = caller;
        return this;
    }

    public TaskBuilder<Result> on(final Handler handler) {
        this.handler = handler;
        return this;
    }

    public TaskBuilder<Result> check(final boolean check) {
        this.check = check;
        return this;
    }

    public TaskBuilder<Result> run(final Callable<Result> callable) {
        if (callable instanceof TaskCallable) {
            this.callable = (TaskCallable<Result>) callable;
        } else {
            this.callable = new WrappedCallable<Result>(callable);
        }
        return this;
    }

    public TaskBuilder<Result> run(final Runnable runnable) {
        this.callable = new WrappedRunnable<Result>(runnable);
        return this;
    }

    public TaskBuilder<Result> success(final Success<Result> success) {
        this.success = success;
        return this;
    }

    public TaskBuilder<Result> failure(final Failure failure) {
        this.failure = failure;
        return this;
    }

    public TaskBuilder<Result> callback(final TaskCallback<Result> callback) {
        this.callback = callback;
        return this;
    }

    public TaskBuilder<Result> serial(final boolean serially) {
        serial = serially;
        return this;
    }
}
