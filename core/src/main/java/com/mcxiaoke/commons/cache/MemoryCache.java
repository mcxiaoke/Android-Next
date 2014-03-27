package com.mcxiaoke.commons.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This class is  to save, restore, remove and return your cached objects.
 */
public class MemoryCache implements IMemoryCache<String, Object> {

    IMemoryCache<String, Object> mCacheStore;

    // Private constructor prevents instantiation from other classes
    private MemoryCache(IMemoryCache<String, Object> cache) {
        this.mCacheStore = cache;
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getDefault()
     * or the first access to SingletonHolder.DEFAULT, not before.
     */
    private static class SingletonHolder {
        public static final MemoryCache DEFAULT = new MemoryCache(CacheFactory.createCache());
    }

    /**
     * Singleton pattern. Returns the same DEFAULT every time. This singleton is thread safe.
     *
     * @return Cache
     */
    public static MemoryCache getDefault() {
        return SingletonHolder.DEFAULT;
    }

    public static MemoryCache create() {
        return create(CacheFactory.createCache());
    }

    public static MemoryCache create(IMemoryCache<String, Object> cache) {
        return new MemoryCache(cache);
    }

    /**
     * Sets the Cache
     *
     * @param cache The new Cache
     */
    private void setCache(IMemoryCache<String, Object> cache) {
        this.mCacheStore = cache;
    }


    /**
     * Returns the Cache object.
     *
     * @return the Cache object.
     */
    private IMemoryCache<String, Object> getCache() {
        return mCacheStore;
    }

    /**
     * Returns the Cacheable object which has the same key value.
     *
     * @param key The key value
     * @return The Cacheable object stored in the mCacheStore
     */
    public Object get(String key) {
        return mCacheStore.get(key);
    }

    /**
     * Returns the Object which has the same key value and is stored in the mCacheStore.
     *
     * @param key   The key value
     * @param clazz The Object's class that you are looking for.
     * @return The Object that which is stored in the mCacheStore.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        return (T) mCacheStore.get(key);
    }


    /**
     * Returns a COPY of all the stored objects which are instances of the given class.
     *
     * @param clazz The Object's Class DEFAULT that you are looking for.
     * @return The List of stored objects.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> toList(Class<T> clazz) {
        List<T> list = new ArrayList<T>();

        for (Object cacheable : this.mCacheStore.snapshot().values()) {
            if (clazz.isInstance(cacheable)) {
                list.add((T) cacheable);
            }
        }
        return list;
    }

    @Override
    public Object put(String key, Object value) {
        return this.mCacheStore.put(key, value);
    }

    @Override
    public Object remove(String key) {
        return this.mCacheStore.remove(key);
    }

    @Override
    public void clear() {
        this.mCacheStore.clear();
    }

    @Override
    public int size() {
        return this.mCacheStore.size();
    }

    @Override
    public int maxSize() {
        return this.mCacheStore.maxSize();
    }

    @Override
    public Map<String, Object> snapshot() {
        return this.mCacheStore.snapshot();
    }
}
