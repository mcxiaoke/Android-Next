package com.mcxiaoke.next.ui.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * User: mcxiaoke
 * Date: 14-2-21
 * Time: 14:10
 */
public class SquaredLinearLayout extends LinearLayout {

    public SquaredLinearLayout(Context context) {
        super(context);
    }

    public SquaredLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SquaredLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalMeasureSpec = Utils.getSquaredMeasureSpec(this, widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }
}
