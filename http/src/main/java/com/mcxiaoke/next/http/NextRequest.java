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

import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.IOUtils;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.ByteString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NextRequest {
    protected final HttpMethod method;
    protected final HttpUrl httpUrl;
    protected NextParams params;
    protected byte[] body;
    protected ProgressListener listener;
    protected boolean debug;

    public static NextRequest head(final String url) {
        return new NextRequest(HttpMethod.HEAD, url);
    }

    public static NextRequest get(final String url) {
        return new NextRequest(HttpMethod.GET, url);
    }

    public static NextRequest delete(final String url) {
        return new NextRequest(HttpMethod.DELETE, url);
    }

    public static NextRequest post(final String url) {
        return new NextRequest(HttpMethod.POST, url);
    }

    public static NextRequest put(final String url) {
        return new NextRequest(HttpMethod.PUT, url);
    }

    public NextRequest(final NextRequest source) {
        this.method = source.method;
        this.httpUrl = source.httpUrl;
        this.params = source.params;
        this.body = source.body;
        this.listener = source.listener;
        this.debug = source.debug;
    }

    public NextRequest(final HttpMethod method, String url) {
        this(method, url, new NextParams());
    }

    public NextRequest(final HttpMethod method, String url, final NextParams params) {
        AssertUtils.notNull(method, "http method can not be null");
        AssertUtils.notEmpty(url, "http url can not be null or empty");
        AssertUtils.notNull(params, "http params can not be null");
        this.method = method;
        this.params = new NextParams(params);
        this.httpUrl = HttpUrl.parse(url);
        AssertUtils.notNull(this.httpUrl, "http url can not be null");
    }

    public NextRequest debug(final boolean debug) {
        this.debug = debug;
        return this;
    }

    public NextRequest progressListener(final ProgressListener listener) {
        this.listener = listener;
        return this;
    }

    public NextRequest userAgent(final String userAgent) {
        return header(HttpConsts.USER_AGENT, userAgent);
    }

    public NextRequest authorization(final String authorization) {
        return header(HttpConsts.AUTHORIZATION, authorization);
    }

    public NextRequest referer(final String referer) {
        return header(HttpConsts.REFERER, referer);
    }

    public NextRequest header(String name, String value) {
        this.params.header(name, value);
        return this;
    }

    public NextRequest headers(Map<String, String> headers) {
        if (headers != null) {
            this.params.headers(headers);
        }
        return this;
    }

    public NextRequest query(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        this.params.query(key, value);
        return this;
    }

    public NextRequest queries(List<KeyValue> queries) {
        this.params.queries(queries);
        return this;
    }

    public NextRequest queries(Map<String, String> queries) {
        this.params.queries(queries);
        return this;
    }

    protected void throwIfNotSupportBody() {
        if (!supportBody()) {
            throw new IllegalStateException("HTTP " + method.name() + " not support http body");
        }
    }

    public NextRequest form(String key, String value) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.form(key, value);
        }
        return this;
    }

    public NextRequest forms(List<KeyValue> forms) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.forms(forms);
        }
        return this;
    }

    public NextRequest forms(Map<String, String> forms) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.forms(forms);
        }
        return this;
    }

    public NextRequest parts(Collection<BodyPart> parts) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            for (final BodyPart part : parts) {
                part(part);
            }
        }
        return this;
    }

    public NextRequest file(String key, File file) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.file(key, file);
        }
        return this;
    }

    public NextRequest file(String key, File file, String contentType) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.file(key, file, contentType);
        }
        return this;
    }

    public NextRequest file(String key, File file, String contentType, String fileName) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.file(key, file, contentType, fileName);
        }
        return this;
    }

    public NextRequest file(String key, byte[] bytes) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.file(key, bytes);
        }
        return this;
    }

    public NextRequest file(String key, byte[] bytes, String contentType) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.params.file(key, bytes, contentType);
        }
        return this;
    }

    public NextRequest body(final byte[] body) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.body = body;
        }
        return this;
    }

    public NextRequest body(final String content, final Charset charset) {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.body = content.getBytes(charset);
        }
        return this;
    }

    public NextRequest body(final File file) throws IOException {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.body = IOUtils.readBytes(file);
        }
        return this;
    }

    public NextRequest body(final Reader reader) throws IOException {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.body = IOUtils.readBytes(reader);
        }
        return this;
    }

    public NextRequest body(final InputStream stream) throws IOException {
//        throwIfNotSupportBody();
        if (supportBody()) {
            this.body = IOUtils.readBytes(stream);
        }
        return this;
    }

    public NextRequest params(final NextParams params) {
        if (params != null) {
            queries(params.queries);
            if (supportBody()) {
                forms(params.forms);
                parts(params.parts);
            }
        }
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public HttpUrl url() {
        return buildUrlWithQueries();
    }

    public HttpMethod method() {
        return method;
    }

    public String originalUrl() {
        return httpUrl.toString();
    }

    public ProgressListener getProgressListener() {
        return listener;
    }

    public NextParams getParams() {
        return params;
    }

    protected boolean supportBody() {
        return HttpMethod.supportBody(method);
    }

    protected NextRequest part(final BodyPart part) {
        this.params.parts.add(part);
        return this;
    }

    protected Map<String, String> headers() {
        return this.params.headers;
    }

    protected List<KeyValue> queries() {
        return this.params.queries;
    }

    protected List<KeyValue> forms() {
        return this.params.forms;
    }

    protected List<BodyPart> parts() {
        return this.params.parts;
    }

    protected boolean hasParts() {
        return this.params.parts.size() > 0;
    }

    protected boolean hasForms() {
        return this.params.forms.size() > 0;
    }

    HttpUrl buildUrlWithQueries() {
        final HttpUrl.Builder builder = httpUrl.newBuilder();
        final List<KeyValue> entrySet = new ArrayList<>(params.queries());
        for (final KeyValue entry : entrySet) {
            builder.addQueryParameter(entry.first, entry.second);
        }
        return builder.build();
    }

    protected void copy(final NextRequest source) {
        this.params = source.params;
        this.body = source.body;
        this.listener = source.listener;
        this.debug = source.debug;
    }

    protected RequestBody getRequestBody() throws IOException {
        if (!supportBody()) {
            return null;
        }
        RequestBody requestBody;
        if (body != null) {
            requestBody = RequestBody.create(HttpConsts.MEDIA_TYPE_OCTET_STREAM, body);
        } else if (hasParts()) {
            final MultipartBody.Builder multipart = new MultipartBody.Builder();
            for (final BodyPart part : parts()) {
                if (part.getBody() != null) {
                    multipart.addFormDataPart(part.getName(), part.getFileName(), part.getBody());
                }
            }
            for (KeyValue entry : forms()) {
                final String key = entry.first;
                final String value = entry.second;
                multipart.addFormDataPart(key, value == null ? "" : value);
            }
            requestBody = multipart.setType(MultipartBody.FORM).build();
        } else if (hasForms()) {
            final FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (KeyValue entry : forms()) {
                final String key = entry.first;
                final String value = entry.second;
                bodyBuilder.add(key, value == null ? "" : value);
            }
            requestBody = bodyBuilder.build();
        } else {
            requestBody = null;
        }
        if (requestBody == null) {
            return getEmptyBody();
        }
        if (listener != null) {
            requestBody = new ProgressRequestBody(requestBody, listener);
        }
        return requestBody;
    }

    @Override
    public String toString() {
        return "Request{HTTP " + method() + " " + url() + ' ' + params + '}';
    }

    public RequestBody getEmptyBody() {
        if (supportBody()) {
            return RequestBody.create(null, ByteString.EMPTY);
        } else {
            return null;
        }
    }

}
