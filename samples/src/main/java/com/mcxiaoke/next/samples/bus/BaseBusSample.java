package com.mcxiaoke.next.samples.bus;

import android.os.Bundle;
import android.os.SystemClock;
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.bus.BusReceiver;
import com.mcxiaoke.next.samples.BaseActivity;
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
 * Time: 13:55
 */
abstract class BaseBusSample extends BaseActivity {
    public static final String TAG = BaseBusSample.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(TAG, "onCreate()");
//        Bus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(TAG, "onDestroy()");
//        Bus.getDefault().unregister(this);
    }


    public void test() {
        LogUtils.v(TAG, "test()");
        final Random random = new Random();
//        SystemClock.sleep(random.nextInt() % 1000);
//        Bus.getDefault().post("Completed " + new Date());
//        SystemClock.sleep(random.nextInt() % 200);
//        Bus.getDefault().post(new Object());
//        SystemClock.sleep(random.nextInt() % 200);
//        Bus.getDefault().post(123456789);
        Bus.getDefault().post("Completed " + 0);
        final TaskCallback<String> callback = new SimpleTaskCallback<String>() {

            @Override
            public void onTaskSuccess(final String s, final Bundle extras) {
                LogUtils.v(TAG, "onTaskSuccess()");
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {

            }
        };
        for (int i = 0; i < 5; i++) {
            final int index = i;
            final Callable<String> callable = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    LogUtils.v(TAG, "call() post event index=" + index);
                    SystemClock.sleep((500 * index));
                    Bus.getDefault().post("Completed " + index);
                    SystemClock.sleep(random.nextInt() % (500 * index));
                    Bus.getDefault().post(new Exception("error"));
                    SystemClock.sleep(random.nextInt() % (500 * index));
                    Bus.getDefault().post(12345);
                    SystemClock.sleep(random.nextInt() % (500 * index));
                    Bus.getDefault().post(new Thread());
                    SystemClock.sleep(random.nextInt() % (500 * index));
                    Bus.getDefault().post(new Object());
                    return "Completed " + new Date();
                }
            };

            TaskQueue.getDefault().add(callable, callback, this);
        }
    }

    @BusReceiver
    public void testReceiver3(Exception event) {
        LogUtils.v(TAG, "testReceiver3() event=" + event
                + " thread=" + Thread.currentThread().getName());
    }

    @BusReceiver
    public void testReceiver4(Integer event) {
        LogUtils.v(TAG, "testReceiver4() event=" + event
                + " thread=" + Thread.currentThread().getName());
    }

    @BusReceiver
    public void testReceiver5(String event) {
        LogUtils.v(TAG, "testReceiver5() event=" + event);
    }
}
