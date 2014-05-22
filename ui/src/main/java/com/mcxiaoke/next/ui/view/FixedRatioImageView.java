package com.mcxiaoke.next.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mcxiaoke.next.ui.R;

/**
 * An image view which always remains square with respect to its width.
 * <p/>
 * User: mcxiaoke
 * Date: 14-4-24
 * Time: 上午11:59
 */

/**
 * 强制高宽比的ImageView，可选以水平或垂直方向为基准
 */
public class FixedRatioImageView extends ImageView {
    private static final float INVALID_RATIO = -1f;

    private static final int STRETCH_HORIZONTAL = 0;
    private static final int STRETCH_VERTICAL = 1;

    private float mRatio = INVALID_RATIO;
    private int mOrientation = STRETCH_HORIZONTAL;

    public FixedRatioImageView(Context context) {
        super(context);
        setUp(context, null);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context, attrs);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioImageView);
            mRatio = a.getDimension(R.styleable.FixedRatioImageView_ratio, INVALID_RATIO);
            mOrientation = a.getInt(R.styleable.FixedRatioImageView_fri_orientation, STRETCH_HORIZONTAL);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mRatio > 0) {
            if (mOrientation == STRETCH_VERTICAL) {
                int height = getMeasuredHeight();
                int width = (int) (height * mRatio);
                setMeasuredDimension(width, height);
            } else {
                int width = getMeasuredWidth();
                int height = (int) (width * mRatio);
                setMeasuredDimension(width, height);
            }
        }
    }

    /**
     * 设置高宽比
     *
     * @param ratio 高宽/宽高比
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
        requestLayout();
    }

}
