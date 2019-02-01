/**
 * User: mcxiaoke
 * Date: 2016/10/25
 * Time: 12:28
 */

package com.mcxiaoke.next.http;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

import java.io.IOException;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody body;
    private final ProgressListener listener;
    private BufferedSink buffer;

    public ProgressRequestBody(RequestBody body, ProgressListener listener) {
        this.body = body;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return body.contentLength();
    }

    public RequestBody getBody() {
        return body;
    }

    public ProgressListener getListener() {
        return listener;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (buffer == null) {
            buffer = Okio.buffer(sink(sink));
        }
        body.writeTo(buffer);
        buffer.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if (listener != null) {
                    listener.onProgress(
                            bytesWritten, contentLength, bytesWritten == contentLength);
                }
            }
        };
    }
}
