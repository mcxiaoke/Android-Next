package com.mcxiaoke.next.http;

import android.util.Pair;

/**
 * User: mcxiaoke
 * Date: 2017/4/12
 * Time: 17:54
 */

public class KeyValue extends Pair<String, String> {

    public static KeyValue of(final String key, final String value) {
        return new KeyValue(key, value);
    }

    public KeyValue(final String key, final String value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return "{" + String.valueOf(first) + ":" + String.valueOf(second) + "}";
    }
}
