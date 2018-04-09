package com.mcxiaoke.next.samples;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AbsListView;
import com.mcxiaoke.next.ui.endless.EndlessListView;

/**
 * User: mcxiaoke
 * Date: 13-11-26
 * Time: 下午4:06
 */
public class MotionTrackListView extends EndlessListView {
    public static final boolean DEBUG = true;
    public static final String TAG = MotionTrackListView.class.getSimpleName();

    public static final int CHECK_DISTANCE_MIN = 5;
    public static final int CHECK_DISTANCE_MAX = 50;

    public interface ScrollCallback {

        void onFlingUp(int distanceY);

        void onFlingDown(int distanceY);

        void onScroll(float distance, float delta);

        void onUp(float distance);
    }

    private GestureDetector mDetector;
    private ScrollCallback mScrollCallback;

    public MotionTrackListView(Context context) {
        super(context);
        initialize(context);
    }

    public MotionTrackListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    public MotionTrackListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        mDetector = new GestureDetector(context, new UpDownGestureDetector());
        mDetector.setIsLongpressEnabled(false);
    }

    public void setScrollCallback(ScrollCallback callback) {
        mScrollCallback = callback;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private float mTranslateY;
    private float mLastTouchY;
    private float mLastY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int index = ev.getActionIndex();
        int action = ev.getActionMasked();
        int pointerId = ev.getPointerId(index);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float downY = ev.getRawY();
                mTranslateY = 0;
                mLastTouchY = downY;
                mLastY = downY;
                if (DEBUG) {
                    Log.v(TAG, "ACTION_DOWN mLastY=" + mLastY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = ev.getRawY();
                float delta = currentY - mLastY;
                mTranslateY += delta;
                mLastY = currentY;
                if (DEBUG) {
                    Log.v(TAG, "ACTION_MOVE currentY=" + currentY + "  mTranslateY=" + mTranslateY);
                    Log.v(TAG, "ACTION_MOVE delta=" + delta);
                }
                if (mScrollCallback != null) {
                    mScrollCallback.onScroll(mTranslateY, delta);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                float endY = ev.getRawY();
                if (DEBUG) {
                    Log.v(TAG, "ACTION_UP  endY=" + endY + " mTranslateY=" + mTranslateY);
                }
                if (mScrollCallback != null) {
                    mScrollCallback.onUp(endY - mLastTouchY);
                }
                mLastTouchY = 0;
                mLastY = 0;
                mTranslateY = 0;
                break;
            default:
                break;
        }
        mDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    class UpDownGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float y1 = e1 == null ? -1 : e1.getRawY();
            float y2 = e2 == null ? -1 : e2.getRawY();
            if (DEBUG) {
                Log.v(TAG, "onFling() y1=" + y1 + " y2=" + y2);
                Log.v(TAG, "onFling() velocityY=" + velocityY);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            boolean ret = super.onScroll(e1, e2, distanceX, distanceY);
            float y1 = e1 == null ? -1 : e1.getY();
            float y2 = e2 == null ? -1 : e2.getY();
            if (DEBUG) {
                Log.v(TAG, "onScroll() y1=" + y1 + " y2=" + y2);
                Log.v(TAG, "onScroll() distanceY=" + distanceY);
            }
            int dy = (int) distanceY;
            int dyAbs = Math.abs(dy);
            if (dyAbs < CHECK_DISTANCE_MIN || dyAbs > CHECK_DISTANCE_MAX) {
                return ret;
            }
            return ret;
        }
    }


}
