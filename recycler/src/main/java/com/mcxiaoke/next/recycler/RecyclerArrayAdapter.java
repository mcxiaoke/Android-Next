/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.next.recycler;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 适用于 RecyclerView的ArrayAdapter，用法基本相同
 * 精简了一些无用的方法，添加了一些实用的方法
 * 主要添加了以下方法：
 * <p/>
 * <p/>
 * public List<T> getAllItems() //获取Adapter里所有的数据项
 * public void add(int index, T object) // 指定位置插入数据
 * public void set(int index, T object) // 指定位置插入数据
 * public void addAll(int index, Collection<? extends T> collection)  //指定位置批量插入数据
 * public void addAll(int index, T... items) //指定位置批量插入数据
 * public boolean contains(T object) //是否包含某个数据项
 * public int indexOf(T object) // 同上，返回index，不包含就返回-1
 * public void removeAt(int index) // 移除某个位置的数据项
 * <p/>
 *
 * @param <T>
 */

/**
 * User: mcxiaoke
 * Date: 15-05-26
 * Time: 16:44
 */
public abstract class RecyclerArrayAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements Filterable {


    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    protected final Object mLock = new Object();

    private Context mContext;

    private Resources mResources;

    private LayoutInflater mInflater;

    /**
     * Contains the list of objects that represent the data of this ArrayAdapter.
     * The content of this list is referred to as "the array" in the documentation.
     */
    protected List<T> mObjects;
    // A copy of the original mObjects array, initialized from and then used instead as soon as
    // the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
    protected List<T> mOriginalValues;
    protected Filter mFilter;

    /**
     * Constructor
     *
     * @param context The current context.
     */
    public RecyclerArrayAdapter(Context context) {
        init(context, new ArrayList<T>());
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public RecyclerArrayAdapter(Context context, T[] objects) {
        init(context, Arrays.asList(objects));
    }

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public RecyclerArrayAdapter(Context context, List<T> objects) {
        init(context, objects);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    @Override
    public VH onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {

    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(object);
            } else {
                mObjects.add(object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified object at the index
     *
     * @param object The object to add at the end of the array.
     */
    public void add(int index, T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * set the specified object at index
     *
     * @param object The object to add at the end of the array.
     */
    public void set(int index, T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.set(index, object);
            } else {
                mObjects.set(index, object);
            }
        }
        notifyDataSetChanged();
    }

    public void setAll(Collection<? extends T> collection) {
        clear();
        addAll(collection);
    }

    public void setAll(T... items) {
        clear();
        addAll(items);
    }

    /**
     * Adds the specified Collection at the end of the array.
     *
     * @param collection The Collection to add at the end of the array.
     */
    public void addAll(Collection<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(collection);
            } else {
                mObjects.addAll(collection);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.addAll(mOriginalValues, items);
            } else {
                Collections.addAll(mObjects, items);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified objects at the specified index in the array.
     *
     * @param collection The objects to insert into the array.
     * @param index      The index at which the object must be inserted.
     */
    public void addAll(int index, Collection<? extends T> collection) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(index, collection);
            } else {
                mObjects.addAll(index, collection);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Inserts the specified objects at the specified index in the array.
     *
     * @param items The objects to insert into the array.
     * @param index The index at which the object must be inserted.
     */
    public void addAll(int index, T... items) {
        List<T> collection = Arrays.asList(items);
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.addAll(index, collection);
            } else {
                mObjects.addAll(index, collection);
            }
        }
        notifyDataSetChanged();
    }

    public List<T> getAllItems() {
        if (mOriginalValues != null) {
            return mOriginalValues;
        } else {
            return mObjects;
        }
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.add(index, object);
            } else {
                mObjects.add(index, object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * check contains the object.
     *
     * @param object The object to remove.
     */
    public boolean contains(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                return mOriginalValues.contains(object);
            } else {
                return mObjects.contains(object);
            }
        }
    }

    /**
     * check contains the object.
     *
     * @param object The object to remove.
     */
    public int indexOf(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                return mOriginalValues.indexOf(object);
            } else {
                return mObjects.indexOf(object);
            }
        }
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(object);
            } else {
                mObjects.remove(object);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified object in index from the array.
     *
     * @param index The index to remove.
     */
    public void removeAt(int index) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.remove(index);
            } else {
                mObjects.remove(index);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Removes the specified range from the array.
     *
     * @param start The start of the range to remove.
     * @param end   the end of the range, not include
     */
    public void removeRange(int start, int end) {
        final int size = getCount();
        if (start < 0 || end > size || start > end) {
            throw new IllegalArgumentException("invalid range (" + start + ", " + end + ")");
        }
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.subList(start, end).clear();
            } else {
                mObjects.subList(start, end).clear();
            }
        }
        notifyDataSetChanged();
    }


    /**
     * Removes the specified objects.
     *
     * @param collection The collection to remove.
     */
    public boolean removeAll(Collection<?> collection) {
        boolean result = false;
        synchronized (mLock) {
            Iterator<?> it;
            if (mOriginalValues != null) {
                it = mOriginalValues.iterator();

            } else {
                it = mObjects.iterator();
            }
            while (it.hasNext()) {
                if (collection.contains(it.next())) {
                    it.remove();
                    result = true;
                }
            }
        }
        notifyDataSetChanged();
        return result;
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                mOriginalValues.clear();
            } else {
                mObjects.clear();
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained
     *                   in this adapter.
     */
    public void sort(Comparator<? super T> comparator) {
        synchronized (mLock) {
            if (mOriginalValues != null) {
                Collections.sort(mOriginalValues, comparator);
            } else {
                Collections.sort(mObjects, comparator);
            }
        }
        notifyDataSetChanged();
    }

    private void init(Context context, List<T> objects) {
        mContext = context;
        mResources = context.getResources();
        mInflater = LayoutInflater.from(context);
        mObjects = objects;
    }

    /**
     * Returns the context associated with this array adapter. The context is used
     * to create views from the resource passed to the constructor.
     *
     * @return The Context associated with this adapter.
     */
    public Context getContext() {
        return mContext;
    }

    public Resources getResources() {
        return mResources;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    public int getCount() {
        return mObjects.size();
    }

    public boolean isEmpty() {
        return mObjects.size() == 0;
    }

    public T getLast() {
        return isEmpty() ? null : getItem(getCount() - 1);
    }

    public T getFirst() {
        return isEmpty() ? null : getItem(0);
    }

    public T getItem(int position) {
        if (mOriginalValues != null) {
            return mOriginalValues.get(0);
        }
        return mObjects.get(position);
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item.
     */
    public int getPosition(T item) {
        return mObjects.indexOf(item);
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void setFilter(Filter filter) {
        mFilter = filter;
    }

    /**
     * <p>An array filter constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class ArrayFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<T>(mObjects);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<T>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = toLowerCase(prefix.toString());

                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<T>(mOriginalValues);
                }

                final ArrayList<T> newValues = new ArrayList<T>();

                for (final T value : values) {
                    final String valueText = toLowerCase(value.toString());

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<T>) results.values;
            notifyDataSetChanged();
        }
    }

    static String toLowerCase(String text) {
        return text == null ? null : text.toLowerCase(Locale.US);
    }
}
