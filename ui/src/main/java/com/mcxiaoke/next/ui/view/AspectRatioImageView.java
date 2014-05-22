package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mcxiaoke.next.ui.R;

import java.lang.reflect.Field;

/**
 * User: mcxiaoke
 * Date: 13-7-29
 * Time: 下午4:10
 */

/**
 * Maintains an aspect ratio based on either width or height. Disabled by default.
 */

/**
 * Preserves the aspect ratio of an image while allowing it to scale up.
 * <p/>
 * To use, set the width/height to 0dip of the side that you wish to resize. It will then be
 * adjusted based on the aspect ratio of the image.
 * <p/>
 * {@link http
 * ://stackoverflow.com/questions/2991110/android-how-to-stretch-an-image-to-the-screen-width
 * -while-maintaining-aspect-ra/2999707}
 */

/**
 * 定制的ImageView，缩放时会保持长宽比
 */
public class AspectRatioImageView extends ImageView {

    private static final String TAG = AspectRatioImageView.class.getSimpleName();
    int mMaxWidth = Integer.MAX_VALUE;
    int mMaxHeight = Integer.MAX_VALUE;
    private int mStretch;

    private static final int STRETCH_UNDEFINED = -1;
    private static final int STRETCH_HORIZONTAL = 0;
    private static final int STRETCH_VERTICAL = 1;

    public AspectRatioImageView(Context context) {
        super(context);
        init(context, null);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // http://stackoverflow.com/questions/8311081/how-to-get-maxwidth-and-maxheight-parameters-of-imageview
        // haaaaack
        try {
            final Field maxWidthField = ImageView.class.getDeclaredField("mMaxWidth");
            final Field maxHeightField = ImageView.class.getDeclaredField("mMaxHeight");
            maxWidthField.setAccessible(true);
            maxHeightField.setAccessible(true);

            mMaxWidth = (Integer) maxWidthField.get(this);
            mMaxHeight = (Integer) maxHeightField.get(this);
        } catch (final SecurityException e) {
            // we don't care if we can't get it. We weren't really supposed to anyhow.
        } catch (final NoSuchFieldException e) {
            // we don't care if we can't get it. We weren't really supposed to anyhow.
        } catch (final IllegalArgumentException e) {
            // we don't care if we can't get it. We weren't really supposed to anyhow.
        } catch (final IllegalAccessException e) {
            // we don't care if we can't get it. We weren't really supposed to anyhow.
        }
        final TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.AspectRatioImageView);
        mStretch = ta.getInt(R.styleable.AspectRatioImageView_ari_stretch, STRETCH_UNDEFINED);

        ta.recycle();

        setAdjustViewBounds(true);
    }


    @Override
    public void setMaxWidth(int maxWidth) {
        super.setMaxWidth(maxWidth);
        mMaxWidth = maxWidth;
    }

    @Override
    public void setMaxHeight(int maxHeight) {
        super.setMaxHeight(maxHeight);
        mMaxHeight = maxHeight;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final Drawable drawable = getDrawable();
        boolean setMeasuredDimension = false;
        if (drawable != null) {

            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            if (MeasureSpec.EXACTLY == MeasureSpec.getMode(heightMeasureSpec)
                    && (height == 0 || STRETCH_VERTICAL == mStretch)) {

                final float diw = drawable.getIntrinsicWidth();
                if (diw > 0) {
                    height = (int) Math.max(getSuggestedMinimumHeight(),
                            Math.min(width * (drawable.getIntrinsicHeight() / diw), mMaxHeight));
                    setMeasuredDimension(width, height);
                    setMeasuredDimension = true;
                }

            } else if (MeasureSpec.EXACTLY == MeasureSpec.getMode(widthMeasureSpec)
                    && (width == 0 || STRETCH_HORIZONTAL == mStretch)) {

                final float dih = drawable.getIntrinsicHeight();
                if (dih > 0) {
                    width = (int) Math.max(getSuggestedMinimumWidth(),
                            Math.min(height * (drawable.getIntrinsicWidth() / dih), mMaxWidth));
                    setMeasuredDimension(width, height);

                    setMeasuredDimension = true;
                }
            }
        }

        if (!setMeasuredDimension) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}