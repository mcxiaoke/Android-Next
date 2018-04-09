package com.mcxiaoke.next.samples;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.mcxiaoke.next.ui.endless.EndlessListView;

/**
 * User: mcxiaoke
 * Date: 13-12-5
 * Time: 上午11:07
 */
public class StickyHeaderSamples extends BaseActivity implements AbsListView.OnScrollListener, MotionTrackListView.ScrollCallback {
    public static final String TAG = StickyHeaderSamples.class.getSimpleName();

    @BindView(R.id.main)
    TouchFrameLayout mMainView;
    @BindView(android.R.id.list)
    MotionTrackListView mListView;

    @BindView(R.id.sticky_header)
    View mStickyHeader;

    View mDummyHeader;

    private static final int MSG_SCROLL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sticky);
        ButterKnife.bind(this);
        mDummyHeader = LayoutInflater.from(this).inflate(R.layout.dummy_header, mListView, false);
        mListView.setOnScrollListener(this);
        mListView.setRefreshMode(EndlessListView.RefreshMode.NONE);
        mListView.addHeaderView(mDummyHeader);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, Data.STRING_ARRAY));
        mListView.setScrollCallback(this);
        mListView.setFadingEdgeLength(0);
        mListView.setHorizontalFadingEdgeEnabled(false);
        mListView.setVerticalFadingEdgeEnabled(false);
    }

    @Override
    public void onFlingUp(int distanceY) {
//        Log.v(TAG, "onScrollUp() distanceY=" + distanceY);
//        mStickyHeader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFlingDown(int distanceY) {
//        Log.v(TAG, "onScrollDown() distanceY=" + distanceY);
//        mStickyHeader.setVisibility(View.VISIBLE);
    }

    private boolean mSticked;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onScroll(float distance, float delta) {
        // 直接用onScrollListener里的onScroll也可以
        int height = mStickyHeader.getHeight();
        Log.v(TAG, "onScroll() distance=" + distance + " delta=" + delta);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            float oldTranslateY = mStickyHeader.getTranslationY();
            Log.v(TAG, "onScroll() old translateY=" + oldTranslateY);
            float translateY = oldTranslateY + delta / 2f;
            Log.v(TAG, "onScroll() new translateY=" + translateY);
            translateY = Math.min(0, Math.max(-1f * height, translateY));
            if (translateY != oldTranslateY) {
                mStickyHeader.setTranslationY(translateY);
            } else {
                Log.e(TAG, "onScroll() no need scroll, return.");
            }
        } else {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mStickyHeader.getLayoutParams();
            final int oldTopMargin = lp.topMargin;
            Log.v(TAG, "onScroll() old topMargin=" + oldTopMargin);
            int topMargin = (int) (oldTopMargin + delta / 2f);
            topMargin = Math.min(0, Math.max(-1 * height, topMargin));
            Log.v(TAG, "onScroll() new topMargin=" + topMargin);
            if (topMargin != lp.topMargin) {
                lp.topMargin = topMargin;
                mStickyHeader.setLayoutParams(lp);
            } else {
                Log.e(TAG, "onScroll() no need update topMargin, return.");
            }
        }
    }

    @Override
    public void onUp(float distance) {
        Log.v(TAG, "onUp() distance=" + distance);
        if (distance > 0) {
            float height = 1.0f * mStickyHeader.getHeight();
            final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mStickyHeader.getLayoutParams();
            float topMargin = lp.topMargin;
            if (topMargin < 0) {
                float offset = Math.abs(topMargin);
                Log.v(TAG, "onUp() topMargin=" + topMargin + " offset=" + offset);
//                lp.topMargin = 0;
//                mStickyHeader.setLayoutParams(lp);
//                mStickyHeader.startAnimation(animation);

//                if ((int) topMargin != lp.topMargin) {
//                    lp.topMargin = (int) topMargin;
//                    mStickyHeader.setLayoutParams(lp);
//                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        Log.v(TAG, "onScrollStateChanged() scrollState=" + scrollState);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
