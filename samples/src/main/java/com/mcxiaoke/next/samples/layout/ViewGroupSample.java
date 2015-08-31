package com.mcxiaoke.next.samples.layout;

import android.os.Bundle;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.R;

/**
 * User: mcxiaoke
 * Date: 15/8/27
 * Time: 10:21
 */
public class ViewGroupSample extends BaseActivity {
    public static final String TAG = ViewGroupSample.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.flow_layout_demo);
    }
}
