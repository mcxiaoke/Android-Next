package com.mcxiaoke.next.task;

/**
 * Task的状态
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:17
 */
class TaskStatus<Result> {

    public static final int IDLE = 0; // 空闲，初始化
    public static final int RUNNING = 1;  // 线程正在运行
    public static final int CANCELLED = 2;  // 任务已取消
    public static final int FAILURE = 3; // 任务已失败
    public static final int SUCCESS = 4; // 任务已成功

    /**
     * 任务的TAG
     */
    final String tag;
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
    // 任务结果
    Result data;
    // 任务异常
    Throwable error;

    public TaskStatus(final String tag) {
        this.tag = tag;
        this.status = IDLE;
    }

    public boolean isDone() {
        return status != IDLE && status != RUNNING;
    }

    public boolean isCancelled() {
        return status == CANCELLED;
    }

    public boolean isRunning() {
        return status == RUNNING;
    }

    public long getDuration() {
        if (endTime < startTime) {
            return 0;
        }
        return endTime - startTime;
    }

    public String getTag() {
        return tag;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getStatus() {
        return status;
    }

    public Result getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public String toString() {
        return "{" +
                "status=" + status +
                ", duration=" + getDuration() + "ms" +
                ", tag='" + tag + '\'' +
                '}';
    }
}
