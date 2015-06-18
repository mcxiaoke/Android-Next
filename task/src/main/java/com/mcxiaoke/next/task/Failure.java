package com.mcxiaoke.next.task;

import android.os.Bundle;

/**
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 14:58
 */
public interface Failure {
    void onFailure(Throwable ex, final Bundle extras);
}
