package com.mcxiaoke.next.http;

import java.lang.reflect.Type;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 17:33
 */
public interface NextResponseTransformer {

    public <T> T transform(Class<T> type);

    public <T> T transform(Type type);
}
