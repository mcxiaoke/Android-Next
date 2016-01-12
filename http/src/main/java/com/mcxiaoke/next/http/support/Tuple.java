package com.mcxiaoke.next.http.support;

/**
 * User: mcxiaoke
 * Date: 16/1/12
 * Time: 13:04
 */
public class Tuple<First, Second, Third> {
    public final First first;
    public final Second second;
    public final Third third;

    public Tuple(final First first, final Second second) {
        this.first = first;
        this.second = second;
        this.third = null;
    }

    public Tuple(final First first, final Second second, final Third third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
