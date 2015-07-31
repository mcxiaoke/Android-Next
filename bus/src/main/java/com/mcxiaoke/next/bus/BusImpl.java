package com.mcxiaoke.next.bus;

/**
 * User: mcxiaoke
 * Date: 15/7/30
 * Time: 18:09
 */
public class BusImpl extends Bus {

    @Override
    public void config(final BusOptions options) {

    }

    @Override
    public boolean register(final Object target) {
        return false;
    }

    @Override
    public boolean unregister(final Object target) {
        return false;
    }

    @Override
    public void notify(Object event) {

    }
}
