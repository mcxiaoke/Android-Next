package com.douban.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.ListAdapter;

/**
 * 一个自定义的DialogFragment
 * 接口和功能基本等同于系统的AlertDialog
 * User: mcxiaoke
 * Date: 13-6-16
 * Date: 13-10-23
 * Time: 下午6:28
 */
public class AlertDialogFragment extends DialogFragment {
    public static final String TAG = AlertDialogFragment.class.getSimpleName();

    public static AlertDialogFragment create(Context context, CharSequence title, CharSequence message) {
        Builder builder = new Builder(context);
        builder.setTitle(title).setMessage(message);
        return builder.create();
    }

    private Params mParams;

    void setParams(Params params) {
        mParams = params;
    }

    public void setTitle(CharSequence title) {
        mParams.mTitle = title;
    }

    public void setMessage(CharSequence message) {
        mParams.mMessage = message;
    }

    public void setIcon(int iconId) {
        mParams.mIconId = iconId;
    }

    public void setIcon(Drawable icon) {
        mParams.mIcon = icon;
    }

    public void setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        mParams.mPositiveButtonText = text;
        mParams.mPositiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        mParams.mNegativeButtonText = text;
        mParams.mNegativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        mParams.mNeutralButtonText = text;
        mParams.mNeutralButtonListener = listener;
    }

    public void setCancelable(boolean cancelable) {
        mParams.mCancelable = cancelable;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        mParams.mOnCancelListener = onCancelListener;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mParams.mOnDismissListener = onDismissListener;
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        mParams.mOnKeyListener = onKeyListener;
    }

    public void setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
        mParams.mItems = items;
        mParams.mOnClickListener = listener;
    }

    public void setAdapter(final ListAdapter adapter, final DialogInterface.OnClickListener listener) {
        mParams.mAdapter = adapter;
        mParams.mOnClickListener = listener;
    }

    public void setView(View view) {
        mParams.mView = view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle sis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mParams.mIconId > 0) {
            builder.setIcon(mParams.mIconId);
        } else if (mParams.mIcon != null) {
            builder.setIcon(mParams.mIconId);
        }
        if (mParams.mTitle != null) {
            builder.setTitle(mParams.mTitle);
        }
        if (mParams.mCustomTitleView != null) {
            builder.setCustomTitle(mParams.mCustomTitleView);
        }
        if (mParams.mMessage != null) {
            builder.setMessage(mParams.mMessage);
        }
        if (mParams.mPositiveButtonText != null) {
            builder.setPositiveButton(mParams.mPositiveButtonText, mParams.mPositiveButtonListener);
        }
        if (mParams.mNegativeButtonText != null) {
            builder.setPositiveButton(mParams.mNegativeButtonText, mParams.mNegativeButtonListener);
        }
        if (mParams.mNeutralButtonText != null) {
            builder.setPositiveButton(mParams.mNeutralButtonText, mParams.mNeutralButtonListener);
        }
        if (mParams.mItems != null) {
            builder.setItems(mParams.mItems, mParams.mOnClickListener);
        }
        if (mParams.mAdapter != null) {
            builder.setAdapter(mParams.mAdapter, mParams.mOnClickListener);
        }
        if (mParams.mView != null) {
            builder.setView(mParams.mView);
        }
        builder.setCancelable(mParams.mCancelable);

        AlertDialog dialog = builder.create();
        if (mParams.mWindowNoTitle) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setOnKeyListener(mParams.mOnKeyListener);
        dialog.setOnCancelListener(mParams.mOnCancelListener);
        dialog.setOnDismissListener(mParams.mOnDismissListener);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    static class Params {
        public int mTheme;
        public boolean mWindowNoTitle;
        public int mIconId = 0;
        public Drawable mIcon;
        public CharSequence mTitle;
        public View mCustomTitleView;
        public CharSequence mMessage;
        public CharSequence mPositiveButtonText;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNeutralButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public boolean mCancelable;
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;
        public CharSequence[] mItems;
        public ListAdapter mAdapter;
        public DialogInterface.OnClickListener mOnClickListener;
        public View mView;
    }

    public static class Builder {
        private Context mContext;
        private Params mParams = new Params();

        public Builder(Builder builder) {
            mContext = builder.mContext;
            mParams = builder.mParams;
        }

        public Builder(Context context) {
            this(context, 0);
        }

        public Builder(Context context, int theme) {
            mContext = context;
            mParams.mTheme = theme;
        }

        public Context getContext() {
            return mContext;
        }

        public Builder setTheme(int mTheme) {
            this.mParams.mTheme = mTheme;
            return this;
        }

        public int getTheme() {
            return mParams.mTheme;
        }

        public Builder setWindowNoTitle(boolean noTitle) {
            this.mParams.mWindowNoTitle = noTitle;
            return this;
        }

        public boolean isWindowNoTitle() {
            return mParams.mWindowNoTitle;
        }

        public Builder setTitle(int titleId) {
            mParams.mTitle = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            mParams.mTitle = title;
            return this;
        }

        public Builder setCustomTitle(View customTitleView) {
            mParams.mCustomTitleView = customTitleView;
            return this;
        }

        public Builder setMessage(int messageId) {
            mParams.mMessage = mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mParams.mMessage = message;
            return this;
        }

        public Builder setIcon(int iconId) {
            mParams.mIconId = iconId;
            return this;
        }

        public Builder setIcon(Drawable icon) {
            mParams.mIcon = icon;
            return this;
        }

        public Builder setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
            mParams.mPositiveButtonText = mContext.getText(textId);
            mParams.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mParams.mPositiveButtonText = text;
            mParams.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
            mParams.mNegativeButtonText = mContext.getText(textId);
            mParams.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mParams.mNegativeButtonText = text;
            mParams.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(int textId, final DialogInterface.OnClickListener listener) {
            mParams.mNeutralButtonText = mContext.getText(textId);
            mParams.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            mParams.mNeutralButtonText = text;
            mParams.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mParams.mCancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            mParams.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            mParams.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            mParams.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setItems(int itemsId, final DialogInterface.OnClickListener listener) {
            mParams.mItems = mContext.getResources().getTextArray(itemsId);
            mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setItems(CharSequence[] items, final DialogInterface.OnClickListener listener) {
            mParams.mItems = items;
            mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setAdapter(final ListAdapter adapter, final DialogInterface.OnClickListener listener) {
            mParams.mAdapter = adapter;
            mParams.mOnClickListener = listener;
            return this;
        }

        public Builder setView(View view) {
            mParams.mView = view;
            return this;
        }

        public AlertDialogFragment create() {
            AlertDialogFragment fragment = new AlertDialogFragment();
            fragment.setParams(mParams);
            return fragment;
        }

    }

}
