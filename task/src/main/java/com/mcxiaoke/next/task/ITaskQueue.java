package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 11:55
 */
interface ITaskQueue {

    /**
     * 设置自定义的ExecutorService
     *
     * @param executor ExecutorService
     */
    void setExecutor(ExecutorService executor);

    <Result> String add(Callable<Result> callable,
                        TaskCallback<Result> callback,
                        Object caller);

    <Result> String add(Callable<Result> callable,
                        TaskCallback<Result> callback);

    <Result> String add(Callable<Result> callable, Object caller);

    <Result> String add(final Callable<Result> callable);

    String add(final Runnable runnable);

    /**
     * 取消NAME对应的任务
     *
     * @param name 任务NAME
     * @return 任务是否存在
     */
    boolean cancel(String name);


    /**
     * 取消由该调用方发起的所有任务
     * 建议在Fragment或Activity的onDestroy中调用
     *
     * @param caller 任务调用方
     * @return 返回取消的数目
     */
    int cancelAll(Object caller);

    /**
     * 便利任务列表，取消所有任务
     */
    void cancelAll();

    /**
     * 获取当前实例的详细信息
     *
     * @return dump output
     */
    String dump();
}
