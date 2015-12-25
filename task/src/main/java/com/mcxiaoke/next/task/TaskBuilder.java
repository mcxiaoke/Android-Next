package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.Handler;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 11:50
 */
public class TaskBuilder<Result> {

    public static final boolean ACTIVE_CHECK_DEFAULT = true;

    Handler handler;
    TaskQueue queue;
    Object caller;
    TaskCallable<Result> action;
    TaskCallback<Result> callback;
    Success<Result> success;
    Failure failure;
    boolean check;
    long delayMillis;
    Bundle extras;

    /**
     * 根据结果类型初始化TaskBuilder
     *
     * @param resultType Result Type
     * @param <Result>   Result Type
     * @return TaskBuilder
     */
    public static <Result> TaskBuilder<Result> create(Class<Result> resultType) {
        return new TaskBuilder<Result>();
    }

    /**
     * 根据Callable初始化TaskBuilder
     *
     * @param action   Callable
     * @param <Result> Result Type
     * @return TaskBuilder
     */
    public static <Result> TaskBuilder<Result> create(Callable<Result> action) {
        return new TaskBuilder<Result>().action(action);
    }

    /**
     * 根据Callback初始化TaskBuilder
     *
     * @param callback TaskCallback
     * @param <Result> Result Type
     * @return TaskBuilder
     */
    public static <Result> TaskBuilder<Result> create(TaskCallback<Result> callback) {
        return new TaskBuilder<Result>().callback(callback);
    }

    /**
     * 根据Callable,Callback,Caller初始化TaskBuilder
     *
     * @param action   callable
     * @param callback callback
     * @param caller   caller
     * @param <Result> Result Type
     * @return TaskBuilder
     */
    public static <Result> TaskBuilder<Result> create(Callable<Result> action,
                                                      TaskCallback<Result> callback,
                                                      Object caller) {
        return new TaskBuilder<Result>().action(action).callback(callback).with(caller);
    }

    private TaskBuilder() {
        this.check = ACTIVE_CHECK_DEFAULT;
    }

    Task<Result> done() {
        return TaskFactory.createTask(this);
    }

    /**
     * 根据Builder信息生成Task对象
     *
     * @return Task
     */
    public TaskFuture build() {
        return done();
    }

    /**
     * 生成Task对象并运行
     *
     * @return TAG
     */
    public String start() {
        return done().start();
    }

    /**
     * 设置caller对象
     *
     * @param caller Task Caller
     * @return TaskBuilder
     */
    public TaskBuilder<Result> with(final Object caller) {
        this.caller = caller;
        return this;
    }

    /**
     * 设置回调接口运行的线程，默认是主线程
     *
     * @param handler Handler
     * @return TaskBuilder
     */
    public TaskBuilder<Result> dispatch(final Handler handler) {
        this.handler = handler;
        return this;
    }

    /**
     * 设置运行此任务的TaskQueue，默认是TaskQueue.getDefault()
     *
     * @param queue TaskQueue
     * @return TaskBuilder
     */
    public TaskBuilder<Result> on(final TaskQueue queue) {
        this.queue = queue;
        return this;
    }

    /**
     * 设置是否检查caller的生命周期
     * 如果是Activity，检查是否 isFinishing()
     * 如果是Fragment，检查 isAdded()
     *
     * @param check check caller active
     * @return TaskBuilder
     */
    public TaskBuilder<Result> check(final boolean check) {
        this.check = check;
        return this;
    }

    /**
     * 延迟执行
     *
     * @param millis 延迟的毫秒数
     * @return TaskBuilder
     */
    public TaskBuilder<Result> delay(final long millis) {
        this.delayMillis = millis;
        return this;
    }

    /**
     * 设置额外参数，会通过callback返回
     *
     * @param extras 延迟的毫秒数
     * @return TaskBuilder
     */
    public TaskBuilder<Result> extras(final Bundle extras) {
        this.extras = extras;
        return this;
    }

    /**
     * 设置需要执行的任务
     *
     * @param action Callable
     * @return TaskBuilder
     */
    public TaskBuilder<Result> action(final Callable<Result> action) {
        if (action instanceof TaskCallable) {
            this.action = (TaskCallable<Result>) action;
        } else {
            this.action = new WrappedCallable<Result>(action);
        }
        return this;
    }

    /**
     * 设置需要执行的任务
     *
     * @param action Runnable
     * @return TaskBuilder
     */
    public TaskBuilder<Result> action(final Runnable action) {
        this.action = new WrappedRunnable<Result>(action);
        return this;
    }

    /**
     * 设置任务成功时的回调接口
     * 优先级高于 callback()
     *
     * @param success Success
     * @return TaskBuilder
     */
    public TaskBuilder<Result> success(final Success<Result> success) {
        this.success = success;
        return this;
    }

    /**
     * 设置任务失败时的回调接口
     * 优先级高于 callback()
     *
     * @param failure Failure
     * @return TaskBuilder
     */
    public TaskBuilder<Result> failure(final Failure failure) {
        this.failure = failure;
        return this;
    }

    /**
     * 设置任务执行的回调接口
     *
     * @param callback TaskCallback
     * @return TaskBuilder
     */
    public TaskBuilder<Result> callback(final TaskCallback<Result> callback) {
        this.callback = callback;
        return this;
    }

}
