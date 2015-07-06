package com.mcxiaoke.next.http;

import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 15/7/6
 * Time: 17:34
 */
class NoEmptyValuesHashMap extends HashMap<String, String> {

    public NoEmptyValuesHashMap() {
        super();
    }

    public NoEmptyValuesHashMap(final int capacity) {
        super(capacity);
    }

    public NoEmptyValuesHashMap(final int capacity, final float loadFactor) {
        super(capacity, loadFactor);
    }

    public NoEmptyValuesHashMap(final Map<String, String> map) {
        super();
        putAll(map);
    }

    @Override
    public String put(final String key, final String value) {
        if (value == null || value.isEmpty() || key.length() == 0) {
            return null;
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> map) {
        for (Entry<? extends String, ? extends String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
}
