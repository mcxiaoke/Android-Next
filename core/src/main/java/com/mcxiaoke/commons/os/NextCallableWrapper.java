package com.mcxiaoke.commons.os;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
class NextCallableWrapper<V> extends NextCallable<V> {

    private Callable<V> mCallable;

    public NextCallableWrapper(Callable<V> callable) {
        mCallable = callable;
    }

    @Override
    public V call() throws Exception {
        return mCallable.call();
    }
}
