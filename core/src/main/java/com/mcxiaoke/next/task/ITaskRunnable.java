package com.mcxiaoke.next.task;

import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 10:44
 */
interface ITaskRunnable extends Runnable {

    boolean cancel();

    void setFuture(final Future<?> future);

}
