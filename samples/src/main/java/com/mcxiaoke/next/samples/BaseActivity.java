package com.mcxiaoke.next.samples;

import android.widget.Toast;
import com.mcxiaoke.next.app.NextBaseActivity;

/**
 * User: mcxiaoke
 * Date: 13-10-25
 * Time: 下午3:50
 */
public class BaseActivity extends NextBaseActivity {


    public void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
