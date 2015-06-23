package com.mcxiaoke.next.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mcxiaoke.next.ui.R;

public class SimpleProgressView extends LinearLayout {

    private TextView textView;
    private ViewGroup progressView;
    private ProgressBar progressBar;
    private TextView progressTextView;

    public SimpleProgressView(Context context) {
        super(context);
        init(null, 0);
    }

    public SimpleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public SimpleProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.cv_simple_progress, this);
        textView = (TextView) findViewById(R.id.spv_text);
        progressView = (ViewGroup) findViewById(R.id.spv_progress);
        progressBar = (ProgressBar) findViewById(R.id.spv_progress_bar);
        progressTextView = (TextView) findViewById(R.id.spv_progress_text);
    }

    public void showProgress() {
        showProgress(false);
    }

    public void showProgress(boolean withText) {
        show();
        textView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        progressTextView.setVisibility(withText ? View.VISIBLE : View.GONE);
    }

    private void showText() {
        show();
        progressView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    public void showText(int resId) {
        String text = getContext().getString(resId);
        showText(text);
    }

    public void showText(CharSequence text) {
        textView.setText(text);
        showText();
    }

    public void showEmpty() {
        showText("");
    }

    public void setTextSize(final float size) {
        textView.setTextSize(size);
    }

    public void setTextSize(final int unit, final float size) {
        textView.setTextSize(unit, size);
    }

    public void setTextColor(final int color) {
        textView.setTextColor(color);
    }

    public void setTextColor(final ColorStateList color) {
        textView.setTextColor(color);
    }

    public void hide() {
        setVisibility(View.GONE);
    }

    public void show() {
        setVisibility(View.VISIBLE);
    }
}
