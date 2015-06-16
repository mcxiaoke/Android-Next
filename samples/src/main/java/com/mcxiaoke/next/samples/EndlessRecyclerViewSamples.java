package com.mcxiaoke.next.samples;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.next.recycler.EndlessRecyclerView;
import com.mcxiaoke.next.recycler.EndlessRecyclerView.OnLoadMoreListener;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskCallback;
import com.mcxiaoke.next.task.TaskQueue;

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
public class EndlessRecyclerViewSamples extends BaseActivity {
    public static final String TAG = EndlessRecyclerViewSamples.class.getSimpleName();

    @InjectView(android.R.id.list)
    EndlessRecyclerView mEndlessRecyclerView;

    private StringRecyclerAdapter mRecyclerAdapter;

    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_recycler);
        ButterKnife.inject(this);
        getActionBar().setTitle(TAG);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerAdapter = new StringRecyclerAdapter(this, new ArrayList<String>());
        mRecyclerAdapter.addAll(buildData());
        mEndlessRecyclerView.setAdapter(mRecyclerAdapter);
        mEndlessRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final EndlessRecyclerView view) {
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
                Thread.sleep(3000);
                return buildData();
            }
        };
        final TaskCallback<List<String>> callback = new SimpleTaskCallback<List<String>>() {
            @Override
            public void onTaskSuccess(List<String> strings, Bundle extras) {
                super.onTaskSuccess(strings, extras);
                if (strings != null) {
                    mRecyclerAdapter.addAll(strings);
                    mEndlessRecyclerView.onComplete();
                } else {
                    mEndlessRecyclerView.enable(false);
                    mEndlessRecyclerView.showText("没有更多数据了。");
                }
            }

            @Override
            public void onTaskFailure(Throwable e, Bundle extras) {
                super.onTaskFailure(e, extras);
                mEndlessRecyclerView.showEmpty();
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
