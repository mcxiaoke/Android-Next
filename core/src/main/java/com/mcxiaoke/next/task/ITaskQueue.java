package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 11:55
 */
interface ITaskQueue {
    /**
     * debug开关
     *
     * @param debug 是否开启DEBUG模式
     */
    void setDebug(boolean debug);

    <Result> TaskTag execute(final Callable<Result> callable,
                             final TaskCallback<Result> callback,
                             final Object caller, final boolean serial);

    <Result> String add(Callable<Result> callable,
                        TaskCallback<Result> callback,
                        Object caller);

    /**
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @return Tag
     */
    <Result> String add(Callable<Result> callable, Object caller);

    <Result> String addSerially(Callable<Result> callable,
                                TaskCallback<Result> callback, Object caller);

    /**
     * @param callable Callable
     * @param caller   Caller
     * @param <Result> Result
     * @return Tag
     */
    <Result> String addSerially(Callable<Result> callable, Object caller);

    /**
     * 取消NAME对应的任务
     *
     * @param name 任务NAME
     * @return 任务是否存在
     */
    boolean cancel(String name);

    /**
     * 取消TAG对应的任务
     *
     * @param tag 任务TAG
     * @return 任务是否存在
     */
    boolean cancel(TaskTag tag);

    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    int cancelAll(Object caller);

    /**
     * 设置自定义的ExecutorService
     *
     * @param executor ExecutorService
     */
    void setExecutor(ThreadPoolExecutor executor);

    /**
     * 便利任务列表，取消所有任务
     */
    void cancelAll();

    /**
     * 获取当前实例的详细信息
     *
     * @param logcat 是否输出到logcat
     * @return dump output
     */
    String dump(boolean logcat);
}
