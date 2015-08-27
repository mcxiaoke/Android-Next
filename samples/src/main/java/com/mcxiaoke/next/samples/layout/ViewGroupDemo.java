package com.mcxiaoke.next.samples.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * User: mcxiaoke
 * Date: 15/8/27
 * Time: 11:54
 */
public class ViewGroupDemo extends ViewGroup {
    public static final String TAG = ViewGroupDemo.class.getSimpleName();

    public ViewGroupDemo(final Context context) {
        super(context);
        setup(context);
    }

    public ViewGroupDemo(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public ViewGroupDemo(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
//        LayoutInflater.from(context).inflate(R.layout.view_group_demo, this, true);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        final View child = getChildAt(0);
        Log.e(TAG, "onMeasure width=" + getMeasuredWidth()
                + " height=" + getMeasuredHeight());
        Log.e(TAG, "onMeasure child width=" + child.getMeasuredWidth()
                + " height=" + child.getMeasuredHeight());
    }

    @Override
    protected void onLayout(final boolean changed, final int l, final int t,
                            final int r, final int b) {
        Log.e(TAG, "onLayout left=" + l + " top=" + t + " right=" + r + " bottom=" + b);
        final View child = getChildAt(0);
        child.layout(l + getPaddingLeft(),
                t + getPaddingTop(),
                l + getPaddingLeft() + child.getMeasuredWidth(),
                t + getPaddingTop() + child.getMeasuredHeight());

    }
}
