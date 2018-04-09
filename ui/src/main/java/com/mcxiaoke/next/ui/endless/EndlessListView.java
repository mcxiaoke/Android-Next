package com.mcxiaoke.next.ui.endless;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.mcxiaoke.next.ui.BuildConfig;
import com.mcxiaoke.next.ui.view.SimpleProgressView;

/**
 * User: mcxiaoke
 * Date: 13-8-14
 * Time: 下午9:32
 */
public class EndlessListView extends ListView implements AbsListView.OnScrollListener,
        EndlessAdapter.OnFooterStateChangeListener {
    public static final String TAG = EndlessListView.class.getSimpleName();
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public enum RefreshMode {
        AUTO, CLICK, NONE
    }

    public interface OnFooterRefreshListener {
        void onFooterRefresh(EndlessListView listView);

        void onFooterIdle(EndlessListView listView);
    }

    private SimpleProgressView mFooter;
    private EndlessAdapter mEndlessAdapter;
    private OnScrollListener mOnScrollListener;
    private OnFooterRefreshListener mOnRefreshListener;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private int mTotalItemCount;
    private boolean mLoading = false;
    private int mScrollState = SCROLL_STATE_IDLE;
    private RefreshMode mRefreshMode = RefreshMode.AUTO;


    public EndlessListView(Context context) {
        super(context);
        initialize(context);
    }

    public EndlessListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
//        mGestureDetector = new GestureDetector(getContext(), new XYScrollDetector());
        mFooter = new SimpleProgressView(context);
        setFadingEdgeLength(0);
        super.setOnScrollListener(this);
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mEndlessAdapter = new EndlessAdapter(getContext(), adapter);
        mEndlessAdapter.setFooterView(mFooter);
        mEndlessAdapter.setFooterStateChangeListener(this);

        super.setAdapter(mEndlessAdapter);
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setRefreshMode(RefreshMode refreshMode) {
        if (DEBUG) {
            Log.v(TAG, "setRefreshMode() mode=" + refreshMode);
        }

        mRefreshMode = refreshMode;
        checkFooterClick();
    }

    public boolean isAutoRefresh() {
        return RefreshMode.AUTO.equals(mRefreshMode);
    }

    public boolean isClickRefresh() {
        return RefreshMode.CLICK.equals(mRefreshMode);
    }

    public RefreshMode getRefreshMode() {
        return mRefreshMode;
    }

    public void showFooterText(int resId) {
        if (DEBUG) {
            Log.v(TAG, "showFooterText() text=" + getResources().getString(resId));
        }
        showFooterText(getResources().getString(resId));
    }

    public void showFooterText(CharSequence text) {
        if (DEBUG) {
            Log.v(TAG, "showFooterText() text=" + text);
        }
        mEndlessAdapter.setState(EndlessAdapter.FooterState.IDLE, true);
        mFooter.showText(text);
    }

    /**
     * 只显示进度圆圈，但是不回调onRefresh
     */
    public void showFooterRefreshing() {
        setRefreshing(false);
    }

    private void setRefreshing(boolean needNotify) {
        if (DEBUG) {
            Log.v(TAG, "setRefreshing() isRefreshing=" + isRefreshing());
        }
        if (isRefreshing()) {
            return;
        }
        mEndlessAdapter.setState(EndlessAdapter.FooterState.PROGRESS, needNotify);
        mFooter.showProgress();
    }

    public void showFooterEmpty() {
        if (DEBUG) {
            Log.v(TAG, "showFooterEmpty() isRefreshing=" + isRefreshing());
        }
        mEndlessAdapter.setState(EndlessAdapter.FooterState.NONE, true);
        mFooter.showEmpty();
    }

    private boolean isRefreshing() {
        return mEndlessAdapter.isRefreshing();
    }

    private void checkFooterClick() {
        if (mFooter != null) {
            if (isClickRefresh()) {
                mFooter.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setRefreshing(true);
                    }
                });
            } else {
                mFooter.setOnClickListener(null);
            }
        }
    }

    private void onRefresh() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onFooterRefresh(this);
        }
    }

    private void onIdle() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onFooterIdle(this);
        }
    }

    private void onNone() {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onFooterIdle(this);
        }
    }

    @Override
    public void onFooterStateChanged(EndlessAdapter.FooterState state, EndlessAdapter adapter) {
        if (EndlessAdapter.FooterState.PROGRESS.equals(state)) {
            onRefresh();
        } else if (EndlessAdapter.FooterState.IDLE.equals(state)) {
            mLoading = false;
            onIdle();
        } else {
            mLoading = false;
            onNone();
        }
    }

    private void checkRefresh() {
        if (DEBUG) {
            Log.v(TAG, "checkRefresh() getRefreshMode=" + getRefreshMode() + " isRefreshing=" + isRefreshing());
            Log.v(TAG, "checkRefresh() mFirstVisibleItem=" + mFirstVisibleItem + " mVisibleItemCount=" + mVisibleItemCount);
            Log.v(TAG, "checkRefresh() mTotalItemCount=" + mTotalItemCount);
        }
        if (!isAutoRefresh()) {
            return;
        }
        if (isRefreshing()) {
            return;
        }
        if (mTotalItemCount == 0 || mVisibleItemCount >= mTotalItemCount) {
            return;
        }
        if (mFirstVisibleItem + mVisibleItemCount >= mTotalItemCount) {
            mLoading = true;
            setRefreshing(true);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
        mScrollState = scrollState;
        if (DEBUG) {
            Log.v(TAG, "onScrollStateChanged() scrollState=" + scrollState);
        }
        if (SCROLL_STATE_IDLE == mScrollState) {
            checkRefresh();
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
