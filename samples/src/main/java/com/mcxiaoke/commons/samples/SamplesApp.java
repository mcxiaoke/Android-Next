package com.mcxiaoke.commons.samples;

import android.app.Application;
import android.util.Log;
import com.mcxiaoke.commons.utils.LogUtils;

/**
 * User: mcxiaoke
 * Date: 13-9-28
 * Time: 下午7:30
 */
public class SamplesApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init(this, Log.VERBOSE, Log.ASSERT);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
