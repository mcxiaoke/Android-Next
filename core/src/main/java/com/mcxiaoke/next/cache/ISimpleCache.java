package com.mcxiaoke.next.cache;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 12:58
 */
public interface ISimpleCache<K, V> {

    void put(K key, V value);

    void put(K key, V value, long expires);

    void get(K key);

    void remove(K key);

    void clear();


}
