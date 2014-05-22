package com.mcxiaoke.next.ui.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

/**
 * Indeterminate progress dialog wrapped up in a DialogFragment to work even when the device
 * orientation is changed. Currently, only supports adding a title and/or message to the progress
 * dialog.  There is an additional parameter of the minimum amount of time to display the progress
 * dialog even after a call to dismiss the dialog {@link #dismiss()} or
 * {@link #dismissAllowingStateLoss()}.
 */
@TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
public class ProgressDialogFragment extends DialogFragment {
    private static final String TAG = ProgressDialogFragment.class.getSimpleName();

    private CharSequence mTitle;
    private CharSequence mMessage;
    private boolean mActivityReady = false;
    private Dialog mOldDialog;
    private boolean mCalledSuperDismiss = false;
    private boolean mAllowStateLoss;

    /**
     * Creates and shows an indeterminate progress dialog.  Once the progress dialog is shown, it
     * will be shown for at least the minDisplayTime (in milliseconds), so that the progress dialog
     * does not flash in and out to quickly.
     */
    public static ProgressDialogFragment show(FragmentManager fragmentManager,
                                              CharSequence title, CharSequence message, boolean cancelable) {
        ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
        dialogFragment.mTitle = title;
        dialogFragment.mMessage = message;
        dialogFragment.setCancelable(cancelable);
        dialogFragment.show(fragmentManager, TAG);
        return dialogFragment;
    }

    public static ProgressDialogFragment show(FragmentManager fragmentManager,
                                              CharSequence title, CharSequence message) {
        return show(fragmentManager, title, message, false);
    }

    /**
     * Creates and shows an indeterminate progress dialog.  Once the progress dialog is shown, it
     * will be shown for at least the minDisplayTime (in milliseconds), so that the progress dialog
     * does not flash in and out to quickly.
     */
    public static ProgressDialogFragment create(CharSequence title, CharSequence message, boolean cancelable) {
        ProgressDialogFragment dialogFragment = new ProgressDialogFragment();
        dialogFragment.mTitle = title;
        dialogFragment.mMessage = message;
        dialogFragment.setCancelable(cancelable);
        return dialogFragment;
    }

    public static ProgressDialogFragment create(CharSequence title, CharSequence message) {
        return create(title, message, false);
    }

    public void show(FragmentManager manager) {
        show(manager, TAG);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_FRAME, 0);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create the progress dialog and set its properties
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setIndeterminateDrawable(null);
        dialog.setTitle(mTitle);
        dialog.setMessage(mMessage);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivityReady = true;

        // Check if superDismiss() had been called before.  This can happen if in a long
        // running operation, the user hits the home button and closes this fragment's activity.
        // Upon returning, we want to dismiss this progress dialog fragment.
        if (mCalledSuperDismiss) {
            superDismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mActivityReady = false;
    }

    /**
     * There is a race condition that is not handled properly by the DialogFragment class.
     * If we don't check that this onDismiss callback isn't for the old progress dialog from before
     * the device orientation change, then this will cause the newly created dialog after the
     * orientation change to be dismissed immediately.
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOldDialog != null && mOldDialog == dialog) {
            // This is the callback from the old progress dialog that was already dismissed before
            // the device orientation change, so just ignore it.
            return;
        }
        super.onDismiss(dialog);
    }

    /**
     * Save the old dialog that is about to get destroyed in case this is due to a change
     * in device orientation.  This will allow us to intercept the callback to
     * {@link #onDismiss(android.content.DialogInterface)} in case the callback happens after a new progress dialog
     * instance was created.
     */
    @Override
    public void onDestroyView() {
        mOldDialog = getDialog();
        super.onDestroyView();
    }

    @Override
    public void dismiss() {
        mAllowStateLoss = false;
        superDismiss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        mAllowStateLoss = true;
        superDismiss();
    }

    private void superDismiss() {
        mCalledSuperDismiss = true;
        if (mActivityReady) {
            // The fragment is either in onStart or past it, but has not gotten to onStop yet.
            // It is safe to dismiss this dialog fragment.
            if (mAllowStateLoss) {
                super.dismissAllowingStateLoss();
            } else {
                super.dismiss();
            }
        }
    }
}
