package com.mcxiaoke.next.http;

import com.mcxiaoke.next.io.ProxyOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:40
 */
public class ProgressOutputStream extends ProxyOutputStream {
    private ProgressCallback callback;
    private long totalSize;
    private long currentSize;

    public ProgressOutputStream(OutputStream out, ProgressCallback callback, long totalSize) {
        super(out);
        this.callback = callback;
        this.totalSize = totalSize;
    }

    @Override
    protected void afterWrite(final int n) throws IOException {
        super.afterWrite(n);
        if (totalSize > 0 && callback != null) {
            currentSize += n;
            callback.onProgress(currentSize, totalSize);
        }
    }

}
