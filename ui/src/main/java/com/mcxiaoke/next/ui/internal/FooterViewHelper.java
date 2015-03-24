package com.mcxiaoke.next.ui.internal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.mcxiaoke.next.ui.R;

/**
 * User: mcxiaoke
 * Date: 13-8-15
 * Time: 下午12:02
 */
public class FooterViewHelper {

    private ViewGroup footerView;
    private TextView textView;
    private ViewGroup progressView;
    private ProgressBar progressBar;
    private TextView progressTextView;

    private Context mContext;

    public FooterViewHelper(Context context) {
        mContext = context;
        footerView = (ViewGroup) LayoutInflater.from(mContext).inflate(R.layout.endless_footer, null);
        textView = (TextView) footerView.findViewById(R.id.endless_text);
        progressView = (ViewGroup) footerView.findViewById(R.id.endless_progress);
//        progressBar = (ProgressBar) footerView.findViewById(R.id.empty_progress_bar);
        progressTextView = (TextView) footerView.findViewById(R.id.endless_progress_text);
    }

    public ViewGroup getFooterView() {
        return footerView;
    }

    public void showProgress() {
        showProgress(false);
    }

    public void showProgress(boolean withText) {
        footerView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        progressTextView.setVisibility(withText ? View.VISIBLE : View.GONE);
    }

    private void showText() {
        footerView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    public void showText(int resId) {
        String text = mContext.getString(resId);
        showText(text);
    }

    public void showText(CharSequence text) {
        textView.setText(text);
        showText();
    }

    public void showEmpty() {
        showText("");
    }

    public void hide() {
        footerView.setVisibility(View.GONE);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        footerView.setOnClickListener(listener);
    }
}
