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
import com.mcxiaoke.next.collection.NoDuplicatesArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http request
 */
public final class NextRequest {
    private URL url;
    private String method;
    private Map<String, String> headers;
    private NextParams params;
    private ProgressCallback callback;
    private Object tag;
    private volatile URI uri;
    private String completeUrl;


    NextResponse execute() throws IOException {
        return NextClient.getDefault().execute(this);
    }

    NextRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.params = builder.params;
        this.callback = builder.callback;
        this.tag = builder.tag != null ? builder.tag : this;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public URL url() {
        return url;
    }

    public URI uri() {
        try {
            URI result = uri;
            return result != null ? result : (uri = url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("URI Syntax error: " + url);
        }
    }

    public String completeUrl() {
        // 去重
        final List<NameValuePair> list = new NoDuplicatesArrayList<NameValuePair>();
        list.addAll(params.getQueries());
        // 支持BODY的HTTP METHOD不添加PARAMS到URL QUERY
        if (!HttpMethod.hasRequestBody(method())) {
            list.addAll(params.getParams());
        }
        if (completeUrl == null) {
            completeUrl = Encoder.appendQuery(url.toString(), list);
        }
        return completeUrl;
    }

    public String method() {
        return method;
    }

    public String encoding() {
        return params.getEncoding();
    }

    public String host() {
        return url.getHost();
    }

    public String protocol() {
        return url.getProtocol();
    }

    public ProgressCallback callback() {
        return callback;
    }

    public boolean isHttps() {
        return ("https").equals(protocol());
    }

    public Map<String, String> headers() {
        return headers;
    }

    public NextParams params() {
        return params;
    }

    public HttpEntity entity() {
        return params.entity();
    }

    public String header(String name) {
        return headers.get(name);
    }

    public Object tag() {
        return tag;
    }

    Builder copyToBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("url=").append(completeUrl());
        sb.append(", method='").append(method()).append('\'');
        sb.append(", headers=").append(headers());
        sb.append(", params=").append(params());
        sb.append(", tag=").append(tag());
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
        URL url;
        String method;
        String encoding;
        NextParams params;
        Map<String, String> headers;
        ProgressCallback callback;
        Object tag;

        public Builder() {
//            this.url=null;
            this.method = HttpMethod.METHOD_GET;
            this.encoding = Charsets.ENCODING_UTF_8;
            this.headers = new HashMap<String, String>();
            this.params = new NextParams(this.encoding);
//            this.callback=null;
//            this.tag=null;
        }

        Builder(NextRequest request) {
            this.url = request.url;
            this.method = request.method;
            this.headers = request.headers;
            this.params = request.params;
            this.callback = request.callback;
            this.tag = request.tag != null ? request.tag : this;
        }

        public Builder url(String uriString) {
            if (uriString == null) {
                throw new IllegalArgumentException("url can not be null");
            }
            this.url = Utils.toURL(uriString);
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
            if (method == null) {
                throw new IllegalArgumentException("encoding can not be null");
            }
            this.encoding = encoding;
            this.params.setEncoding(encoding);
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
            if (this.url == null) throw new IllegalStateException("url can not be null");
            if (this.method == null) throw new IllegalStateException("method can not be null");
            if (this.encoding == null) throw new IllegalStateException("encoding can not be null");
            return new NextRequest(this);
        }
    }
}
