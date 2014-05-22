/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mcxiaoke.next.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class NoDuplicatesCopyOnWriteArrayList<E> extends CopyOnWriteArrayList<E> {

    public NoDuplicatesCopyOnWriteArrayList() {
        super();
    }

    public NoDuplicatesCopyOnWriteArrayList(final Collection<? extends E> collection) {
        super(collection);
    }

    public NoDuplicatesCopyOnWriteArrayList(final E[] array) {
        super(array);
    }

    @Override
    public boolean add(final E e) {
        return !contains(e) && super.add(e);
    }

    @Override
    public void add(final int index, final E element) {
        if (!contains(element)) {
            super.add(index, element);
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        final Collection<E> copy = new ArrayList<E>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> collection) {
        final Collection<E> copy = new ArrayList<E>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }
}
