package com.douban.ui.abs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.actionbarsherlock.view.ActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 13-10-22
 * Time: 下午4:00
 */

/**
 * 高级版的ShareActionProvider
 * 支持自定义优先显示的分享目标
 */
public class AdvancedShareActionProvider extends ActionProvider implements MenuItem.OnMenuItemClickListener {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = AdvancedShareActionProvider.class.getSimpleName();

    /**
     * 默认显示的分享目标数量
     */
    public static final int DEFAULT_LIST_LENGTH = 4;

    private final Object mLock = new Object();

    private Context mContext;
    private PackageManager mPackageManager;
    private Intent mIntent;
    private int mDefaultLength = DEFAULT_LIST_LENGTH;
    private CharSequence mExpandLabel = "See all…";
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;

    private List<ResolveInfo> mActivities = new ArrayList<ResolveInfo>();
    private List<String> mCustomPackages = new ArrayList<String>();

    public AdvancedShareActionProvider(Context context) {
        super(context);
        mContext = context;
        mPackageManager = context.getPackageManager();
    }

    /**
     * 设置MenuItem的点击事件
     *
     * @param listener
     */
    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    /**
     * 添加自定义的分享目标（不会重新排序）
     *
     * @param pkg 包名
     */
    public void addCustomPackage(String pkg) {
        if (!mCustomPackages.contains(pkg)) {
            mCustomPackages.add(pkg);
        }
    }

    /**
     * 添加自定义的分享目标（不会重新排序）
     *
     * @param pkgs 包名集合
     */
    public void addCustomPackages(Collection<String> pkgs) {
        for (String pkg : pkgs) {
            addCustomPackage(pkg);
        }
    }

    /**
     * 清空自定义的分享目标
     */
    public void clearCustomPackages() {
        mCustomPackages.clear();
    }

    /**
     * 设置默认显示的分享目标数量
     *
     * @param length 数量
     */
    public void setDefaultLength(int length) {
        mDefaultLength = length;
    }

    public int getDefaultLength() {
        return mDefaultLength;
    }

    public void setExpandLabel(CharSequence label) {
        mExpandLabel = label;
    }

    /**
     * 设置分享Intent
     * 设置Intent会同时重新加载分享目标列表
     *
     * @param intent Intent
     */
    public void setShareIntent(Intent intent) {
        mIntent = intent;
        reloadActivities();
    }

    /**
     * 设置Intent Extras
     *
     * @param extras Bundle
     */
    public void setIntentExtras(Bundle extras) {
        mIntent.replaceExtras(extras);
    }

    /**
     * 添加额外的参数到Intent
     *
     * @param extras Bundle
     */
    public void addIntentExtras(Bundle extras) {
        mIntent.putExtras(extras);
    }

    /**
     * 添加额外的参数到Intent
     *
     * @param subject Intent.EXTRA_SUBJECT
     * @param text    Intent.EXTRA_TEXT
     */
    public void setIntentExtras(String subject, String text) {
        setIntentExtras(subject, text, null);
    }

    /**
     * 添加额外的参数到Intent
     *
     * @param imageUri Intent.EXTRA_STREAM
     */
    public void setIntentExtras(Uri imageUri) {
        setIntentExtras(null, null, imageUri);
    }

    /**
     * 添加额外的参数到Intent
     *
     * @param subject  Intent.EXTRA_SUBJECT
     * @param text     Intent.EXTRA_TEXT
     * @param imageUri Intent.EXTRA_STREAM
     */
    public void setIntentExtras(String subject, String text, Uri imageUri) {
        if (DEBUG) {
            Log.v(TAG, "setIntentExtras() subject=" + subject);
            Log.v(TAG, "setIntentExtras() text=" + text);
            Log.v(TAG, "setIntentExtras() imageUri=" + imageUri);
        }
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        mIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (imageUri != null) {
            mIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        }
    }

    /**
     * 重新加载目标Activity列表
     */
    private void reloadActivities() {
        loadActivities();
        sortActivities();
    }

    /**
     * 根据Intent读取Activity列表
     */
    private void loadActivities() {
        if (mIntent != null) {
            synchronized (mLock) {
                mActivities.clear();
                List<ResolveInfo> activities =
                        mPackageManager.queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (activities != null) {
                    if (DEBUG) {
                        Log.v(TAG, "loadActivities() size=" + activities.size());
                    }
                    mActivities.clear();
                    mActivities.addAll(activities);
                }
            }
        }
    }

    /**
     * 对Activity列表排序，自定义的分享目标排在前面
     */
    private void sortActivities() {
        if (mActivities.size() > 0 && mCustomPackages.size() > 0) {
            if (DEBUG) {
                Log.v(TAG, "sortActivities() mActivities size=" + mActivities.size());
                Log.v(TAG, "sortActivities() mCustomPackages size=" + mCustomPackages.size());
            }
            List<ResolveInfo> customActivities = new ArrayList<ResolveInfo>();
            for (String pkg : mCustomPackages) {
                synchronized (mLock) {
                    int index = findPackageIndex(pkg);
                    if (index > 0) {
                        ResolveInfo resolveInfo = mActivities.remove(index);
                        if (resolveInfo != null) {
                            customActivities.add(resolveInfo);
                        }
                    }
                }
            }
            if (customActivities.size() > 0) {
                if (DEBUG) {
                    Log.v(TAG, "sortActivities() found customActivities size=" + customActivities.size());
                }
                synchronized (mLock) {
                    mActivities.addAll(0, customActivities);
                }
            }
        }
    }

    /**
     * 根据报名查找某个ActivityInfo
     *
     * @param pkg 包名
     * @return index
     */
    private int findPackageIndex(String pkg) {
        int index = -1;
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            ActivityInfo info = mActivities.get(i).activityInfo;
            if (pkg.equals(info.packageName)) {
                index = i;
                break;
            }
        }
        if (DEBUG) {
            Log.v(TAG, "findPackageIndex() pkg=" + pkg + " index=" + index);
        }
        return index;
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean onPerformDefaultAction() {
        return super.onPerformDefaultAction();
    }

    @Override
    public boolean hasSubMenu() {
        return true;
    }

    /**
     * 根据Activity列表生成PopupMenu
     *
     * @param subMenu SubMenu that will be displayed
     */
    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        subMenu.clear();
        if (DEBUG) {
            Log.v(TAG, "onPrepareSubMenu() mDefaultLength=" + mDefaultLength + " mActivities.size()=" + mActivities.size());
        }
        int length = Math.min(mDefaultLength, mActivities.size());
        for (int i = 0; i < length; i++) {
            ResolveInfo appInfo = mActivities.get(i);
            subMenu.add(0, i, i, appInfo.loadLabel(mPackageManager))
                    .setIcon(appInfo.loadIcon(mPackageManager))
                    .setOnMenuItemClickListener(this);
        }

        if (mDefaultLength < mActivities.size()) {
            subMenu = subMenu.addSubMenu(Menu.NONE, mDefaultLength, mDefaultLength, mExpandLabel);

            for (int i = 0; i < mActivities.size(); i++) {
                ResolveInfo appInfo = mActivities.get(i);

                subMenu.add(0, i, i, appInfo.loadLabel(mPackageManager))
                        .setIcon(appInfo.loadIcon(mPackageManager))
                        .setOnMenuItemClickListener(this);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean handled = false;
        if (mOnMenuItemClickListener != null) {
            handled = mOnMenuItemClickListener.onMenuItemClick(item);
        }
        if (handled) {
            return true;
        }
        ResolveInfo resolveInfo = mActivities.get(item.getItemId());
        ComponentName chosenName = null;
        if (resolveInfo.activityInfo != null) {
            chosenName = new ComponentName(
                    resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name);
        }
        Intent intent = new Intent(mIntent);
        intent.setComponent(chosenName);
        if (DEBUG) {
            Log.v(TAG, "onMenuItemClick() target=" + chosenName);
        }
        mContext.startActivity(intent);
        return true;
    }
}
