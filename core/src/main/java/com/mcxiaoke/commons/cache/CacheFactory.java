package com.mcxiaoke.commons.cache;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:43
 */
public final class CacheFactory {

    private CacheFactory() {
    }

    public static ICache<Long, ICacheValue> createLruCache(int maxSize) {
        return new LruCache<Long, ICacheValue>(maxSize);
    }

    public static ICache<Long, ICacheValue> createCache() {
        return new MapCache<Long, ICacheValue>();
    }
}
