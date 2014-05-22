package com.mcxiaoke.next.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * LinearLayout that not duplicate parent state to child views
 * User: mcxiaoke
 * Date: 13-10-9
 * Time: 下午12:09
 */

/**
 * 不向子View传递PRESSED状态的RelativeLayout
 */
public class NoPressStateRelativeLayout extends RelativeLayout {

    public NoPressStateRelativeLayout(Context context) {
        super(context);
    }

    public NoPressStateRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
//        super.setPressed(pressed);
    }
}
