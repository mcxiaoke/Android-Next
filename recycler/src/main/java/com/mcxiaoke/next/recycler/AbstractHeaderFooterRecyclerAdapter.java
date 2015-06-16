package com.mcxiaoke.next.recycler;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * User: mcxiaoke
 * Date: 15/5/28
 * Time: 10:07
 */
abstract class AbstractHeaderFooterRecyclerAdapter extends HeaderFooterRecyclerAdapter {

    @Override
    protected int getContentItemCount() {
        return 0;
    }

    @Override
    protected int getContentItemViewType(final int position) {
        return super.getContentItemViewType(position);
    }

    @Override
    protected int getFooterItemCount() {
        return 0;
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
    protected int getHeaderItemViewType(final int position) {
        return super.getHeaderItemViewType(position);
    }

    @Override
    public void notifyHeaderItemMoved(final int fromPosition, final int toPosition) {
        super.notifyHeaderItemMoved(fromPosition, toPosition);
    }

    @Override
    public void notifyHeaderItemRangeRemoved(final int positionStart, final int itemCount) {
        super.notifyHeaderItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public void notifyHeaderItemRemoved(final int position) {
        super.notifyHeaderItemRemoved(position);
    }

    @Override
    protected void onBindContentItemViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    protected void onBindHeaderItemViewHolder(final ViewHolder holder, final int position) {

    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    protected ViewHolder onCreateFooterItemViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }
}
