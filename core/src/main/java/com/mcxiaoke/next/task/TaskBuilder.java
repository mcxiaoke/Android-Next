package com.mcxiaoke.next.task;

import android.os.Handler;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 11:50
 */
public class TaskBuilder<Result> {

    Handler handler;
    TaskQueue queue;
    Object caller;
    TaskCallable<Result> callable;
    TaskCallback<Result> callback;
    Success<Result> success;
    Failure failure;
    boolean serial;
    boolean check;

    /**
     * 根据Callable初始化TaskBuilder
     *
     * @param callable Callable
     * @param <Result> Result Type
     * @return TaskBuilder
     */
    public static <Result> TaskBuilder<Result> create(Callable<Result> callable) {
        return new TaskBuilder<Result>().run(callable);
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

    private TaskBuilder() {
    }

    /**
     * 根据Builder信息生成Task对象
     *
     * @return Task
     */
    public Task<Result> build() {
        return new Task<Result>(this);
    }

    /**
     * 生成Task对象并运行
     *
     * @return TAG
     */
    public String start() {
        return build().start();
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
     * 设置需要执行的任务
     *
     * @param callable Callable
     * @return TaskBuilder
     */
    public TaskBuilder<Result> run(final Callable<Result> callable) {
        if (callable instanceof TaskCallable) {
            this.callable = (TaskCallable<Result>) callable;
        } else {
            this.callable = new WrappedCallable<Result>(callable);
        }
        return this;
    }

    /**
     * 设置需要执行的任务
     *
     * @param runnable Runnable
     * @return TaskBuilder
     */
    public TaskBuilder<Result> run(final Runnable runnable) {
        this.callable = new WrappedRunnable<Result>(runnable);
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

    /**
     * 设置是否按新顺序单线程执行添加的任务
     *
     * @param serial 是否顺序执行
     * @return TaskBuilder
     */
    public TaskBuilder<Result> serial(final boolean serial) {
        this.serial = serial;
        return this;
    }
}
