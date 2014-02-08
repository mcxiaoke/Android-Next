/*
 * Copyright (c) 2012 Palomino Labs, Inc.
 */

package com.mcxiaoke.commons.url;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.util.BitSet;

import static java.lang.Character.isHighSurrogate;
import static java.lang.Character.isLowSurrogate;

/**
 * Encodes unsafe characters as a sequence of %XX hex-encoded bytes.
 * <p/>
 * This is typically done when encoding components of URLs. See {@link UrlPercentEncoders} for pre-configured
 * PercentEncoder instances.
 */
public final class PercentEncoder {

    /**
     * Amount to addPart to a lowercase ascii alpha char to make it an uppercase. Used for hex encoding.
     */
    private static final int UPPER_CASE_DIFF = 'a' - 'A';

    private final BitSet safeChars;
    private final CharsetEncoder encoder;
    private final StringBuilder outputBuf = new StringBuilder();

    /**
     * @param safeChars      the set of chars to NOT encode, stored as a bitset with the int positions corresponding to
     *                       those chars set to true. Treated as read only.
     * @param charsetEncoder charset encoder to encode characters with. Make sure to not re-use CharsetEncoder instances
     *                       across threads.
     */
    public PercentEncoder(BitSet safeChars, CharsetEncoder charsetEncoder) {
        this.safeChars = safeChars;
        this.encoder = charsetEncoder;
    }

    /**
     * @param input input string
     * @return the input string with every character that's not in safeChars turned into its byte representation via the
     * instance's encoder and then percent-encoded
     * @throws java.nio.charset.MalformedInputException      if encoder is configured to report errors and malformed input is detected
     * @throws java.nio.charset.UnmappableCharacterException if encoder is configured to report errors and an unmappable character is
     *                                                       detected
     */

    public String encode(CharSequence input) throws MalformedInputException, UnmappableCharacterException {
        // output buf will be at least as long as the input
        outputBuf.setLength(0);
        outputBuf.ensureCapacity(input.length());

        // need to handle surrogate pairs, so need to be able to handle 2 chars worth of stuff at once

        // why is this a float? sigh.
        int maxBytes = 1 + (int) encoder.maxBytesPerChar();
        ByteBuffer byteBuffer = ByteBuffer.allocate(maxBytes * 2);
        CharBuffer charBuffer = CharBuffer.allocate(2);

        for (int i = 0; i < input.length(); i++) {

            char c = input.charAt(i);

            if (safeChars.get(c)) {
                outputBuf.append(c);
                continue;
            }

            // not a safe char
            charBuffer.clear();
            byteBuffer.clear();
            charBuffer.append(c);
            if (isHighSurrogate(c)) {
                if (input.length() > i + 1) {
                    // get the low surrogate as well
                    char lowSurrogate = input.charAt(i + 1);
                    if (isLowSurrogate(lowSurrogate)) {
                        charBuffer.append(lowSurrogate);
                        i++;
                    } else {
                        throw new IllegalArgumentException(
                                "Invalid UTF-16: Char " + (i) + " is a high surrogate (\\u" + Integer
                                        .toHexString(c) + "), but char " + (i + 1) + " is not a low surrogate (\\u" + Integer
                                        .toHexString(lowSurrogate) + ")");
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Invalid UTF-16: The last character in the input string was a high surrogate (\\u" + Integer
                                    .toHexString(c) + ")");
                }
            }
            addEncodedChars(outputBuf, byteBuffer, charBuffer, encoder);
        }

        return outputBuf.toString();
    }

    /**
     * Encode charBuffer to bytes as per charsetEncoder, then percent-encode those bytes into output.
     *
     * @param output         where the encoded versions of the contents of charBuffer will be written
     * @param byteBuffer     encoded chars buffer in write state. Will be written to, flipped, and fully read from.
     * @param charBuffer     unencoded chars buffer containing one or two chars in write mode. Will be flipped to and
     *                       fully read from.
     * @param charsetEncoder encoder
     */
    private static void addEncodedChars(StringBuilder output, ByteBuffer byteBuffer, CharBuffer charBuffer,
                                        CharsetEncoder charsetEncoder) throws MalformedInputException, UnmappableCharacterException {
        // need to read from the char buffer, which was most recently written to
        charBuffer.flip();

        charsetEncoder.reset();
        CoderResult result = charsetEncoder.encode(charBuffer, byteBuffer, true);
        checkResult(result);
        result = charsetEncoder.flush(byteBuffer);
        checkResult(result);

        // read contents of bytebuffer
        byteBuffer.flip();

        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();

            int msbits = (b >> 4) & 0xF;
            int lsbits = b & 0xF;

            char msbitsChar = Character.forDigit(msbits, 16);
            char lsbitsChar = Character.forDigit(lsbits, 16);

            output.append('%');
            output.append(capitalizeIfLetter(msbitsChar));
            output.append(capitalizeIfLetter(lsbitsChar));
        }
    }

    /**
     * @param result result to check
     * @throws IllegalStateException                         if result is overflow
     * @throws java.nio.charset.MalformedInputException      if result represents malformed input
     * @throws java.nio.charset.UnmappableCharacterException if result represents an unmappable character
     */
    private static void checkResult(CoderResult result) throws MalformedInputException, UnmappableCharacterException {
        if (result.isOverflow()) {
            throw new IllegalStateException("Byte buffer overflow; this should not happen.");
        }
        if (result.isMalformed()) {
            throw new MalformedInputException(result.length());
        }
        if (result.isUnmappable()) {
            throw new UnmappableCharacterException(result.length());
        }
    }

    /**
     * @param c ascii lowercase alphabetic or numeric char
     * @return char uppercased if a letter
     */
    private static char capitalizeIfLetter(char c) {
        if (Character.isLetter(c)) {
            return (char) (c - UPPER_CASE_DIFF);
        }

        return c;
    }
}
