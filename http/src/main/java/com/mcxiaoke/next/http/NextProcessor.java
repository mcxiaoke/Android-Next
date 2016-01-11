package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 11:05
 */
public interface NextProcessor<T> {
    T transform(T data);
}
