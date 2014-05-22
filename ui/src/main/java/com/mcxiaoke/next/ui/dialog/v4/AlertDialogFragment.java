package com.mcxiaoke.next.ui.dialog.v4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.Arrays;

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
    public static final boolean DEBUG = false;

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

    public void setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mParams.mCanceledOnTouchOutside = canceledOnTouchOutside;
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
        if (DEBUG) {
            Log.v(TAG, "onCreate()");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        if (DEBUG) {
            Log.v(TAG, "onCreateDialog() mParams=" + mParams);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (mParams.mIconId > 0) {
            builder.setIcon(mParams.mIconId);
        } else if (mParams.mIcon != null) {
            builder.setIcon(mParams.mIcon);
        }
        if (mParams.mTitle != null) {
            builder.setTitle(mParams.mTitle);
        }
        if (mParams.mMessage != null) {
            builder.setMessage(mParams.mMessage);
        }

        if (mParams.mPositiveButtonText != null) {
            builder.setPositiveButton(mParams.mPositiveButtonText, mParams.mPositiveButtonListener);
        }
        if (mParams.mNegativeButtonText != null) {
            builder.setNegativeButton(mParams.mNegativeButtonText, mParams.mNegativeButtonListener);
        }
        if (mParams.mNeutralButtonText != null) {
            builder.setNeutralButton(mParams.mNeutralButtonText, mParams.mNeutralButtonListener);
        }

        if (mParams.mCustomTitleView != null) {
            builder.setCustomTitle(mParams.mCustomTitleView);
        }

        if (mParams.mView != null) {
            builder.setView(mParams.mView);
        }

        if (mParams.mIsSingleChoice) {
            if (mParams.mItems != null) {
                builder.setSingleChoiceItems(mParams.mItems, mParams.mCheckedItem, mParams.mOnClickListener);
            } else if (mParams.mAdapter != null) {
                builder.setSingleChoiceItems(mParams.mAdapter, mParams.mCheckedItem, mParams.mOnClickListener);
            }
        } else if (mParams.mIsMultiChoice) {
            if (mParams.mItems != null) {
                builder.setMultiChoiceItems(mParams.mItems, mParams.mCheckedItems, mParams.mOnCheckboxClickListener);
            }
        } else {
            if (mParams.mItems != null) {
                builder.setItems(mParams.mItems, mParams.mOnClickListener);
            } else if (mParams.mAdapter != null) {
                builder.setAdapter(mParams.mAdapter, mParams.mOnClickListener);
            }
        }

        builder.setCancelable(mParams.mCancelable);
        builder.setOnKeyListener(mParams.mOnKeyListener);

        AlertDialog dialog = builder.create();
        if (mParams.mWindowNoTitle) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        dialog.setCanceledOnTouchOutside(mParams.mCanceledOnTouchOutside);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (DEBUG) {
            Log.v(TAG, "onDismiss()");
        }
        if (mParams.mOnDismissListener != null) {
            mParams.mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (DEBUG) {
            Log.v(TAG, "onCancel()");
        }
        if (mParams.mOnCancelListener != null) {
            mParams.mOnCancelListener.onCancel(dialog);
        }
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
        // 主题
        public int mTheme;
        // 是否隐藏标题栏
        public boolean mWindowNoTitle;
        // 图标
        public int mIconId;
        // 图标
        public Drawable mIcon;
        // 标题
        public CharSequence mTitle;
        // 自定义标题栏
        public View mCustomTitleView;
        // 内容
        public CharSequence mMessage;
        // 按钮
        public CharSequence mPositiveButtonText;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        // 按钮
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNegativeButtonListener;

        // 按钮
        public CharSequence mNeutralButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;

        // 是否可以Cancel，默认为true
        public boolean mCancelable = true;

        // 是否触摸对话框意外区域Cancel
        public boolean mCanceledOnTouchOutside;

        // 几个Listener
        public DialogInterface.OnCancelListener mOnCancelListener;
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mOnKeyListener;

        // 列表数据源
        public CharSequence[] mItems;
        public ListAdapter mAdapter;
        public boolean[] mCheckedItems;

        // 列表属性
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public int mCheckedItem = -1;

        // 列表Listener
        public DialogInterface.OnClickListener mOnClickListener;
        public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
        public AdapterView.OnItemSelectedListener mOnItemSelectedListener;

        // 自定义View
        public View mView;

        @Override
        public String toString() {
            return "Params{" +
                    "mTheme=" + mTheme +
                    ", mWindowNoTitle=" + mWindowNoTitle +
                    ", mIconId=" + mIconId +
                    ", mIcon=" + mIcon +
                    ", mTitle=" + mTitle +
                    ", mCustomTitleView=" + mCustomTitleView +
                    ", mMessage=" + mMessage +
                    ", mPositiveButtonText=" + mPositiveButtonText +
                    ", mPositiveButtonListener=" + mPositiveButtonListener +
                    ", mNegativeButtonText=" + mNegativeButtonText +
                    ", mNegativeButtonListener=" + mNegativeButtonListener +
                    ", mNeutralButtonText=" + mNeutralButtonText +
                    ", mNeutralButtonListener=" + mNeutralButtonListener +
                    ", mCancelable=" + mCancelable +
                    ", mCanceledOnTouchOutside=" + mCanceledOnTouchOutside +
                    ", mOnCancelListener=" + mOnCancelListener +
                    ", mOnDismissListener=" + mOnDismissListener +
                    ", mOnKeyListener=" + mOnKeyListener +
                    ", mItems=" + Arrays.toString(mItems) +
                    ", mAdapter=" + mAdapter +
                    ", mOnClickListener=" + mOnClickListener +
                    ", mView=" + mView +
                    '}';
        }
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

        public Builder setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
            mParams.mCanceledOnTouchOutside = canceledOnTouchOutside;
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

        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems,
                                           final DialogInterface.OnMultiChoiceClickListener listener) {
            mParams.mItems = mContext.getResources().getTextArray(itemsId);
            mParams.mOnCheckboxClickListener = listener;
            mParams.mCheckedItems = checkedItems;
            mParams.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems,
                                           final DialogInterface.OnMultiChoiceClickListener listener) {
            mParams.mItems = items;
            mParams.mOnCheckboxClickListener = listener;
            mParams.mCheckedItems = checkedItems;
            mParams.mIsMultiChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(int itemsId, int checkedItem,
                                            final DialogInterface.OnClickListener listener) {
            mParams.mItems = mContext.getResources().getTextArray(itemsId);
            mParams.mOnClickListener = listener;
            mParams.mCheckedItem = checkedItem;
            mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, final DialogInterface.OnClickListener listener) {
            mParams.mItems = items;
            mParams.mOnClickListener = listener;
            mParams.mCheckedItem = checkedItem;
            mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, final DialogInterface.OnClickListener listener) {
            mParams.mAdapter = adapter;
            mParams.mOnClickListener = listener;
            mParams.mCheckedItem = checkedItem;
            mParams.mIsSingleChoice = true;
            return this;
        }

        public Builder setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
            mParams.mOnItemSelectedListener = listener;
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
