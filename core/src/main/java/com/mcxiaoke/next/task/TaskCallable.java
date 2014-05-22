package com.mcxiaoke.next.task;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public abstract class TaskCallable<V> implements Callable<V> {
    public TaskMessage mMessage;

    public TaskCallable() {

    }

    public TaskCallable(TaskMessage message) {
        mMessage = message;

    }

}
