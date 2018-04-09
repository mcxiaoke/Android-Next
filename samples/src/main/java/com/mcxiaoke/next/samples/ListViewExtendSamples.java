package com.mcxiaoke.next.samples;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;
import com.mcxiaoke.next.ui.list.ListViewExtend;
import com.mcxiaoke.next.ui.list.ListViewExtend.OnRefreshListener;

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
public class ListViewExtendSamples extends BaseActivity {
    public static final String TAG = ListViewExtendSamples.class.getSimpleName();

    @BindView(android.R.id.list)
    ListViewExtend mListView;

    private StringListAdapter mArrayAdapter;

    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list_extend);
        ButterKnife.bind(this);
        getActionBar().setTitle(TAG);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initListView();
    }

    private void initListView() {
        mArrayAdapter = new StringListAdapter(this, new ArrayList<String>());
        mArrayAdapter.addAll(buildData());
        mListView.setAdapter(mArrayAdapter);
        mListView.setRefreshMode(ListViewExtend.MODE_AUTO);
        mListView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final ListViewExtend listView) {
                doLoadMore();
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
            public void onTaskSuccess(List<String> strings, Bundle extras) {
                super.onTaskSuccess(strings, extras);
                if (strings != null) {
                    mArrayAdapter.addAll(strings);
                    mListView.showFooterEmpty();
                } else {
                    mListView.setRefreshMode(ListViewExtend.MODE_NONE);
                    mListView.showFooterText("没有更多数据了。");
                }
            }

            @Override
            public void onTaskFailure(Throwable e, Bundle extras) {
                super.onTaskFailure(e, extras);
                mListView.showFooterEmpty();
            }
        };
        TaskQueue.getDefault().add(callable, callback, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TaskQueue.getDefault().cancelAll(this);
    }
}
