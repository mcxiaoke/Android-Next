package com.mcxiaoke.next.ui.widget;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;
import com.mcxiaoke.next.ui.BuildConfig;
import com.mcxiaoke.next.ui.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 13-10-22
 * Time: 下午4:00
 *
 * 添加自定义ShareTarget支持，Updated: 2013-12-24
 */

/**
 * 高级版的ShareActionProvider
 * 支持自定义优先显示的分享目标
 */
@TargetApi(VERSION_CODES.ICE_CREAM_SANDWICH)
public class AdvancedShareActionProvider extends ActionProvider implements MenuItem.OnMenuItemClickListener {
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = AdvancedShareActionProvider.class.getSimpleName();

    public static final int WEIGHT_MAX = Integer.MAX_VALUE;
    public static final int WEIGHT_DEFAULT = 0;

    /**
     * 默认显示的分享目标数量
     */
    public static final int DEFAULT_LIST_LENGTH = 4;

    private final Object mLock = new Object();

    private int mDefaultLength;
    private CharSequence mExpandLabel;
    private volatile int mWeightCounter;

    private Context mContext;
    private PackageManager mPackageManager;
    private Intent mIntent;

    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener;
    private List<String> mExtraPackages = new ArrayList<String>();
    private List<String> mToRemovePackages = new ArrayList<String>();
    private List<ShareTarget> mExtraTargets = new ArrayList<ShareTarget>();

    private List<ShareTarget> mShareTargets = new ArrayList<ShareTarget>();


    public AdvancedShareActionProvider(Context context) {
        super(context);
        mContext = context;
        mPackageManager = context.getPackageManager();
        mWeightCounter = WEIGHT_MAX;
        mDefaultLength = DEFAULT_LIST_LENGTH;
        mExpandLabel = mContext.getString(R.string.share_action_provider_expand_label);
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
     * 注意：必须在setShareIntent之前调用
     *
     * @param pkg 包名
     */
    public void addCustomPackage(String pkg) {
        if (!mExtraPackages.contains(pkg)) {
            mExtraPackages.add(pkg);
        }
    }

    /**
     * 添加自定义的分享目标（不会重新排序）
     * 注意：必须在setShareIntent之前调用
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
        mExtraPackages.clear();
    }

    /**
     * 从分享列表移除指定的app
     * 注意：必须在setShareIntent之前调用
     *
     * @param pkg
     */
    public void removePackage(String pkg) {

        mToRemovePackages.add(pkg);
    }

    /**
     * 添加自定义的分享目标t
     * 注意：必须在setShareIntent之前调用
     *
     * @param target
     */
    public void addShareTarget(ShareTarget target) {
        target.weight = --mWeightCounter;
        mExtraTargets.add(target);
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
     * 注意：必须在setShareIntent之后调用
     *
     * @param extras Bundle
     */
    public void setIntentExtras(Bundle extras) {
        mIntent.replaceExtras(extras);
    }

    /**
     * 添加额外的参数到Intent
     * 注意：必须在setShareIntent之后调用
     *
     * @param extras Bundle
     */
    public void addIntentExtras(Bundle extras) {
        mIntent.putExtras(extras);
    }

    /**
     * 添加额外的参数到Intent
     * 注意：必须在setShareIntent之后调用
     *
     * @param subject Intent.EXTRA_SUBJECT
     * @param text    Intent.EXTRA_TEXT
     */
    public void setIntentExtras(String subject, String text) {
        setIntentExtras(subject, text, null);
    }

    /**
     * 添加额外的参数到Intent
     * 注意：必须在setShareIntent之后调用
     *
     * @param imageUri Intent.EXTRA_STREAM
     */
    public void setIntentExtras(Uri imageUri) {
        setIntentExtras(null, null, imageUri);
    }

    /**
     * 添加额外的参数到Intent
     * 注意：必须在setShareIntent之后调用
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

    public List<ShareTarget> getShareTargets() {
        return mShareTargets;
    }

    public List<ShareTarget> getDefaultShareTargets() {
        int length = Math.min(mDefaultLength, mShareTargets.size());
        return mShareTargets.subList(0, length);
    }

    /**
     * 重新加载目标Activity列表
     */
    private void reloadActivities() {
        loadShareTargets();
        sortShareTargets();
    }

    private void loadShareTargets() {
        if (mIntent != null) {
            mShareTargets.clear();
            List<ResolveInfo> activities =
                    mPackageManager.queryIntentActivities(mIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (activities == null || activities.isEmpty()) {
                return;
            }
            for (ResolveInfo resolveInfo : activities) {
                ShareTarget target = toShareTarget(resolveInfo);
                mShareTargets.add(target);
            }
        }
    }

    private void sortShareTargets() {
        if (mShareTargets.size() > 0) {
            if (DEBUG) {
                Log.v(TAG, "sortShareTargets() mShareTargets size=" + mShareTargets.size());
                Log.v(TAG, "sortShareTargets() mExtraPackages size=" + mExtraPackages.size());
            }
            for (String pkg : mExtraPackages) {
                ShareTarget target = findShareTarget(pkg);
                if (target != null) {
                    target.weight = --mWeightCounter;
                }
            }
            for (String pkg : mToRemovePackages) {
                ShareTarget target = findShareTarget(pkg);
                if (target != null) {
                    mShareTargets.remove(target);
                }
            }
            mShareTargets.addAll(mExtraTargets);
            Collections.sort(mShareTargets);
            mExtraTargets.clear();
            mExtraPackages.clear();
            mToRemovePackages.clear();

            final int size = mShareTargets.size();
            for (int i = 0; i < size; i++) {
                mShareTargets.get(i).id = i;
            }
        }
    }

    /**
     * 根据报名查找某个ShareTarget
     *
     * @param pkg 包名
     * @return index
     */
    private ShareTarget findShareTarget(String pkg) {
        for (ShareTarget target : mShareTargets) {
            if (pkg.equals(target.packageName)) {
                return target;
            }
        }
        return null;
    }

    /**
     * 根据ResolveInfo生成ShareTarget
     *
     * @param resolveInfo ResolveInfo
     * @return ShareTarget
     */
    private ShareTarget toShareTarget(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.activityInfo == null) {
            return null;
        }
        ActivityInfo info = resolveInfo.activityInfo;
        ShareTarget target = new ShareTarget(info.loadLabel(mPackageManager), info.loadIcon(mPackageManager), null);
        target.packageName = info.packageName;
        target.className = info.name;
        return target;
    }

    @Override
    public View onCreateActionView() {
        return null;
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
            Log.v(TAG, "onPrepareSubMenu() mDefaultLength=" + mDefaultLength + " mShareTargets.size()=" + mShareTargets.size());
        }
        int length = Math.min(mDefaultLength, mShareTargets.size());
        Resources res = mContext.getResources();
        for (int i = 0; i < length; i++) {
            ShareTarget target = mShareTargets.get(i);
            subMenu.add(0, i, i, target.title).setIcon(target.icon).setOnMenuItemClickListener(this);
        }

        if (mDefaultLength < mShareTargets.size()) {
            subMenu = subMenu.addSubMenu(Menu.NONE, mDefaultLength, mDefaultLength, mExpandLabel);

            for (int i = 0; i < mShareTargets.size(); i++) {
                ShareTarget target = mShareTargets.get(i);
                subMenu.add(0, i, i, target.title).setIcon(target.icon).setOnMenuItemClickListener(this);
            }
        }
    }

    /**
     * 按顺序处理，如果某一阶段返回true，忽略后续的处理
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        boolean handled = false;

        ShareTarget target = mShareTargets.get(item.getItemId());

        // 首先响应target自带的listener
        if (target.listener != null) {
            handled = target.listener.onMenuItemClick(item);
        }

        if (handled) {
            return true;
        }

        // 其次响应外部设置的listener
        if (mOnMenuItemClickListener != null) {
            handled = mOnMenuItemClickListener.onMenuItemClick(item);
        }
        if (handled) {
            return true;
        }

        if (target.packageName == null || target.className == null) {
            return true;
        }

        // 最后响应默认的intent
        ComponentName chosenName = new ComponentName(
                target.packageName,
                target.className);
        Intent intent = new Intent(mIntent);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(chosenName);
        if (DEBUG) {
            Log.v(TAG, "onMenuItemClick() target=" + chosenName);
        }
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "onMenuItemClick() error: " + e);
            Toast.makeText(mContext, R.string.share_action_provider_target_not_found, Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
