package com.mcxiaoke.next.samples;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.next.ui.widget.ArrayAdapterCompat;

import java.util.List;

/**
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午4:51
 */
public class StringListAdapter extends ArrayAdapterCompat<String> {
    private LayoutInflater mInflater;

    public StringListAdapter(Context context, List<String> objects) {
        super(context, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
        return convertView;
    }
}
