package com.douban.ui.samples;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.douban.ui.widget.AdvancedShareActionProvider;

public class MainActivity extends SherlockFragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextView;
    private AdvancedShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mTextView = (TextView) findViewById(android.R.id.text1);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            mTextView.append("Received Intent:\n");
            mTextView.append("Action: " + action + "\n");
            mTextView.append("Extra Text: " + text + "\n");
            mTextView.append("Extra subject: " + subject + "\n");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        MenuItem share = menu.findItem(R.id.menu_share);
        mShareActionProvider = (AdvancedShareActionProvider) share.getActionProvider();
        updateShareIntent();
        return true;
    }

    private void updateShareIntent() {
        if (mShareActionProvider != null) {
            final String pkg = getPackageName();
            mShareActionProvider.addCustomPackage("com.douban.shuo");
            mShareActionProvider.addCustomPackage(pkg);
            mShareActionProvider.addCustomPackage("com.twitter.android");
            mShareActionProvider.setDefaultLength(3);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "I am some text for sharing!");
            mShareActionProvider.setShareIntent(intent);
//            mShareActionProvider.addIntentExtras("I am subject.", "I am some text for sharing!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
