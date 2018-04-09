package com.mcxiaoke.next.samples.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.samples.license.LicenseHelper;
import com.mcxiaoke.next.samples.license.LicenseInfo;
import com.mcxiaoke.next.samples.license.LicenseView;
import com.mcxiaoke.next.ui.widget.ArrayAdapterCompat;
import com.mcxiaoke.next.utils.LogUtils;

import java.util.List;

/**
 * User: mcxiaoke
 * Date: 14-5-27
 * Time: 16:12
 */
public class LicenseSamples extends BaseActivity {
    public static final String TAG = LicenseSamples.class.getSimpleName();

    @BindView(android.R.id.list)
    ListView mListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_list);
        ButterKnife.bind(this);
        setLicenses();
    }

    private void setLicenses() {
        List<LicenseInfo> licenses = LicenseHelper.parse(this, R.raw.licenses);
        if (licenses == null || licenses.isEmpty()) {
            return;
        }

        LogUtils.e(TAG, "" + licenses);

        LicenseAdapter adapter = new LicenseAdapter(this, licenses);
        mListView.setAdapter(adapter);
    }

    static class LicenseAdapter extends ArrayAdapterCompat<LicenseInfo> {
        private LayoutInflater mInflater;

        public LicenseAdapter(final Context context, final List<LicenseInfo> objects) {
            super(context, objects);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, View convertView,
                            final ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_license, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final LicenseInfo info = getItem(position);
            if (info != null) {
                holder.view.setLicenseInfo(info);
            }
            return convertView;
        }


        static class ViewHolder {
            LicenseView view;

            public ViewHolder(View root) {
                view = (LicenseView) root.findViewById(R.id.license);
            }
        }


    }

}
