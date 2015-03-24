/***
 Copyright (c) 2008-2009 CommonsWare, LLC
 Portions (c) 2009 Google, Inc.

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.mcxiaoke.next.ui.list;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.mcxiaoke.next.ui.internal.AdapterWrapper;
import com.mcxiaoke.next.ui.list.ListViewExtend.OnRefreshListener;
import com.mcxiaoke.next.ui.view.SimpleProgressView;

/**
 * User: mcxiaoke
 * Date: 13-8-14
 * Time: 下午9:32
 */
class AdapterExtend extends AdapterWrapper {
    private static final String TAG = AdapterExtend.class.getSimpleName();
    private static final boolean DEBUG = false;

    private Context mContext;
    private boolean mEnableRefreshing;
    private boolean mRefreshing;
    private SimpleProgressView mFooter;
    private OnRefreshListener mRefreshListener;

    /**
     * Constructor wrapping a supplied ListAdapter
     */
    public AdapterExtend(Context context, ListAdapter wrapped) {
        super(wrapped);
        mContext = context;
        mEnableRefreshing = true;
        mRefreshing = false;
        mFooter = new SimpleProgressView(context);
    }

    public void setRefreshListener(OnRefreshListener listener) {
        mRefreshListener = listener;
    }

    public boolean isRefreshing() {
        return mRefreshing;
    }

    public boolean isEnableRefreshing() {
        return mEnableRefreshing;
    }

    public void setEnableRefreshing(final boolean value) {
        mEnableRefreshing = value;
    }

    public void setOnClickListener(OnClickListener listener) {
        mFooter.setOnClickListener(listener);
    }

    /**
     * How many items are in the data set represented by this
     * Adapter.
     */
    @Override
    public int getCount() {
        if (isEnableRefreshing()) {
            return super.getCount() + 1;
        }
        return (super.getCount());
    }

    /**
     * Masks ViewType so the AdapterView replaces the
     * "Pending" row when new data is loaded.
     */
    public int getItemViewType(int position) {
        if (position == getWrappedAdapter().getCount() && isEnableRefreshing()) {
            return (IGNORE_ITEM_VIEW_TYPE);
        }

        return (super.getItemViewType(position));
    }

    /**
     * Masks ViewType so the AdapterView replaces the
     * "Pending" row when new data is loaded.
     *
     * @see #getItemViewType(int)
     */
    public int getViewTypeCount() {
        if (isEnableRefreshing()) {
            return (super.getViewTypeCount() + 1);
        }
        return super.getViewTypeCount();
    }

    @Override
    public Object getItem(int position) {
        if (position >= super.getCount()) {
            return (null);
        }

        return (super.getItem(position));
    }

    @Override
    public boolean areAllItemsEnabled() {
        return (false);
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= super.getCount()) {
            return (false);
        }

        return (super.isEnabled(position));
    }

    /**
     * Get a View that displays the data at the specified
     * position in the data set. In this case, if we are at
     * the end of the list and we are still in append mode, we
     * ask for a pending view and return it, plus kick off the
     * background task to append more data to the wrapped
     * adapter.
     *
     * @param position    Position of the item whose data we want
     * @param convertView View to recycle, if not null
     * @param parent      ViewGroup containing the returned View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == super.getCount() && isEnableRefreshing()) {
            return (mFooter);
        }
        return (super.getView(position, convertView, parent));
    }

    public void onStateChanged(boolean refreshing, boolean needNotify) {
        boolean stateChanged = refreshing != mRefreshing;
        mRefreshing = refreshing;
        if (stateChanged && needNotify && mRefreshListener != null) {
            mRefreshListener.onRefresh(null);
        }
    }

    /**
     * Getter method for the Context being held by the adapter
     *
     * @return Context
     */
    protected Context getContext() {
        return (mContext);
    }

    public void setRefreshing(boolean needNotify) {
        if (DEBUG) {
            Log.v(TAG, "setRefreshing() isRefreshing=" + isRefreshing());
        }
        if (isRefreshing()) {
            return;
        }
        onStateChanged(true, needNotify);
        mFooter.showProgress();
    }

    public void showFooterEmpty() {
        if (DEBUG) {
            Log.v(TAG, "showFooterEmpty() isRefreshing=" + isRefreshing());
        }
        onStateChanged(false, true);
        mFooter.showEmpty();
    }

    public void showFooterText(int resId) {
        if (DEBUG) {
            Log.v(TAG, "showFooterText() text=" + getContext().getString(resId));
        }
        showFooterText(getContext().getString(resId));
    }

    public void showFooterText(CharSequence text) {
        if (DEBUG) {
            Log.v(TAG, "showFooterText() text=" + text);
        }
        onStateChanged(false, true);
        mFooter.showText(text);
    }
}
