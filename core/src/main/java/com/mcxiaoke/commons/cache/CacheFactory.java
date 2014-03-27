package com.mcxiaoke.commons.cache;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:43
 */
public final class CacheFactory {

    private CacheFactory() {
    }

    public static IMemoryCache<String, Object> createLruCache(int maxSize) {
        return new LruCache<String, Object>(maxSize);
    }

    public static IMemoryCache<String, Object> createCache() {
        return new MapCache<String, Object>();
    }
}
