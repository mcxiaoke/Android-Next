package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.http.entity.ContentType;
import com.mcxiaoke.next.http.entity.mime.HttpMultipartMode;
import com.mcxiaoke.next.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
final class NextParams implements Consts {

    public static final String DEFAULT_NAME = "nofilename";

    private List<NameValuePair> params;
    private List<StreamPart> parts;

    public NextParams() {
        params = new ArrayList<NameValuePair>();
        parts = new ArrayList<StreamPart>();
    }

    public NextParams(String key, String value) {
        this();
        put(key, value);
    }

    public NextParams(Map<String, String> map) {
        this();
        put(map);
    }

    public NextParams put(String key, File file, String contentType) {
        StreamPart part = StreamPart.create(key, file, contentType);
        return put(part);
    }

    public NextParams put(String key, File file, String contentType, String fileName) {
        StreamPart part = StreamPart.create(key, file, contentType, fileName);
        return put(part);
    }

    public NextParams put(String key, byte[] bytes, String contentType) {
        StreamPart part = StreamPart.create(key, bytes, contentType);
        return put(part);
    }

    public NextParams put(String key, byte[] bytes, String contentType, String fileName) {
        StreamPart part = StreamPart.create(key, bytes, contentType, fileName);
        return put(part);
    }

    public NextParams put(String key, InputStream stream, String contentType) {
        StreamPart part = StreamPart.create(key, stream, contentType);
        return put(part);
    }

    public NextParams put(String key, InputStream stream, String contentType, String fileName) {
        StreamPart part = new StreamPart(key, stream, contentType, fileName);
        return put(part);
    }

    public NextParams put(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
        return this;
    }

    public NextParams put(Map<String, String> map) {
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public List<NameValuePair> getParams() {
        return this.params;
    }

    public String appendQueryString(String url) {
        return Encoder.appendQueryString(url, params);
    }

    private NextParams put(final StreamPart part) {
        this.parts.add(part);
        return this;
    }

    public HttpEntity getHttpEntity() {
        HttpEntity entity = null;
        if (hasParts()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(Charsets.UTF_8);
            for (StreamPart part : parts) {
                final File file = part.getFile();
                if (file != null) {
                    builder.addBinaryBody(part.getName(), part.getFile(), part.getContentType(), part.getFileName());
                    continue;
                }
                final byte[] bytes = part.getBytes();
                if (bytes != null) {
                    builder.addBinaryBody(part.getName(), part.getBytes(), part.getContentType(), part.getFileName());
                    continue;
                }
                final InputStream stream = part.getStream();
                if (stream != null) {
                    builder.addBinaryBody(part.getName(), part.getStream(), part.getContentType(), part.getFileName());
                }
            }
            for (NameValuePair param : params) {
                builder.addTextBody(param.getName(), param.getValue());
            }
            entity = builder.build();
        } else if (hasParams()) {
            try {
                entity = new UrlEncodedFormEntity(params, Charsets.ENCODING_UTF_8);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    private boolean hasParts() {
        return parts.size() > 0;
    }

    private boolean hasParams() {
        return params.size() > 0;
    }

    static class StreamPart {

        private String name;
        private ContentType contentType;
        private File file;
        private byte[] bytes;
        private InputStream stream;
        private String fileName;
        private long length;

        private StreamPart(String name, File file, String mimeType, String fileName) {
            this.name = name;
            this.file = file;
            this.length = file.length();
            this.contentType = ContentType.create(mimeType);
            this.fileName = fileName;
        }

        private StreamPart(String name, byte[] bytes, String mimeType, String fileName) {
            this.name = name;
            this.bytes = bytes;
            this.length = bytes.length;
            this.contentType = ContentType.create(mimeType);
            this.fileName = fileName;
        }

        private StreamPart(String name, InputStream stream, String mimeType, String fileName) {
            this.name = name;
            this.stream = stream;
            this.length = -1;
            this.contentType = ContentType.create(mimeType);
            this.fileName = fileName;
        }

        public static StreamPart create(String name, File file, String mimeType) {
            return create(name, file, mimeType, file.getName());
        }

        public static StreamPart create(String name, File file, String mimeType, String fileName) {
            return new StreamPart(name, file, mimeType, fileName);
        }

        public static StreamPart create(String name, byte[] bytes, String mimeType) {
            return create(name, bytes, mimeType, DEFAULT_NAME);
        }

        public static StreamPart create(String name, byte[] bytes, String mimeType, String fileName) {
            return new StreamPart(name, bytes, mimeType, fileName);
        }

        public static StreamPart create(String name, InputStream stream, String mimeType) {
            return create(name, stream, mimeType, DEFAULT_NAME);
        }

        public static StreamPart create(String name, InputStream stream, String mimeType, String fileName) {
            return new StreamPart(name, stream, mimeType, fileName);
        }

        public String getName() {
            return name;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public File getFile() {
            return file;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public InputStream getStream() {
            return stream;
        }

        public String getFileName() {
            return fileName;
        }

        public long getLength() {
            return length;
        }
    }
}
