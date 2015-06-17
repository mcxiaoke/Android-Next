package com.mcxiaoke.next.task;

import com.mcxiaoke.next.task.TaskQueue.TaskStatus;

/**
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 12:17
 */
public class TaskResult<Result> {

    TaskStatus status;
    Result data;
    Throwable error;
    long startTime;
    long endTime;
    final String tag;
    final int hashCode;

    public TaskResult(final String tag, final int hashCode) {
        this.tag = tag;
        this.hashCode = hashCode;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    public String getTag() {
        return tag;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Result getData() {
        return data;
    }
}
