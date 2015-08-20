package com.mcxiaoke.next.func;

import com.mcxiaoke.next.func.functions.Action1;
import com.mcxiaoke.next.func.functions.Func1;
import com.mcxiaoke.next.func.functions.Func2;
import com.mcxiaoke.next.func.functions.Predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 15/8/20
 * Time: 10:24
 */
public class Functions {

    // ==================================================================
    // ==================================================================
    // Common Operators
    // ==================================================================
    // ==================================================================
    public static <T> Collection<T> concat(Collection<? extends T>... collections) {
        List<T> list = new ArrayList<T>();
        for (Collection<? extends T> c : collections) {
            list.addAll(c);
        }
        return list;
    }

    public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> collection) {
        return Collections.max(collection);
    }

    public static <T> T max(Comparator<? super T> cmp, Collection<? extends T> collection) {
        return Collections.max(collection, cmp);
    }

    // ==================================================================
    // ==================================================================
    // Conditional Operators
    // ==================================================================
    // ==================================================================


    public static <T> Collection<T> distinct(Collection<? extends T> collection) {
        return new HashSet<T>(collection);
    }

    public static <T> Collection<T> filter(Predicate<? super T> p,
                                           Iterable<? extends T> iterable) {
        List<T> list = new ArrayList<T>();
        for (T t : iterable) {
            if (p.accept(t)) {
                list.add(t);
            }
        }
        return list;
    }

    public static <T> boolean all(Predicate<? super T> p, Iterable<T> iterable) {
        for (final T t : iterable) {
            if (!p.accept(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean any(Predicate<? super T> p, Iterable<T> iterable) {
        for (final T t : iterable) {
            if (p.accept(t)) {
                return true;
            }
        }
        return false;
    }


    // ==================================================================
    // ==================================================================
    // Map Operators
    // ==================================================================
    // ==================================================================

    public static <T, R> void forEach(Action1<? super T> func, Iterable<T> iterable) {
        for (T t : iterable) {
            func.call(t);
        }
    }

    public static <T> List<List<T>> zip(List<T>... lists) {
        List<List<T>> zipped = new ArrayList<List<T>>();
        for (List<T> list : lists) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                List<T> zippedItem;
                if (i >= zipped.size()) {
                    zipped.add(zippedItem = new ArrayList<T>());
                } else {
                    zippedItem = zipped.get(i);
                }
                zippedItem.add(list.get(i));
            }
        }
        return zipped;
    }

    public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable,
                                  final R initializer) {
        R r = initializer;
        final Iterator<? extends T> it = iterable.iterator();
        while (it.hasNext()) {
            r = func2.call(r, it.next());
        }
        return r;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable) {
        R r = null;
        final Iterator<? extends T> it = iterable.iterator();
        if (it.hasNext()) {
            r = (R) it.next();
            while (it.hasNext()) {
                r = func2.call(r, it.next());
            }
        }
        return r;
    }

    public static <T, R> void map(Func1<? super T, ? extends R> func, Collection<T> from, Collection<R> to) {
        for (T c : from) {
            to.add(func.call(c));
        }
    }

    public static <T, R> Iterable<R> map(Func1<? super T, ? extends R> func, Iterable<T> iterable) {
        List<R> r = new ArrayList<R>();
        for (final T t : iterable) {
            r.add(func.call(t));
        }
        return r;
    }

    public static <T, R> Collection<R> map(Func1<? super T, ? extends R> func, Collection<T> collection) {
        List<R> r = new ArrayList<R>();
        for (T c : collection) {
            r.add(func.call(c));
        }
        return r;
    }

    public static <T, R> List<R> map(Func1<? super T, ? extends R> func, List<T> list) {
        List<R> r = new ArrayList<R>();
        for (T l : list) {
            r.add(func.call(l));
        }
        return r;
    }

    public static <T, R> Set<R> map(Func1<? super T, ? extends R> func, Set<T> set) {
        Set<R> r = new HashSet<R>();
        for (T s : set) {
            r.add(func.call(s));
        }
        return r;
    }
}
