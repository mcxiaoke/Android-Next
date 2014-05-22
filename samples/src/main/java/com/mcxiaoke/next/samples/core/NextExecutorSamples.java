package com.mcxiaoke.next.samples.core;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.task.TaskCallable;
import com.mcxiaoke.next.task.TaskExecutor;
import com.mcxiaoke.next.task.TaskMessage;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.utils.StringUtils;

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

    private void doRequest() {
        if (mRunning) {
            showToast("task is running already!");
            return;
        }
        mRunning = true;

        final String url = mEditText.getText().toString();
        if (StringUtils.isEmpty(url)) {
            showToast("url cannot be null.");
            return;
        }

        final TaskCallback<String> callback = new TaskCallback<String>() {
            @Override
            public void onTaskSuccess(final String result, final TaskMessage message) {
                println("success, end http request.\n");
                println("Response:\n");
                println(result);
                println("\n");
                mRunning = false;
                hideProgressIndicator();
            }

            @Override
            public void onTaskFailure(final Throwable ex, final TaskMessage message) {
                println("failure, end http request.\n");
                println("Error:\n");
                println("" + Log.getStackTraceString(ex));
                println("\n");
                mRunning = false;
                hideProgressIndicator();
            }
        };
        final TaskMessage message = new TaskMessage(1001, 123, 456, true);
        final TaskCallable<String> callable = new TaskCallable<String>(message) {
            @Override
            public String call() throws Exception {
                final NextRequest request = NextRequest.get(url);
                return request.asString();
            }
        };
        clearText();
        println("url:" + url);
        println("Start http request...");
        showProgressIndicator();
        TaskExecutor.getDefault().execute(callable, callback, this);
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
