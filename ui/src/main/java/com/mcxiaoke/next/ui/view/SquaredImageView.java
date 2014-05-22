package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An image view which always remains square with respect to its width.
 * <p/>
 * User: mcxiaoke
 * Date: 13-10-8
 * Time: 上午11:59
 */

/**
 * 强制高宽相等的正方形ImageView
 */
public class SquaredImageView extends ImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquaredImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
