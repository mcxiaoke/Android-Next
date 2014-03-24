/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcxiaoke.commons.utils;

import android.content.Context;
import com.mcxiaoke.commons.Charsets;
import com.mcxiaoke.commons.io.StringBuilderWriter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * General IO stream manipulation utilities.
 * <p/>
 * This class provides static utility methods for input/output operations.
 */
public final class IOUtils {

    private static final int EOF = -1;
    /**
     * The Unix directory separator character.
     */
    public static final char DIR_SEPARATOR_UNIX = '/';
    /**
     * The Windows directory separator character.
     */
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    /**
     * The system directory separator character.
     */
    public static final char DIR_SEPARATOR = File.separatorChar;
    /**
     * The Unix line separator string.
     */
    public static final String LINE_SEPARATOR_UNIX = "\n";
    /**
     * The Windows line separator string.
     */
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    /**
     * The system line separator string.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    ;

    /**
     * The default buffer size ({@value}) to use for
     * {@link #copyLarge(java.io.InputStream, java.io.OutputStream)}
     * and
     * {@link #copyLarge(java.io.Reader, java.io.Writer)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    /**
     * The default buffer size to use for the skip() methods.
     */
    private static final int SKIP_BUFFER_SIZE = 2048;

    // Allocated in the relevant skip method if necessary.
    /*
     * N.B. no need to synchronize these because:
     * - we don't care if the buffer is created multiple times (the data is ignored)
     * - we always use the same size buffer, so if it it is recreated it will still be OK
     * (if the buffer size were variable, we would need to synch. to ensure some other thread
     * did not create a smaller one)
     */
    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;

    /**
     * Instances should NOT be constructed in standard programming.
     */
    private IOUtils() {
        super();
    }

    //-----------------------------------------------------------------------

    public static void close(URLConnection conn) {
        if (conn instanceof HttpURLConnection) {
            ((HttpURLConnection) conn).disconnect();
        }
    }

    public static void closeQuietly(Reader input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Writer output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }

    public static void closeQuietly(Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ioe) {
                // ignored
            }
        }
    }

    public static void closeQuietly(Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException ioe) {
                // ignored
            }
        }
    }

    public static void closeQuietly(ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ioe) {
                // ignored
            }
        }
    }

    public static InputStream toBufferedInputStream(InputStream input) throws IOException {
        return new BufferedInputStream(input);
    }

    public static BufferedReader toBufferedReader(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    // read toByteArray
    //-----------------------------------------------------------------------

    public static byte[] toByteArray(File file) throws IOException {
        return toByteArray(new FileInputStream(file));
    }

    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    /**
     * Get contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * Use this method instead of <code>toByteArray(InputStream)</code>
     * when <code>InputStream</code> size is known.
     * <b>NOTE:</b> the method checks that the length can safely be cast to an int without truncation
     * before using {@link com.mcxiaoke.commons.utils.IOUtils#toByteArray(java.io.InputStream, int)} to read into the byte array.
     * (Arrays can have no more than Integer.MAX_VALUE entries anyway)
     *
     * @param input the <code>InputStream</code> to read from
     * @param size  the size of <code>InputStream</code>
     * @return the requested byte array
     * @throws java.io.IOException      if an I/O error occurs or <code>InputStream</code> size differ from parameter size
     * @throws IllegalArgumentException if size is less than zero or size is greater than Integer.MAX_VALUE
     * @see com.mcxiaoke.commons.utils.IOUtils#toByteArray(java.io.InputStream, int)
     * @since 2.1
     */
    public static byte[] toByteArray(InputStream input, long size) throws IOException {

        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + size);
        }

        return toByteArray(input, (int) size);
    }

    /**
     * Get the contents of an <code>InputStream</code> as a <code>byte[]</code>.
     * Use this method instead of <code>toByteArray(InputStream)</code>
     * when <code>InputStream</code> size is known
     *
     * @param input the <code>InputStream</code> to read from
     * @param size  the size of <code>InputStream</code>
     * @return the requested byte array
     * @throws java.io.IOException      if an I/O error occurs or <code>InputStream</code> size differ from parameter size
     * @throws IllegalArgumentException if size is less than zero
     * @since 2.1
     */
    public static byte[] toByteArray(InputStream input, int size) throws IOException {

        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        byte[] data = new byte[size];
        int offset = 0;
        int readed;

        while (offset < size && (readed = input.read(data, offset, size - offset)) != EOF) {
            offset += readed;
        }

        if (offset != size) {
            throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
        }

        return data;
    }

    /**
     * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
     * using the default character encoding of the platform.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input the <code>Reader</code> to read from
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     */
    public static byte[] toByteArray(Reader input) throws IOException {
        return toByteArray(input, Charset.defaultCharset());
    }

    /**
     * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
     * using the specified character encoding.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input    the <code>Reader</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static byte[] toByteArray(Reader input, Charset encoding) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    /**
     * Get the contents of a <code>Reader</code> as a <code>byte[]</code>
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input    the <code>Reader</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested byte array
     * @throws NullPointerException                         if the input is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static byte[] toByteArray(Reader input, String encoding) throws IOException {
        return toByteArray(input, Charsets.toCharset(encoding));
    }

    /**
     * Get the contents of a <code>String</code> as a <code>byte[]</code>
     * using the default character encoding of the platform.
     * <p/>
     * This is the same as {@link String#getBytes()}.
     *
     * @param input the <code>String</code> to convert
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs (never occurs)
     * @deprecated Use {@link String#getBytes()}
     */
    @Deprecated
    public static byte[] toByteArray(String input) throws IOException {
        return input.getBytes();
    }

    /**
     * Get the contents of a <code>URI</code> as a <code>byte[]</code>.
     *
     * @param uri the <code>URI</code> to read
     * @return the requested byte array
     * @throws NullPointerException if the uri is null
     * @throws java.io.IOException  if an I/O exception occurs
     * @since 2.4
     */
    public static byte[] toByteArray(URI uri) throws IOException {
        return IOUtils.toByteArray(uri.toURL());
    }

    /**
     * Get the contents of a <code>URL</code> as a <code>byte[]</code>.
     *
     * @param url the <code>URL</code> to read
     * @return the requested byte array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O exception occurs
     * @since 2.4
     */
    public static byte[] toByteArray(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        try {
            return IOUtils.toByteArray(conn);
        } finally {
            close(conn);
        }
    }

    /**
     * Get the contents of a <code>URLConnection</code> as a <code>byte[]</code>.
     *
     * @param urlConn the <code>URLConnection</code> to read
     * @return the requested byte array
     * @throws NullPointerException if the urlConn is null
     * @throws java.io.IOException  if an I/O exception occurs
     * @since 2.4
     */
    public static byte[] toByteArray(URLConnection urlConn) throws IOException {
        InputStream inputStream = urlConn.getInputStream();
        try {
            return IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
    }

    // read char[]
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an <code>InputStream</code> as a character array
     * using the default character encoding of the platform.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param is the <code>InputStream</code> to read from
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static char[] toCharArray(InputStream is) throws IOException {
        return toCharArray(is, Charset.defaultCharset());
    }

    /**
     * Get the contents of an <code>InputStream</code> as a character array
     * using the specified character encoding.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param is       the <code>InputStream</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static char[] toCharArray(InputStream is, Charset encoding)
            throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    /**
     * Get the contents of an <code>InputStream</code> as a character array
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param is       the <code>InputStream</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested character array
     * @throws NullPointerException                         if the input is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static char[] toCharArray(InputStream is, String encoding) throws IOException {
        return toCharArray(is, Charsets.toCharset(encoding));
    }

    /**
     * Get the contents of a <code>Reader</code> as a character array.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input the <code>Reader</code> to read from
     * @return the requested character array
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static char[] toCharArray(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    // read toString
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an <code>InputStream</code> as a String
     * using the default character encoding of the platform.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     */
    public static String toString(InputStream input) throws IOException {
        return toString(input, Charset.defaultCharset());
    }

    /**
     * Get the contents of an <code>InputStream</code> as a String
     * using the specified character encoding.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * </p>
     *
     * @param input    the <code>InputStream</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static String toString(InputStream input, Charset encoding) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    /**
     * Get the contents of an <code>InputStream</code> as a String
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input    the <code>InputStream</code> to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested String
     * @throws NullPointerException                         if the input is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     */
    public static String toString(InputStream input, String encoding)
            throws IOException {
        return toString(input, Charsets.toCharset(encoding));
    }

    /**
     * Get the contents of a <code>Reader</code> as a String.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input the <code>Reader</code> to read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     */
    public static String toString(Reader input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    /**
     * Gets the contents at the given URI.
     *
     * @param uri The URI source.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException if an I/O exception occurs.
     * @since 2.1
     */
    public static String toString(URI uri) throws IOException {
        return toString(uri, Charset.defaultCharset());
    }

    /**
     * Gets the contents at the given URI.
     *
     * @param uri      The URI source.
     * @param encoding The encoding name for the URL contents.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException if an I/O exception occurs.
     * @since 2.3.
     */
    public static String toString(URI uri, Charset encoding) throws IOException {
        return toString(uri.toURL(), Charsets.toCharset(encoding));
    }

    /**
     * Gets the contents at the given URI.
     *
     * @param uri      The URI source.
     * @param encoding The encoding name for the URL contents.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException                          if an I/O exception occurs.
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 2.1
     */
    public static String toString(URI uri, String encoding) throws IOException {
        return toString(uri, Charsets.toCharset(encoding));
    }

    /**
     * Gets the contents at the given URL.
     *
     * @param url The URL source.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException if an I/O exception occurs.
     * @since 2.1
     */
    public static String toString(URL url) throws IOException {
        return toString(url, Charset.defaultCharset());
    }

    /**
     * Gets the contents at the given URL.
     *
     * @param url      The URL source.
     * @param encoding The encoding name for the URL contents.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException if an I/O exception occurs.
     * @since 2.3
     */
    public static String toString(URL url, Charset encoding) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            return toString(inputStream, encoding);
        } finally {
            inputStream.close();
        }
    }

    /**
     * Gets the contents at the given URL.
     *
     * @param url      The URL source.
     * @param encoding The encoding name for the URL contents.
     * @return The contents of the URL as a String.
     * @throws java.io.IOException                          if an I/O exception occurs.
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 2.1
     */
    public static String toString(URL url, String encoding) throws IOException {
        return toString(url, Charsets.toCharset(encoding));
    }

    /**
     * Get the contents of a <code>byte[]</code> as a String
     * using the default character encoding of the platform.
     *
     * @param input the byte array to read from
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs (never occurs)
     * @deprecated Use {@link String#String(byte[])}
     */
    @Deprecated
    public static String toString(byte[] input) throws IOException {
        return new String(input);
    }

    /**
     * Get the contents of a <code>byte[]</code> as a String
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param input    the byte array to read from
     * @param encoding the encoding to use, null means platform default
     * @return the requested String
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs (never occurs)
     */
    public static String toString(byte[] input, String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }

    // readLines
    //-----------------------------------------------------------------------

    /**
     * Get the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the default character encoding of the platform.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input the <code>InputStream</code> to read from, not null
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static List<String> readLines(InputStream input) throws IOException {
        return readLines(input, Charset.defaultCharset());
    }

    /**
     * Get the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input    the <code>InputStream</code> to read from, not null
     * @param encoding the encoding to use, null means platform default
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static List<String> readLines(InputStream input, Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        return readLines(reader);
    }

    /**
     * Get the contents of an <code>InputStream</code> as a list of Strings,
     * one entry per line, using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     *
     * @param input    the <code>InputStream</code> to read from, not null
     * @param encoding the encoding to use, null means platform default
     * @return the list of Strings, never null
     * @throws NullPointerException                         if the input is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static List<String> readLines(InputStream input, String encoding) throws IOException {
        return readLines(input, Charsets.toCharset(encoding));
    }

    /**
     * Get the contents of a <code>Reader</code> as a list of Strings,
     * one entry per line.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     *
     * @param input the <code>Reader</code> to read from, not null
     * @return the list of Strings, never null
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static List<String> readLines(Reader input) throws IOException {
        BufferedReader reader = toBufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }


    /**
     * Convert the specified CharSequence to an input stream, encoded as bytes
     * using the default character encoding of the platform.
     *
     * @param input the CharSequence to convert
     * @return an input stream
     * @since 2.0
     */
    public static InputStream toInputStream(CharSequence input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    /**
     * Convert the specified CharSequence to an input stream, encoded as bytes
     * using the specified character encoding.
     *
     * @param input    the CharSequence to convert
     * @param encoding the encoding to use, null means platform default
     * @return an input stream
     * @since 2.3
     */
    public static InputStream toInputStream(CharSequence input, Charset encoding) {
        return toInputStream(input.toString(), encoding);
    }

    /**
     * Convert the specified CharSequence to an input stream, encoded as bytes
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param input    the CharSequence to convert
     * @param encoding the encoding to use, null means platform default
     * @return an input stream
     * @throws java.io.IOException                          if the encoding is invalid
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 2.0
     */
    public static InputStream toInputStream(CharSequence input, String encoding) throws IOException {
        return toInputStream(input, Charsets.toCharset(encoding));
    }

    //-----------------------------------------------------------------------

    /**
     * Convert the specified string to an input stream, encoded as bytes
     * using the default character encoding of the platform.
     *
     * @param input the string to convert
     * @return an input stream
     * @since 1.1
     */
    public static InputStream toInputStream(String input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    /**
     * Convert the specified string to an input stream, encoded as bytes
     * using the specified character encoding.
     *
     * @param input    the string to convert
     * @param encoding the encoding to use, null means platform default
     * @return an input stream
     * @since 2.3
     */
    public static InputStream toInputStream(String input, Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }

    /**
     * Convert the specified string to an input stream, encoded as bytes
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param input    the string to convert
     * @param encoding the encoding to use, null means platform default
     * @return an input stream
     * @throws java.io.IOException                          if the encoding is invalid
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = input.getBytes(Charsets.toCharset(encoding));
        return new ByteArrayInputStream(bytes);
    }

    // write byte[]
    //-----------------------------------------------------------------------

    /**
     * Writes bytes from a <code>byte[]</code> to an <code>OutputStream</code>.
     *
     * @param data   the byte array to write, do not modify during output,
     *               null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(byte[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the default character encoding of the platform.
     * <p/>
     * This method uses {@link String#String(byte[])}.
     *
     * @param data   the byte array to write, do not modify during output,
     *               null ignored
     * @param output the <code>Writer</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(byte[] data, Writer output) throws IOException {
        writeTo(data, output, Charset.defaultCharset());
    }

    /**
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the specified character encoding.
     * <p/>
     * This method uses {@link String#String(byte[], String)}.
     *
     * @param data     the byte array to write, do not modify during output,
     *                 null ignored
     * @param output   the <code>Writer</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void writeTo(byte[] data, Writer output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data, Charsets.toCharset(encoding)));
        }
    }

    /**
     * Writes bytes from a <code>byte[]</code> to chars on a <code>Writer</code>
     * using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link String#String(byte[], String)}.
     *
     * @param data     the byte array to write, do not modify during output,
     *                 null ignored
     * @param output   the <code>Writer</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void writeTo(byte[] data, Writer output, String encoding) throws IOException {
        writeTo(data, output, Charsets.toCharset(encoding));
    }

    // write char[]
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a <code>char[]</code> to a <code>Writer</code>
     * using the default character encoding of the platform.
     *
     * @param data   the char array to write, do not modify during output,
     *               null ignored
     * @param output the <code>Writer</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes chars from a <code>char[]</code> to bytes on an
     * <code>OutputStream</code>.
     * <p/>
     * This method uses {@link String#String(char[])} and
     * {@link String#getBytes()}.
     *
     * @param data   the char array to write, do not modify during output,
     *               null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(char[] data, OutputStream output)
            throws IOException {
        writeTo(data, output, Charset.defaultCharset());
    }

    /**
     * Writes chars from a <code>char[]</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * This method uses {@link String#String(char[])} and
     * {@link String#getBytes(String)}.
     *
     * @param data     the char array to write, do not modify during output,
     *                 null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void writeTo(char[] data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**
     * Writes chars from a <code>char[]</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link String#String(char[])} and
     * {@link String#getBytes(String)}.
     *
     * @param data     the char array to write, do not modify during output,
     *                 null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void writeTo(char[] data, OutputStream output, String encoding)
            throws IOException {
        writeTo(data, output, Charsets.toCharset(encoding));
    }

    // write CharSequence
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a <code>CharSequence</code> to a <code>Writer</code>.
     *
     * @param data   the <code>CharSequence</code> to write, null ignored
     * @param output the <code>Writer</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.0
     */
    public static void writeTo(CharSequence data, Writer output) throws IOException {
        if (data != null) {
            writeTo(data.toString(), output);
        }
    }

    /**
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     * <p/>
     * This method uses {@link String#getBytes()}.
     *
     * @param data   the <code>CharSequence</code> to write, null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.0
     */
    public static void writeTo(CharSequence data, OutputStream output)
            throws IOException {
        writeTo(data, output, Charset.defaultCharset());
    }

    /**
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>CharSequence</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void writeTo(CharSequence data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            writeTo(data.toString(), output, encoding);
        }
    }

    /**
     * Writes chars from a <code>CharSequence</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>CharSequence</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 2.0
     */
    public static void writeTo(CharSequence data, OutputStream output, String encoding) throws IOException {
        writeTo(data, output, Charsets.toCharset(encoding));
    }

    // write String
    //-----------------------------------------------------------------------

    /**
     * Writes chars from a <code>String</code> to a <code>Writer</code>.
     *
     * @param data   the <code>String</code> to write, null ignored
     * @param output the <code>Writer</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform.
     * <p/>
     * This method uses {@link String#getBytes()}.
     *
     * @param data   the <code>String</code> to write, null ignored
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeTo(String data, OutputStream output)
            throws IOException {
        writeTo(data, output, Charset.defaultCharset());
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>String</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void writeTo(String data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**
     * Writes chars from a <code>String</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link String#getBytes(String)}.
     *
     * @param data     the <code>String</code> to write, null ignored
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void writeTo(String data, OutputStream output, String encoding)
            throws IOException {
        writeTo(data, output, Charsets.toCharset(encoding));
    }

    // writeLines
    //-----------------------------------------------------------------------

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the default character
     * encoding of the platform and the specified line ending.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param output     the <code>OutputStream</code> to write to, not null, not closed
     * @throws NullPointerException if the output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeLines(Collection<?> lines, String lineEnding,
                                  OutputStream output) throws IOException {
        writeLines(lines, lineEnding, output, Charset.defaultCharset());
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the specified character
     * encoding and the specified line ending.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param output     the <code>OutputStream</code> to write to, not null, not closed
     * @param encoding   the encoding to use, null means platform default
     * @throws NullPointerException if the output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void writeLines(Collection<?> lines, String lineEnding, OutputStream output, Charset encoding)
            throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        Charset cs = Charsets.toCharset(encoding);
        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineEnding.getBytes(cs));
        }
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * an <code>OutputStream</code> line by line, using the specified character
     * encoding and the specified line ending.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param output     the <code>OutputStream</code> to write to, not null, not closed
     * @param encoding   the encoding to use, null means platform default
     * @throws NullPointerException                         if the output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void writeLines(Collection<?> lines, String lineEnding,
                                  OutputStream output, String encoding) throws IOException {
        writeLines(lines, lineEnding, output, Charsets.toCharset(encoding));
    }

    /**
     * Writes the <code>toString()</code> value of each item in a collection to
     * a <code>Writer</code> line by line, using the specified line ending.
     *
     * @param lines      the lines to write, null entries produce blank lines
     * @param lineEnding the line separator to use, null is system default
     * @param writer     the <code>Writer</code> to write to, not null, not closed
     * @throws NullPointerException if the input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void writeLines(Collection<?> lines, String lineEnding,
                                  Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineEnding == null) {
            lineEnding = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineEnding);
        }
    }

    // copy from InputStream
    //-----------------------------------------------------------------------

    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the <code>copyLarge(InputStream, OutputStream)</code> method.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p/>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input       the <code>InputStream</code> to read from
     * @param output      the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     *                    -ve values are ignored
     * @param length      : number of bytes to copy. -ve means all
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length)
            throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy some or all bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input bytes.
     * <p/>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     *
     * @param input       the <code>InputStream</code> to read from
     * @param output      the <code>OutputStream</code> to write to
     * @param inputOffset : number of bytes to skip from input before copying
     *                    -ve values are ignored
     * @param length      : number of bytes to copy. -ve means all
     * @param buffer      the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output,
                                 final long inputOffset, final long length, byte[] buffer) throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        final int bufferLength = buffer.length;
        int bytesToRead = bufferLength;
        if (length > 0 && length < bufferLength) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }
        return totalRead;
    }

    /**
     * Copy bytes from an <code>InputStream</code> to chars on a
     * <code>Writer</code> using the default character encoding of the platform.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * This method uses {@link java.io.InputStreamReader}.
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output the <code>Writer</code> to write to
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void copy(InputStream input, Writer output)
            throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    /**
     * Copy bytes from an <code>InputStream</code> to chars on a
     * <code>Writer</code> using the specified character encoding.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * This method uses {@link java.io.InputStreamReader}.
     *
     * @param input    the <code>InputStream</code> to read from
     * @param output   the <code>Writer</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void copy(InputStream input, Writer output, Charset encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(encoding));
        copy(in, output);
    }

    /**
     * Copy bytes from an <code>InputStream</code> to chars on a
     * <code>Writer</code> using the specified character encoding.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * This method uses {@link java.io.InputStreamReader}.
     *
     * @param input    the <code>InputStream</code> to read from
     * @param output   the <code>Writer</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if the input or output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }

    // copy from Reader
    //-----------------------------------------------------------------------

    /**
     * Copy chars from a <code>Reader</code> to a <code>Writer</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     * Large streams (over 2GB) will return a chars copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of chars cannot be returned as an int. For large streams
     * use the <code>copyLarge(Reader, Writer)</code> method.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @return the number of characters copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy chars from a large (over 2GB) <code>Reader</code> to a <code>Writer</code>.
     * <p/>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     *
     * @param input  the <code>Reader</code> to read from
     * @param output the <code>Writer</code> to write to
     * @param buffer the buffer to be used for the copy
     * @return the number of characters copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Copy some or all chars from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input chars.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     *
     * @param input       the <code>Reader</code> to read from
     * @param output      the <code>Writer</code> to write to
     * @param inputOffset : number of chars to skip from input before copying
     *                    -ve values are ignored
     * @param length      : number of chars to copy. -ve means all
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(Reader input, Writer output, final long inputOffset, final long length)
            throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy some or all chars from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>, optionally skipping input chars.
     * <p/>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     *
     * @param input       the <code>Reader</code> to read from
     * @param output      the <code>Writer</code> to write to
     * @param inputOffset : number of chars to skip from input before copying
     *                    -ve values are ignored
     * @param length      : number of chars to copy. -ve means all
     * @param buffer      the buffer to be used for the copy
     * @return the number of chars copied
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(Reader input, Writer output, final long inputOffset, final long length, char[] buffer)
            throws IOException {
        if (inputOffset > 0) {
            skipFully(input, inputOffset);
        }
        if (length == 0) {
            return 0;
        }
        int bytesToRead = buffer.length;
        if (length > 0 && length < buffer.length) {
            bytesToRead = (int) length;
        }
        int read;
        long totalRead = 0;
        while (bytesToRead > 0 && EOF != (read = input.read(buffer, 0, bytesToRead))) {
            output.write(buffer, 0, read);
            totalRead += read;
            if (length > 0) { // only adjust length if not reading to the end
                // Note the cast must work because buffer.length is an integer
                bytesToRead = (int) Math.min(length - totalRead, buffer.length);
            }
        }
        return totalRead;
    }

    /**
     * Copy chars from a <code>Reader</code> to bytes on an
     * <code>OutputStream</code> using the default character encoding of the
     * platform, and calling flush.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     * Due to the implementation of OutputStreamWriter, this method performs a
     * flush.
     * <p/>
     * This method uses {@link java.io.OutputStreamWriter}.
     *
     * @param input  the <code>Reader</code> to read from
     * @param output the <code>OutputStream</code> to write to
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static void copy(Reader input, OutputStream output)
            throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    /**
     * Copy chars from a <code>Reader</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding, and
     * calling flush.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * </p>
     * <p>
     * Due to the implementation of OutputStreamWriter, this method performs a
     * flush.
     * </p>
     * <p>
     * This method uses {@link java.io.OutputStreamWriter}.
     * </p>
     *
     * @param input    the <code>Reader</code> to read from
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException if the input or output is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.3
     */
    public static void copy(Reader input, OutputStream output, Charset encoding) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, Charsets.toCharset(encoding));
        copy(input, out);
        // XXX Unless anyone is planning on rewriting OutputStreamWriter,
        // we have to flush here.
        out.flush();
    }

    /**
     * Copy chars from a <code>Reader</code> to bytes on an
     * <code>OutputStream</code> using the specified character encoding, and
     * calling flush.
     * <p/>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedReader</code>.
     * <p/>
     * Character encoding names can be found at
     * <a href="http://www.iana.org/assignments/character-sets">IANA</a>.
     * <p/>
     * Due to the implementation of OutputStreamWriter, this method performs a
     * flush.
     * <p/>
     * This method uses {@link java.io.OutputStreamWriter}.
     *
     * @param input    the <code>Reader</code> to read from
     * @param output   the <code>OutputStream</code> to write to
     * @param encoding the encoding to use, null means platform default
     * @throws NullPointerException                         if the input or output is null
     * @throws java.io.IOException                          if an I/O error occurs
     * @throws java.nio.charset.UnsupportedCharsetException thrown instead of {@link java.io.UnsupportedEncodingException} in version 2.2 if the encoding is not
     *                                                      supported.
     * @since 1.1
     */
    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }

    // content equals
    //-----------------------------------------------------------------------

    /**
     * Compare the contents of two Streams to determine if they are equal or
     * not.
     * <p/>
     * This method buffers the input internally using
     * <code>BufferedInputStream</code> if they are not already buffered.
     *
     * @param input1 the first stream
     * @param input2 the second stream
     * @return true if the content of the streams are equal or they both don't
     * exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws java.io.IOException  if an I/O error occurs
     */
    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (EOF != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return ch2 == EOF;
    }

    /**
     * Compare the contents of two Readers to determine if they are equal or
     * not.
     * <p/>
     * This method buffers the input internally using
     * <code>BufferedReader</code> if they are not already buffered.
     *
     * @param input1 the first reader
     * @param input2 the second reader
     * @return true if the content of the readers are equal or they both don't
     * exist, false otherwise
     * @throws NullPointerException if either input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 1.1
     */
    public static boolean contentEquals(Reader input1, Reader input2)
            throws IOException {

        input1 = toBufferedReader(input1);
        input2 = toBufferedReader(input2);

        int ch = input1.read();
        while (EOF != ch) {
            int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        int ch2 = input2.read();
        return ch2 == EOF;
    }

    /**
     * Compare the contents of two Readers to determine if they are equal or
     * not, ignoring EOL characters.
     * <p/>
     * This method buffers the input internally using
     * <code>BufferedReader</code> if they are not already buffered.
     *
     * @param input1 the first reader
     * @param input2 the second reader
     * @return true if the content of the readers are equal (ignoring EOL differences),  false otherwise
     * @throws NullPointerException if either input is null
     * @throws java.io.IOException  if an I/O error occurs
     * @since 2.2
     */
    public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2)
            throws IOException {
        BufferedReader br1 = toBufferedReader(input1);
        BufferedReader br2 = toBufferedReader(input2);

        String line1 = br1.readLine();
        String line2 = br2.readLine();
        while (line1 != null && line2 != null && line1.equals(line2)) {
            line1 = br1.readLine();
            line2 = br2.readLine();
        }
        return line1 == null ? line2 == null ? true : false : line1.equals(line2);
    }

    /**
     * Skip bytes from an input byte stream.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.Reader}.
     *
     * @param input  byte stream to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see java.io.InputStream#skip(long)
     * @since 2.0
     */
    public static long skip(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_BYTE_BUFFER == null) {
            SKIP_BYTE_BUFFER = new byte[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_BYTE_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skip characters from an input character stream.
     * This implementation guarantees that it will read as many characters
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.Reader}.
     *
     * @param input  character stream to skip
     * @param toSkip number of characters to skip.
     * @return number of characters actually skipped.
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @see java.io.Reader#skip(long)
     * @since 2.0
     */
    public static long skip(Reader input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to synch. to ensure some other thread did not create a smaller one)
         */
        if (SKIP_CHAR_BUFFER == null) {
            SKIP_CHAR_BUFFER = new char[SKIP_BUFFER_SIZE];
        }
        long remain = toSkip;
        while (remain > 0) {
            long n = input.read(SKIP_CHAR_BUFFER, 0, (int) Math.min(remain, SKIP_BUFFER_SIZE));
            if (n < 0) { // EOF
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Skip the requested number of bytes or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.InputStream#skip(long)} may
     * not skip as many bytes as requested (most likely because of reaching EOF).
     *
     * @param input  stream to skip
     * @param toSkip the number of bytes to skip
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws java.io.EOFException     if the number of bytes skipped was incorrect
     * @see java.io.InputStream#skip(long)
     * @since 2.0
     */
    public static void skipFully(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Bytes to skip must not be negative: " + toSkip);
        }
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Bytes to skip: " + toSkip + " actual: " + skipped);
        }
    }

    /**
     * Skip the requested number of characters or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.Reader#skip(long)} may
     * not skip as many characters as requested (most likely because of reaching EOF).
     *
     * @param input  stream to skip
     * @param toSkip the number of characters to skip
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if toSkip is negative
     * @throws java.io.EOFException     if the number of characters skipped was incorrect
     * @see java.io.Reader#skip(long)
     * @since 2.0
     */
    public static void skipFully(Reader input, long toSkip) throws IOException {
        long skipped = skip(input, toSkip);
        if (skipped != toSkip) {
            throw new EOFException("Chars to skip: " + toSkip + " actual: " + skipped);
        }
    }


    /**
     * Read characters from an input character stream.
     * This implementation guarantees that it will read as many characters
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.Reader}.
     *
     * @param input  where to read input from
     * @param buffer destination
     * @param offset inital offset into buffer
     * @param length length to read, must be >= 0
     * @return actual length read; may be less than requested if EOF was reached
     * @throws java.io.IOException if a read error occurs
     * @since 2.2
     */
    public static int read(Reader input, char[] buffer, int offset, int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            int location = length - remaining;
            int count = input.read(buffer, offset + location, remaining);
            if (EOF == count) { // EOF
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }

    /**
     * Read characters from an input character stream.
     * This implementation guarantees that it will read as many characters
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.Reader}.
     *
     * @param input  where to read input from
     * @param buffer destination
     * @return actual length read; may be less than requested if EOF was reached
     * @throws java.io.IOException if a read error occurs
     * @since 2.2
     */
    public static int read(Reader input, char[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }

    /**
     * Read bytes from an input stream.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.InputStream}.
     *
     * @param input  where to read input from
     * @param buffer destination
     * @param offset inital offset into buffer
     * @param length length to read, must be >= 0
     * @return actual length read; may be less than requested if EOF was reached
     * @throws java.io.IOException if a read error occurs
     * @since 2.2
     */
    public static int read(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        if (length < 0) {
            throw new IllegalArgumentException("Length must not be negative: " + length);
        }
        int remaining = length;
        while (remaining > 0) {
            int location = length - remaining;
            int count = input.read(buffer, offset + location, remaining);
            if (EOF == count) { // EOF
                break;
            }
            remaining -= count;
        }
        return length - remaining;
    }

    /**
     * Read bytes from an input stream.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up; this may not always be the case for
     * subclasses of {@link java.io.InputStream}.
     *
     * @param input  where to read input from
     * @param buffer destination
     * @return actual length read; may be less than requested if EOF was reached
     * @throws java.io.IOException if a read error occurs
     * @since 2.2
     */
    public static int read(InputStream input, byte[] buffer) throws IOException {
        return read(input, buffer, 0, buffer.length);
    }

    /**
     * Read the requested number of characters or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.Reader#read(char[], int, int)} may
     * not read as many characters as requested (most likely because of reaching EOF).
     *
     * @param input  where to read input from
     * @param buffer destination
     * @param offset inital offset into buffer
     * @param length length to read, must be >= 0
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if length is negative
     * @throws java.io.EOFException     if the number of characters read was incorrect
     * @since 2.2
     */
    public static void readFully(Reader input, char[] buffer, int offset, int length) throws IOException {
        int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    /**
     * Read the requested number of characters or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.Reader#read(char[], int, int)} may
     * not read as many characters as requested (most likely because of reaching EOF).
     *
     * @param input  where to read input from
     * @param buffer destination
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if length is negative
     * @throws java.io.EOFException     if the number of characters read was incorrect
     * @since 2.2
     */
    public static void readFully(Reader input, char[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }

    /**
     * Read the requested number of bytes or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.InputStream#read(byte[], int, int)} may
     * not read as many bytes as requested (most likely because of reaching EOF).
     *
     * @param input  where to read input from
     * @param buffer destination
     * @param offset inital offset into buffer
     * @param length length to read, must be >= 0
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if length is negative
     * @throws java.io.EOFException     if the number of bytes read was incorrect
     * @since 2.2
     */
    public static void readFully(InputStream input, byte[] buffer, int offset, int length) throws IOException {
        int actual = read(input, buffer, offset, length);
        if (actual != length) {
            throw new EOFException("Length to read: " + length + " actual: " + actual);
        }
    }

    /**
     * Read the requested number of bytes or fail if there are not enough left.
     * <p/>
     * This allows for the possibility that {@link java.io.InputStream#read(byte[], int, int)} may
     * not read as many bytes as requested (most likely because of reaching EOF).
     *
     * @param input  where to read input from
     * @param buffer destination
     * @throws java.io.IOException      if there is a problem reading the file
     * @throws IllegalArgumentException if length is negative
     * @throws java.io.EOFException     if the number of bytes read was incorrect
     * @since 2.2
     */
    public static void readFully(InputStream input, byte[] buffer) throws IOException {
        readFully(input, buffer, 0, buffer.length);
    }

    /**
     * get an asset using ACCESS_STREAMING mode. This provides access to files that have been bundled with an
     * application as assets -- that is, files placed in to the "assets" directory.
     *
     * @param context
     * @param fileName The name of the asset to open. This name can be hierarchical.
     * @return
     */
    public static String readStringFromAssets(Context context, String fileName) {
        if (context == null || StringUtils.isEmpty(fileName)) {
            return null;
        }

        StringBuilder s = new StringBuilder("");
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get content from a raw resource. This can only be used with resources whose value is the name of an
     * asset files -- that is, it can be used to open drawable, sound, and raw resources; it will fail on string and
     * color resources.
     *
     * @param context
     * @param resId   The resource identifier to open, as generated by the appt tool.
     * @return
     */
    public static String readStringFromRaw(Context context, int resId) {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
            BufferedReader br = new BufferedReader(in);
            String line;
            while ((line = br.readLine()) != null) {
                s.append(line);
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    public static String readString(String filePath, Charset charset) {
        return readString(new File(filePath), charset.name());
    }

    public static String readString(String filePath, String charsetName) {
        return readString(new File(filePath), charsetName);
    }

    public static String readString(File file, Charset charset) {
        return readString(file, charset.name());
    }

    /**
     * read file
     *
     * @param filePath
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static String readString(File file, String charsetName) {
        if (file == null || !file.isFile()) {
            return null;
        }
        StringBuilder fileContent = new StringBuilder();

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeQuietly(reader);
        }
    }

    public static boolean writeString(String filePath, String content) {
        return writeString(filePath, content, false);
    }

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeString(String filePath, String content, boolean append) {
        return writeString(filePath != null ? new File(filePath) : null, content, append);
    }

    public static boolean writeString(File file, String content) {
        return writeString(file, content, false);
    }

    public static boolean writeString(File file, String content, boolean append) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeQuietly(fileWriter);
        }
    }


    /**
     * write file
     *
     * @param filePath
     * @param stream
     * @return
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeStream(String filePath, InputStream stream) {
        return writeStream(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeStream(String filePath, InputStream stream, boolean append) {
        return writeStream(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * write file
     *
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeStream(File file, InputStream stream) {
        return writeStream(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeStream(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeQuietly(o);
            closeQuietly(stream);
        }
    }

    public static boolean writeBytes(File file, byte[] data) {
        return writeBytes(file, data, false);
    }

    public static boolean writeBytes(File file, byte[] data, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            o.write(data);
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeQuietly(o);
        }
    }


    /**
     * read file to string list, a element of list is a line
     *
     * @param filePath
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            reader.close();
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * get file name from path, not include suffix
     * <p/>
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @param filePath
     * @return file name from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * get file name from path, include suffix
     * <p/>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @param filePath
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder name from path
     * <p/>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * get suffix of file from path
     * <p/>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * Creates the directory named by the trailing filename of this file, including the complete directory path required
     * to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target directory already exists, false one of
     * the directories can not be created.
     * <ul>
     * <li>if {@link FileUtils#getFolderName(String)} return null, return false</li>
     * <li>if target directory already exists, return true</li>
     * <li>return {@link java.io.File#makeFolder}</li>
     * </ul>
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (StringUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }


    /**
     * @param filePath
     * @return
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (StringUtils.isEmpty(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * get file size
     * <ul>
     * <li>if path is null or empty, return -1</li>
     * <li>if path exist and it is a file, return file size, else return -1</li>
     * <ul>
     *
     * @param path
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    public static long getFileSize(String path) {
        if (StringUtils.isEmpty(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }
}
