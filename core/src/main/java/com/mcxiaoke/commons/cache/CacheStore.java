package com.mcxiaoke.commons.cache;

import java.util.ArrayList;
import java.util.List;


/**
 * This singleton class is  to save, restore, remove and return your cached objects.
 */
public class CacheStore {

    ICache<Long, ICacheValue> store;

    // Private constructor prevents instantiation from other classes
    private CacheStore() {
        setDefault();
    }

    private void setDefault() {
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory store.
//        final int cacheSize = maxMemory / 8;
        setCache(CacheFactory.createCache());
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getDefault()
     * or the first access to SingletonHolder.DEFAULT, not before.
     */
    private static class SingletonHolder {
        public static final CacheStore DEFAULT = new CacheStore();
    }

    /**
     * Singleton pattern. Returns the same DEFAULT every time. This singleton is thread safe.
     *
     * @return Cache
     */
    public static CacheStore getDefault() {
        return SingletonHolder.DEFAULT;
    }

    /**
     * Returns the Cache object.
     *
     * @return the Cache object.
     */
    private ICache<Long, ICacheValue> getCache() {
        return store;
    }

    /**
     * Returns the Cacheable object stored in the store. If the Cacheable Object is not in the store,
     * it will be saved before return it.
     *
     * @param cacheable The Cacheable object that you get from somewhere.
     * @return The Cacheable object stored in the store.
     */
    public ICacheValue get(ICacheValue cacheable) {
        ICacheValue ce = store.get(cacheable.getKey());

        if (ce == null) {
            add(cacheable);
            ce = get(cacheable.getKey());
        }
        return ce;
    }

    /**
     * Returns the Cacheable object which has the same key value.
     *
     * @param key The key value
     * @return The Cacheable object stored in the store
     */
    public ICacheValue get(Long key) {
        return store.get(key);
    }

    /**
     * Returns the Object which has the same key value and is stored in the store.
     *
     * @param key   The key value
     * @param clazz The Object's class that you are looking for.
     * @return The Object that which is stored in the store.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Long key, Class<T> clazz) {
        return (T) store.get(key);
    }


    /**
     * Returns the Cacheable Object that is stored in the store. If not, it previously will be saved in the store.
     *
     * @param cacheable The Cacheable Object that you get from somewhere.
     * @param clazz     The Object's class that you are looking for.
     * @return The Cacheable Object that which is stored in the store.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(ICacheValue cacheable, Class<T> clazz) {
        T ce = (T) store.get(cacheable.getKey());

        if (ce == null) {
            add(cacheable);
            ce = get(cacheable.getKey(), clazz);
        }
        return ce;
    }

    /**
     * Sets the Cache
     *
     * @param cache The new Cache
     */
    private void setCache(ICache<Long, ICacheValue> cache) {
        this.store = cache;
    }

    /**
     * Adds a List of Cacheables into store.
     *
     * @param cacheables The Cacheable List
     */
    public void add(List<ICacheValue> cacheables) {
        for (ICacheValue cacheable : cacheables) {
            this.store.put(cacheable.getKey(), cacheable);
        }
    }

    /**
     * Adds a Cacheable Object into store.
     *
     * @param cacheable Cache
     * @return The  key
     */
    public long add(ICacheValue cacheable) {
        this.store.put(cacheable.getKey(), cacheable);
        return cacheable.getKey();
    }

    /**
     * Adds a List of some class that you need to store. Typically the class will be your Pojo Class.
     *
     * @param cacheables The List to store in the store.
     */
    public <T> void addList(List<T> cacheables) {
        for (T t : cacheables) {
            if (t instanceof ICacheValue) {
                this.store.put(((ICacheValue) t).getKey(), (ICacheValue) t);
            }
        }
    }

    /**
     * Adds a List of some class that you need to store. Typically the class will be your Pojo Class.
     *
     * @param cacheables The List to store into store.
     * @return List<T> The List that you previously stored in the store.
     */
    public <T> List<T> addAndReturnList(List<T> cacheables) {
        for (T t : cacheables) {
            if (t instanceof ICacheValue) {
                this.store.put(((ICacheValue) t).getKey(), (ICacheValue) t);
            }
        }
        return cacheables;
    }

    /**
     * Removes the Cacheable Object previously stored in the store.
     *
     * @param cacheable The Cacheable Object.
     */
    public void remove(ICacheValue cacheable) {
        this.store.remove(cacheable.getKey());
    }

    /**
     * Removes all the Cacheable Objects which are instances of clazz.
     *
     * @param clazz The class which instances should be removed.
     */
    public <T> void removeAll(Class<T> clazz) {
        List<T> list = toArray(clazz);

        for (T t : list) {
            if (t instanceof ICacheValue) {
                this.store.remove(((ICacheValue) t).getKey());
            }
        }
    }

    /**
     * Returns a COPY of all the stored Cacheable objects.
     *
     * @return The List of Cacheable objects.
     */
    public List<ICacheValue> toArray() {
        return (List<ICacheValue>) this.store.snapshot().values();
    }

    /**
     * Returns a COPY of all the stored objects which are instances of the given class.
     *
     * @param clazz The Object's Class DEFAULT that you are looking for.
     * @return The List of stored objects.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> toArray(Class<T> clazz) {
        List<T> list = new ArrayList<T>();

        for (ICacheValue cacheable : this.store.snapshot().values()) {
            if (clazz.isInstance(cacheable)) {
                list.add((T) cacheable);
            }
        }
        return list;
    }

    /**
     * Returns a COPY of all the stored Cacheable objects in primitive array.
     *
     * @return The array of stored objects.
     */
    public ICacheValue[] toPrimitiveArray() {
        return (ICacheValue[]) this.store.snapshot().values().toArray();
    }

    /**
     * Clears all the store.
     */
    public void clearCache() {
        this.store.clear();
    }

}
