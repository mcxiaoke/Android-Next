package com.mcxiaoke.next.cache;

import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 13:35
 */
class LruCacheImpl<K, V> implements IMemoryCache<K, V> {

    private LruCacheCompat<K, V> mCache;

    public LruCacheImpl() {
        mCache = new LruCacheCompat<K, V>(Integer.MAX_VALUE / 2);
    }

    public LruCacheImpl(int maxSize) {
        mCache = new LruCacheCompat<K, V>(maxSize);
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
        mCache.evictAll();
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
