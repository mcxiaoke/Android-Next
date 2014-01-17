package com.mcxiaoke.commons.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.mcxiaoke.commons.BuildConfig;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 13-5-3
 * Time: 上午10:06
 */
public final class SystemUtils {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = SystemUtils.class.getSimpleName();

    public static final int HEAP_SIZE_LARGE = 48 * 1024 * 1024;

    private SystemUtils() {
    }

    public static boolean isLargeHeap() {
        return Runtime.getRuntime().maxMemory() > HEAP_SIZE_LARGE;
    }


    public static boolean noSdcard() {
        return !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * check if free size of SDCARD and CACHE dir is OK
     *
     * @param needSize how much space should release at least
     * @return true if has enough space
     */
    public static boolean noFreeSpace(long needSize) {
        long freeSpace = getFreeSpace();
        return freeSpace < needSize * 3;
    }

    @SuppressWarnings("deprecation")
    public static long getFreeSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context context, EditText editText) {
        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void toggleSoftInput(Context context, View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, 0);
        }
    }

    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static <Params, Progress, Result> void execute(AsyncTask<Params, Progress, Result> task, Params... params) {
        if (task != null) {
            if (hasHoneycomb()) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            } else {
                task.execute(params);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    public static void mediaScan(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    // 魅族开发指南推荐的判断是否存在SmartBar的方法
    public static boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return (Boolean) method.invoke(null);
        } catch (Exception e) {
            // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
            return Build.DEVICE.equals("mx2");
        }
    }

    public static void insertText(final EditText editText, final String text) {
        int start = Math.max(editText.getSelectionStart(), 0);
        if (DEBUG) {
            LogUtils.v(TAG, "insertText() start=" + start + "  text=" + text);
        }
        editText.getText().insert(start, text);
    }

    public static void replaceSelectionText(final EditText editText, final String text) {
        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        if (DEBUG) {
            LogUtils.v(TAG, "replaceSelectionText() start=" + start + " end=" + end + " text=" + text);
        }
        editText.getText().replace(Math.min(start, end), Math.max(start, end),
                text, 0, text.length());
    }

    public static String dumpIntent(Intent intent) {

        StringBuilder builder = new StringBuilder();
        if (intent != null) {
            builder.append("Intent: {\n");
            builder.append("Action=").append(intent.getAction()).append("\n");
            builder.append("Data=").append(intent.getData()).append("\n");
            String categories = StringUtils.getPrintString(intent.getCategories());
            builder.append("Categories=[").append(categories).append("]\n");
            builder.append("Component=").append(intent.getComponent()).append("\n");
            builder.append("Type=").append(intent.getType()).append("\n");
            builder.append("Package=").append(intent.getPackage()).append("\n");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Set<String> keys = bundle.keySet();
                for (String key : keys) {
                    builder.append("Extra={").append(key).append("=").append(bundle.get(key)).append("}\n");
                }
            }
            builder.append("}\n");
        }

        return builder.toString();
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean isPreHoneycomb() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isPreIceCreamSandwich() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @TargetApi(11)
    public static void setStrictMode(boolean enable) {
        if (!enable) {
            return;
        }
        if (hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }


}
