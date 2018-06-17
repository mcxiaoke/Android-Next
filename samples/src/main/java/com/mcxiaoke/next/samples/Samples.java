package com.mcxiaoke.next.samples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.gson.reflect.TypeToken;
import com.mcxiaoke.next.http.HttpAsync;
import com.mcxiaoke.next.samples.bus.BasicBusSample;
import com.mcxiaoke.next.samples.bus.BasicBusSample2;
import com.mcxiaoke.next.samples.core.TaskQueueSamples;
import com.mcxiaoke.next.samples.http.NextClientSamples;
import com.mcxiaoke.next.samples.layout.LineLayoutSample;
import com.mcxiaoke.next.samples.layout.ViewGroupSample;
import com.mcxiaoke.next.samples.license.LicenseInfo;
import com.mcxiaoke.next.task.TaskBuilder;
import com.mcxiaoke.next.ui.widget.AdvancedShareActionProvider;
import com.mcxiaoke.next.ui.widget.ArrayAdapterCompat;
import com.mcxiaoke.next.ui.widget.ShareTarget;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午3:50
 */

/**
 * Samples主界面，同时也是AdvancedShareActionProvider的使用示例
 */
public class Samples extends BaseActivity {
    public static final String TAG = Samples.class.getSimpleName();

    @BindView(R.id.list)
    ListView mListView;

    private List<SampleInfo> mSampleListData;


    private AdvancedShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);
        initSamples();
        initListView();
        someTest();
    }

    private void someTest() {
        long start = System.nanoTime();
        for (int i = 0; i < 10000; ++i) {
            HttpAsync.class.getSimpleName();
            TaskBuilder.class.getSimpleName();
        }
        long ms = (System.nanoTime() - start) / 1000000;
        Log.e("someTest", "getSimpleName: " + ms + "ms");

        start = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            Type typeToken = new TypeToken<LicenseInfo>() {
            }.getType();
        }
        ms = (System.nanoTime() - start) / 1000000;
        Log.e("someTest", "getTypeToken: " + ms + "ms");

    }

    private void initSamples() {
        mSampleListData = new ArrayList<>();
        mSampleListData.add(new SampleInfo(RecyclerViewSample.TAG, RecyclerViewSample.class));
        mSampleListData.add(new SampleInfo(ViewGroupSample.TAG, ViewGroupSample.class));
        mSampleListData.add(new SampleInfo(LineLayoutSample.TAG, LineLayoutSample.class));
        mSampleListData.add(new SampleInfo(BasicBusSample.TAG, BasicBusSample.class));
        mSampleListData.add(new SampleInfo(BasicBusSample2.TAG, BasicBusSample2.class));
        mSampleListData.add(new SampleInfo(ListViewExtendSamples.TAG, ListViewExtendSamples.class));
        mSampleListData.add(new SampleInfo(StickyHeaderSamples.TAG, StickyHeaderSamples.class));
        mSampleListData.add(new SampleInfo(AlertDialogSamples.TAG, AlertDialogSamples.class));
        mSampleListData.add(new SampleInfo(TaskQueueSamples.TAG, TaskQueueSamples.class));
        mSampleListData.add(new SampleInfo(NextClientSamples.TAG, NextClientSamples.class));

        mSampleListData.add(new SampleInfo(BasicBusSample.TAG, BasicBusSample.class));
        mSampleListData.add(new SampleInfo(BasicBusSample2.TAG, BasicBusSample2.class));
        mSampleListData.add(new SampleInfo(ListViewExtendSamples.TAG, ListViewExtendSamples.class));
        mSampleListData.add(new SampleInfo(StickyHeaderSamples.TAG, StickyHeaderSamples.class));
        mSampleListData.add(new SampleInfo(AlertDialogSamples.TAG, AlertDialogSamples.class));
        mSampleListData.add(new SampleInfo(TaskQueueSamples.TAG, TaskQueueSamples.class));
        mSampleListData.add(new SampleInfo(NextClientSamples.TAG, NextClientSamples.class));

    }

    private void initListView() {
        mListView.setAdapter(new SampleListAdapter(this, mSampleListData));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SampleInfo sample = (SampleInfo) parent.getItemAtPosition(position);
                if (sample != null) {
                    Intent intent = new Intent(getActivity(), sample.target);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            StringBuilder builder = new StringBuilder();
            builder.append("Received Intent:\n");
            builder.append("Action: ").append(action).append("\n");
            builder.append("Extra Text: ").append(text).append("\n");
            builder.append("Extra subject: ").append(subject).append("\n");
            showToast(builder.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem share = menu.findItem(R.id.menu_share);
        mShareActionProvider = (AdvancedShareActionProvider) share.getActionProvider();
        updateShareIntent();
        return true;
    }

    private void updateShareIntent() {
        if (mShareActionProvider != null) {
            final MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.v(TAG, "Share Target, onMenuItemClicked");
                    return true;
                }
            };
            ShareTarget target = new ShareTarget("ShareTarget",
                    getResources().getDrawable(android.R.drawable.ic_menu_share), listener);
            mShareActionProvider.addShareTarget(target);
            final String pkg = getPackageName();
            mShareActionProvider.addCustomPackage("com.twitter.android");
            mShareActionProvider.addCustomPackage(pkg);
            mShareActionProvider.addCustomPackage("com.twitter.android");
            mShareActionProvider.removePackage("com.google.android.apps.plus");
            mShareActionProvider.setDefaultLength(3);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "I am some text for sharing!");
            mShareActionProvider.setShareIntent(intent);
//            mShareActionProvider.setIntentExtras("I am subject.", "I am some text for sharing!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    static class SampleInfo {
        public String name;
        public Class<?> target;

        public SampleInfo(String name, Class<?> target) {
            this.name = name;
            this.target = target;
        }
    }

    static class SampleListAdapter extends ArrayAdapterCompat<SampleInfo> {
        private LayoutInflater mInflater;

        public SampleListAdapter(Context context, List<SampleInfo> objects) {
            super(context, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            final SampleInfo sample = getItem(position);
            if (sample != null) {
                TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
                textView.setText(sample.name);
            }

            return convertView;
        }
    }
}
