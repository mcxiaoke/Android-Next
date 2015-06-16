package com.mcxiaoke.next.cache;

import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 17:18
 */
public interface IMemoryCache<K, V> {
    /**
     * Returns the value for {@code key} if it exists in the cache or can be
     * created by {@code #create}. If a value was returned, it is moved to the
     * head of the queue. This returns null if a value is not cached and cannot
     * be created.
     */
    V get(K key);

    /**
     * Caches {@code value} for {@code key}. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    V put(K key, V value);

    /**
     * Caches {@code value} for {@code key} with expires. The value is moved to the head of
     * the queue.
     *
     * @return the previous value mapped by {@code key}.
     */
    V put(K key, V value, long expires);

    /**
     * Removes the entry for {@code key} if it exists.
     *
     * @return the previous value mapped by {@code key}.
     */
    V remove(K key);

    /**
     * Clear the cache, calling {@link #remove} on each removed entry.
     */
    void clear();

    /**
     * For all other caches, this returns the sum of
     * the sizes of the entries in this cache.
     */
    int size();

    /**
     * For caches that do not override {@link #size}, this returns the maximum
     * number of entries in the cache. For all other caches, this returns the
     * maximum sum of the sizes of the entries in this cache.
     */
    int maxSize();

    /**
     * Returns a copy of the current contents of the cache, ordered from least
     * recently accessed to most recently accessed.
     */
    Map<K, V> snapshot();

}
