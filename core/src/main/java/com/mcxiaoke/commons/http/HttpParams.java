package com.mcxiaoke.commons.http;

import com.mcxiaoke.commons.Charsets;
import com.mcxiaoke.commons.http.entity.ContentType;
import com.mcxiaoke.commons.http.entity.mime.HttpMultipartMode;
import com.mcxiaoke.commons.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
class HttpParams implements HttpConsts {

    public static final String DEFAULT_NAME = "nofilename";

    private List<NameValuePair> params;
    private List<StreamPart> parts;

    public HttpParams() {
        params = new ArrayList<NameValuePair>();
        parts = new ArrayList<StreamPart>();
    }

    public HttpParams(String key, String value) {
        this();
        put(key, value);
    }

    public HttpParams(Map<String, String> map) {
        this();
        put(map);
    }

    public HttpParams put(String key, File file, String contentType) {
        StreamPart part = StreamPart.create(key, file, contentType);
        return put(part);
    }

    public HttpParams put(String key, File file, String contentType, String fileName) {
        StreamPart part = StreamPart.create(key, file, contentType, fileName);
        return put(part);
    }

    public HttpParams put(String key, byte[] bytes, String contentType) {
        StreamPart part = StreamPart.create(key, bytes, contentType);
        return put(part);
    }

    public HttpParams put(String key, byte[] bytes, String contentType, String fileName) {
        StreamPart part = StreamPart.create(key, bytes, contentType, fileName);
        return put(part);
    }

    public HttpParams put(String key, InputStream stream, String contentType) {
        StreamPart part = StreamPart.create(key, stream, contentType);
        return put(part);
    }

    public HttpParams put(String key, InputStream stream, String contentType, String fileName) {
        StreamPart part = new StreamPart(key, stream, contentType, fileName);
        return put(part);
    }

    public HttpParams put(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
        return this;
    }

    public HttpParams put(Map<String, String> map) {
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

    private HttpParams put(final StreamPart part) {
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
                builder.addBinaryBody(part.getName(), part.getStream(), part.getContentType(), part.getFileName());
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
        private InputStream stream;
        private String fileName;

        public static StreamPart create(String name, File file, String mimeType) {
            return create(name, file, mimeType, file.getName());
        }

        public static StreamPart create(String name, File file, String mimeType, String fileName) {
            try {
                return new StreamPart(name, new FileInputStream(file), mimeType, fileName);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public static StreamPart create(String name, byte[] bytes, String mimeType) {
            return create(name, bytes, mimeType, DEFAULT_NAME);
        }

        public static StreamPart create(String name, byte[] bytes, String mimeType, String fileName) {
            return create(name, new ByteArrayInputStream(bytes), mimeType, fileName);
        }


        public static StreamPart create(String name, InputStream stream, String mimeType) {
            return create(name, stream, mimeType, DEFAULT_NAME);
        }

        public static StreamPart create(String name, InputStream stream, String mimeType, String fileName) {
            return new StreamPart(name, stream, mimeType, fileName);
        }

        private StreamPart(String name, InputStream stream, String mimeType, String fileName) {
            this.name = name;
            this.stream = stream;
            this.contentType = ContentType.create(mimeType);
            this.fileName = fileName;
        }


        public String getName() {
            return name;
        }

        public ContentType getContentType() {
            return contentType;
        }

        public InputStream getStream() {
            return stream;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
