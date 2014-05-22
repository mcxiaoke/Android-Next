package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * LinearLayout that not duplicate parent state to child views
 * User: mcxiaoke
 * Date: 13-10-9
 * Time: 下午12:09
 */

/**
 * 不向子View传递PRESSED状态的FrameLayout
 */
public class NoPressStateFrameLayout extends FrameLayout {

    public NoPressStateFrameLayout(Context context) {
        super(context);
    }

    public NoPressStateFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
//        super.setPressed(pressed);
    }
}
