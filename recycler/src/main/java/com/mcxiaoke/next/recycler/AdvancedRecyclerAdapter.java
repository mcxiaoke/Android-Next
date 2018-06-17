package com.mcxiaoke.next.recycler;

import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.ViewGroup;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.ItemViewHolder;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.ViewHolderCreator;
import com.mcxiaoke.next.recycler.AdvancedRecyclerView.SimpleViewHolderCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 2018/6/17
 * Time: 17:13
 */
class AdvancedRecyclerAdapter extends HeaderFooterRecyclerAdapter {
    private static final String TAG = "AdvancedRecyclerAdapter";
    private static final int VIEW_TYPE_LOADING_HEADER = 90001;
    private static final int VIEW_TYPE_LOADING_FOOTER = 90002;
    private static final int DEFAULT_LOADING_HEADER_LAYOUT = R.layout.layout_advanced_recycler_view_loading;
    private static final int DEFAULT_LOADING_FOOTER_LAYOUT = R.layout.layout_advanced_recycler_view_loading;

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

    private ViewHolderCreator<ItemViewHolder> mHeaderCreator =
            new SimpleViewHolderCreator(DEFAULT_LOADING_HEADER_LAYOUT);
    private ViewHolderCreator<ItemViewHolder> mFooterCreator =
            new SimpleViewHolderCreator(DEFAULT_LOADING_FOOTER_LAYOUT);


    private RecyclerView.Adapter mWrapped;
    private Handler mHandler = new Handler();
    private boolean mHeaderLoading = false;
    private boolean mFooterLoading = false;
    private List<ViewHolderCreator> mHeaders = new ArrayList<>();
    private List<ViewHolderCreator> mFooters = new ArrayList<>();

    public AdvancedRecyclerAdapter(final RecyclerView.Adapter<?> adapter) {
        mWrapped = adapter;
        mWrapped.registerAdapterDataObserver(mAdapterDataObserver);
    }

    public boolean isHeaderLoading() {
        return mHeaderLoading;
    }

    public void setHeaderLoading(final boolean loading) {
        if (loading != mHeaderLoading) {
            mHeaderLoading = loading;
            if (loading) {
                notifyHeaderItemInserted(0);
            } else {
                notifyHeaderItemRemoved(0);
            }
        }
    }

    public boolean isFooterLoading() {
        return mFooterLoading;
    }

    public void setFooterLoading(final boolean loading) {
        if (loading != mFooterLoading) {
            mFooterLoading = loading;
            if (loading) {
                notifyFooterItemInserted(0);
            } else {
                notifyFooterItemRemoved(0);
            }
        }
    }

    public void setLoadingHeader(final ViewHolderCreator<ItemViewHolder> creator) {
        mHeaderCreator = creator;
        notifyHeaderItemChanged(0);
    }

    public void setLoadingFooter(final ViewHolderCreator<ItemViewHolder> creator) {
        mFooterCreator = creator;
        notifyFooterItemChanged(0);
    }

    public void setLoadingHeader(@LayoutRes final int layoutRes) {
        setLoadingHeader(new SimpleViewHolderCreator(layoutRes));
    }

    public void setLoadingFooter(@LayoutRes final int layoutRes) {
        setLoadingFooter(new SimpleViewHolderCreator(layoutRes));
    }

    public void addHeader(ViewHolderCreator header) {
        mHeaders.add(header);
        notifyHeaderItemInserted(mHeaders.size());
    }

    public int removeHeader(ViewHolderCreator header) {
        int index = mHeaders.indexOf(header);
        if (index != -1) {
            mHeaders.remove(index);
            notifyHeaderItemRemoved(index);
        }
        return index;
    }

    public void addFooter(ViewHolderCreator footer) {
        mFooters.add(footer);
        notifyFooterItemInserted(mFooters.size());
    }

    public int removeFooter(ViewHolderCreator footer) {
        int index = mFooters.indexOf(footer);
        if (index != -1) {
            mFooters.remove(index);
            notifyFooterItemRemoved(index);
        }
        return index;
    }

    public Adapter getWrapped() {
        return mWrapped;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mWrapped.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mWrapped.onDetachedFromRecyclerView(recyclerView);
        mWrapped.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewAttachedToWindow(@NonNull final ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mWrapped.onViewAttachedToWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewDetachedFromWindow(@NonNull final ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mWrapped.onViewDetachedFromWindow(holder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewRecycled(@NonNull final ViewHolder holder) {
        super.onViewRecycled(holder);
        mWrapped.onViewRecycled(holder);
    }

    @Override
    protected int getHeaderItemCount() {
        int count = 0;
        if (mHeaderLoading) {
            count++;
        }
        count += mHeaders.size();
        return count;
    }

    @Override
    protected int getFooterItemCount() {
        int count = 0;
        if (mFooterLoading) {
            count++;
        }
        count += mFooters.size();
        return count;
    }

    @Override
    protected int getContentItemCount() {
        return mWrapped.getItemCount();
    }

    @Override
    protected int getHeaderItemViewType(final int position) {
        if (mHeaderLoading) {
            if (position == 0) {
                return VIEW_TYPE_LOADING_HEADER;
            } else {
                return position - 1;
            }
        }
        return position;
    }

    @Override
    protected int getFooterItemViewType(final int position) {
        if (mFooterLoading) {
            if (position == 0) {
                return VIEW_TYPE_LOADING_FOOTER;
            } else {
                return position - 1;
            }
        }
        return position;
    }

    @Override
    protected int getContentItemViewType(final int position) {
        return mWrapped.getItemViewType(position);
    }


    @Override
    protected ViewHolder onCreateHeaderItemViewHolder(final ViewGroup parent, final int viewType) {
        final ViewHolder holder;
        if (viewType == VIEW_TYPE_LOADING_HEADER) {
            holder = mHeaderCreator.create(parent);
        } else {
            holder = mHeaders.get(viewType).create(parent);
        }
        // https://stackoverflow.com/questions/35474751/java-lang-throwable-addinarray-in-adapter
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    protected ViewHolder onCreateFooterItemViewHolder(final ViewGroup parent, final int viewType) {
        final ViewHolder holder;
        if (viewType == VIEW_TYPE_LOADING_FOOTER) {
            holder = mFooterCreator.create(parent);
        } else {
            holder = mFooters.get(viewType).create(parent);
        }
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    protected ViewHolder onCreateContentItemViewHolder(final ViewGroup parent, final int viewType) {
        return mWrapped.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void onBindHeaderItemViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(position);
        }
    }

    @Override
    protected void onBindFooterItemViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(position);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindContentItemViewHolder(final ViewHolder holder, final int position) {
        mWrapped.onBindViewHolder(holder, position);
    }

}
