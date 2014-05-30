package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:21
 */

/**
 * POST/PUT write data progress callback
 */
public interface ProgressCallback {
    ProgressCallback DEFAULT = new ProgressCallback() {
        @Override
        public void onProgress(long currentSize, long totalSize) {

        }
    };

    void onProgress(long currentSize, long totalSize);
}
