package com.mcxiaoke.next.samples;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.next.recycler.RecyclerArrayAdapter;

import java.util.List;

/**
 * User: mcxiaoke
 * Date: 15-06-16
 * Time: 下午4:51
 */
public class StringRecyclerAdapter extends RecyclerArrayAdapter<String, MyHolder> {

    public StringRecyclerAdapter(Context context, List<String> objects) {
        super(context, objects);
    }

    @Override
    public MyHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new MyHolder(getInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        TextView textView = (TextView) holder.itemView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
    }
}


class MyHolder extends ViewHolder {

    public MyHolder(final View itemView) {
        super(itemView);
    }
}
