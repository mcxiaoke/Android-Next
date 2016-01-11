package com.mcxiaoke.next.http.transformer;

import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 12:25
 */
public class StringTransformer implements HttpTransformer<String> {
    @Override
    public String transform(final NextResponse response) throws IOException {
        return response.string();
    }
}
