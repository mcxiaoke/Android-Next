package com.mcxiaoke.next.samples;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.next.task.TaskExecutor;
import com.mcxiaoke.next.task.TaskMessage;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.ui.endless.EndlessListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * EndlessListView使用示例
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午4:44
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public class EndlessListViewSamples extends BaseActivity {
    public static final String TAG = EndlessListViewSamples.class.getSimpleName();

    @InjectView(android.R.id.list)
    EndlessListView mEndlessListView;

    private StringListAdapter mArrayAdapter;

    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_endless);
        ButterKnife.inject(this);
        getActionBar().setTitle(TAG);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initListView();
    }

    private void initListView() {
        mArrayAdapter = new StringListAdapter(this, new ArrayList<String>());
        mArrayAdapter.addAll(buildData());
        mEndlessListView.setAdapter(mArrayAdapter);
//        mEndlessListView.setRefreshMode(EndlessListView.RefreshMode.CLICK);
        mEndlessListView.setRefreshMode(EndlessListView.RefreshMode.AUTO);
        mEndlessListView.setOnFooterRefreshListener(new EndlessListView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(EndlessListView listView) {
                doLoadMore();
            }

            @Override
            public void onFooterIdle(EndlessListView listView) {

            }
        });
    }


    private List<String> buildData() {
        if (mIndex > 70) {
            return null;
        }
        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            strings.add("List Item: " + String.valueOf(mIndex++));
        }
        return strings;
    }

    private void doLoadMore() {
        final Callable<List<String>> callable = new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                Thread.sleep(2000);
                return buildData();
            }
        };
        final TaskCallback<List<String>> callback = new SimpleTaskCallback<List<String>>() {
            @Override
            public void onTaskSuccess(List<String> strings, TaskMessage message) {
                super.onTaskSuccess(strings, message);
                if (strings != null) {
                    mArrayAdapter.addAll(strings);
                    mEndlessListView.showFooterEmpty();
                } else {
                    mEndlessListView.setRefreshMode(EndlessListView.RefreshMode.NONE);
                    mEndlessListView.showFooterText("没有更多数据了。");
                }
            }

            @Override
            public void onTaskFailure(Throwable e, TaskMessage message) {
                super.onTaskFailure(e, message);
                mEndlessListView.showFooterEmpty();
            }
        };
        TaskExecutor.getDefault().execute(callable, callback, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskExecutor.getDefault().cancelAll(this);
    }
}
