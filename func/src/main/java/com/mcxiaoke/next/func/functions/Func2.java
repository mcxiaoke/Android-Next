package com.mcxiaoke.next.func.functions;

/**
 * User: mcxiaoke
 * Date: 15/8/20
 * Time: 10:43
 */
public interface Func2<T1, T2, R> extends Function {
    R call(T1 t1, T2 t2);
}
