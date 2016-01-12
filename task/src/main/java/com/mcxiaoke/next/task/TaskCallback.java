package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:39
 */

import android.os.Bundle;

/**
 * 任务回调接口
 *
 * @param <Result> 类型参数，任务执行结果
 */
public interface TaskCallback<Result> {
    String TASK_THREAD = "task_thread";
    String TASK_GROUP = "task_group";
    String TASK_NAME = "task_name";
    String TASK_SEQUENCE = "task_sequence";
    String TASK_DELAY = "task_delay";
    String TASK_DURATION = "task_duration";

    /**
     * 任务开始
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param name   TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskStarted(String name, Bundle extras);

    /**
     * 任务完成
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param name   TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskFinished(String name, Bundle extras);

    /**
     * 任务取消
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param name   TASK NAME
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskCancelled(String name, Bundle extras);

    /**
     * 回调，任务执行完成
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param result 执行结果
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    void onTaskSuccess(Result result, Bundle extras);

    /**
     * 回调，任务执行失败
     * 注意：此方法默认运行于主线程，可通过 TaskBuilder.dispatch(handler)更改
     *
     * @param ex     失败原因，异常
     * @param extras 附加结果，需要返回额外的信息时会用到
     */
    void onTaskFailure(Throwable ex, Bundle extras);


}
