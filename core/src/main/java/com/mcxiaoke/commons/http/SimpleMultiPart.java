/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

/*
    This code is taken from Rafael Sanches' blog.
    http://blog.rafaelsanches.com/2011/01/29/upload-using-multipart-post-using-httpclient-in-android/
*/

package com.mcxiaoke.commons.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
class SimpleMultiPart {
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private String boundary = null;

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    boolean isSetLast = false;
    boolean isSetFirst = false;

    public SimpleMultiPart() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.boundary = buf.toString();

    }

    public void writeFirstBoundaryIfNeeds() {
        if (!isSetFirst) {
            writeBoundary();
        }

        isSetFirst = true;
    }

    private void writeBoundary() {
        try {
            out.write(("--" + boundary + "\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }

        try {
            out.write(("--" + boundary + "--\r\n").getBytes());
            out.flush();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        isSetLast = true;
    }


    public void addPart(final String key, final String value) {
        addPart(key, value, "text/plain; charset=UTF-8");
    }

    public void addPart(final String key, final String value, final String contentType) {
        writeBoundary();
        try {
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n").getBytes());
            out.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes());
            out.write(value.getBytes());
            out.write(("\r\n").getBytes());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }


    public void addPart(final String key, final File value, final String contentType) {
        try {
            addPart(key, value.getName(), new FileInputStream(value), contentType);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addPart(final String key, final String fileName, final InputStream fin, String contentType) {
        writeBoundary();
        try {
            contentType = "Content-Type: " + contentType + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n").getBytes());
            out.write(contentType.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4096];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.write(("\r\n").getBytes());

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    public String getContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        writeLastBoundaryIfNeeds();
        outstream.write(out.toByteArray());
    }

    public InputStream getContent() throws IOException {
        writeLastBoundaryIfNeeds();
        return new ByteArrayInputStream(out.toByteArray());
    }
}