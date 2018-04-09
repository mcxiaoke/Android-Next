package com.mcxiaoke.next.samples.core;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.mcxiaoke.next.cache.IMemoryCache;
import com.mcxiaoke.next.cache.MemoryCache;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.task.Failure;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.Success;
import com.mcxiaoke.next.task.TaskBuilder;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.StringUtils;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-22
 * Time: 15:13
 */
public class TaskQueueSamples extends BaseActivity {
    public static final String TAG = TaskQueueSamples.class.getSimpleName();

    @BindView(R.id.input)
    EditText mEditText;

    @BindView(R.id.button1)
    Button mButton;

    @BindView(R.id.text1)
    TextView mTextView;

    @OnClick(R.id.button1)
    public void onClick(View view) {
        doRequest();
    }

    private boolean mRunning;

    private Object mCaller = new Object();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_next_executor);
        ButterKnife.bind(this);
        mEditText.setText("https://api.github.com/users/mcxiaoke");
        final IMemoryCache<String, String> cache = MemoryCache.mapCache();
        cache.put("key", "value");
        cache.put("hello", "world");
        Log.v(TAG, "cache key" + "=" + cache.get("key"));
        Log.v(TAG, "cache hello" + "=" + cache.get("hello"));
    }

    private int mCounter;

    private void doRequest() {

        TaskQueue.setDebug(true);

        final String url = mEditText.getText().toString();
        if (StringUtils.isEmpty(url)) {
            showToast("url cannot be null.");
            return;
        }

        final TaskCallback<String> callback = new TaskCallback<String>() {
            @Override
            public void onTaskCancelled(final String name, final Bundle extras) {
                println("task cancelled " + " index:"
                        + extras.getInt("index") + " thread:" + extras.getString(TASK_THREAD));
            }

            @Override
            public void onTaskStarted(final String name, final Bundle extras) {
                println("task started " + " index:" + extras.getInt("index"));
            }

            @Override
            public void onTaskFinished(final String name, final Bundle extras) {
//                println("task finished " + " index:"
//                        + extras.getInt("index") + " thread:" + extras.getString(TASK_THREAD));
            }

            @Override
            public void onTaskSuccess(final String result, final Bundle extras) {
                println("task success, index:"
                        + extras.getInt("index") + " thread: " + extras.getString(TASK_THREAD));
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                println("task failure index:"
                        + extras.getInt("index") + " thread:" + extras.getString(TASK_THREAD));
            }
        };

        TaskQueue concurrent = TaskQueue.concurrent(2);
        TaskQueue singleThread = TaskQueue.singleThread();

        TaskBuilder.create(getCallable(url, true)).with(this).check(true).callback(callback).on(concurrent).start();
        TaskBuilder.create(getCallable(url, true)).with(mCaller).check(true).callback(callback).on(concurrent).start();
        TaskBuilder.create(getCallable(url, true)).with(mEditText).check(true).callback(callback).on(concurrent).start();
        TaskBuilder.create(getCallable(url, true)).with(new JSONObject()).check(true).callback(callback).on(concurrent).start();

        TaskBuilder.create(getCallable(url, false)).with(this).check(true).callback(callback).on(singleThread).start();
        TaskBuilder.create(getCallable(url, false)).with(mCaller).check(true).callback(callback).on(singleThread).start();
        TaskBuilder.create(getCallable(url, false)).with(mEditText).check(true).callback(callback).on(singleThread).start();
        TaskBuilder.create(getCallable(url, false)).with(new JSONObject()).check(true).callback(callback).on(singleThread).start();

        TaskBuilder.create(getCallable(url, true), callback, this).on(concurrent).start();
        TaskBuilder.create(getCallable(url, false), callback, this).on(singleThread).start();
        TaskBuilder.create(getCallable(url, true), callback, this).on(concurrent).start();
        TaskBuilder.create(getCallable(url, false), callback, this).on(singleThread).start();
        TaskBuilder.create(getCallable(url, true), callback, "hello1").on(concurrent).start();
        TaskBuilder.create(getCallable(url, false), callback, "hello2").on(singleThread).start();
        TaskBuilder.create(getCallable(url, true), callback, "hello3").on(concurrent).start();
        TaskBuilder.create(getCallable(url, false), callback, "hello4").on(singleThread).start();

        singleThread.add(getCallable(url, true), callback, new View(this));
        singleThread.add(getCallable(url, true), callback, new View(this));

        final TaskQueue queue = TaskQueue.concurrent(10);
        final JSONObject o1 = new JSONObject();
        final JSONObject o2 = new JSONObject();
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    queue.add(getCallable(url, false), callback, o1);
                    Log.e(TAG, " create thread No." + i);
                }
            }
        }.start();

//
//        TaskQueue.getDefault().execute(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                queue.cancelAll(o1);
//                return null;
//            }
//        }, callback, this, false);
//        TaskQueue.getDefault().execute(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                SystemClock.sleep(200);
//                queue.cancelAll(o2);
//                return null;
//            }
//        }, callback, this, false);

    }

    private void taskDemo() {
        final String testUrl = "https://api.github.com/users/mcxiaoke";

        TaskBuilder.create(JSONObject.class).action(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.getDefault().get(testUrl).string();
                return new JSONObject(response);
            }
        }).callback(new SimpleTaskCallback<JSONObject>() {
            @Override
            public void onTaskSuccess(final JSONObject result, final Bundle extras) {
                super.onTaskSuccess(result, extras);
                Log.v("Task", "onTaskSuccess() result=" + result);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                super.onTaskFailure(ex, extras);
                Log.e("Task", "onTaskFailure() error=" + ex);
            }
        }).with(this).start();

        TaskBuilder.create(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.getDefault().get(testUrl).string();
                return new JSONObject(response);
            }
        }).success(new Success<JSONObject>() {
            @Override
            public void onSuccess(final JSONObject result, final Bundle extras) {
                Log.v("Task", "onSuccess() result=" + result);
            }
        }).failure(new Failure() {
            @Override
            public void onFailure(final Throwable ex, final Bundle extras) {
                Log.e("Task", "onFailure() error=" + ex);
            }
        }).with(this).start();

        /**
         TaskBuilder.create(callable) // 设置Callable
         .with(caller) // 设置Caller
         .action(callable) // 设置Callable/Runnable
         .callback(callback) // 设置TaskCallback
         .success(success) //设置任务成功回调
         .failure(failure) //设置任务失败回调
         .check(false) //设置是否检查Caller
         .dispatch(handler)// 回调方法所在线程，默认是主线程
         .on(queue) // 设置自定义的TaskQueue
         .build() // 生成 TaskInfo 对象
         .start(); // 开始运行任务
         **/

        TaskBuilder.create(String.class).callback(new TaskCallback<String>() {
            @Override
            public void onTaskStarted(final String name, final Bundle extras) {

            }

            @Override
            public void onTaskFinished(final String name, final Bundle extras) {
            }

            @Override
            public void onTaskCancelled(final String name, final Bundle extras) {

            }

            @Override
            public void onTaskSuccess(final String s, final Bundle extras) {
                final String group = extras.getString(TASK_GROUP);
                final String name = extras.getString(TASK_NAME);
                final int sequence = extras.getInt(TASK_SEQUENCE);
                final long delay = extras.getLong(TASK_DELAY);
                final long duration = extras.getLong(TASK_DURATION);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {

            }
        }).start();
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private Callable<String> getCallable(final String url, final boolean serial) {
        final int index = ++mCounter;
        println("Start http request. index:" + index + " serial:" + serial);
        return new TaskCallable<String>(TAG) {
            @Override
            public String call() throws Exception {
                final int random = Math.abs(RANDOM.nextInt());
                SystemClock.sleep(random % 5000 + 5000);
                if (random % 8 == 0) {
                    throw new IllegalArgumentException("random exception");
                }
                return "hello, world";
            }
        }.putExtra("index", index);
    }

    private void clearText() {
        mTextView.setText(null);
    }

    private void println(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, text);
                mTextView.append(text);
                mTextView.append("\n");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskQueue.getDefault().cancelAll(mCaller);
    }
}
