package com.mcxiaoke.next.samples.layout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * User: mcxiaoke
 * Date: 15/8/27
 * Time: 11:20
 */
public class CustomViewGroup extends ViewGroup {
    public CustomViewGroup(final Context context) {
        super(context);
        setup(context);
    }

    public CustomViewGroup(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public CustomViewGroup(final Context context, final AttributeSet attrs,
                           final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        TextView tv = new TextView(context);
        tv.setBackgroundColor(Color.RED);
        tv.setText(" tv.setBackgroundColor(Color.RED);");
        addView(tv);

        for (int i = 0; i < 200; i++) {
            tv = new TextView(context);
            tv.setBackgroundColor(Color.RED);
            tv.setText(" Some Item " + i);
            addView(tv);
        }


        tv = new TextView(context);
        tv.setBackgroundColor(Color.GREEN);
        tv.setText(" 2015-08-27");
        addView(tv);
        tv = new TextView(context);
        tv.setBackgroundColor(Color.YELLOW);
        tv.setText(" 10 YELLOW");
        addView(tv);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxLineHeight = Math.max(maxLineHeight, child.getMeasuredHeight());
            }
        }
    }

    private int maxLineHeight;

    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
                            final int r, final int b) {
        int maxWidth = r - l;
        int line = 1;
        int xPos = 0;
        int padding = 12;
        int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            int w = child.getMeasuredWidth();
            int h = child.getMeasuredHeight();

            if (xPos + w + padding > maxWidth) {
                // break line
                line++;
                xPos = 0;
            }
            int left = xPos + padding;
            int top = maxLineHeight * line;
            int right = left + w;
            int bottom = top + h;
            child.layout(left, top, right, bottom);
            xPos += w + padding;
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
        public int left;
        public int top;


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
