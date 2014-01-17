package com.mcxiaoke.commons.samples;

import android.app.Activity;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午3:50
 */
public class BaseActivity extends Activity {


    public void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public BaseActivity getActivity() {
        return this;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
