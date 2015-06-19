package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:51
 */
public interface Task<Result> extends TaskFuture, ITaskActions<Result> {
}
