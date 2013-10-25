package com.douban.ui.samples;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.douban.ui.dialog.AlertDialogFragment;
import com.douban.ui.widget.AdvancedShareActionProvider;

public class MainActivity extends SherlockFragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(android.R.id.text1)
    TextView mTextView;
    @InjectView(android.R.id.button1)
    Button mButton1;
    @InjectView(android.R.id.button2)
    Button mButton2;
    @InjectView(android.R.id.button3)
    Button mButton3;

    private AdvancedShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Views.inject(this);

        mButton1.setText("简单对话框");
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(false);
            }
        });
        mButton2.setText("列表对话框");
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(true);
            }
        });
        mButton3.setText("自定义对话框");
        mButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });
    }

    private void showAlertDialog(boolean list) {
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(this);
        builder.setTitle(list ? "列表对话框" : "简单对话框");
        if (list) {
            String[] items = new String[10];
            for (int i = 0; i < 10; i++) {
                items[i] = "List Item " + i;
            }
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showToast("List Item Clicked: " + which);
                }
            });
        } else {
            builder.setMessage("江上春风留客舟，无穷归思满东流。与君尽日闲临水，贪看飞花忘却愁。你确定要关闭对话框？");
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("Positive Button Clicked!");
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("Negative Button Clicked!");
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showToast("AlertDialogFragment is dismissed!");
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showToast("AlertDialogFragment is cancelled!");
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                showToast("onKey() keyCode=" + keyCode);
                return false;
            }
        });
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        AlertDialogFragment dialog = builder.create();
        dialog.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
    }

    private void showCustomDialog() {
        AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(this);
        builder.setCustomTitle(LayoutInflater.from(this).inflate(R.layout.dialog_custom_title, null));
        builder.setView(LayoutInflater.from(this).inflate(R.layout.dialog_custom, null));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("Positive Button Clicked!");
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                showToast("Negative Button Clicked!");
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showToast("AlertDialogFragment is dismissed!");
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showToast("AlertDialogFragment is cancelled!");
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//                showToast("onKey() keyCode=" + keyCode);
                return false;
            }
        });
        builder.setCancelable(true);
        builder.setCanceledOnTouchOutside(false);
        AlertDialogFragment dialog = builder.create();
        dialog.show(getSupportFragmentManager(), AlertDialogFragment.TAG);
    }

    private void showToast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
            mTextView.getEditableText().clear();
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
