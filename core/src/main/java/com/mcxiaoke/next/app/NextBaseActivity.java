package com.mcxiaoke.next.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.MenuItem;
import com.mcxiaoke.next.utils.LogUtils;

/**
 * User: mcxiaoke
 * Date: 14-3-5
 * Time: 11:49
 */
@TargetApi(VERSION_CODES.HONEYCOMB)
public abstract class NextBaseActivity extends Activity {
    public static final boolean DEBUG = false;
    private static final String BASE_TAG = NextBaseActivity.class.getSimpleName();
    private boolean mPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            LogUtils.d(BASE_TAG, "onCreate() activity=" + getActivity());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            LogUtils.d(BASE_TAG, "onDestroy() activity=" + getActivity());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtils.v(BASE_TAG, "onBackPressed() activity=" + getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onHomeClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isPaused() {
        return mPaused;
    }

    protected void onHomeClick() {
        if (isFinishing()) {
            return;
        }

        LogUtils.v(BASE_TAG, "onHomeClick() activity=" + getActivity());
        onBackPressed();
    }

    protected NextBaseActivity getActivity() {
        return this;
    }

    public void showProgressIndicator() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressIndicator() {
        setProgressBarIndeterminateVisibility(false);
    }

    public void setActionBarTitle(CharSequence text) {
        getActionBar().setTitle(text);
    }

    public void setActionBarTitle(int resId) {
        getActionBar().setTitle(resId);
    }

    public void setActionBarSubtitle(CharSequence text) {
        getActionBar().setSubtitle(text);
    }

    public void setActionBarSubtitle(int resId) {
        getActionBar().setSubtitle(resId);
    }

}
