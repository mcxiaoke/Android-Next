package com.mcxiaoke.next.http.transformer;

import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 14:09
 */
public class ResponseTransformer implements HttpTransformer<NextResponse> {
    @Override
    public NextResponse transform(final NextResponse response) throws IOException {
        return response;
    }
}
