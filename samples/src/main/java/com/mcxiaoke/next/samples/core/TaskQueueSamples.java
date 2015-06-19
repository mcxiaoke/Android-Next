package com.mcxiaoke.next.samples.core;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mcxiaoke.next.cache.IMemoryCache;
import com.mcxiaoke.next.cache.MemoryCache;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextResponse;
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

    @InjectView(R.id.input)
    EditText mEditText;

    @InjectView(R.id.button1)
    Button mButton;

    @InjectView(R.id.text1)
    TextView mTextView;

    @OnClick(R.id.button1)
    public void onClick(View view) {
        doRequest();
    }

    private boolean mRunning;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.act_next_executor);
        ButterKnife.inject(this);
        mEditText.setText("https://api.github.com/users/mcxiaoke");
        TaskQueue.getDefault().setDebug(true);

        final IMemoryCache<String, String> cache = MemoryCache.mapCache();
        cache.put("key", "value");
        cache.put("hello", "world");
        Log.v(TAG, "cache key" + "=" + cache.get("key"));
        Log.v(TAG, "cache hello" + "=" + cache.get("hello"));
    }

    private int mCounter;

    private void doRequest() {

        final String url = mEditText.getText().toString();
        if (StringUtils.isEmpty(url)) {
            showToast("url cannot be null.");
            return;
        }

        final TaskCallback<String> callback = new SimpleTaskCallback<String>() {
            @Override
            public void onTaskSuccess(final String result, final Bundle extras) {
                println("success, end http request index:" + extras.getInt("index"));
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                println("failure, end http request index:" + extras.getInt("index"));
            }
        };

        TaskBuilder.create(getCallable(url, true)).with(this).callback(callback).serial(true).start();
        TaskBuilder.create(getCallable(url, false)).with(this).callback(callback).serial(false).start();

        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);

        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
    }

    private void taskDemo() {
        final String testUrl = "https://api.github.com/users/mcxiaoke";

        TaskBuilder.create(JSONObject.class).action(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.get(testUrl).string();
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
        }).with(this).serial(false).start();

        TaskBuilder.create(new Callable<JSONObject>() {
            @Override
            public JSONObject call() throws Exception {
                final String response = NextClient.get(testUrl).string();
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
        }).serial(true).with(this).start();

        /**
         TaskBuilder.create(callable) // 设置Callable
         .with(caller) // 设置Caller
         .action(callable) // 设置Callable/Runnable
         .callback(callback) // 设置TaskCallback
         .success(success) //设置任务成功回调
         .failure(failure) //设置任务失败回调
         .check(false) //设置是否检查Caller
         .dispatch(handler)// 回调方法所在线程，默认是主线程
         .serial(false) // 是否按顺序依次执行
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
                SystemClock.sleep(Math.abs(RANDOM.nextInt()) % 3000 + 2000);
                final NextResponse response = NextClient.get(url);
                return response.string();
            }
        }.putExtra("index", index);
    }

    private void clearText() {
        mTextView.setText(null);
    }

    private void println(CharSequence text) {
        mTextView.append(text);
        mTextView.append("\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskQueue.getDefault().cancelAll(this);
    }
}
