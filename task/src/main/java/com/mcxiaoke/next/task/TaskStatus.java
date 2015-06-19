package com.mcxiaoke.next.task;

/**
 * Task的状态
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:17
 */
class TaskStatus<Result> {
    /**
     * 任务当前状态
     */
    int status;
    /**
     * 线程启动时间
     */
    long startTime;
    /**
     * 线程结束时间
     */
    long endTime;

    public TaskStatus() {
        this.status = TaskFuture.IDLE;
    }

    public boolean isDone() {
        return status != TaskFuture.IDLE && status != TaskFuture.RUNNING;
    }

    public boolean isCancelled() {
        return status == TaskFuture.CANCELLED;
    }

    public long getDuration() {
        if (endTime < startTime) {
            return 0;
        }
        return endTime - startTime;
    }

    public int getStatus() {
        return status;
    }
}
