package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * LinearLayout that not duplicate parent state to child views
 * User: mcxiaoke
 * Date: 13-10-9
 * Time: 下午12:09
 */

/**
 * 不向子View传递PRESSED状态的LinearLayout
 */
public class NoPressStateLinearLayout extends LinearLayout {

    public NoPressStateLinearLayout(Context context) {
        super(context);
    }

    public NoPressStateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
//        super.setPressed(pressed);
    }
}
