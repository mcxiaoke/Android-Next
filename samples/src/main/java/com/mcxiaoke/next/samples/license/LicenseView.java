package com.mcxiaoke.next.samples.license;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.utils.StringUtils;
import com.mcxiaoke.next.utils.ViewUtils;

/**
 * User: mcxiaoke
 * Date: 14-5-27
 * Time: 15:41
 */
public class LicenseView extends LinearLayout {

    private TextView mTitle;
    private TextView mUrl;
    private TextView mCopyright;
    private TextView mText;

    private Context mContext;
    private LayoutInflater mInflater;
    private Resources mRes;

    public LicenseView(final Context context) {
        super(context);
        setUp(context, null);
    }

    public LicenseView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setUp(context, attrs);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public LicenseView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        setUp(context, attrs);
    }

    private void setUp(Context context, AttributeSet attrs) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRes = context.getResources();

        setOrientation(LinearLayout.VERTICAL);

        mInflater.inflate(R.layout.license_view, this);
        mTitle = ViewUtils.findById(this, R.id.license_title);
        mUrl = ViewUtils.findById(this, R.id.license_url);
        mCopyright = ViewUtils.findById(this, R.id.license_copyright);
        mText = ViewUtils.findById(this, R.id.license_text);
    }

    public void setLicenseInfo(final LicenseInfo info) {
        if (info != null) {
            setName(info.name);
            setUrl(info.url);
            setCopyright(info.copyright);
            setText(info.license);
        }
    }

    public void setName(CharSequence name) {
        if (StringUtils.isNotEmpty(name)) {
            mTitle.setText(name);
            mTitle.setVisibility(View.VISIBLE);
        } else {
            mTitle.setVisibility(View.GONE);
        }
    }

    public void setUrl(CharSequence url) {
        if (StringUtils.isNotEmpty(url)) {
            mUrl.setText(url);
            mUrl.setVisibility(View.VISIBLE);
        } else {
            mUrl.setVisibility(View.GONE);
        }
    }

    public void setCopyright(CharSequence copyright) {
        if (StringUtils.isNotEmpty(copyright)) {
            mCopyright.setText(copyright);
            mCopyright.setVisibility(View.VISIBLE);
        } else {
            mCopyright.setVisibility(View.GONE);
        }
    }

    public void setText(CharSequence text) {
        if (StringUtils.isNotEmpty(text)) {
            mText.setText(text);
            mText.setVisibility(View.VISIBLE);
        } else {
            mText.setVisibility(View.GONE);
        }
    }

}
