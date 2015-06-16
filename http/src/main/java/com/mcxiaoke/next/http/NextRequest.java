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

import com.mcxiaoke.next.collection.NoDuplicatesArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http request
 */
public final class NextRequest {
    private boolean debug;
    private String method;
    private String originalUrl;
    private String encoding;
    private Map<String, String> headers;
    private NextParams params;
    private ProgressCallback callback;
    private Object tag;

    public static NextRequest get(final String url) {
        return new NextRequest(HttpMethod.METHOD_GET, url);
    }

    public static NextRequest delete(final String url) {
        return new NextRequest(HttpMethod.METHOD_DELETE, url);
    }

    public static NextRequest post(final String url) {
        return new NextRequest(HttpMethod.METHOD_POST, url);
    }

    public static NextRequest put(final String url) {
        return new NextRequest(HttpMethod.METHOD_PUT, url);
    }

    public NextRequest(final String method, String url) {
        headers = new HashMap<String, String>();
        params = new NextParams();
        method(method).url(url);
    }

    public NextRequest debug(final boolean debug) {
        this.debug = debug;
        return this;
    }

    public NextRequest method(final String method) {
        if (!HttpMethod.isValid(method)) {
            throw new IllegalArgumentException("invalid http method: " + method);
        }
        this.method = method;
        return this;
    }

    public NextRequest url(final String url) {
        if (url == null) {
            throw new NullPointerException("url is null or empty");
        }
        this.originalUrl = url;
        return this;
    }

    public NextRequest encoding(final String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("encoding can not be null");
        }
        this.encoding = encoding;
        return this;
    }

    public NextRequest callback(final ProgressCallback callback) {
        this.callback = callback;
        return this;
    }

    public NextRequest tag(final String tag) {
        this.tag = tag;
        return this;
    }

    public NextRequest userAgent(final String userAgent) {
        return header(HttpConsts.USER_AGENT, userAgent);
    }

    public NextRequest authorization(final String authorization) {
        return header(HttpConsts.AUTHORIZATION, authorization);
    }

    public NextRequest header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    public NextRequest headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public NextRequest query(String key, String value) {
        this.params.query(key, value);
        return this;
    }

    public NextRequest queries(Map<String, String> map) {
        this.params.queries(map);
        return this;
    }

    public NextRequest param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public NextRequest params(Map<String, String> map) {
        this.params.putAll(map);
        return this;
    }

    public NextRequest params(NextParams params) {
        this.params.putAll(params);
        return this;
    }

    public NextRequest param(String key, File file) {
        this.params.put(key, file);
        return this;
    }

    public NextRequest param(String key, File file, String mimeType) {
        this.params.put(key, file, mimeType);
        return this;
    }

    public NextRequest param(String key, File file, String mimeType, String fileName) {
        this.params.put(key, file, mimeType, fileName);
        return this;
    }

    public NextRequest param(String key, byte[] bytes) {
        this.params.put(key, bytes);
        return this;
    }

    public NextRequest param(String key, byte[] bytes, String mimeType) {
        this.params.put(key, bytes, mimeType);
        return this;
    }

    public NextRequest param(String key, InputStream stream) {
        this.params.put(key, stream);
        return this;
    }

    public NextRequest param(String key, InputStream stream, String mimeType) {
        this.params.put(key, stream, mimeType);
        return this;
    }

    public NextRequest removeHeader(String key) {
        this.headers.remove(key);
        return this;
    }

    public NextRequest removeParam(String key) {
        this.params.removeParam(key);
        return this;
    }

    public NextRequest body(final byte[] body) {
        this.params.body(body);
        return this;
    }

    public NextRequest body(final String body, final Charset charset) {
        this.params.body(body, charset);
        return this;
    }

    public NextRequest body(final String body) {
        this.params.body(body);
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getMethod() {
        return method;
    }

    public String getEncoding() {
        return encoding;
    }

    public Object getTag() {
        return tag;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getCompleteUrl() {
        return createCompleteUrl();
    }

    public URL getURL() {
        return Utils.toURL(createCompleteUrl());
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public NextParams getParams() {
        return params;
    }

    public ProgressCallback getCallback() {
        return callback;
    }

    public HttpEntity getEntity() {
        if (HttpMethod.hasRequestBody(method)) {
            return params.entity();
        }
        return null;
    }

    private String createCompleteUrl() {
        // 去重
        final List<NameValuePair> list = new NoDuplicatesArrayList<NameValuePair>();
        list.addAll(params.getQueries());
        // 支持BODY的HTTP METHOD不添加PARAMS到URL QUERY
        if (!HttpMethod.hasRequestBody(method)) {
            list.addAll(params.getParams());
        }
        return Utils.appendQuery(originalUrl, list);
    }

    public NextResponse execute() throws IOException {
        return NextClient.getDefault().execute(this);
    }

    @Override
    public String toString() {
        return "NextRequest{" +
                "debug=" + debug +
                ", method='" + method + '\'' +
                ", originalUrl='" + originalUrl + '\'' +
                ", completeUrl='" + getCompleteUrl() + '\'' +
                ", encoding='" + encoding + '\'' +
                ", headers=" + headers +
                ", params=" + params +
                ", tag=" + tag +
                '}';
    }

    public String dump() {
        return "NextRequest{" +
                "\ndebug=" + debug +
                "\n method='" + method + '\'' +
                "\noriginalUrl='" + originalUrl + '\'' +
                "\ncompleteUrl='" + getCompleteUrl() + '\'' +
                "\nencoding='" + encoding + '\'' +
                "\nheaders=" + headers +
                "\nparams=" + params.dump() +
                "\ntag=" + tag +
                '}';
    }
}
