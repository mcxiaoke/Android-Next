package com.mcxiaoke.next.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:21
 */
class MemoryCacheImpl<K, V> implements IMemoryCache<K, V> {

    static class Entry<T> {
        public T data;
        public int size;
        public long expire;

        public Entry() {

        }

        public Entry(final T data) {
            this.data = data;
        }

        public Entry(final T data, final long expire) {
            this.data = data;
            this.expire = expire;
        }

        public boolean isExpired() {
            return expire > 0 && expire < System.currentTimeMillis();
        }
    }

    private final Object mLock = new Object();
    private IMemoryCache<K, Entry<V>> cache;

    public MemoryCacheImpl(IMemoryCache<K, Entry<V>> cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) {
        final Entry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            remove(key);
            return null;
        }
        return entry.data;
    }

    @Override
    public V put(K key, V value) {
        final Entry<V> entry = new Entry<V>(value);
        final Entry<V> ret = cache.put(key, entry);
        return ret == null ? null : ret.data;
    }

    @Override
    public V put(final K key, final V value, final long expires) {
        final Entry<V> entry = new Entry<V>(value, expires);
        final Entry<V> ret = cache.put(key, entry);
        return ret == null ? null : ret.data;
    }

    @Override
    public V remove(K key) {
        final Entry<V> ret = cache.remove(key);
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
        return cache.maxSize();
    }

    @Override
    public Map<K, V> snapshot() {
        final Map<K, V> map = new HashMap<K, V>();
        for (final Map.Entry<K, Entry<V>> entry : cache.snapshot().entrySet()) {
            final Entry<V> c = entry.getValue();
            if (c == null || c.isExpired()) {
                continue;
            }
            map.put(entry.getKey(), c.data);
        }
        return map;
    }

}
