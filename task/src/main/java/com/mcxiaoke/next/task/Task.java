package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:51
 */
abstract class Task<Result> implements ITask<Result> {

    abstract void onStarted(final TaskStatus<Result> status);

    abstract void onFinished(final TaskStatus<Result> status);

    abstract void onCancelled(final TaskStatus<Result> status);

    abstract void onSuccess(final TaskStatus<Result> status);

    abstract void onFailure(final TaskStatus<Result> status);

    abstract void onDone(final TaskStatus<Result> status);

    abstract Result call() throws Exception;
}
