package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.annotation.NotThreadSafe;
import com.mcxiaoke.next.http.entity.ContentType;
import com.mcxiaoke.next.http.entity.mime.HttpMultipartMode;
import com.mcxiaoke.next.http.entity.mime.MultipartEntityBuilder;
import com.mcxiaoke.next.utils.MimeUtils;
import com.mcxiaoke.next.utils.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
@NotThreadSafe
public final class NextParams implements HttpConsts {


    private String encoding;
    // URL QUERY PARAM
    private List<NameValuePair> queries;
    // COMMON PARAM
    private List<NameValuePair> params;
    // BODY PARAM
    private List<StreamPart> parts;
    // RAW BODY
    private byte[] mBody;
    // HTTP ENTITY
    private HttpEntity mEntity;

    public NextParams() {
        this(Charsets.ENCODING_UTF_8);
    }

    public NextParams(String enc) {
        encoding = enc;
        queries = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        parts = new ArrayList<StreamPart>();
    }

    private void invalidateEntity() {
        mEntity = null;
    }

    public void setEncoding(final String enc) {
        encoding = enc;
        invalidateEntity();
    }

    public String getEncoding() {
        return encoding;
    }

    public NextParams removeParam(String key) {
        this.params.remove(key);
        return this;
    }

    public NextParams removeQuery(String key) {
        this.queries.remove(key);
        return this;
    }

    public NextParams body(final byte[] body) {
        this.mBody = body;
        invalidateEntity();
        return this;
    }

    public NextParams body(final String body, final Charset charset) {
        return body(body.getBytes(charset));
    }

    public NextParams body(final String body) {
        return body(body, CHARSET_UTF8);
    }

    public NextParams query(String key, String value) {
        this.queries.add(new BasicNameValuePair(key, value));
        invalidateEntity();
        return this;
    }

    public NextParams queries(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            query(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public NextParams put(String key, String value) {
        this.params.add(new BasicNameValuePair(key, value));
        invalidateEntity();
        return this;
    }

    public NextParams putAll(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public NextParams put(String key, File file) {
        StreamPart part = StreamPart.create(key, file);
        return addPart(part);
    }

    public NextParams put(String key, File file, String contentType) {
        StreamPart part = StreamPart.create(key, file, contentType);
        return addPart(part);
    }

    public NextParams put(String key, File file, String contentType, String fileName) {
        StreamPart part = StreamPart.create(key, file, contentType, fileName);
        return addPart(part);
    }

    public NextParams put(String key, byte[] bytes) {
        StreamPart part = StreamPart.create(key, bytes);
        return addPart(part);
    }

    public NextParams put(String key, byte[] bytes, String contentType) {
        StreamPart part = StreamPart.create(key, bytes, contentType);
        return addPart(part);
    }

    public NextParams put(String key, InputStream stream) {
        StreamPart part = StreamPart.create(key, stream);
        return addPart(part);
    }

    public NextParams put(String key, InputStream stream, String contentType) {
        StreamPart part = StreamPart.create(key, stream, contentType);
        return addPart(part);
    }

    public NextParams putAll(NextParams p) {
        this.queries.addAll(p.getQueries());
        this.params.addAll(p.getParams());
        this.parts.addAll(p.getParts());
        invalidateEntity();
        return this;
    }

    void clearQueries() {
        this.queries.clear();
        invalidateEntity();
    }

    void clearParams() {
        this.params.clear();
        invalidateEntity();
    }

    void clearParts() {
        this.parts.clear();
        invalidateEntity();
    }

    void clear() {
        clearQueries();
        clearParams();
        clearParts();
    }

    List<NameValuePair> getQueries() {
        return this.queries;
    }

    List<NameValuePair> getParams() {
        return this.params;
    }

    List<StreamPart> getParts() {
        return parts;
    }

    private NextParams addPart(final StreamPart part) {
        this.parts.add(part);
        invalidateEntity();
        return this;
    }

    private HttpEntity createHttpEntity() {
        HttpEntity entity;
        // first check raw bytes body
        if (mBody != null && mBody.length > 0) {
            entity = new ByteArrayEntity(mBody);
        }
        // then check multipart body
        else if (hasParts()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.setCharset(Charset.forName(encoding));
            for (StreamPart part : parts) {
                final File file = part.getFile();
                if (file != null) {
                    builder.addBinaryBody(part.getName(), part.getFile(),
                            part.getContentType(), part.getFileName());
                    continue;
                }
                final byte[] bytes = part.getBytes();
                if (bytes != null) {
                    builder.addBinaryBody(part.getName(), part.getBytes(),
                            part.getContentType(), part.getFileName());
                    continue;
                }
                final InputStream stream = part.getStream();
                if (stream != null) {
                    builder.addBinaryBody(part.getName(), part.getStream(),
                            part.getContentType(), part.getFileName());
                }
            }
            for (NameValuePair param : params) {
                builder.addTextBody(param.getName(), param.getValue());
            }
            entity = builder.build();
        }
        // then check form parameters
        else if (hasParams()) {
            try {
                entity = new UrlEncodedFormEntity(params, encoding);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        // no entity
        else {
            entity = null;
        }
        return entity;
    }

    HttpEntity entity() {
        if (mEntity == null) {
            mEntity = createHttpEntity();
        }
        return mEntity;
    }

    private boolean hasParts() {
        return parts.size() > 0;
    }

    private boolean hasParams() {
        return params.size() > 0;
    }

    @Override
    public String toString() {
        return "NextParams{" +
                "encoding='" + encoding + '\'' +
                ", queries=" + queries +
                ", params=" + params +
                ", parts=" + parts +
                '}';
    }

    public String dump() {
        StringBuilder builder = new StringBuilder();
        builder.append("encoding:").append(getEncoding()).append("\n");
        builder.append("queries:[").append(StringUtils.toString(getQueries())).append("]\n");
        builder.append("params:[").append(StringUtils.toString(getParams())).append("]\n");
        builder.append("parts:[").append(StringUtils.toString(getParts())).append("]\n");
        return builder.toString();
    }

    static class StreamPart {

        static final String DEFAULT_NAME = "nofilename";
        static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

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

        private StreamPart(String name, byte[] bytes, String mimeType) {
            this.name = name;
            this.bytes = bytes;
            this.length = bytes.length;
            this.contentType = ContentType.create(mimeType);
            this.fileName = DEFAULT_NAME;
        }

        private StreamPart(String name, InputStream stream, String mimeType) {
            this.name = name;
            this.stream = stream;
            this.length = -1;
            this.contentType = ContentType.create(mimeType);
            this.fileName = DEFAULT_NAME;
        }

        public static StreamPart create(String name, File file) {
            final String mimeType = MimeUtils.getMimeTypeFromPath(file.getPath());
            return create(name, file, mimeType, file.getName());
        }

        public static StreamPart create(String name, File file, String mimeType) {
            return create(name, file, mimeType, file.getName());
        }

        public static StreamPart create(String name, File file, String mimeType, String fileName) {
            return new StreamPart(name, file, mimeType, fileName);
        }

        public static StreamPart create(String name, byte[] bytes) {
            return create(name, bytes, APPLICATION_OCTET_STREAM);
        }

        public static StreamPart create(String name, byte[] bytes, String mimeType) {
            return new StreamPart(name, bytes, mimeType);
        }

        public static StreamPart create(String name, InputStream stream) {
            return create(name, stream, APPLICATION_OCTET_STREAM);
        }

        public static StreamPart create(String name, InputStream stream, String mimeType) {
            return new StreamPart(name, stream, mimeType);
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

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("StreamPart{");
            sb.append("name='").append(name).append('\'');
            sb.append(", contentType=").append(contentType);
            sb.append(", length=").append(length);
            sb.append(", file=").append(file);
            sb.append('}');
            return sb.toString();
        }
    }
}
