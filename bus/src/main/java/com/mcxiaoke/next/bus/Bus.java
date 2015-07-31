package com.mcxiaoke.next.bus;

/**
 * User: mcxiaoke
 * Date: 15/7/30
 * Time: 18:08
 */
public abstract class Bus implements IBus {

    private static class SingletonHolder {
        static final Bus INSTANCE = new BusImpl();
    }

    public static Bus getDefault() {
        return SingletonHolder.INSTANCE;
    }
}
