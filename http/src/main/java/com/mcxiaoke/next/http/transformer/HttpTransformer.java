package com.mcxiaoke.next.http.transformer;

import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:24
 */
public interface HttpTransformer<T> {

    T transform(NextResponse response) throws IOException;
}
