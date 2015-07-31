package com.mcxiaoke.next.bus;

/**
 * User: mcxiaoke
 * Date: 15/7/31
 * Time: 13:19
 */
public interface Intro {

    /**
     *
     *   实现思路：
     *
     *   1. 调用 register(target)时，查找target和它的基类里所有满足下列条件的方法：
     *      1. 使用onBusEvent作为方法名的public方法
     *      2. 使用@BusReceiver注解的public方法
     *      3. 使用IBusEvent作为参数的public方法
     *      4. 使用BusOptions配置的自定义方法名
     *      第一版只支持第一种
     *
     *  2. 调用notify(IBusEvent)方法时，调用对应target里的方法，需要支持多种Scheduler：
     *      1. 在调用者线程调用Receiver的方法
     *      2. 在主线程调用Receiver的方法
     *      3. 在其它的线程调用Receiver的方法
     *      第一版只支持
     *  3. 需要支持一些自定义配置，比如：
     *      1. 执行Receiver方法的默认线程
     *      2. 特殊的Receiver方法名识别
     *
     *  4. 一些优化：
     *      1. 方法和注解查找缓存
     *      2. 使用弱引用避免内存泄露
     *
     *
     */
}
