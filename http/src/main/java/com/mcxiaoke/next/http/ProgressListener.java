package com.mcxiaoke.next.http;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 14:21
 */

/**
 * http write data progress listener
 */
public interface ProgressListener {

    void update(long bytesRead, long contentLength, boolean done);
}
