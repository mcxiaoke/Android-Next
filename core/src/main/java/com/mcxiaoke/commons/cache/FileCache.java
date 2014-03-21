package com.mcxiaoke.commons.cache;

import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 18:19
 */
public class FileCache implements ICache<String, String> {

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String put(String key, String value) {
        return null;
    }

    @Override
    public String remove(String key) {
        return null;
    }

    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int maxSize() {
        return 0;
    }

    @Override
    public Map<String, String> snapshot() {
        return null;
    }
}
