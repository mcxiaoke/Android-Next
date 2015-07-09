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
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = EndlessRecyclerAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_FOOTER = Integer.MAX_VALUE - 1;

    private RecyclerView.Adapter mWrapped;
    private ViewState mViewState;

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            Log.v(TAG, "onItemRangeRemoved() start=" + positionStart + " count=" + itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(final int fromPosition, final int toPosition, final int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            Log.v(TAG, "onItemRangeMoved() start=" + fromPosition + " count=" + itemCount);
            notifyItemRangeChanged(fromPosition, itemCount);
        }

        @Override
        public void onItemRangeInserted(final int positionStart, final int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            Log.v(TAG, "onItemRangeInserted() start=" + positionStart + " count=" + itemCount);
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(final int positionStart, final int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            Log.v(TAG, "onItemRangeChanged() start=" + positionStart + " count=" + itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }
    };

    public EndlessRecyclerAdapter(final RecyclerView.Adapter<?> adapter, final ViewState state) {
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

    @Override
    public void setHasStableIds(final boolean hasStableIds) {
        mWrapped.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(final int position) {
        if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            return position;
        } else {
            return mWrapped.getItemId(position);
        }
    }

    @Override
    public int getItemCount() {
        return mWrapped.getItemCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mWrapped.getItemCount()) {
            return VIEW_TYPE_FOOTER;
        }
        return mWrapped.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent,
                                         final int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return createFooterViewHolder(parent, viewType);
        } else {
            return mWrapped.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int type = getItemViewType(position);
        if (type == VIEW_TYPE_FOOTER) {
            bindFooterViewHolder(holder, position);
        } else {
            mWrapped.onBindViewHolder(holder, position);
        }
    }


    private int getFooterCount() {
        return (mViewState.getState() == EndlessRecyclerView.STATE_HIDE) ? 0 : 1;
    }

    private ViewHolder createFooterViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.recycler_footer, parent, false);
        return new SimpleViewHolder(view);
    }

    private void bindFooterViewHolder(final ViewHolder holder, final int position) {
        final SimpleProgressView view = (SimpleProgressView) holder.itemView;
        switch (mViewState.getState()) {
            case EndlessRecyclerView.STATE_PROGRESS:
                view.showProgress();
                break;
            case EndlessRecyclerView.STATE_TEXT:
                view.showText(mViewState.getText());
                break;
            case EndlessRecyclerView.STATE_HIDE:
                view.hide();
                break;
            default:
                break;
        }
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
