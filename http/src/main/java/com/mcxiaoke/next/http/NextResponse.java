package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class NextResponse implements Closeable {
    public static final String TAG = NextClient.TAG;

    private final int code;
    private final String message;
    private final int contentLength;
    private final String contentType;
    private final InputStream stream;
    private final Map<String, List<String>> headers;
    private byte[] content;
    private boolean consumed;

    NextResponse(int code, String message,
                 int contentLength, String contentType,
                 final Map<String, List<String>> headers, InputStream is) {
        this.code = code;
        this.message = message;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.headers = headers;
        this.stream = is;
    }

    public boolean successful() {
        return Utils.isSuccess(code);
    }

    public boolean redirect() {
        return Utils.isRedirect(code);
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public int contentLength() {
        return contentLength;
    }

    public String contentType() {
        return contentType;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public String header(String name) {
        List<String> value = headers.get(name);
        return value != null ? value.get(0) : null;
    }

    public String location() {
        return header(Consts.LOCATION);
    }

    public InputStream stream() {
        if (consumed) {
            return new ByteArrayInputStream(content);
//            throw new IllegalStateException("the input stream is consumed.");
        }
        return stream;
    }

    public byte[] bytes() throws IOException {
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

    public String string() throws IOException {
        return string(Consts.ENCODING_UTF8);
    }

    public String string(String charsetName) throws IOException {
        return new String(bytes(), charsetName);
    }

    public void close() throws IOException {
        IOUtils.closeQuietly(stream);
    }

    public String dumpContent() {
        try {
            return StringUtils.safeSubString(string(), 256);
        } catch (IOException e) {
            return "IOException";
        }
    }

    public String dumpHeaders() {
        Map<String, List<String>> headers = headers();
        if (headers == null || headers.isEmpty()) {
            return Consts.EMPTY_STRING;
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
        final StringBuilder sb = new StringBuilder("Response{");
        sb.append("code=").append(code);
        sb.append(", message='").append(message);
        sb.append(", contentLength=").append(contentLength);
        sb.append(", contentType='").append(contentType);
        sb.append(", consumed=").append(consumed);
        sb.append(", headers='").append(dumpHeaders());
        sb.append('}');
        return sb.toString();
    }
}
