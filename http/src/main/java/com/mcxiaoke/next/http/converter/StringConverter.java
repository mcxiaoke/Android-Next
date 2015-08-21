package com.mcxiaoke.next.http.converter;

import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:25
 */
public class StringConverter implements ResponseConverter<String> {
    @Override
    public String convert(final NextResponse response) throws IOException {
        return response.string();
    }
}
