package com.mcxiaoke.next.os;

/**
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:39
 */

/**
 * 任务回调接口
 *
 * @param <Result> 类型参数，任务执行结果
 */
public interface TaskCallback<V> {
    ;


    /**
     * 回调，任务执行完成
     *
     * @param result 执行结果
     * @param extras 附加结果，需要返回多种结果时会用到
     * @param object 附加结果，需要返回多种结果时会用到
     */
    public void onTaskSuccess(V result, NextMessage message);

    /**
     * 回调，任务执行失败
     *
     * @param e      失败原因，异常
     * @param extras 附加结果，需要返回额外的信息时会用到
     */
    public void onTaskFailure(Throwable ex, NextMessage message);


}
