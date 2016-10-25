/**
 * User: mcxiaoke
 * Date: 2016/10/25
 * Time: 12:28
 */

package com.mcxiaoke.next.http;

public interface ProgressListener {
    void onProgress(long bytesWritten, long contentLength, boolean done);
}
