package com.mcxiaoke.next.samples.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.ProgressCallback;
import com.mcxiaoke.next.samples.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 17:19
 */
public class NextClientSamples extends BaseActivity {
    public static final String TAG = NextClientSamples.class.getSimpleName();
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            final String url = "http://moment.douban.com/app/";

            // simple use
            // NextResponse response = NextClient.get(url);

            // advanced use
            final NextClient client = new NextClient();
            final NextRequest request = NextRequest.newBuilder()
                    .url(url)
                    .encoding("UTF-8")
                    .method("GET")
                    .header("X-UDID", "cxgdg4543gd64tgdgs2tgdgst4")
                    .param("image", new File("IMG_20141222.jpg"), "image/jpeg")
                    .param("param1", "value1")
                            // http progress callback, for monitor upload/download file progress
                    .callback(new ProgressCallback() {
                        @Override
                        public void onProgress(final long currentSize, final long totalSize) {
                            Log.v(TAG, "http progress: " + currentSize * 100 / totalSize);
                        }
                    }).build();


            final NextResponse response = client.execute(request);
            // get response meta-data
            Log.v(TAG, "http response successful: " + response.successful());
            Log.v(TAG, "http response statusCode: " + response.code());
            Log.v(TAG, "http response statusMessage: " + response.message());
            Log.v(TAG, "http response contentLength: " + response.contentLength());
            Log.v(TAG, "http response contentType: " + response.contentType());
            // get 301/302/30x location header
            Log.v(TAG, "http response location: " + response.location());
            Log.v(TAG, "http response Server:" + response.header("Server"));
            Log.v(TAG, "http response Connection: " + response.header("Connection"));
            // get body as string
            Log.v(TAG, "http response content: " + response.string());
            // get body as  bytes
            final byte[] bytes = response.bytes();
            final Bitmap bitmap1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // get body as  stream
            final InputStream stream = response.stream();
            final Bitmap bitmap2 = BitmapFactory.decodeStream(stream);
            // get body as reader
            final InputStreamReader reader = response.reader(Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
