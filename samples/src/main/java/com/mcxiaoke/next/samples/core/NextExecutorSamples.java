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
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskExecutor;
import com.mcxiaoke.next.task.TaskMessage;
import com.mcxiaoke.next.utils.StringUtils;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 14-5-22
 * Time: 15:13
 */
public class NextExecutorSamples extends BaseActivity {
    public static final String TAG = NextExecutorSamples.class.getSimpleName();

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
        TaskExecutor.getDefault().setDebug(true);
    }

    private int mCounter;

    private void doRequest() {

        final String url = mEditText.getText().toString();
        if (StringUtils.isEmpty(url)) {
            showToast("url cannot be null.");
            return;
        }

        final TaskCallback<String> callback = new TaskCallback<String>() {
            @Override
            public void onTaskSuccess(final String result, final TaskMessage message) {
                final int type = message.type;
                println("success, end http request index:" + type);
            }

            @Override
            public void onTaskFailure(final Throwable ex, final TaskMessage message) {
                final int type = message.type;
                println("failure, end http request index:" + type);
            }
        };

        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
        TaskExecutor.getDefault().executeSerially(getCallable(url), callback, this);
    }

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private Callable<String> getCallable(final String url) {
        final int index = ++mCounter;
        println("Start http request. index:" + index);
        final TaskMessage message = new TaskMessage(index, 123, 456, true);
        return new TaskCallable<String>(message) {
            @Override
            public String call() throws Exception {
                SystemClock.sleep(RANDOM.nextInt() % 3000);
                final NextRequest request = NextRequest.get(url);
                return request.asString();
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
        TaskExecutor.getDefault().cancelAll(this);
    }
}
