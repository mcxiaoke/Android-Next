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

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            int widthSpec = MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.AT_MOST);
            int heightSpec = MeasureSpec.makeMeasureSpec(sizeHeight, MeasureSpec.AT_MOST);
            measureChild(child, widthSpec, heightSpec);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (lineWidth + width > sizeWidth) {
                mLineHeights.add(lineHeight);
                maxHeight += lineHeight;
                maxWidth = Math.max(maxWidth, lineWidth);
                lineWidth = 0;
                lineHeight = 0;
            }
            lineHeight = Math.max(lineHeight, height + lp.topMargin + lp.bottomMargin);
            lineWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;

            Log.e(TAG, "measure line height=" + lineHeight + " width=" + lineWidth);
        }
        mLineHeights.add(lineHeight);
        maxHeight += lineHeight;

        Log.e(TAG, "measure mLineHeights=" + mLineHeights);
        Log.e(TAG, "measure width=" + sizeWidth + " height=" + sizeHeight);
        Log.e(TAG, "measure maxWidth=" + maxWidth + " maxHeight=" + maxHeight);

        maxWidth = maxWidth + getPaddingLeft() + getPaddingRight();
        maxHeight = maxHeight + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY ? sizeWidth : maxWidth,
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : maxHeight);
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
                            final int r, final int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = r - l - getPaddingRight();
        int bottom = b - t - getPaddingBottom();

        Log.e(TAG, "layout left=" + left + " top=" + top + " right=" + right + " bottom=" + bottom);
        int maxWidth = getMeasuredWidth();
        int maxHeight = getMeasuredHeight();
        int count = getChildCount();
        int xPos = left;
        int yPos = top;
        int lineWidth = maxWidth;
        int lineHeight = 0;
        int line = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int cw = child.getMeasuredWidth();
            int ch = child.getMeasuredHeight();
            // break line
            if (xPos + cw > lineWidth) {
                xPos = left;
                yPos += lineHeight + lp.topMargin + lp.bottomMargin;
                line++;
            }
            xPos += lp.leftMargin;
            int cl = xPos;
            int ct = yPos;
            int cr = cl + cw;
            int cb = ct + ch;
            Log.e(TAG, "layout " + i + " width=" + cw + " height=" + ch);
            Log.e(TAG, "layout " + i + " line=" + line + " xPos=" + xPos + " yPos=" + yPos);
            Log.e(TAG, "layout " + i + " cl=" + cl + " ct=" + ct + " cr=" + line + " cb=" + cb);
            Log.e(TAG, "\n");
            child.layout(cl, ct, cr, cb);
            xPos += cw + lp.rightMargin;
            lineHeight = Math.max(lineHeight, ch);
        }
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
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
