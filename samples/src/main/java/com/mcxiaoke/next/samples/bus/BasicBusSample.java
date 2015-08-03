package com.mcxiaoke.next.samples.bus;

import android.os.Bundle;
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.bus.BusReceiver;
import com.mcxiaoke.next.samples.BuildConfig;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.utils.LogUtils;

/**
 * User: mcxiaoke
 * Date: 15/7/31
 * Time: 13:27
 */
public class BasicBusSample extends BaseBusSample {
    public static final String TAG = BasicBusSample.class.getSimpleName();
    private static final boolean DEBUG = BuildConfig.DEBUG;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "onCreate()");
        setContentView(R.layout.act_bus_basic);
        Bus.getDefault().register(this);
        test();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy()");
        Bus.getDefault().unregister(this);
    }

    @BusReceiver
    public void testReceiver2(CharSequence event) {
        LogUtils.v(TAG, "testReceiver2() event=" + event
                + " thread=" + Thread.currentThread().getName());
    }

}
