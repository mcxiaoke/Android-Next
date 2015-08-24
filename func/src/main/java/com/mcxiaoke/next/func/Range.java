package com.mcxiaoke.next.func;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * User: mcxiaoke
 * Date: 15/8/24
 * Time: 13:59
 */
public class Range implements Iterable<Integer> {

    public static Range range(int max) {
        return new Range(max);
    }

    private int start;
    private int end;
    private int step;

    public Range(int end) {
        this(0, end);
    }

    public Range(int start, int end) {
        this(start, end, 1);
    }

    public Range(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }


    @Override
    public Iterator<Integer> iterator() {
        final int max = end;
        return new Iterator<Integer>() {

            private int current = start;

            @Override
            public boolean hasNext() {
                return current < max;
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    return current += step;
                } else {
                    throw new NoSuchElementException("Range reached the end");
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Can't remove values from a Range");
            }
        };
    }
}