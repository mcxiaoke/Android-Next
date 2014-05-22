package com.mcxiaoke.next.os;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public abstract class NextCallable<V> implements Callable<V> {
    public NextMessage mMessage;

    public NextCallable() {

    }

    public NextCallable(NextMessage message) {
        mMessage = message;

    }

}
