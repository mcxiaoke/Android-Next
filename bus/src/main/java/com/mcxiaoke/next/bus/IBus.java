package com.mcxiaoke.next.bus;

/**
 * User: mcxiaoke
 * Date: 15/7/30
 * Time: 18:06
 */
public interface IBus {

    void config(BusOptions options);

    boolean register(Object target);

    boolean unregister(Object target);

    void notify(Object event);


}
