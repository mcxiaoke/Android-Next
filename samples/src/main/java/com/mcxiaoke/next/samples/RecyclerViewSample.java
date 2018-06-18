package com.mcxiaoke.next.samples;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.next.recycler.AdvancedRecyclerArrayAdapter;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.ItemViewHolder;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.OnLoadDataListener;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.ViewHolderCreator;
import com.mcxiaoke.next.task.SimpleTaskCallback;
import com.mcxiaoke.next.task.TaskBuilder;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 2018/6/17
 * Time: 17:53
 */
public class RecyclerViewSample extends BaseActivity {
    public static final String TAG = "NextRecyclerViewSample";

    private AdvancedRecyclerView recyclerView;
    private SampleAdapter arrayAdapter;
    private Random random = new Random();
    private static int sCounter = 0;

    static class SimpleViewHolder extends ItemViewHolder {
        private TextView textView;

        public SimpleViewHolder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text1);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void bind(final int position) {
            super.bind(position);
            textView.setText("Header Text " + position);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_next_recycler_view);

        arrayAdapter = new SampleAdapter();
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(arrayAdapter);
        recyclerView.setEnableHeaderLoading(true);
        recyclerView.setEnableFooterLoading(true);
        recyclerView.setOnLoadDataListener(new OnLoadDataListener() {
            @Override
            public void onHeaderLoading(final AdvancedRecyclerView recyclerView) {
                addHeaderDataAsync();
            }


            @Override
            public void onFooterLoading(final AdvancedRecyclerView recyclerView) {
                addFooterDataAsync();
            }
        });
        recyclerView.addHeader(R.layout.layout_simple_header);
        arrayAdapter.addAll(Data.TITLES);
        arrayAdapter.addAll(Data.TITLES);
    }


    private void addHeaderDataAsync() {
        TaskBuilder.create(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                SystemClock.sleep(random.nextInt(8) * 1000);
                return generateData(random.nextInt(30));
            }
        }).callback(new SimpleTaskCallback<List<String>>() {

            @Override
            public void onTaskSuccess(final List<String> strings, final Bundle extras) {
                super.onTaskSuccess(strings, extras);
                LogUtils.i(TAG, "addHeaderDataAsync thread=" + Thread.currentThread());
                arrayAdapter.addAll(0, strings);
                recyclerView.setHeaderLoading(false);
                recyclerView.setEnableHeaderLoading(false);
                recyclerView.addHeader(new ViewHolderCreator<ViewHolder>() {
                    @Override
                    public ViewHolder create(final ViewGroup parent) {
                        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                        View view = inflater.inflate(R.layout.layout_simple_header, parent, false);
                        return new SimpleViewHolder(view);
                    }

                    @Override
                    public void bind(final ViewHolder holder, final int position) {

                    }
                });
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                super.onTaskFailure(ex, extras);
            }
        }).with(this).start();
    }


    private void addFooterDataAsync() {
        TaskBuilder.create(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                SystemClock.sleep(random.nextInt(8) * 1000);
                return generateData(random.nextInt(30));
            }
        }).callback(new SimpleTaskCallback<List<String>>() {

            @Override
            public void onTaskSuccess(final List<String> strings, final Bundle extras) {
                super.onTaskSuccess(strings, extras);
                LogUtils.i(TAG, "addFooterDataAsync thread=" + Thread.currentThread());
                arrayAdapter.addAll(strings);
                recyclerView.setFooterLoading(false);
                if (sCounter > 10) {
                    recyclerView.addFooter(R.layout.layout_simple_header);
                    recyclerView.setEnableFooterLoading(false);
                }
            }

            @Override
            public void onTaskFailure(final Throwable ex, final Bundle extras) {
                super.onTaskFailure(ex, extras);
            }
        }).with(this).start();
    }

    private List<String> generateData(int count) {
        sCounter++;
        final List<String> data = new ArrayList<>();
        for (int i = 0; i < 10 + count; i++) {
            data.add("List Item AAA " + sCounter + " - No. " + i);
        }
        return data;
    }

    static class SampleAdapter extends AdvancedRecyclerArrayAdapter<String, ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
            TextView textView = holder.itemView.findViewById(android.R.id.text1);
            textView.setText(getItem(position));
        }

        @Nullable
        @Override
        public Object getItemId(@NonNull final String item) {
            return item;
        }
    }
}
