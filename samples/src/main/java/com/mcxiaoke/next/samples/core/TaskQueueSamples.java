package com.mcxiaoke.next.samples.core;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.utils.StringUtils;

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
        TaskQueue.getDefault().setDebug(true);
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

        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);
        TaskQueue.getDefault().addSerially(getCallable(url, true), callback, this);

        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
        TaskQueue.getDefault().add(getCallable(url, false), callback, this);
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private Callable<String> getCallable(final String url, final boolean serial) {
        final int index = ++mCounter;
        println("Start http request. index:" + index + " serial:" + serial);
        final Bundle extras = new Bundle();
        extras.putInt("index", index);
        return new TaskCallable<String>(TAG) {
            @Override
            public String call() throws Exception {
                SystemClock.sleep(RANDOM.nextInt() % 3000);
                final NextResponse response = NextClient.get(url);
                return response.string();
            }
        };
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
