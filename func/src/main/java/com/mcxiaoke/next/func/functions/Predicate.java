package com.mcxiaoke.next.func.functions;

/**
 * User: mcxiaoke
 * Date: 15/8/20
 * Time: 11:16
 */
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean accept(T t);
}
