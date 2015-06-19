package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/19
 * Time: 10:11
 */
interface ITaskCallbacks<Result> {

    Result onExecute() throws Exception;

    void onStarted();

    void onFinished();

    void onCancelled();

    void onSuccess(Result result);

    void onFailure(Throwable ex);

    void onDone();
}
