package com.mcxiaoke.next.samples.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class SimpleLineLayout extends ViewGroup {
    public static final String TAG = SimpleLineLayout.class.getSimpleName();

    public SimpleLineLayout(Context context) {
        super(context);
    }

    public SimpleLineLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleLineLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int maxWidth = 0;
        int maxHeight = 0;
        int availableWidth = sizeWidth;
        int availableHeight = sizeHeight;
        int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            // 从右往左消耗空间
            final View child = getChildAt(i);
            int widthSpec = MeasureSpec.makeMeasureSpec(availableWidth, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY);
            measureChild(child, widthSpec, heightSpec);
            Log.v(TAG, "onMeasure() child:" + i + " available=" + availableWidth
                    + " w=" + child.getMeasuredWidth() + " h=" + child.getMeasuredHeight());
            availableWidth -= child.getMeasuredWidth();
            maxWidth += child.getMeasuredWidth();
            maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
        }
        maxWidth = maxWidth + getPaddingLeft() + getPaddingRight();
        maxHeight = maxHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : maxWidth,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : maxHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int right = r - l - getPaddingRight();
        int top = getPaddingTop();
        int bottom = b - t - getPaddingBottom();

        int count = getChildCount();
        int offset = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            int cLeft, cTop, cRight, cBottom;
            cTop = top;
            cBottom = cTop + ch;
            if (i == count - 1) {
                // 最后一个右对齐
                cRight = right;
                cLeft = cRight - cw;
            } else {
                cLeft = left + offset;
                cRight = cLeft + cw;
                offset += cw;
            }
            child.layout(cLeft, cTop, cRight, cBottom);
        }
    }

//    @Override
//    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return new LayoutParams(getContext(), attrs);
//    }
//
//    @Override
//    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
//        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
//        return new LayoutParams(p);
//    }
//
//    public static class LayoutParams extends MarginLayoutParams {
//        public int left;
//        public int top;
//
//
//        public LayoutParams(Context c, AttributeSet attrs) {
//            super(c, attrs);
//        }
//
//        public LayoutParams(int width, int height) {
//            super(width, height);
//        }
//
//        public LayoutParams(MarginLayoutParams source) {
//            super(source);
//        }
//
//        public LayoutParams(ViewGroup.LayoutParams source) {
//            super(source);
//        }
//    }
}
