package com.mcxiaoke.next.http.converter;

import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:24
 */
public interface ResponseConverter<T> {

    T convert(NextResponse response) throws IOException;
}
