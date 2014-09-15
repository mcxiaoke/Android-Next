package com.mcxiaoke.next.ui.widget;

import android.graphics.drawable.Drawable;
import android.view.MenuItem;

/**
 * User: mcxiaoke
 * Date: 13-12-24
 * Time: 上午10:32
 * <p/>
 * 分享目标
 */
public class ShareTarget implements Comparable<ShareTarget> {

    public int id; // id reserved
    public int weight;// weight for sort
    public String packageName; // share intent package name
    public String className; // share intent class name

    public CharSequence title; // menu item title
    public Drawable icon;// menu item icon
    public MenuItem.OnMenuItemClickListener listener; //menu item click listener

    public ShareTarget(CharSequence title, Drawable icon,
                       final MenuItem.OnMenuItemClickListener listener) {
        this(title, icon, 0, listener);
    }

    public ShareTarget(CharSequence title, Drawable icon, int id,
                       final MenuItem.OnMenuItemClickListener listener) {
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.listener = listener;
    }

    @Override
    public int compareTo(ShareTarget another) {
        return another.weight - weight;
    }

    @Override
    public String toString() {
        return "ShareTarget{" +
                "id=" + id +
                ", weight=" + weight +
                ", packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", title=" + title +
                ", icon=" + icon +
                ", listener=" + listener +
                '}';
    }
}
