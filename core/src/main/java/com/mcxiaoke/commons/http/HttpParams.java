package com.mcxiaoke.commons.http;

import com.mcxiaoke.commons.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
class HttpParams implements HttpConsts {

    private Map<String, String> params;
    private Map<String, StreamPart> streams;

    public HttpParams() {
        params = new HashMap<String, String>();
        streams = new HashMap<String, StreamPart>();
    }

    public HttpParams(String key, String value) {
        this();
        put(key, value);
    }

    public HttpParams(Map<String, String> map) {
        this();
        put(map);
    }

    public HttpParams put(String key, File file, String contentType) throws FileNotFoundException {
        StreamPart part = new StreamPart(file, contentType);
        return put(key, part);
    }

    public HttpParams put(String key, byte[] bytes, String contentType) {
        StreamPart part = new StreamPart(bytes, contentType);
        return put(key, part);
    }

    public HttpParams put(String key, byte[] bytes, String contentType, String fileName) {
        StreamPart part = new StreamPart(bytes, contentType, fileName);
        return put(key, part);
    }

    public HttpParams put(String key, InputStream stream, String contentType) {
        StreamPart part = new StreamPart(stream, contentType);
        return put(key, part);
    }

    public HttpParams put(String key, InputStream stream, String contentType, String fileName) {
        StreamPart part = new StreamPart(stream, contentType, fileName);
        return put(key, part);
    }

    public HttpParams put(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public HttpParams put(Map<String, String> map) {
        this.params.putAll(map);
        return this;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public boolean hasStream() {
        return this.streams.size() > 0;
    }

    public String getEncodedString() {
        return Encoder.encode(params);
    }

    public String appendQueryString(String url) {
        return Encoder.appendQueryString(url, params);
    }


    private HttpParams put(String key, final StreamPart part) {
        this.streams.put(key, part);
        return this;
    }

    private void writeMultiPartBody(HttpURLConnection conn) throws IOException {
        if (streams.isEmpty()) {
            return;
        }
        // 首先必须写入CONTENT_TYPE
        SimpleMultiPart multiPart = new SimpleMultiPart();
        for (Map.Entry<String, StreamPart> entry : streams.entrySet()) {
            String key = entry.getKey();
            StreamPart part = entry.getValue();
            multiPart.addPart(key, part.fileName, part.inputStream, part.contentType);
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            multiPart.addPart(entry.getKey(), entry.getValue());
        }
        conn.setRequestProperty(CONTENT_TYPE, multiPart.getContentType());
        conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(multiPart.getContentLength()));
        multiPart.writeTo(conn.getOutputStream());
    }

    private void writeFormEncodedBody(HttpURLConnection conn) throws IOException {
        if (params.isEmpty()) {
            return;
        }
        String body = getEncodedString();
        byte[] data = body.getBytes(Encoder.ENCODING_UTF8);
        int contentLength = data.length;
        // 首先必须写入CONTENT_TYPE
        conn.setRequestProperty(CONTENT_TYPE, DEFAULT_CONTENT_TYPE);
        conn.setRequestProperty(CONTENT_LENGTH, String.valueOf(contentLength));
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            bos.write(data);
            bos.writeTo(conn.getOutputStream());
            bos.flush();
        } finally {
            forceClose(bos);
        }
    }

    void writeTo(HttpURLConnection conn) throws IOException {
        if (hasStream()) {
            writeMultiPartBody(conn);
        } else {
            writeFormEncodedBody(conn);
        }
    }

    private static void forceClose(Closeable close) {
        if (close != null) {
            try {
                close.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpParams{");
        sb.append("params=").append(StringUtils.getPrintString(params));
        sb.append(", streams=").append(StringUtils.getPrintString(streams));
        sb.append('}');
        return sb.toString();
    }

    static class StreamPart {

        public static final String FILENAME_DEFAULT = "nofilename";
        public String fileName;
        public String contentType;
        public InputStream inputStream;

        public StreamPart(File file, String contentType) throws FileNotFoundException {
            this(new FileInputStream(file), contentType, file.getName());
        }

        public StreamPart(byte[] bytes, String contentType) {
            this(new ByteArrayInputStream(bytes), contentType, FILENAME_DEFAULT);
        }

        public StreamPart(byte[] bytes, String fileName, String contentType) {
            this(new ByteArrayInputStream(bytes), contentType, fileName);
        }

        public StreamPart(InputStream stream, String contentType) {
            this(stream, contentType, FILENAME_DEFAULT);
        }

        public StreamPart(InputStream stream, String contentType, String fileName) {
            initialize(stream, contentType, fileName);
        }

        private void initialize(InputStream stream, String contentType, String fileName) {
            this.inputStream = stream;
            this.contentType = contentType;
            this.fileName = fileName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StreamPart{");
            sb.append("fileName='").append(fileName).append('\'');
            sb.append(", contentType='").append(contentType).append('\'');
            sb.append(", inputStream=").append(inputStream);
            sb.append('}');
            return sb.toString();
        }
    }
}
