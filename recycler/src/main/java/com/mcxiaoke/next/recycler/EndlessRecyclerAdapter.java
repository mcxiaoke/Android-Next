package com.mcxiaoke.next.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.next.recycler.EndlessRecyclerView.ViewState;
import com.mcxiaoke.next.ui.view.SimpleProgressView;

/**
 * User: mcxiaoke
 * Date: 15/5/27
 * Time: 17:25
 */
class EndlessRecyclerAdapter
        extends AbstractHeaderFooterRecyclerAdapter {
    private static final String TAG = EndlessRecyclerAdapter.class.getSimpleName();

    private RecyclerView.Adapter mWrapped;
    private ViewState mViewState;

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Log.v(TAG, "onItemRangeRemoved() start=" + positionStart + " count=" + itemCount);
            notifyContentItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(final int fromPosition, final int toPosition, final int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            Log.v(TAG, "onItemRangeMoved() start=" + fromPosition + " count=" + itemCount);
            notifyContentItemRangeChanged(fromPosition, itemCount);
        }

        @Override
        public void onItemRangeInserted(final int positionStart, final int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            Log.v(TAG, "onItemRangeInserted() start=" + positionStart + " count=" + itemCount);
            notifyContentItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(final int positionStart, final int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            Log.v(TAG, "onItemRangeChanged() start=" + positionStart + " count=" + itemCount);
            notifyContentItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            final int headerCount = getHeaderItemCount();
            final int contentCount = getContentItemCount();
            Log.v(TAG, "onChanged() headerCount=" + headerCount + " contentCount=" + contentCount);
            notifyDataSetChanged();
        }
    };

    public EndlessRecyclerAdapter(final RecyclerView.Adapter adapter, final ViewState state) {
        mWrapped = adapter;
        mWrapped.registerAdapterDataObserver(mAdapterDataObserver);
        mViewState = state;
    }

    public Adapter getWrapped() {
        return mWrapped;
    }

    public void updateState() {
        notifyDataSetChanged();
    }

    @Override
    protected int getContentItemViewType(final int position) {
        return mWrapped.getItemViewType(position);
    }

    @Override
    protected int getFooterItemViewType(final int position) {
        return super.getFooterItemViewType(position);
    }

    @Override
    public long getItemId(final int position) {
        return mWrapped.getItemId(position);
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mWrapped.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mWrapped.onDetachedFromRecyclerView(recyclerView);
        mWrapped.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(final ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mWrapped.onViewAttachedToWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewDetachedFromWindow(final ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mWrapped.onViewDetachedFromWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewRecycled(final ViewHolder holder) {
        mWrapped.onViewRecycled(holder);
    }

    @Override
    public void setHasStableIds(final boolean hasStableIds) {
        mWrapped.setHasStableIds(hasStableIds);
    }

    @Override
    protected int getFooterItemCount() {
        return (mViewState.getDisplay() == EndlessRecyclerView.DISPLAY_HIDE) ? 0 : 1;
    }

    @Override
    protected int getContentItemCount() {
        return mWrapped.getItemCount();
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(final ViewGroup parent, final int viewType) {
        return mWrapped.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected ViewHolder onCreateFooterItemViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final SimpleProgressView view = (SimpleProgressView) inflater.inflate(R.layout.recycler_footer, parent, false);
        Log.v(TAG, "onCreateFooterItemViewHolder() view=" + view);
        return new SimpleViewHolder(view);
    }

    @Override
    protected void onBindFooterItemViewHolder(final ViewHolder holder, final int position) {
        final SimpleProgressView view = (SimpleProgressView) holder.itemView;
        Log.v(TAG, "onBindFooterItemViewHolder() view=" + view);
        switch (mViewState.getDisplay()) {
            case EndlessRecyclerView.DISPLAY_PROGRESS:
                view.showProgress();
                break;
            case EndlessRecyclerView.DISPLAY_TEXT:
                view.showText(mViewState.getText());
                break;
            case EndlessRecyclerView.DISPLAY_HIDE:
                view.hide();
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindContentItemViewHolder(final ViewHolder holder, final int position) {
        mWrapped.onBindViewHolder(holder, position);
    }


    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
