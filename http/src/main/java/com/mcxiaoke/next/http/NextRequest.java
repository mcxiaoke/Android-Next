/*
 * Copyright (C) 2013-2014 Xiaoke Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcxiaoke.next.http;

import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.annotation.NotThreadSafe;
import com.mcxiaoke.next.collection.NoDuplicatesArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http request
 */
@NotThreadSafe
public final class NextRequest {
    private String mOriginalUrl;
    private String mMethod;
    private String mEncoding;
    private Map<String, String> mHeaders;
    private NextParams mParams;
    private ProgressCallback mCallback;
    private Object mTag;
    private String mCompleteUrl;
    private volatile URL mURL;

    NextResponse execute() throws IOException {
        return NextClient.getDefault().execute(this);
    }

    NextRequest(Builder builder) {
        this.mOriginalUrl = builder.originalUrl;
        this.mMethod = builder.method;
        this.mEncoding = builder.encoding;
        this.mHeaders = builder.headers;
        this.mParams = builder.params;
        this.mParams.setEncoding(builder.encoding);
        this.mCallback = builder.callback;
        this.mTag = builder.tag != null ? builder.tag : this;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String originalUrl() {
        return mOriginalUrl;
    }

    public URL url() {
        try {
            URL result = mURL;
            return result != null ? result : (mURL = new URL(completeUrl()));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + mURL, e);
        }
    }

    private String completeUrl() {
        if (mCompleteUrl == null) {
            mCompleteUrl = createCompleteUrl();
        }
        return mCompleteUrl;
    }

    private String createCompleteUrl() {
        // 去重
        final List<NameValuePair> list = new NoDuplicatesArrayList<NameValuePair>();
        list.addAll(mParams.getQueries());
        // 支持BODY的HTTP METHOD不添加PARAMS到URL QUERY
        if (!HttpMethod.hasRequestBody(method())) {
            list.addAll(mParams.getParams());
        }
        return Utils.appendQuery(mOriginalUrl, list);
    }

    public String method() {
        return mMethod;
    }

    public String encoding() {
        return mParams.getEncoding();
    }

    public String host() {
        return mURL.getHost();
    }

    public String protocol() {
        return mURL.getProtocol();
    }

    public ProgressCallback callback() {
        return mCallback;
    }

    public boolean isHttps() {
        return ("https").equals(protocol());
    }

    public Map<String, String> headers() {
        return mHeaders;
    }

    NextParams params() {
        return mParams;
    }

    public HttpEntity entity() {
        return mParams.entity();
    }

    public String header(String name) {
        return mHeaders.get(name);
    }

    public Object tag() {
        return mTag;
    }

    Builder copyToBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("mURL=").append(completeUrl());
        sb.append(", mMethod='").append(method()).append('\'');
        sb.append(", mHeaders=").append(headers());
        sb.append(", mParams=").append(params());
        sb.append(", mTag=").append(tag());
        sb.append('}');
        return sb.toString();
    }


    /**
     * ************************************************
     * <p/>
     * REQUEST BUILDER
     * <p/>
     * **************************************************
     */

    public static class Builder {
        String originalUrl;
        String method;
        String encoding;
        NextParams params;
        Map<String, String> headers;
        ProgressCallback callback;
        Object tag;

        public Builder() {
//            this.mURL=null;
            this.method = HttpMethod.METHOD_GET;
            this.encoding = Charsets.ENCODING_UTF_8;
            this.headers = new HashMap<String, String>();
            this.params = new NextParams(this.encoding);
//            this.mCallback=null;
//            this.mTag=null;
        }

        Builder(NextRequest request) {
            this.originalUrl = request.mOriginalUrl;
            this.method = request.mMethod;
            this.encoding = request.mEncoding;
            this.headers = request.mHeaders;
            this.params = request.mParams;
            this.callback = request.mCallback;
            this.tag = request.mTag != null ? request.mTag : this;
        }

        public Builder url(String url) {
            if (url == null) {
                throw new IllegalArgumentException("url can not be null");
            }
            this.originalUrl = url;
            return this;
        }

        public Builder method(final String method) {
            if (method == null) {
                throw new IllegalArgumentException("create can not be null");
            }
            if (!HttpMethod.METHODS.contains(method)) {
                throw new IllegalArgumentException("unsupported method: " + method);

            }
            this.method = method;
            return this;
        }

        public Builder encoding(final String encoding) {
            if (encoding == null) {
                throw new IllegalArgumentException("encoding can not be null");
            }
            this.encoding = encoding;
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            this.headers.remove(name);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder query(String key, String value) {
            this.params.query(key, value);
            return this;
        }

        public Builder queries(Map<String, String> map) {
            this.params.queries(map);
            return this;
        }

        public Builder param(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Builder params(Map<String, String> map) {
            this.params.putAll(map);
            return this;
        }

        public Builder params(NextParams params) {
            this.params.putAll(params);
            return this;
        }

        public Builder param(String key, File file) {
            this.params.put(key, file);
            return this;
        }

        public Builder param(String key, File file, String mimeType) {
            this.params.put(key, file, mimeType);
            return this;
        }

        public Builder param(String key, File file, String mimeType, String fileName) {
            this.params.put(key, file, mimeType, fileName);
            return this;
        }

        public Builder param(String key, byte[] bytes) {
            this.params.put(key, bytes);
            return this;
        }

        public Builder param(String key, byte[] bytes, String mimeType) {
            this.params.put(key, bytes, mimeType);
            return this;
        }

        public Builder param(String key, InputStream stream) {
            this.params.put(key, stream);
            return this;
        }

        public Builder param(String key, InputStream stream, String mimeType) {
            this.params.put(key, stream, mimeType);
            return this;
        }

        public Builder head(String url) {
            return create(url, HttpMethod.METHOD_HEAD);
        }

        public Builder get(String url) {
            return create(url, HttpMethod.METHOD_GET);
        }

        public Builder delete(String url) {
            return create(url, HttpMethod.METHOD_DELETE);
        }

        public Builder post(String url) {
            return create(url, HttpMethod.METHOD_POST);
        }

        public Builder put(String url) {
            return create(url, HttpMethod.METHOD_PUT);
        }

        public Builder patch(String url) {
            return create(url, HttpMethod.METHOD_PATCH);
        }

        private Builder create(final String url, final String method) {
            return url(url).method(method);
        }

        public void callback(final ProgressCallback pc) {
            if (callback == null) {
                this.callback = ProgressCallback.DEFAULT;
            } else {
                this.callback = pc;
            }
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public NextRequest build() {
            if (this.originalUrl == null) throw new IllegalStateException("mURL can not be null");
            if (this.method == null) throw new IllegalStateException("mMethod can not be null");
            if (this.encoding == null) throw new IllegalStateException("encoding can not be null");
            return new NextRequest(this);
        }
    }
}
