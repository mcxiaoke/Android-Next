package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class NextResponse implements Closeable {
    private final int code;
    private final String message;
    private int contentLength;
    private String contentType;
    private boolean consumed;
    private InputStream stream;
    private Map<String, List<String>> headers;
    private byte[] content;

    public static NextResponse create(int code, String message) {
        return new NextResponse(code, message);
    }

    private NextResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public NextResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public NextResponse setContentLength(int contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public NextResponse setStream(InputStream stream) {
        this.stream = new BufferedInputStream(stream);
        return this;
    }

    public NextResponse setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public boolean isSuccessful() {
        return getCode() >= HttpURLConnection.HTTP_OK
                && getCode() < HttpURLConnection.HTTP_BAD_REQUEST;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        List<String> value = headers.get(name);
        return value != null ? value.get(0) : null;
    }

    public InputStream getAsStream() {
        if (consumed) {
            return new ByteArrayInputStream(content);
//            throw new IllegalStateException("the input stream is consumed.");
        }
        return stream;
    }

    public byte[] getAsBytes() throws IOException {
        if (content == null) {
            try {
                content = IOUtils.readBytes(stream);
            } finally {
                consumed = true;
                IOUtils.closeQuietly(stream);
            }
        }
        return content;
    }

    public String getAsAsString() throws IOException {
        return getAsAsString(NextConsts.ENCODING_UTF8);
    }

    public String getAsAsString(String charsetName) throws IOException {
        return new String(getAsBytes(), charsetName);
    }

    public void close() throws IOException {
        IOUtils.closeQuietly(stream);
    }

    private String dumpContent() {
        try {
            return StringUtils.safeSubString(getAsAsString(), 256);
        } catch (IOException e) {
            return "IOException";
        }
    }

    private String dumpHeaders() {
        Map<String, List<String>> headers = getHeaders();
        if (headers == null || headers.isEmpty()) {
            return NextConsts.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            if (key != null) {
                builder.append(key).append(":").append(headers.get(key).get(0)).append("; ");
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResponse{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message);
        sb.append(", contentLength=").append(contentLength);
        sb.append(", contentType='").append(contentType);
        sb.append(", headers=['").append(dumpHeaders()).append(']');
        sb.append(", content=[").append(dumpContent()).append("]");
        sb.append(", consumed=").append(consumed);
        sb.append('}');
        return sb.toString();
    }
}
