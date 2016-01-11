package com.mcxiaoke.next.async.callback;

import android.os.Bundle;
import com.mcxiaoke.next.http.NextResponse;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 10:51
 */
public abstract class ResponseCallback implements AsyncCallback<NextResponse> {

    @Override
    public void onTaskCancelled(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskFailure(final Throwable ex, final Bundle extras) {

    }

    @Override
    public void onTaskFinished(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskStarted(final String name, final Bundle extras) {

    }

    @Override
    public void onTaskSuccess(final NextResponse response, final Bundle extras) {

    }
}
