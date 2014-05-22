package com.mcxiaoke.next.ui.typeface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mcxiaoke.next.ui.R;

import java.util.HashMap;
import java.util.Map;

public class FontCache {

    static class SingletonHolder {
        public static final FontCache INSTANCE = new FontCache();
    }

    private Map<String, Typeface> mCache;

    private FontCache() {
        mCache = new HashMap<String, Typeface>();
    }

    public static FontCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setFont(TextView tv, AttributeSet attrs) {
        TypedArray a = tv.getContext().obtainStyledAttributes(attrs, R.styleable.FontFaceStyle);
        final String fontName = a.getString(R.styleable.FontFaceStyle_font_path);
        final boolean useCache = a.getBoolean(R.styleable.FontFaceStyle_font_use_cache, false);
        setFont(tv, fontName, useCache);
        a.recycle();
    }

    public void setFont(TextView tv, String fontName) {
        setFont(tv, fontName, true);
    }

    public void setFont(TextView tv, String fontName, boolean useCache) {
        if (TextUtils.isEmpty(fontName)) {
            return;
        }

        Context context = tv.getContext();
        Typeface typeface;
        if (useCache) {
            typeface = mCache.get(fontName);
            if (typeface == null) {
                typeface = Typeface.createFromAsset(context.getAssets(), fontName);
                mCache.put(fontName, typeface);
            }
        } else {
            typeface = Typeface.createFromAsset(tv.getContext().getAssets(), fontName);
        }

        tv.setTypeface(typeface);
    }

    public void clear() {
        mCache.clear();
    }
}
