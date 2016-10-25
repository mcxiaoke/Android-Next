/**
 * User: mcxiaoke
 * Date: 2016/10/25
 * Time: 12:28
 */

package com.mcxiaoke.next.http;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import java.io.IOException;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody body;
    private final ProgressListener listener;
    private BufferedSource buffer;
    private long totalRead;

    public ProgressResponseBody(ResponseBody body, ProgressListener listener) {
        this.body = body;
        this.listener = listener;
        totalRead = 0L;
    }

    @Override
    public MediaType contentType() {
        return body.contentType();
    }

    @Override
    public long contentLength() {
        return body.contentLength();
    }

    public long totalBytesRead() {
        return totalRead;
    }

    @Override
    public BufferedSource source() {
        if (buffer == null) {
            buffer = Okio.buffer(source(body.source()));
        }
        return buffer;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalRead += bytesRead != -1 ? bytesRead : 0;
                if (listener != null) {
                    listener.onProgress(
                            totalRead, body.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
