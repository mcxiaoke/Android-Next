package com.mcxiaoke.next.samples.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 15/8/27
 * Time: 16:01
 */
public class FlowLayout extends ViewGroup {
    public static final String TAG = FlowLayout.class.getSimpleName();

    public FlowLayout(final Context context) {
        super(context);
        setup(context);
    }

    public FlowLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public FlowLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {

    }

    private List<Integer> mLineHeights = new ArrayList<Integer>();
    private int mVerticalSpacing = 0;
    private int mHorizontalSpacing = 24;

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int maxWidth = 0;
        int maxHeight = 0;
        int lineWidth = 0;
        int lineHeight = 0;
        int count = getChildCount();

        mLineHeights.clear();
        int lineCount = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int widthSpec = MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.AT_MOST);
            int heightSpec = MeasureSpec.makeMeasureSpec(sizeHeight, MeasureSpec.AT_MOST);
            measureChild(child, widthSpec, heightSpec);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int width = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int height = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            if (lineWidth + width > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                mLineHeights.add(lineHeight);
                maxHeight += lineHeight;
                maxWidth = Math.max(maxWidth, lineWidth - mHorizontalSpacing);
                lineHeight = height;
                lineWidth = width;
                lineCount++;
            } else {
                lineHeight = Math.max(lineHeight, height);
                lineWidth += width + mHorizontalSpacing;
            }

            if (i == count - 1) {
                mLineHeights.add(lineHeight);
                maxHeight += lineHeight;
            }
            Log.e(TAG, "measure " + lineCount + " height=" + height + " width=" + width);
            Log.e(TAG, "measure " + lineCount + " lineHeight=" + lineHeight + " lineWidth=" + lineWidth);
        }
        Log.e(TAG, "measure mLineHeights=" + mLineHeights + " lineCount=" + lineCount);
        Log.e(TAG, "measure width=" + sizeWidth + " height=" + sizeHeight);
        Log.e(TAG, "measure maxWidth=" + maxWidth + " maxHeight=" + maxHeight);

        maxWidth = maxWidth + getPaddingLeft() + getPaddingRight();
        maxHeight = maxHeight + getPaddingTop() + getPaddingBottom() + lineCount * mVerticalSpacing;
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : maxWidth,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : maxHeight);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
                            final int r, final int b) {
        Log.e(TAG, "layout l=" + l + " t=" + t
                + " r=" + r + " b=" + b);

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = r - l - getPaddingRight();
        int bottom = b - t - getPaddingBottom();

        int maxWidth = right - left;
        int maxHeight = bottom - top;
        Log.e(TAG, "layout left=" + left + " top=" + top
                + " right=" + right + " bottom=" + bottom);
        Log.e(TAG, "layout maxWidth=" + maxWidth + " maxHeight=" + maxHeight);
        int count = getChildCount();
        final List<Integer> heights = mLineHeights;
        int xPos = left;
        int yPos = top;
        int line = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childWSpace = childWidth + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight();

            if (xPos + childWSpace > maxWidth) {
                xPos = left;
                yPos += heights.get(line) + mVerticalSpacing;
                line++;
            }

            int lineHeight = 0;
            int verticalOffset = 0;
            if (line < heights.size()) {
                lineHeight = heights.get(line);
                verticalOffset = lineHeight - childHeight;
            }

            int cl = xPos + lp.leftMargin;
            int ct = yPos + lp.topMargin + verticalOffset;
            int cr = cl + childWidth;
            int cb = ct + childHeight;

            Log.e(TAG, "layout line:" + line + " index:" + i + " w=" + childWidth + " h=" + childHeight);
            Log.e(TAG, "layout cl=" + cl + " ct=" + ct + " cr=" + cr + " cb=" + cb);
            child.layout(cl, ct, cr, cb);
            xPos += childWSpace + mHorizontalSpacing;
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawChildMargins(canvas);
    }

    private void drawChildMargins(final Canvas canvas) {
        Paint paint0 = new Paint();
        paint0.setColor(Color.WHITE);
        paint0.setStyle(Style.FILL);
        Paint paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setStyle(Style.FILL);
        Paint paint2 = new Paint();
        paint2.setColor(Color.BLUE);
        paint2.setStyle(Style.FILL);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int margin = lp.leftMargin;
            int left = child.getLeft();
            int top = child.getTop();
            int right = child.getRight();
            int bottom = child.getBottom();
            Rect rectLeft = new Rect(left - margin, top, left, bottom);
            Rect rectTop = new Rect(left, top - margin, right, top);
            Rect rectRight = new Rect(right, top, right + margin, bottom);
            Rect rectBottom = new Rect(left, bottom, right, bottom + margin);
            canvas.drawRect(rectLeft, paint1);
            canvas.drawRect(rectTop, paint1);
            canvas.drawRect(rectRight, paint2);
            canvas.drawRect(rectBottom, paint2);
        }
        int lineCount = mLineHeights.size();
        int yPos = getPaddingTop();
        for (int i = 0; i < lineCount; i++) {
            yPos += mLineHeights.get(i) + mVerticalSpacing;
            canvas.drawLine(0, yPos, getMeasuredWidth(), yPos, paint0);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
