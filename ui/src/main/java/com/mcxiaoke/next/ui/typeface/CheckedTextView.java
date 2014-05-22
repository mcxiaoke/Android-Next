package com.mcxiaoke.next.ui.typeface;


import android.content.Context;
import android.util.AttributeSet;


public class CheckedTextView extends android.widget.CheckedTextView {
    public CheckedTextView(Context context) {
        super(context);
    }

    public CheckedTextView(Context context, AttributeSet attrs) {
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
