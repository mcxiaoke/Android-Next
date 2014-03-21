package com.mcxiaoke.commons.cache;

import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:21
 */
class LruCache<K, V> implements ICache<K, V> {
    private android.support.v4.util.LruCache<K, V> cache;

    public LruCache(int maxSize) {
        cache = new android.support.v4.util.LruCache<K, V>(maxSize);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public V put(K key, V value) {
        return cache.put(key, value);
    }

    @Override
    public V remove(K key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.evictAll();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public int maxSize() {
        return cache.maxSize();
    }

    @Override
    public Map<K, V> snapshot() {
        return cache.snapshot();
    }

}
