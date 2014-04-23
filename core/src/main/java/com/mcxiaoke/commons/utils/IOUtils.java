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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
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
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * General IO stream manipulation utilities.
 */
public final class IOUtils {

    private static final int EOF = -1;
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final char DIR_SEPARATOR = File.separatorChar;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
    private static final int SKIP_BUFFER_SIZE = 2048;

    // Allocated in the relevant skip method if necessary.
    /*
     * N.B. no need to synchronize these because:
     * - we don't care if the buffer is created multiple times (the data is ignored)
     * - we always use the same size buffer, so if it it is recreated it will still be OK
     * (if the buffer size were variable, we would need to sync. to ensure some other thread
     * did not create a smaller one)
     */
    private static char[] SKIP_CHAR_BUFFER;
    private static byte[] SKIP_BYTE_BUFFER;

    /**
     * Instances should NOT be constructed in standard programming.
     */
    private IOUtils() {
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
        } catch (IOException ignored) {
            // ignore
        }
    }

    public static void closeQuietly(Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    public static void closeQuietly(Selector selector) {
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    public static void closeQuietly(ServerSocket sock) {
        if (sock != null) {
            try {
                sock.close();
            } catch (IOException ignored) {
                // ignored
            }
        }
    }

    // read readBytes
    //-----------------------------------------------------------------------

    public static byte[] readBytes(File file) throws IOException {
        return readBytes(new FileInputStream(file));
    }

    public static byte[] readBytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static byte[] readBytes(InputStream input, int size) throws IOException {

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

    public static byte[] readBytes(Reader input) throws IOException {
        return readBytes(input, Charset.defaultCharset());
    }

    public static byte[] readBytes(Reader input, Charset encoding) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output, encoding);
        return output.toByteArray();
    }

    public static byte[] readBytes(Reader input, String encoding) throws IOException {
        return readBytes(input, Charsets.toCharset(encoding));
    }

    public static byte[] readBytes(URI uri) throws IOException {
        return IOUtils.readBytes(uri.toURL());
    }

    public static byte[] readBytes(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        try {
            return readBytes(conn);
        } finally {
            close(conn);
        }
    }

    public static byte[] readBytes(URLConnection urlConn) throws IOException {
        InputStream inputStream = urlConn.getInputStream();
        try {
            return IOUtils.readBytes(inputStream);
        } finally {
            inputStream.close();
        }
    }

    // read char[]
    //-----------------------------------------------------------------------

    public static char[] readChars(InputStream is) throws IOException {
        return readChars(is, Charset.defaultCharset());
    }

    public static char[] readChars(InputStream is, Charset encoding)
            throws IOException {
        CharArrayWriter output = new CharArrayWriter();
        copy(is, output, encoding);
        return output.toCharArray();
    }

    public static char[] readChars(InputStream is, String encoding) throws IOException {
        return readChars(is, Charsets.toCharset(encoding));
    }

    public static char[] readChars(Reader input) throws IOException {
        CharArrayWriter sw = new CharArrayWriter();
        copy(input, sw);
        return sw.toCharArray();
    }

    // read readString
    //-----------------------------------------------------------------------

    public static String readString(InputStream input) throws IOException {
        return readString(input, Charset.defaultCharset());
    }

    public static String readString(InputStream input, Charset encoding) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }

    public static String readString(InputStream input, String encoding)
            throws IOException {
        return readString(input, Charsets.toCharset(encoding));
    }

    public static String readString(Reader input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static String readString(URI uri) throws IOException {
        return readString(uri, Charset.defaultCharset());
    }

    public static String readString(URI uri, Charset encoding) throws IOException {
        return readString(uri.toURL(), Charsets.toCharset(encoding));
    }

    public static String readString(URI uri, String encoding) throws IOException {
        return readString(uri, Charsets.toCharset(encoding));
    }

    public static String readString(URL url) throws IOException {
        return readString(url, Charset.defaultCharset());
    }

    public static String readString(URL url, Charset encoding) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            return readString(inputStream, encoding);
        } finally {
            inputStream.close();
        }
    }

    public static String readString(URL url, String encoding) throws IOException {
        return readString(url, Charsets.toCharset(encoding));
    }

    public static String readString(byte[] input, String encoding) throws IOException {
        return new String(input, Charsets.toCharset(encoding));
    }

    // readStringList
    //-----------------------------------------------------------------------

    public static List<String> readStringList(Reader input) throws IOException {
        BufferedReader reader = new BufferedReader(input);
        List<String> list = new ArrayList<String>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    public static List<String> readStringList(InputStream input) throws IOException {
        return readStringList(input, Charset.defaultCharset());
    }

    public static List<String> readStringList(InputStream input, Charset encoding) throws IOException {
        InputStreamReader reader = new InputStreamReader(input, Charsets.toCharset(encoding));
        return readStringList(reader);
    }

    public static List<String> readStringList(InputStream input, String encoding) throws IOException {
        return readStringList(input, Charsets.toCharset(encoding));
    }

    public static List<String> readStringList(String filePath, String encoding) throws IOException {
        return readStringList(filePath, Charsets.toCharset(encoding));
    }

    public static List<String> readStringList(String filePath, Charset charset) throws IOException {
        FileInputStream stream = new FileInputStream(filePath);
        return readStringList(stream, charset);
    }

    public static List<String> readStringList(File file, String encoding) throws IOException {
        return readStringList(file, Charsets.toCharset(encoding));
    }

    public static List<String> readStringList(File file, Charset charset) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        return readStringList(stream, charset);
    }

    // toInputStream
    //-----------------------------------------------------------------------

    public static InputStream toInputStream(CharSequence input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    public static InputStream toInputStream(CharSequence input, Charset encoding) {
        return toInputStream(input.toString(), encoding);
    }

    public static InputStream toInputStream(CharSequence input, String encoding) throws IOException {
        return toInputStream(input, Charsets.toCharset(encoding));
    }

    public static InputStream toInputStream(String input) {
        return toInputStream(input, Charset.defaultCharset());
    }

    public static InputStream toInputStream(String input, Charset encoding) {
        return new ByteArrayInputStream(input.getBytes(Charsets.toCharset(encoding)));
    }

    public static InputStream toInputStream(String input, String encoding) throws IOException {
        byte[] bytes = input.getBytes(Charsets.toCharset(encoding));
        return new ByteArrayInputStream(bytes);
    }

    // write byte[]
    //-----------------------------------------------------------------------

    public static void writeBytes(byte[] data, OutputStream output)
            throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void writeBytes(byte[] data, Writer output) throws IOException {
        writeBytes(data, output, Charset.defaultCharset());
    }

    public static void writeBytes(byte[] data, Writer output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data, Charsets.toCharset(encoding)));
        }
    }

    public static void writeBytes(byte[] data, Writer output, String encoding) throws IOException {
        writeBytes(data, output, Charsets.toCharset(encoding));
    }

    // write char[]
    //-----------------------------------------------------------------------

    public static void writeChars(char[] data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void writeChars(char[] data, OutputStream output)
            throws IOException {
        writeChars(data, output, Charset.defaultCharset());
    }

    public static void writeChars(char[] data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(new String(data).getBytes(Charsets.toCharset(encoding)));
        }
    }

    public static void writeChars(char[] data, OutputStream output, String encoding)
            throws IOException {
        writeChars(data, output, Charsets.toCharset(encoding));
    }

    // write CharSequence
    //-----------------------------------------------------------------------

    public static void writeCharSequence(CharSequence data, Writer output) throws IOException {
        if (data != null) {
            writeString(data.toString(), output);
        }
    }

    public static void writeCharSequence(CharSequence data, OutputStream output)
            throws IOException {
        writeCharSequence(data, output, Charset.defaultCharset());
    }

    public static void writeCharSequence(CharSequence data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            writeString(data.toString(), output, encoding);
        }
    }

    public static void writeCharSequence(CharSequence data, OutputStream output, String encoding) throws IOException {
        writeCharSequence(data, output, Charsets.toCharset(encoding));
    }

    // write String
    //-----------------------------------------------------------------------

    public static void writeString(String data, Writer output) throws IOException {
        if (data != null) {
            output.write(data);
        }
    }

    public static void writeString(String data, OutputStream output)
            throws IOException {
        writeString(data, output, Charset.defaultCharset());
    }

    public static void writeString(String data, OutputStream output, Charset encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }

    public static void writeString(String data, OutputStream output, String encoding)
            throws IOException {
        writeString(data, output, Charsets.toCharset(encoding));
    }

    // writeList
    //-----------------------------------------------------------------------

    public static void writeList(Collection<?> lines,
                                 String filePath) throws IOException {
        writeList(lines, filePath, Charset.defaultCharset());
    }

    public static void writeList(Collection<?> lines,
                                 File file) throws IOException {
        writeList(lines, file, Charset.defaultCharset());
    }

    public static void writeList(Collection<?> lines,
                                 String filePath, String encoding) throws IOException {
        writeList(lines, filePath, Charsets.toCharset(encoding));
    }

    public static void writeList(Collection<?> lines,
                                 File file, String encoding) throws IOException {
        writeList(lines, file, Charsets.toCharset(encoding));
    }

    public static void writeList(Collection<?> lines,
                                 String filePath, Charset charset) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        writeList(lines, fos, charset);
    }

    public static void writeList(Collection<?> lines,
                                 File file, Charset charset) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        writeList(lines, fos, charset);
    }

    public static void writeList(Collection<?> lines,
                                 OutputStream output) throws IOException {
        writeList(lines, output, Charset.defaultCharset());
    }

    public static void writeList(Collection<?> lines, OutputStream output, String encoding)
            throws IOException {
        writeList(lines, output, Charsets.toCharset(encoding));
    }

    public static void writeList(Collection<?> lines, OutputStream output, Charset encoding)
            throws IOException {
        writeList(lines, output, encoding, LINE_SEPARATOR);
    }

    public static void writeList(Collection<?> lines,
                                 OutputStream output, String encoding, String lineSeparator) throws IOException {
        writeList(lines, output, Charsets.toCharset(encoding), lineSeparator);
    }

    public static void writeList(Collection<?> lines, OutputStream output, Charset encoding, String lineSeparator)
            throws IOException {
        if (lines == null) {
            return;
        }
        if (lineSeparator == null) {
            lineSeparator = LINE_SEPARATOR;
        }
        Charset cs = Charsets.toCharset(encoding);
        for (Object line : lines) {
            if (line != null) {
                output.write(line.toString().getBytes(cs));
            }
            output.write(lineSeparator.getBytes(cs));
        }
    }

    public static void writeList(Collection<?> lines,
                                 Writer writer) throws IOException {
        writeList(lines, LINE_SEPARATOR, writer);
    }

    public static void writeList(Collection<?> lines, String lineSeparator,
                                 Writer writer) throws IOException {
        if (lines == null) {
            return;
        }
        if (lineSeparator == null) {
            lineSeparator = LINE_SEPARATOR;
        }
        for (Object line : lines) {
            if (line != null) {
                writer.write(line.toString());
            }
            writer.write(lineSeparator);
        }
    }

    // copy from File

    public static void copyLegacy(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static void copy(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    // copy from InputStream
    //-----------------------------------------------------------------------

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

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

    public static long copyLarge(InputStream input, OutputStream output, long inputOffset, long length)
            throws IOException {
        return copyLarge(input, output, inputOffset, length, new byte[DEFAULT_BUFFER_SIZE]);
    }

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

    public static void copy(InputStream input, Writer output)
            throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    public static void copy(InputStream input, Writer output, Charset encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(encoding));
        copy(in, output);
    }

    public static void copy(InputStream input, Writer output, String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }

    // copy from Reader
    //-----------------------------------------------------------------------

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
    }

    public static long copyLarge(Reader input, Writer output, char[] buffer) throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static long copyLarge(Reader input, Writer output, final long inputOffset, final long length)
            throws IOException {
        return copyLarge(input, output, inputOffset, length, new char[DEFAULT_BUFFER_SIZE]);
    }

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

    public static void copy(Reader input, OutputStream output)
            throws IOException {
        copy(input, output, Charset.defaultCharset());
    }

    public static void copy(Reader input, OutputStream output, Charset encoding) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(output, Charsets.toCharset(encoding));
        copy(input, out);
        // XXX Unless anyone is planning on rewriting OutputStreamWriter,
        // we have to flush here.
        out.flush();
    }

    public static void copy(Reader input, OutputStream output, String encoding) throws IOException {
        copy(input, output, Charsets.toCharset(encoding));
    }

    // content equals
    //-----------------------------------------------------------------------

    public static long skip(InputStream input, long toSkip) throws IOException {
        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        /*
         * N.B. no need to synchronize this because: - we don't care if the buffer is created multiple times (the data
         * is ignored) - we always use the same size buffer, so if it it is recreated it will still be OK (if the buffer
         * size were variable, we would need to sync. to ensure some other thread did not create a smaller one)
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
    public static String readStringFromAssets(Context context, String fileName) throws IOException {
        if (context == null || StringUtils.isEmpty(fileName)) {
            return null;
        }

        StringBuilder s = new StringBuilder("");
        InputStreamReader in = new InputStreamReader(context.getResources().getAssets().open(fileName));
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            s.append(line);
        }
        return s.toString();
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
    public static String readStringFromRaw(Context context, int resId) throws IOException {
        if (context == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(resId));
        BufferedReader br = new BufferedReader(in);
        String line;
        while ((line = br.readLine()) != null) {
            s.append(line);
        }
        return s.toString();
    }

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    public static String readString(String filePath, Charset charset) throws IOException {
        return readString(new File(filePath), charset);
    }

    public static String readString(String filePath, String charsetName) throws IOException {
        return readString(new File(filePath), Charsets.toCharset(charsetName));
    }

    public static String readString(File file, String charsetName) throws IOException {
        return readString(file, Charsets.toCharset(charsetName));
    }

    /**
     * read file
     *
     * @param file        file
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws IOException if an error occurs while operator BufferedReader
     */
    public static String readString(File file, Charset charset) throws IOException {
        if (file == null || !file.isFile()) {
            return null;
        }
        StringBuilder fileContent = new StringBuilder();

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charset);
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
        } finally {
            closeQuietly(reader);
        }
    }

    public static boolean writeString(String filePath, String content) throws IOException {
        return writeString(filePath, content, false);
    }

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return true
     * @throws IOException if an error occurs while operator FileWriter
     */
    public static boolean writeString(String filePath, String content, boolean append) throws IOException {
        return writeString(filePath != null ? new File(filePath) : null, content, append);
    }

    public static boolean writeString(File file, String content) throws IOException {
        return writeString(file, content, false);
    }

    public static boolean writeString(File file, String content, boolean append) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, append);
            fileWriter.write(content);
            fileWriter.close();
            return true;
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
    public static boolean writeStream(String filePath, InputStream stream) throws IOException {
        return writeStream(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws IOException if an error occurs while operator FileOutputStream
     */
    public static boolean writeStream(String filePath, InputStream stream, boolean append) throws IOException {
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
    public static boolean writeStream(File file, InputStream stream) throws IOException {
        return writeStream(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws IOException if an error occurs while operator FileOutputStream
     */
    public static boolean writeStream(File file, InputStream stream, boolean append) throws IOException {
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
        } finally {
            closeQuietly(o);
            closeQuietly(stream);
        }
    }

    public static boolean writeBytes(String filePath, byte[] data) throws IOException {
        return writeBytes(filePath, data, false);
    }

    public static boolean writeBytes(String filePath, byte[] data, boolean append) throws IOException {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return writeBytes(file, data, false);
    }

    public static boolean writeBytes(File file, byte[] data) throws IOException {
        return writeBytes(file, data, false);
    }

    public static boolean writeBytes(File file, byte[] data, boolean append) throws IOException {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            o.write(data);
            o.flush();
            return true;
        } finally {
            closeQuietly(o);
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
    public static boolean delete(String path) {
        if (StringUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        return delete(file);
    }

    public static boolean delete(File file) {
        if (file == null) {
            return true;
        }
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
                delete(f);
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

    private static final String RESERVED_CHARS = "|\\?*<\":>+[]/'";

}
