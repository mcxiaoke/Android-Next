package com.mcxiaoke.next.cache;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:21
 */
class LruCache<K, V> extends MemoryCacheImpl<K, V> {
    private IMemoryCache<K, Entry<V>> cache;

    public LruCache(int maxSize) {
        super(new LruCacheImpl<K, Entry<V>>(maxSize));
    }

}
