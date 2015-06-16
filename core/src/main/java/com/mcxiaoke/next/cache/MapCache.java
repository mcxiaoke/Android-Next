package com.mcxiaoke.next.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:21
 */
class MapCache<K, V> implements IMemoryCache<K, V> {
    private Map<K, CacheEntry<V>> cache;

    public MapCache() {
        cache = new HashMap<K, CacheEntry<V>>();
    }

    @Override
    public V get(K key) {
        final CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            remove(key);
            return null;
        }
        return entry.data;
    }

    @Override
    public V put(K key, V value) {
        final CacheEntry<V> entry = new CacheEntry<V>(value);
        final CacheEntry<V> ret = cache.put(key, entry);
        return ret == null ? null : ret.data;
    }

    @Override
    public V put(final K key, final V value, final long expires) {
        final CacheEntry<V> entry = new CacheEntry<V>(value, expires);
        final CacheEntry<V> ret = cache.put(key, entry);
        return ret == null ? null : ret.data;
    }

    @Override
    public V remove(K key) {
        final CacheEntry<V> ret = cache.remove(key);
        return ret == null ? null : ret.data;
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
        final Map<K, V> map = new HashMap<K, V>();
        for (final Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
            final CacheEntry<V> c = entry.getValue();
            if (c == null || c.isExpired()) {
                continue;
            }
            map.put(entry.getKey(), c.data);
        }
        return map;
    }

}
