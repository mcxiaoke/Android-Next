package com.mcxiaoke.next.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.ViewGroup;

@SuppressWarnings("WeakerAccess")
abstract class HeaderFooterRecyclerAdapter
        extends RecyclerView.Adapter {
    private static final String TAG = HeaderFooterRecyclerAdapter.class.getSimpleName();

    protected static final int VIEW_TYPE_MAX_COUNT = Integer.MAX_VALUE / 10;
    protected static final int HEADER_VIEW_TYPE_OFFSET = 0;
    protected static final int FOOTER_VIEW_TYPE_OFFSET = HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;
    protected static final int CONTENT_VIEW_TYPE_OFFSET = FOOTER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT;

    private int headerItemCount;
    private int contentItemCount;
    private int footerItemCount;

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public final ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder() viewType=" + viewType
                + " headerItemCount=" + headerItemCount
                + " contentItemCount=" + contentItemCount
                + " footerItemCount=" + footerItemCount);
        // Delegate to proper methods based on the viewType ranges.
        if (viewType >= HEADER_VIEW_TYPE_OFFSET && viewType < HEADER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
            return onCreateHeaderItemViewHolder(parent, viewType - HEADER_VIEW_TYPE_OFFSET);
        } else if (viewType >= FOOTER_VIEW_TYPE_OFFSET && viewType < FOOTER_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
            return onCreateFooterItemViewHolder(parent, viewType - FOOTER_VIEW_TYPE_OFFSET);
        } else if (viewType >= CONTENT_VIEW_TYPE_OFFSET && viewType < CONTENT_VIEW_TYPE_OFFSET + VIEW_TYPE_MAX_COUNT) {
            return onCreateContentItemViewHolder(parent, viewType - CONTENT_VIEW_TYPE_OFFSET);
        } else {
            // This shouldn't happen as we check that the viewType provided by the client is valid.
            throw new IllegalStateException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() position=" + position
                + " headerItemCount=" + headerItemCount
                + " contentItemCount=" + contentItemCount
                + " footerItemCount=" + footerItemCount);
        // Delegate to proper methods based on the viewType ranges.
        if (headerItemCount > 0 && position < headerItemCount) {
            onBindHeaderItemViewHolder(holder, position);
        } else if (contentItemCount > 0 && position - headerItemCount < contentItemCount) {
            onBindContentItemViewHolder(holder, position - headerItemCount);
        } else if (footerItemCount > 0 && position - headerItemCount - contentItemCount >= 0) {
            onBindFooterItemViewHolder(holder, position - headerItemCount - contentItemCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getItemCount() {
        // Cache the counts and return the sum of them.
        headerItemCount = getHeaderItemCount();
        contentItemCount = getContentItemCount();
        footerItemCount = getFooterItemCount();
        Log.v(TAG, "getItemCount()"
                + " headerItemCount=" + headerItemCount
                + " contentItemCount=" + contentItemCount
                + " footerItemCount=" + footerItemCount);
        return headerItemCount + contentItemCount + footerItemCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int getItemViewType(int position) {
        Log.v(TAG, "getItemCount() position=" + position
                + " headerItemCount=" + headerItemCount
                + " contentItemCount=" + contentItemCount
                + " footerItemCount=" + footerItemCount);
        // Delegate to proper methods based on the position, but validate first.
        if (headerItemCount > 0 && position < headerItemCount) {
            return validateViewType(getHeaderItemViewType(position)) + HEADER_VIEW_TYPE_OFFSET;
        } else if (contentItemCount > 0 && position - headerItemCount < contentItemCount) {
            return validateViewType(getContentItemViewType(position - headerItemCount)) + CONTENT_VIEW_TYPE_OFFSET;
        } else {
            return validateViewType(getFooterItemViewType(position - headerItemCount - contentItemCount)) + FOOTER_VIEW_TYPE_OFFSET;
        }
    }

    /**
     * Validates that the view type is within the valid range.
     *
     * @param viewType the view type.
     * @return the given view type.
     */
    private int validateViewType(int viewType) {
        if (viewType < 0 || viewType >= VIEW_TYPE_MAX_COUNT) {
            throw new IllegalStateException("viewType must be between 0 and " + VIEW_TYPE_MAX_COUNT);
        }
        return viewType;
    }

    /**
     * Notifies that a header item is inserted.
     *
     * @param position the position of the header item.
     */
    public final void notifyHeaderItemInserted(int position) {
        int newHeaderItemCount = getHeaderItemCount();
//        if (position < 0 || position >= newHeaderItemCount) {
//            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - " + (newHeaderItemCount - 1) + "].");
//        }
        notifyItemInserted(position);
    }

    /**
     * Notifies that multiple header items are inserted.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyHeaderItemRangeInserted(int positionStart, int itemCount) {
        int newHeaderItemCount = getHeaderItemCount();
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newHeaderItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for header items [0 - " + (newHeaderItemCount - 1) + "].");
        }
        notifyItemRangeInserted(positionStart, itemCount);
    }

    /**
     * Notifies that a header item is changed.
     *
     * @param position the position.
     */
    public final void notifyHeaderItemChanged(int position) {
        if (position < 0 || position >= headerItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - " + (headerItemCount - 1) + "].");
        }
        notifyItemChanged(position);
    }

    /**
     * Notifies that multiple header items are changed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyHeaderItemRangeChanged(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount >= headerItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for header items [0 - " + (headerItemCount - 1) + "].");
        }
        notifyItemRangeChanged(positionStart, itemCount);
    }


    /**
     * Notifies that an existing header item is moved to another position.
     *
     * @param fromPosition the original position.
     * @param toPosition   the new position.
     */
    public void notifyHeaderItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= headerItemCount || toPosition >= headerItemCount) {
            throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition " + toPosition + " is not within the position bounds for header items [0 - " + (headerItemCount - 1) + "].");
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Notifies that a header item is removed.
     *
     * @param position the position.
     */
    public void notifyHeaderItemRemoved(int position) {
//        if (position < 0 || position >= headerItemCount) {
//            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for header items [0 - " + (headerItemCount - 1) + "].");
//        }
        notifyItemRemoved(position);
    }

    /**
     * Notifies that multiple header items are removed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public void notifyHeaderItemRangeRemoved(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > headerItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - " + (positionStart + itemCount - 1) + "] is not within the position bounds for header items [0 - " + (headerItemCount - 1) + "].");
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    /**
     * Notifies that a content item is inserted.
     *
     * @param position the position of the content item.
     */
    public final void notifyContentItemInserted(int position) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        if (position < 0 || position >= newContentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position + " is not within the position bounds for content items [0 - " + (newContentItemCount - 1) + "].");
        }
        notifyItemInserted(position + newHeaderItemCount);
    }

    /**
     * Notifies that multiple content items are inserted.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeInserted(int positionStart, int itemCount) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newContentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - "
                    + (newContentItemCount - 1) + "].");
        }
        notifyItemRangeInserted(positionStart + newHeaderItemCount, itemCount);
    }

    /**
     * Notifies that a content item is changed.
     *
     * @param position the position.
     */
    public final void notifyContentItemChanged(int position) {
        if (position < 0 || position >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position
                    + " is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemChanged(position + headerItemCount);
    }

    /**
     * Notifies that multiple content items are changed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeChanged(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > contentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - "
                    + (contentItemCount - 1) + "].");
        }
        notifyItemRangeChanged(positionStart + headerItemCount, itemCount);
    }

    /**
     * Notifies that an existing content item is moved to another position.
     *
     * @param fromPosition the original position.
     * @param toPosition   the new position.
     */
    public final void notifyContentItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= contentItemCount || toPosition >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition + " or toPosition "
                    + toPosition + " is not within the position bounds for content items [0 - " + (contentItemCount - 1) + "].");
        }
        notifyItemMoved(fromPosition + headerItemCount, toPosition + headerItemCount);
    }

    /**
     * Notifies that a content item is removed.
     *
     * @param position the position.
     */
    public final void notifyContentItemRemoved(int position) {
        if (position < 0 || position >= contentItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position
                    + " is not within the position bounds for content items [0 - "
                    + (contentItemCount - 1) + "].");
        }
        notifyItemRemoved(position + headerItemCount);
    }

    /**
     * Notifies that multiple content items are removed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyContentItemRangeRemoved(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > contentItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for content items [0 - "
                    + (contentItemCount - 1) + "].");
        }
        notifyItemRangeRemoved(positionStart + headerItemCount, itemCount);
    }

    /**
     * Notifies that a footer item is inserted.
     *
     * @param position the position of the content item.
     */
    public final void notifyFooterItemInserted(int position) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        int newFooterItemCount = getFooterItemCount();
//        if (position < 0 || position >= newFooterItemCount) {
//            throw new IndexOutOfBoundsException("The given position " + position
//                    + " is not within the position bounds for footer items [0 - "
//                    + (newFooterItemCount - 1) + "].");
//        }
        notifyItemInserted(position + newHeaderItemCount + newContentItemCount);
    }

    /**
     * Notifies that multiple footer items are inserted.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyFooterItemRangeInserted(int positionStart, int itemCount) {
        int newHeaderItemCount = getHeaderItemCount();
        int newContentItemCount = getContentItemCount();
        int newFooterItemCount = getFooterItemCount();
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > newFooterItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for footer items [0 - "
                    + (newFooterItemCount - 1) + "].");
        }
        notifyItemRangeInserted(positionStart + newHeaderItemCount + newContentItemCount, itemCount);
    }

    /**
     * Notifies that a footer item is changed.
     *
     * @param position the position.
     */
    public final void notifyFooterItemChanged(int position) {
        if (position < 0 || position >= footerItemCount) {
            throw new IndexOutOfBoundsException("The given position " + position
                    + " is not within the position bounds for footer items [0 - "
                    + (footerItemCount - 1) + "].");
        }
        notifyItemChanged(position + headerItemCount + contentItemCount);
    }

    /**
     * Notifies that multiple footer items are changed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyFooterItemRangeChanged(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > footerItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for footer items [0 - "
                    + (footerItemCount - 1) + "].");
        }
        notifyItemRangeChanged(positionStart + headerItemCount + contentItemCount, itemCount);
    }

    /**
     * Notifies that an existing footer item is moved to another position.
     *
     * @param fromPosition the original position.
     * @param toPosition   the new position.
     */
    public final void notifyFooterItemMoved(int fromPosition, int toPosition) {
        if (fromPosition < 0 || toPosition < 0 || fromPosition >= footerItemCount || toPosition >= footerItemCount) {
            throw new IndexOutOfBoundsException("The given fromPosition " + fromPosition
                    + " or toPosition " + toPosition + " is not within the position bounds for footer items [0 - "
                    + (footerItemCount - 1) + "].");
        }
        notifyItemMoved(fromPosition + headerItemCount + contentItemCount, toPosition + headerItemCount + contentItemCount);
    }

    /**
     * Notifies that a footer item is removed.
     *
     * @param position the position.
     */
    public final void notifyFooterItemRemoved(int position) {
//        if (position < 0 || position >= footerItemCount) {
//            throw new IndexOutOfBoundsException("The given position " + position
//                    + " is not within the position bounds for footer items [0 - " + (footerItemCount - 1) + "].");
//        }
        notifyItemRemoved(position + headerItemCount + contentItemCount);
    }

    /**
     * Notifies that multiple footer items are removed.
     *
     * @param positionStart the position.
     * @param itemCount     the item count.
     */
    public final void notifyFooterItemRangeRemoved(int positionStart, int itemCount) {
        if (positionStart < 0 || itemCount < 0 || positionStart + itemCount > footerItemCount) {
            throw new IndexOutOfBoundsException("The given range [" + positionStart + " - "
                    + (positionStart + itemCount - 1) + "] is not within the position bounds for footer items [0 - "
                    + (footerItemCount - 1) + "].");
        }
        notifyItemRangeRemoved(positionStart + headerItemCount + contentItemCount, itemCount);
    }

    /**
     * Gets the header item view type. By default, this method returns 0.
     *
     * @param position the position.
     * @return the header item view type (within the range [0 - VIEW_TYPE_MAX_COUNT-1]).
     */
    protected int getHeaderItemViewType(int position) {
        return 0;
    }

    /**
     * Gets the footer item view type. By default, this method returns 0.
     *
     * @param position the position.
     * @return the footer item view type (within the range [0 - VIEW_TYPE_MAX_COUNT-1]).
     */
    protected int getFooterItemViewType(int position) {
        return 0;
    }

    /**
     * Gets the content item view type. By default, this method returns 0.
     *
     * @param position the position.
     * @return the content item view type (within the range [0 - VIEW_TYPE_MAX_COUNT-1]).
     */
    protected int getContentItemViewType(int position) {
        return 0;
    }

    /**
     * Gets the header item count. This method can be called several times, so it should not calculate the count every time.
     *
     * @return the header item count.
     */
    protected abstract int getHeaderItemCount();

    /**
     * Gets the footer item count. This method can be called several times, so it should not calculate the count every time.
     *
     * @return the footer item count.
     */
    protected abstract int getFooterItemCount();

    /**
     * Gets the content item count. This method can be called several times, so it should not calculate the count every time.
     *
     * @return the content item count.
     */
    protected abstract int getContentItemCount();

    /**
     * This method works exactly the same as {@link #onCreateViewHolder(ViewGroup, int)}, but for header items.
     *
     * @param parent   the parent view.
     * @param viewType the view type for the header.
     * @return the view holder.
     */
    protected abstract ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int viewType);

    /**
     * This method works exactly the same as {@link #onCreateViewHolder(ViewGroup, int)}, but for footer items.
     *
     * @param parent   the parent view.
     * @param viewType the view type for the footer.
     * @return the view holder.
     */
    protected abstract ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int viewType);

    /**
     * This method works exactly the same as {@link #onCreateViewHolder(ViewGroup, int)}, but for content items.
     *
     * @param parent   the parent view.
     * @param viewType the view type for the content.
     * @return the view holder.
     */
    protected abstract ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType);

    /**
     * This method works exactly the same as {@link #onBindViewHolder(ViewHolder, int)}, but for header items.
     *
     * @param holder   the view holder for the header item.
     * @param position the position.
     */
    protected abstract void onBindHeaderItemViewHolder(ViewHolder holder, int position);

    /**
     * This method works exactly the same as {@link #onBindViewHolder(ViewHolder, int)}, but for footer items.
     *
     * @param holder   the view holder for the footer item.
     * @param position the position.
     */
    protected abstract void onBindFooterItemViewHolder(ViewHolder holder, int position);

    /**
     * This method works exactly the same as {@link #onBindViewHolder(ViewHolder, int)}, but for content items.
     *
     * @param holder   the view holder for the content item.
     * @param position the position.
     */
    protected abstract void onBindContentItemViewHolder(ViewHolder holder, int position);

}