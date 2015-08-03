package com.mcxiaoke.next.samples.bus;

import android.os.Bundle;
import android.os.SystemClock;
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.bus.BusReceiver;
import com.mcxiaoke.next.samples.BuildConfig;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 15/7/31
 * Time: 13:27
 */
public class BasicBusSample2 extends BaseBusSample {
    public static final String TAG = BasicBusSample2.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "onCreate()");
        setContentView(R.layout.act_bus_basic);
        test();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy()");
    }


    @BusReceiver
    public void testReceiver2(String event) {
        LogUtils.v(TAG, "testReceiver2() event=" + event
                + " thread=" + Thread.currentThread().getName());
    }

}
