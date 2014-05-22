package com.mcxiaoke.next.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:21
 */
class MapCache<K, V> implements IMemoryCache<K, V> {
    private Map<K, V> cache;

    public MapCache() {
        cache = new HashMap<K, V>();
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
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public int maxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Map<K, V> snapshot() {
        return new HashMap<K, V>(cache);
    }

}
