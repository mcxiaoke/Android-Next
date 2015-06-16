package com.mcxiaoke.next.cache;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:43
 */
public final class MemoryCache {

    private MemoryCache() {
    }

    public static <K, V> IMemoryCache<K, V> lruCache(int maxSize) {
        return new MemoryCacheImpl<K, V>(new LruCacheImpl<K, MemoryCacheImpl.Entry<V>>(maxSize));
    }

    public static <K, V> IMemoryCache<K, V> mapCache() {
        return new MemoryCacheImpl<K, V>(new MapCacheImpl<K, MemoryCacheImpl.Entry<V>>());
    }
}
