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

package com.mcxiaoke.next.ui.endless;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import com.mcxiaoke.next.ui.internal.AdapterWrapper;

/**
 * User: mcxiaoke
 * Date: 13-8-14
 * Time: 下午9:32
 */
class EndlessAdapter extends AdapterWrapper {

    public enum FooterState {
        NONE,
        PROGRESS,
        IDLE,
    }

    public interface OnFooterStateChangeListener {
        void onFooterStateChanged(FooterState state, EndlessAdapter adapter);
    }

    private Context mContext;
    private FooterState mState = FooterState.NONE;
    private View mFooter = null;
    private OnFooterStateChangeListener mFooterStateChangeListener;

    /**
     * Constructor wrapping a supplied ListAdapter
     */
    public EndlessAdapter(Context context, ListAdapter wrapped) {
        super(wrapped);
        mContext = context;
    }

    public void setFooterStateChangeListener(OnFooterStateChangeListener listener) {
        mFooterStateChangeListener = listener;
    }

    public void setFooterView(View footerView) {
        mFooter = footerView;
    }

    public FooterState getState() {
        return mState;
    }

    public boolean isRefreshing() {
        return FooterState.PROGRESS.equals(mState);
    }

    public boolean isIdle() {
        return FooterState.IDLE.equals(mState);
    }

    public boolean isFooterShowing() {
        return true;
    }

    /**
     * How many items are in the data set represented by this
     * Adapter.
     */
    @Override
    public int getCount() {
        if (isFooterShowing()) {
            return super.getCount() + 1;
        }
        return (super.getCount());
    }

    /**
     * Masks ViewType so the AdapterView replaces the
     * "Pending" row when new data is loaded.
     */
    public int getItemViewType(int position) {
        if (position == getWrappedAdapter().getCount()) {
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
        return (super.getViewTypeCount() + 1);
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
        if (position == super.getCount() && isFooterShowing()) {
            return (mFooter);
        }
        return (super.getView(position, convertView, parent));
    }

    public void setState(FooterState newState, boolean needNotify) {
        boolean stateChanged = !(mState.equals(newState));
        FooterState oldState = mState;
        mState = newState;
        if (stateChanged && needNotify && mFooterStateChangeListener != null) {
            mFooterStateChangeListener.onFooterStateChanged(mState, this);
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
}
