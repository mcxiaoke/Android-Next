package com.mcxiaoke.commons.os;

import android.os.Bundle;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-14
 * Time: 17:23
 */
public abstract class NextCallable<V> implements Callable<V> {
    public Bundle extras;
    public Object obj;

    public NextCallable() {

    }

}
