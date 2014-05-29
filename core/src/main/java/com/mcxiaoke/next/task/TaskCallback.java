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

    /**
     * 任务所在的线程开始执行
     * 注意：不是添加到线程池的时间，是run()方法开始运行的时间
     *
     * @param tag    TASK TAG
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    public void onTaskStarted(final String tag, final Bundle extras);

    /**
     * 回调，任务执行完成
     *
     * @param result 执行结果
     * @param extras 附加结果，需要返回多种结果时会用到
     */
    public void onTaskSuccess(Result result, final Bundle extras);

    /**
     * 回调，任务执行失败
     *
     * @param e      失败原因，异常
     * @param extras 附加结果，需要返回额外的信息时会用到
     */
    public void onTaskFailure(Throwable ex, final Bundle extras);


}
