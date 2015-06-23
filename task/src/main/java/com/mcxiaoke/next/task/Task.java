package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:51
 */
interface Task<Result> extends TaskFuture, ITaskCallbacks<Result> {
}
