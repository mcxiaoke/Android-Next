package com.mcxiaoke.next.recycler;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.next.utils.LogUtils;

/**
 * User: mcxiaoke
 * Date: 2018/6/17
 * Time: 16:52
 */
public class AdvancedRecyclerView extends RecyclerView {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public ItemViewHolder(final View itemView) {
            super(itemView);
        }

        public void bind(final int position) {

        }
    }

    public interface ViewHolderCreator<VH extends ViewHolder> {

        VH create(final ViewGroup parent);

        void bind(final VH holder, final int position);
    }

    public static class SimpleViewHolderCreator implements ViewHolderCreator<ItemViewHolder> {
        @LayoutRes
        private int layoutRes;

        public SimpleViewHolderCreator(@LayoutRes final int layoutRes) {
            this.layoutRes = layoutRes;
        }

        @Override
        public ItemViewHolder create(final ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(layoutRes, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void bind(final ItemViewHolder holder, final int position) {
            holder.bind(position);
        }
    }

    public interface OnLoadDataListener {
        void onHeaderLoading(AdvancedRecyclerView recyclerView);

        void onFooterLoading(AdvancedRecyclerView recyclerView);
    }

    public static final String TAG = "AdvancedRecyclerView";
    public static final int DEFAULT_SCROLL_OFFSET = 30;
    public static final int DEFAULT_THRESHOLD = 3;
    public static final boolean DEFAULT_HEADER_LOADING_ENABLE = false;
    public static final boolean DEFAULT_FOOTER_LOADING_ENABLE = false;

    private boolean mEnableHeaderLoading = DEFAULT_HEADER_LOADING_ENABLE;
    private boolean mEnableFooterLoading = DEFAULT_FOOTER_LOADING_ENABLE;
    private int mLoadingThreshold = DEFAULT_THRESHOLD;
    private OnLoadDataListener mOnLoadDataListener;
    private AdvancedRecyclerAdapter mAdapterProxy;
    private LinearLayoutManager mLayoutManager;
    private int mDy;
    private OnScrollListener mLoadingScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LogUtils.v(TAG, "onScrollStateChanged " + newState + " dy=" + mDy);
            if (Math.abs(mDy) > DEFAULT_SCROLL_OFFSET) {
                processScroll(mDy);
            }
            mDy = 0;
        }

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LogUtils.v(TAG, "onScrolled dy=" + dy);
            mDy += dy;
        }
    };

    private void processScroll(final int dy) {
        LogUtils.d(TAG, "processScroll dy=" + dy);
        final int threshold = mLoadingThreshold;
        final int firstPosition = mLayoutManager.findFirstVisibleItemPosition();
        final int lastPosition = mLayoutManager.findLastVisibleItemPosition();
        final int visibleItemCount = getChildCount();
        final int totalItemCount = mLayoutManager.getItemCount();
        if (dy > 0) {
            if (isEnableFooterLoading()) {
                if (!isFooterLoading()) {
                    if (lastPosition + threshold >= totalItemCount) {
                        setFooterLoading(true);
                        notifyFooterLoading();
                    }
                }
            }
        } else {
            if (isEnableHeaderLoading()) {
                if (!isHeaderLoading()) {
                    if (firstPosition - threshold <= 0) {
                        setHeaderLoading(true);
                        notifyHeaderLoading();
                    }
                }
            }
        }
    }


    public AdvancedRecyclerView(final Context context) {
        super(context);
        setup(context, null);
    }

    public AdvancedRecyclerView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public AdvancedRecyclerView(final Context context, @Nullable final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs);
    }

    private void setup(final Context context, final AttributeSet attrs) {
        mLayoutManager = new LinearLayoutManager(context);
        super.setLayoutManager(mLayoutManager);
    }

    private void notifyHeaderLoading() {
        LogUtils.d(TAG, "notifyHeaderLoading");
        if (mOnLoadDataListener != null) {
            mOnLoadDataListener.onHeaderLoading(this);
        }
    }

    private void notifyFooterLoading() {
        LogUtils.d(TAG, "notifyFooterLoading");
        if (mOnLoadDataListener != null) {
            mOnLoadDataListener.onFooterLoading(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnScrollListener(mLoadingScrollListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnScrollListener(mLoadingScrollListener);
    }

    @Override
    public void setLayoutManager(final LayoutManager layout) {
        throw new IllegalStateException("No need to set LayoutManager, default use LinearLayoutManager");
    }

    @Override
    public void setAdapter(final Adapter adapter) {
        if (adapter == null) {
            mAdapterProxy = null;
            super.setAdapter(null);
        } else {
            mAdapterProxy = new AdvancedRecyclerAdapter(adapter);
            super.setAdapter(mAdapterProxy);
        }
    }

    public boolean isEnableHeaderLoading() {
        return mEnableHeaderLoading;
    }

    public void setEnableHeaderLoading(final boolean enable) {
        mEnableHeaderLoading = enable;
    }

    public boolean isEnableFooterLoading() {
        return mEnableFooterLoading;
    }

    public void setEnableFooterLoading(final boolean enable) {
        mEnableFooterLoading = enable;
    }

    public boolean isHeaderLoading() {
        return mAdapterProxy.isHeaderLoading();
    }

    public void setHeaderLoading(final boolean loading) {
        mAdapterProxy.setHeaderLoading(loading);
    }

    public boolean isFooterLoading() {
        return mAdapterProxy.isFooterLoading();
    }

    public void setFooterLoading(final boolean loading) {
        mAdapterProxy.setFooterLoading(loading);
    }

    public int getLoadingThreshold() {
        return mLoadingThreshold;
    }

    public void setLoadingThreshold(final int threshold) {
        this.mLoadingThreshold = threshold;
    }

    public void setOnLoadDataListener(final OnLoadDataListener onLoadDataListener) {
        this.mOnLoadDataListener = onLoadDataListener;
    }

    public void setLoadingHeader(final ViewHolderCreator<ItemViewHolder> header) {
        mAdapterProxy.setLoadingHeader(header);
    }

    public void setLoadingFooter(final ViewHolderCreator<ItemViewHolder> footer) {
        mAdapterProxy.setLoadingFooter(footer);
    }

    public void setLoadingHeader(@LayoutRes final int layoutRes) {
        mAdapterProxy.setLoadingHeader(layoutRes);
    }

    public void setLoadingFooter(@LayoutRes final int layoutRes) {
        mAdapterProxy.setLoadingFooter(layoutRes);
    }

    public void addHeader(ViewHolderCreator<?> header) {
        mAdapterProxy.addHeader(header);
    }

    public void addHeader(@LayoutRes final int headerResId) {
        mAdapterProxy.addHeader(new SimpleViewHolderCreator(headerResId));
    }

    public void addFooter(ViewHolderCreator<?> footer) {
        mAdapterProxy.addFooter(footer);
    }

    public void addFooter(@LayoutRes final int footerResId) {
        mAdapterProxy.addFooter(new SimpleViewHolderCreator(footerResId));
    }

    public int removeHeader(ViewHolderCreator<?> header) {
        return mAdapterProxy.removeHeader(header);
    }

    public int removeFooter(ViewHolderCreator<?> footer) {
        return mAdapterProxy.removeFooter(footer);
    }
}
