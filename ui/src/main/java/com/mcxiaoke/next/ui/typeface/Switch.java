package com.mcxiaoke.next.ui.typeface;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;


@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Switch extends android.widget.Switch {
    public Switch(Context context) {
        super(context);
    }

    public Switch(Context context, AttributeSet attrs) {
        super(context, attrs);

        // return early for eclipse preview mode
        if (isInEditMode()) return;

        FontCache.getInstance().setFont(this, attrs);
    }

    public void setFont(String fontPath) {
        FontCache.getInstance().setFont(this, fontPath);
    }

    public void setFont(int resId) {
        String fontPath = getContext().getString(resId);
        setFont(fontPath);
    }
}
