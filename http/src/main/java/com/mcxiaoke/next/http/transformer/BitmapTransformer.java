package com.mcxiaoke.next.http.transformer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.mcxiaoke.next.http.NextResponse;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 16/1/11
 * Time: 14:29
 */
public class BitmapTransformer implements HttpTransformer<Bitmap> {
    @Override
    public Bitmap transform(final NextResponse response) throws IOException {
        return BitmapFactory.decodeStream(response.stream());
    }
}
