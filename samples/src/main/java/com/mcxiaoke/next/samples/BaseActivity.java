package com.mcxiaoke.next.samples;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.os.Bundle;
import android.widget.Toast;
import com.mcxiaoke.next.app.NextBaseActivity;

/**
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午3:50
 */
@SuppressLint("Registered")
public class BaseActivity extends NextBaseActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
