package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.annotation.NotThreadSafe;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
@NotThreadSafe
public class NextResponse implements Closeable {
    public static final String TAG = NextClient.TAG;

    private final int mStatusCode;
    private final String mMessage;
    private final int mContentLength;
    private final String mContentType;
    private final InputStream mStream;
    private final Map<String, List<String>> mHeaders;
    private byte[] mBytes;
    private boolean mConsumed;

    NextResponse(int code, String message,
                 int contentLength, String contentType,
                 final Map<String, List<String>> headers, InputStream is) {
        this.mStatusCode = code;
        this.mMessage = message;
        this.mContentLength = contentLength;
        this.mContentType = contentType;
        this.mHeaders = headers;
        this.mStream = new BufferedInputStream(is);
    }

    public boolean successful() {
        return Utils.isSuccess(mStatusCode);
    }

    public boolean redirect() {
        return Utils.isRedirect(mStatusCode);
    }

    public int code() {
        return mStatusCode;
    }

    public String message() {
        return mMessage;
    }

    public int contentLength() {
        return mContentLength;
    }

    public String contentType() {
        return mContentType;
    }

    public Map<String, List<String>> headers() {
        return mHeaders;
    }

    public String header(String name) {
        List<String> value = mHeaders.get(name);
        return value != null ? value.get(0) : null;
    }

    public String location() {
        return header(Consts.LOCATION);
    }

    private InputStream getInputStream() {
        if (mConsumed) {
//            return new ByteArrayInputStream(getByteArray());
            throw new IllegalStateException("the input stream is consumed.");
        }
        return mStream;
    }

    private byte[] getByteArray() throws IOException {
        if (mBytes == null) {
            try {
                mBytes = IOUtils.readBytes(mStream);
            } finally {
                mConsumed = true;
                close();
            }
        }
        return mBytes;
    }

    public InputStream stream() {
        return getInputStream();
    }

    public byte[] bytes() throws IOException {
        return getByteArray();
    }

    public InputStreamReader reader() throws IOException {
        return reader(Charsets.UTF_8);
    }

    public InputStreamReader reader(Charset charset) {
        return new InputStreamReader(getInputStream(), charset);
    }

    public String string() throws IOException {
        return string(Charsets.UTF_8);
    }

    public String string(Charset charset) throws IOException {
        return new String(getByteArray(), charset);
    }

    public int writeTo(OutputStream os) throws IOException {
        return IOUtils.copy(getInputStream(), os);
    }

    public boolean writeTo(File file) throws IOException {
        return IOUtils.writeStream(file, getInputStream());
    }

    public void close() throws IOException {
        IOUtils.closeQuietly(mStream);
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
        sb.append("mStatusCode=").append(mStatusCode);
        sb.append(", mMessage='").append(mMessage);
        sb.append(", mContentLength=").append(mContentLength);
        sb.append(", mContentType='").append(mContentType);
        sb.append(", mConsumed=").append(mConsumed);
        sb.append(", mHeaders='").append(dumpHeaders());
        sb.append('}');
        return sb.toString();
    }
}
