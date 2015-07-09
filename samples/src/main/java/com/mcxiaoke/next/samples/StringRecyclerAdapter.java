package com.mcxiaoke.next.samples;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.mcxiaoke.next.recycler.HeaderFooterRecyclerArrayAdapter;

import java.util.List;

/**
 * User: mcxiaoke
 * Date: 15-06-16
 * Time: 下午4:51
 */
public class StringRecyclerAdapter extends HeaderFooterRecyclerArrayAdapter<String> {

    public StringRecyclerAdapter(Context context, List<String> objects) {
        super(context, objects);
    }

    @Override
    protected int getHeaderItemViewType(final int position) {
        return super.getHeaderItemViewType(position);
    }

    @Override
    protected int getFooterItemViewType(final int position) {
        return super.getFooterItemViewType(position);
    }

    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected int getFooterItemCount() {
        return 0;
    }

    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    protected ViewHolder onCreateFooterItemViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    protected void onBindHeaderItemViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    protected int getContentItemViewType(final int position) {
        return super.getContentItemViewType(position);
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(final ViewGroup parent, final int viewType) {
        return new MyHolder(getInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    protected void onBindContentItemViewHolder(final ViewHolder holder, final int position) {
        TextView textView = (TextView) holder.itemView.findViewById(android.R.id.text1);
        textView.setText(getItem(position));
    }


}


class MyHolder extends ViewHolder {

    public MyHolder(final View itemView) {
        super(itemView);
    }
}
