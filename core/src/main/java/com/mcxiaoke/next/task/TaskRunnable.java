package com.mcxiaoke.next.task;

import java.util.concurrent.Future;

/**
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 11:45
 */
interface TaskRunnable extends Runnable {

    boolean cancel();

    boolean isSerial();

    boolean isRunning();

    int getHashCode();

    String getTag();

    void setFuture(Future<?> future);
}
