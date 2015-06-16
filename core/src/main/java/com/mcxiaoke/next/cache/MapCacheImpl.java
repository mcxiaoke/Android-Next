package com.mcxiaoke.next.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 13:35
 */
class MapCacheImpl<K, V> implements IMemoryCache<K, V> {

    private Map<K, V> mCache;

    public MapCacheImpl() {
        mCache = new HashMap<K, V>();
    }

    @Override
    public V get(final K key) {
        return mCache.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return mCache.put(key, value);
    }

    @Override
    public V put(final K key, final V value, final long expires) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(final K key) {
        return mCache.remove(key);
    }

    @Override
    public void clear() {
        mCache.clear();
    }

    @Override
    public int size() {
        return mCache.size();
    }

    @Override
    public int maxSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Map<K, V> snapshot() {
        throw new UnsupportedOperationException();
    }
}
