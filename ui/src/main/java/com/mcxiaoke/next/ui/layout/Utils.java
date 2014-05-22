package com.mcxiaoke.next.ui.layout;

import android.view.View;
import android.view.ViewGroup;

/**
 * User: mcxiaoke
 * Date: 14-2-21
 * Time: 14:16
 */
final class Utils {

    /**
     * http://stackoverflow.com/questions/8981029/simple-way-to-do-dynamic-but-square-layout
     */
    public static int getSquaredMeasureSpec(final ViewGroup viewGroup, int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (widthMode == View.MeasureSpec.EXACTLY && widthSize > 0) {
            size = widthSize;
        } else if (heightMode == View.MeasureSpec.EXACTLY && heightSize > 0) {
            size = heightSize;
        } else {
            size = widthSize < heightSize ? widthSize : heightSize;
        }

        return View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY);
    }
}
