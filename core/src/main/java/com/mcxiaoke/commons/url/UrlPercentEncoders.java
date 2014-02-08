/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.mcxiaoke.commons.url;

import java.nio.charset.Charset;
import java.util.BitSet;

import static java.nio.charset.CodingErrorAction.REPLACE;

/**
 * See RFC 3986, RFC 1738 and http://www.lunatech-research.com/archives/2009/02/03/what-every-web-developer-must-know-about-url-encoding.
 */
public final class UrlPercentEncoders {

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    /**
     * an encoder for RFC 3986 reg-names
     */

    private static final BitSet REG_NAME_BIT_SET = new BitSet();

    private static final BitSet PATH_BIT_SET = new BitSet();
    private static final BitSet MATRIX_BIT_SET = new BitSet();
    private static final BitSet QUERY_BIT_SET = new BitSet();
    private static final BitSet FRAGMENT_BIT_SET = new BitSet();

    static {
        // RFC 3986 'reg-name'. This is not very aggressive... it's quite possible to have DNS-illegal names out of this.
        // Regardless, it will at least be URI-compliant even if it's not HTTP URL-compliant.
        addUnreserved(REG_NAME_BIT_SET);
        addSubdelims(REG_NAME_BIT_SET);

        // Represents RFC 3986 'pchar'. Remove delimiter that starts matrix section.
        addPChar(PATH_BIT_SET);
        PATH_BIT_SET.clear((int) ';');

        // Remove delims for HTTP matrix params as per RFC 1738 S3.3. The other reserved chars ('/' and '?') are already excluded.
        addPChar(MATRIX_BIT_SET);
        MATRIX_BIT_SET.clear((int) ';');
        MATRIX_BIT_SET.clear((int) '=');

        /*
        * at this point it represents RFC 3986 'query'.
        * Remove delimiters for HTTP queries
        * http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1 also specifies that "+" can mean space in a query,
        * so we will make sure to say that '+' is not safe to leave as-is
        */
        addQuery(QUERY_BIT_SET);
        QUERY_BIT_SET.clear((int) '=');
        QUERY_BIT_SET.clear((int) '&');
        QUERY_BIT_SET.clear((int) '+');

        addFragment(FRAGMENT_BIT_SET);
    }

    public static PercentEncoder getRegNameEncoder() {
        return new PercentEncoder(REG_NAME_BIT_SET, UTF_8.newEncoder().onMalformedInput(REPLACE)
                .onUnmappableCharacter(REPLACE));
    }

    public static PercentEncoder getPathEncoder() {
        return new PercentEncoder(PATH_BIT_SET, UTF_8.newEncoder().onMalformedInput(REPLACE)
                .onUnmappableCharacter(REPLACE));
    }

    public static PercentEncoder getMatrixEncoder() {
        return new PercentEncoder(MATRIX_BIT_SET, UTF_8.newEncoder().onMalformedInput(REPLACE)
                .onUnmappableCharacter(REPLACE));
    }

    public static PercentEncoder getQueryEncoder() {
        return new PercentEncoder(QUERY_BIT_SET, UTF_8.newEncoder().onMalformedInput(REPLACE)
                .onUnmappableCharacter(REPLACE));
    }

    public static PercentEncoder getFragmentEncoder() {
        return new PercentEncoder(FRAGMENT_BIT_SET, UTF_8.newEncoder().onMalformedInput(REPLACE)
                .onUnmappableCharacter(REPLACE));
    }

    private UrlPercentEncoders() {
    }

    /**
     * Add code points for 'fragment' chars
     *
     * @param fragmentBitSet bit set
     */
    private static void addFragment(BitSet fragmentBitSet) {
        addPChar(fragmentBitSet);
        fragmentBitSet.set((int) '/');
        fragmentBitSet.set((int) '?');
    }

    /**
     * Add code points for 'query' chars
     *
     * @param queryBitSet bit set
     */
    private static void addQuery(BitSet queryBitSet) {
        addPChar(queryBitSet);
        queryBitSet.set((int) '/');
        queryBitSet.set((int) '?');
    }

    /**
     * Add code points for 'pchar' chars.
     *
     * @param bs bitset
     */
    private static void addPChar(BitSet bs) {
        addUnreserved(bs);
        addSubdelims(bs);
        bs.set((int) ':');
        bs.set((int) '@');
    }

    /**
     * Add codepoints for 'unreserved' chars
     *
     * @param bs bitset to addPart codepoints to
     */
    private static void addUnreserved(BitSet bs) {

        for (int i = 'a'; i <= 'z'; i++) {
            bs.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            bs.set(i);
        }
        for (int i = '0'; i <= '9'; i++) {
            bs.set(i);
        }
        bs.set((int) '-');
        bs.set((int) '.');
        bs.set((int) '_');
        bs.set((int) '~');
    }

    /**
     * Add codepoints for 'sub-delims' chars
     *
     * @param bs bitset to addPart codepoints to
     */
    private static void addSubdelims(BitSet bs) {
        bs.set((int) '!');
        bs.set((int) '$');
        bs.set((int) '&');
        bs.set((int) '\'');
        bs.set((int) '(');
        bs.set((int) ')');
        bs.set((int) '*');
        bs.set((int) '+');
        bs.set((int) ',');
        bs.set((int) ';');
        bs.set((int) '=');
    }
}
