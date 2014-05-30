package com.mcxiaoke.next.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:40
 */
public class ProgressOutputStream extends BufferedOutputStream {
    private ProgressCallback callback;
    private long totalSize;
    private long currentSize;

    public ProgressOutputStream(OutputStream out, ProgressCallback callback, long totalSize) {
        super(out);
        this.callback = callback;
        this.totalSize = totalSize;
    }

    @Override
    public synchronized void write(byte[] buffer, int offset, int length) throws IOException {
        super.write(buffer, offset, length);
        if (totalSize > 0 && callback != null) {
            currentSize += length;
            callback.onProgress(currentSize, totalSize);
        }
    }

}
