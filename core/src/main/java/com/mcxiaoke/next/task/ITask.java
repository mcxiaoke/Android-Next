package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:37
 */
interface ITask<Result> {

    String getGroup();

    String getName();

    /**
     * 将任务添加到线程池，开始执行
     *
     * @return TAG
     */
    TaskTag start();

    /**
     * 取消执行当前任务，从线程池移除
     *
     * @return 是否成功取消
     */
    boolean cancel();


    boolean isSerial();

    TaskTag getTag();
}
