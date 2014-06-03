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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    private final Map<String, String> mHeaders;
    private byte[] mBytes;
    private boolean mConsumed;

    NextResponse(int code, String message,
                 int contentLength, String contentType,
                 final Map<String, List<String>> rawHeaders, InputStream is) {
        this.mStatusCode = code;
        this.mMessage = message;
        this.mContentLength = contentLength;
        this.mContentType = contentType;
        this.mHeaders = new HashMap<String, String>();
        this.mStream = new BufferedInputStream(is);
        cleanHeaders(rawHeaders);
    }

    /**
     * clean headers, remove null key entry, remove multi value
     *
     * @param rawHeaders Raw Headers
     */
    private void cleanHeaders(Map<String, List<String>> rawHeaders) {
        if (rawHeaders == null || rawHeaders.isEmpty()) {
            return;
        }
        final Map<String, String> headers = new HashMap<String, String>();
        Set<Entry<String, List<String>>> entrySet = rawHeaders.entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
            final String key = entry.getKey();
            if (StringUtils.isEmpty(key)) {
                continue;
            }
            final List<String> values = entry.getValue();
            if (values == null || values.isEmpty()) {
                continue;
            }
            final String value = values.get(0);
            mHeaders.put(key, value);
        }
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

    public Map<String, String> headers() {
        return mHeaders;
    }

    public String header(String name) {
        return mHeaders.get(name);
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
        Map<String, String> headers = headers();
        if (headers == null || headers.isEmpty()) {
            return Consts.EMPTY_STRING;
        }
        StringBuilder builder = new StringBuilder();
        Set<String> keySet = headers.keySet();
        for (String key : keySet) {
            if (key != null) {
                builder.append(key).append(":").append(headers.get(key)).append("; ");
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
