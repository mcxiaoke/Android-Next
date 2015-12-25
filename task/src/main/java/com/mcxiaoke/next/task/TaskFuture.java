package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:37
 */
public interface TaskFuture {

    int IDLE = 0; // 空闲，初始化
    int RUNNING = 1;  // 线程正在运行
    int CANCELLED = 2;  // 任务已取消
    int FAILURE = 3; // 任务已失败
    int SUCCESS = 4; // 任务已成功

    String getGroup();

    String getName();

    String start();

    boolean cancel();

    boolean isFinished();

    boolean isCancelled();

    long getDuration();

    int getStatus();
}
