package com.mcxiaoke.commons.samples;

import android.app.Application;
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
        LogUtils.setEnable(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
