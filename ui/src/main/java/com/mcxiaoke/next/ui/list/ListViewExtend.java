package com.mcxiaoke.next.ui.list;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * User: mcxiaoke
 * Date: 15-2-24
 * Time: 下午9:32
 */
public class ListViewExtend extends ListView implements AbsListView.OnScrollListener {
    public static final String TAG = ListViewExtend.class.getSimpleName();
    public static final boolean DEBUG = false;

    public static final int MODE_NONE = 0;
    public static final int MODE_MANUAL = 1;
    public static final int MODE_AUTO = 2;

    public interface OnRefreshListener {
        void onRefresh(ListViewExtend listView);
    }

    private AdapterExtend mEndlessAdapter;
    private OnScrollListener mOnScrollListener;
    private OnRefreshListener mOnRefreshListener;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private int mRefreshMode = MODE_NONE;


    public ListViewExtend(Context context) {
        super(context);
        setUp(context);
    }

    public ListViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    public ListViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp(context);
    }

    private void setUp(Context context) {
        setFadingEdgeLength(0);
        super.setOnScrollListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mEndlessAdapter = new AdapterExtend(getContext(), adapter);
        mEndlessAdapter.setRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final ListViewExtend listView) {
                notifyRefresh();
            }
        });
        super.setAdapter(mEndlessAdapter);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setRefreshMode(int mode) {
        if (DEBUG) {
            Log.v(TAG, "setRefreshMode() mode=" + mode);
        }
        mRefreshMode = mode;
        checkManualRefresh();
    }

    public int getRefreshMode() {
        return mRefreshMode;
    }

    /**
     * 只显示进度圆圈，但是不回调onRefresh
     */
    public void showFooterRefreshing() {
        mEndlessAdapter.setRefreshing(false);
    }

    public void showFooterEmpty() {
        mEndlessAdapter.showFooterEmpty();
    }

    public void showFooterText(int resId) {
        mEndlessAdapter.showFooterText(resId);
    }

    public void showFooterText(CharSequence text) {
        mEndlessAdapter.showFooterText(text);
    }

    public boolean isRefreshing() {
        return mEndlessAdapter.isRefreshing();
    }

    private void notifyRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh(this);
        }
    }

    private void checkManualRefresh() {
        if (mEndlessAdapter == null) {
            return;
        }
        if (mRefreshMode == MODE_MANUAL) {
            mEndlessAdapter.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEndlessAdapter.setRefreshing(true);
                }
            });
        } else {
            mEndlessAdapter.setOnClickListener(null);
        }
    }

    private void checkAutoRefresh() {
        if (DEBUG) {
            Log.v(TAG, "checkRefresh() getRefreshMode=" + getRefreshMode()
                    + " isRefreshing=" + isRefreshing());
            Log.v(TAG, "checkRefresh() mFirstVisibleItem=" + mFirstVisibleItem
                    + " mVisibleItemCount=" + mVisibleItemCount);
            Log.v(TAG, "checkRefresh() mTotalItemCount=" + mTotalItemCount);
        }
        if (mRefreshMode != MODE_AUTO) {
            return;
        }
        if (isRefreshing()) {
            return;
        }
        if (mTotalItemCount == 0 || mVisibleItemCount >= mTotalItemCount) {
            return;
        }
        if (mFirstVisibleItem + mVisibleItemCount >= mTotalItemCount) {
            mEndlessAdapter.setRefreshing(true);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        if (DEBUG) {
            Log.v(TAG, "onScrollStateChanged() scrollState=" + scrollState);
        }
        if (SCROLL_STATE_IDLE == scrollState) {
            checkAutoRefresh();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        mTotalItemCount = totalItemCount;
    }
}
