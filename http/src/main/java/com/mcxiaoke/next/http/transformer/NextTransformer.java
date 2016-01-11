package com.mcxiaoke.next.http.transformer;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 11:05
 * F from
 * T to
 */
public interface NextTransformer<F, T> {
    T transform(F data);
}
