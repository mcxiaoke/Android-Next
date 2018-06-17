/*
 * Copyright (C) 2017 Pascal Welsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.next.recycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Simple {@link RecyclerView.Adapter} implementation analog to {@link android.widget.ArrayAdapter}
 * for {@link android.support.v7.widget.RecyclerView}. Holds to a list of objects of type {@link T}
 *
 * @param <T>  item type (a immutable pojo works best)
 * @param <VH> {@link RecyclerView.ViewHolder} for item {@link T}
 * @author Pascal Welsch on 04.07.14.
 * Major update 09.05.17.
 */
@SuppressWarnings("WeakerAccess")
public abstract class AdvancedRecyclerArrayAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    /**
     * Lock used to modify the content of {@link #mObjects}. Any write operation
     * performed on the array should be synchronized on this lock.
     */
    private final Object mLock = new Object();

    private final List<T> mObjects = new ArrayList<>();

    @SuppressWarnings("ConstantConditions")
    public AdvancedRecyclerArrayAdapter(@NonNull final List<T> objects) {
        if (objects == null) {
            throw new IllegalStateException("null is not supported. Use an empty list.");
        }
        addAll(objects);
    }

    public AdvancedRecyclerArrayAdapter() {

    }

    /**
     * Adds the specified object at the end of the array.
     *
     * @param object The object to add at the end of the array.
     */
    public void add(@NonNull final T object) {
        synchronized (mLock) {
            final int position = getItemCount();
            mObjects.add(object);
            notifyItemInserted(position);
        }
    }

    /**
     * Adds the specified list of objects at the end of the array.
     *
     * @param collection The objects to add at the end of the array.
     */
    public void addAll(@NonNull final Collection<T> collection) {
        final int length = collection.size();
        if (length == 0) {
            return;
        }
        synchronized (mLock) {
            final int position = getItemCount();
            mObjects.addAll(collection);
            notifyItemRangeInserted(position, length);
        }
    }

    public void addAll(int index, @NonNull final Collection<T> collection) {
        final int length = collection.size();
        if (length == 0) {
            return;
        }
        synchronized (mLock) {
            mObjects.addAll(index, collection);
            notifyItemRangeInserted(index, length);
        }
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    @SafeVarargs
    public final void addAll(T... items) {
        final int length = items.length;
        if (length == 0) {
            return;
        }
        synchronized (mLock) {
            final int position = getItemCount();
            Collections.addAll(mObjects, items);
            notifyItemRangeInserted(position, length);
        }
    }

    @SafeVarargs
    public final void addAll(int index, T... items) {
        final int length = items.length;
        if (length == 0) {
            return;
        }
        synchronized (mLock) {
            mObjects.addAll(index, Arrays.asList(items));
            notifyItemRangeInserted(index, length);
        }
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        if (mObjects.isEmpty()) {
            return;
        }
        synchronized (mLock) {
            final int size = getItemCount();
            mObjects.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * Returns the item at the specified position.
     *
     * @param position index of the item to return
     * @return the item at the specified position or {@code null} when not found
     */
    @Nullable
    public T getItem(final int position) {
        if (position < 0 || position >= mObjects.size()) {
            return null;
        }
        return mObjects.get(position);
    }

    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    /**
     * Return a stable id for an item. The item doesn't have to be part of the underlying data set.
     *
     * If you don't have an id field simply return the {@code item} itself
     *
     * @param item for which a stable id should be generated
     * @return a identifier for the given item
     */
    @Nullable
    public abstract Object getItemId(@NonNull T item);

    /**
     * Returns the items in the adapter as a unmodifiable list. Use the mutate functions to change
     * the items of this adapter ({@link #add(Object)}, {@link #remove(Object)}) or replace the list
     * entirely ({#link {@link #swap(List)}})
     *
     * @return the current items in this adapter
     */
    @NonNull
    public List<T> getItems() {
        return Collections.unmodifiableList(new ArrayList<T>(mObjects));
    }

    /**
     * Returns the position of the specified item in the array.
     *
     * @param item The item to retrieve the position of.
     * @return The position of the specified item or -1 if there is no such item.
     */
    public int getPosition(@NonNull final T item) {
        return mObjects.indexOf(item);
    }

    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(@NonNull T object, int index) {
        synchronized (mLock) {
            mObjects.add(index, object);
            notifyItemInserted(index);
        }
    }

    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * <p>
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * so that you can change its behavior depending on your UI.
     * For example, if you are using DiffUtil with a
     * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * <p>
     * This method is called only if {@link #isItemTheSame(Object, Object)} returns
     * {@code true} for these items.
     *
     * @param oldItem The position of the item in the old list
     * @param newItem The position of the item in the new list which replaces the
     *                oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    public boolean isContentTheSame(@Nullable final T oldItem, @Nullable final T newItem) {
        return (oldItem == newItem) || (oldItem != null && oldItem.equals(newItem));
    }

    /**
     * Called by the DiffUtil to decide whether two object represent the same Item.
     * <p>
     * For example, if your items have unique ids, this method should check their id equality.
     *
     * @param oldItem The position of the item in the old list
     * @param newItem The position of the item in the new list
     * @return True if the two items represent the same object or false if they are different.
     * @see #getItemId(Object)
     */
    public boolean isItemTheSame(@Nullable final T oldItem, @Nullable final T newItem) {

        if (oldItem == null && newItem == null) {
            return true;
        }
        if (oldItem == null || newItem == null) {
            return false;
        }

        final Object oldId = getItemId(oldItem);
        final Object newId = getItemId(newItem);

        return (oldId == newId) || (oldId != null && oldId.equals(newId));
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(@NonNull T object) {
        synchronized (mLock) {
            final int position = getPosition(object);
            final boolean removed = mObjects.remove(object);
            if (removed) {
                notifyItemRemoved(position);
            }
        }

    }

    /**
     * replaces the old with the new item. The new item will not be added when the old one is not
     * found.
     *
     * @param oldObject will be removed
     * @param newObject is added only when hte old item is removed
     */
    public void replaceItem(@NonNull final T oldObject, @NonNull final T newObject) {
        synchronized (mLock) {
            final int position = getPosition(oldObject);
            if (position == -1) {
                // not found, don't replace
                return;
            }

            mObjects.remove(position);
            mObjects.add(position, newObject);

            if (isItemTheSame(oldObject, newObject)) {
                if (isContentTheSame(oldObject, newObject)) {
                    // visible content hasn't changed, don't notify
                    return;
                }

                // item with same stable id has changed
                notifyItemChanged(position, newObject);
            } else {
                // item replaced with another one with a different id
                notifyItemRemoved(position);
                notifyItemInserted(position);
            }
        }
    }

    /**
     * Sorts the content of this adapter using the specified comparator.
     *
     * @param comparator The comparator used to sort the objects contained in this adapter.
     */
    public void sort(@NonNull Comparator<? super T> comparator) {
        final ArrayList<T> copy = new ArrayList<>(mObjects);
        Collections.sort(copy, comparator);
        swap(copy);
    }

    /**
     * Swaps the data, removes all existing data and replaces them with a new set of data. {@link
     * DiffUtil} will coordinate to update notifications. Make sure {@link #getItemId(Object)} is
     * implemented correctly.
     *
     * @param newObjects new set of data
     * @see #isContentTheSame(Object, Object)
     * @see #isItemTheSame(Object, Object)
     */
    @SuppressWarnings("ConstantConditions")
    public void swap(@Nullable final List<T> newObjects) {
        if (newObjects == null) {
            clear();
        } else {
            synchronized (mLock) {
                final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    @Override
                    public boolean areContentsTheSame(final int oldItemPosition,
                                                      final int newItemPosition) {
                        final T oldItem = mObjects.get(oldItemPosition);
                        final T newItem = newObjects.get(newItemPosition);
                        return isContentTheSame(oldItem, newItem);
                    }

                    @Override
                    public boolean areItemsTheSame(final int oldItemPosition,
                                                   final int newItemPosition) {
                        final T oldItem = mObjects.get(oldItemPosition);
                        final T newItem = newObjects.get(newItemPosition);
                        return isItemTheSame(oldItem, newItem);
                    }

                    @Override
                    public int getNewListSize() {
                        return newObjects.size();
                    }

                    @Override
                    public int getOldListSize() {
                        return mObjects.size();
                    }
                });
                mObjects.clear();
                mObjects.addAll(newObjects);
                result.dispatchUpdatesTo(this);
            }
        }
    }

}