package com.mcxiaoke.next.cache;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 13:02
 */
class CacheEntry<T> {
    public T data;
    public int size;
    public long expire;

    public CacheEntry() {

    }

    public CacheEntry(final T data) {
        this.data = data;
    }

    public CacheEntry(final T data, final long expire) {
        this.data = data;
        this.expire = expire;
    }

    public boolean isExpired() {
        return expire > 0 && expire < System.currentTimeMillis();
    }
}
