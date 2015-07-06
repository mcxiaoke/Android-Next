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
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class NextRequest {
    private final HttpMethod method;
    private final String originalUrl;
    private final HttpUrl.Builder httpUrl;
    private NextParams params;
    private byte[] body;
    private ProgressListener listener;
    private boolean debug;

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

    public NextRequest(final NextRequest request) {
        this.method = request.method;
        this.originalUrl = request.originalUrl;
        this.httpUrl = request.url().newBuilder();
        this.params = new NextParams(request.params);
        this.body = request.body;
        this.listener = request.listener;
        this.debug = request.debug;
    }

    public NextRequest(final HttpMethod method, String url) {
        this(method, url, new NextParams());
    }

    public NextRequest(final HttpMethod method, String url, final NextParams params) {
        AssertUtils.notNull(method, "http method can not be null");
        AssertUtils.notEmpty(url, "http url can not be null or empty");
        AssertUtils.notNull(params, "http params can not be null");
        this.method = method;
        this.originalUrl = url;
        this.httpUrl = HttpUrl.parse(url).newBuilder();
        this.params = new NextParams(params);
    }

    public NextRequest debug(final boolean debug) {
        this.debug = debug;
        return this;
    }

    public NextRequest progress(final ProgressListener listener) {
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
            this.params.headers.putAll(headers);
        }
        return this;
    }

    public NextRequest query(String key, String value) {
        this.params.queries.put(key, value);
        this.httpUrl.addQueryParameter(key, value);
        return this;
    }

    public NextRequest queries(Map<String, String> queries) {
        if (queries != null) {
            for (Map.Entry<String, String> entry : queries.entrySet()) {
                query(entry.getKey(), entry.getValue());
            }
        }

        return this;
    }

    public NextRequest forms(String key, String value) {
        this.params.forms.put(key, value);
        return this;
    }

    public NextRequest forms(Map<String, String> forms) {
        if (forms != null) {
            for (Map.Entry<String, String> entry : forms.entrySet()) {
                forms(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public NextRequest parts(Collection<BodyPart> parts) {
        for (final BodyPart part : parts) {
            part(part);
        }
        return this;
    }

    public NextRequest file(String key, File file) {
        this.params.file(key, file);
        return this;
    }

    public NextRequest file(String key, File file, String contentType) {
        this.params.file(key, file, contentType);
        return this;
    }

    public NextRequest file(String key, File file, String contentType, String fileName) {
        this.params.file(key, file, contentType, fileName);
        return this;
    }

    public NextRequest file(String key, byte[] bytes) {
        this.params.file(key, bytes);
        return this;
    }

    public NextRequest file(String key, byte[] bytes, String contentType) {
        this.params.file(key, bytes, contentType);
        return this;
    }

    public NextRequest body(final byte[] body) {
        this.body = body;
        return this;
    }

    public NextRequest body(final String content, final Charset charset) {
        this.body = content.getBytes(charset);
        return this;
    }

    public NextRequest body(final File file) throws IOException {
        this.body = IOUtils.readBytes(file);
        return this;
    }

    public NextRequest body(final Reader reader) throws IOException {
        this.body = IOUtils.readBytes(reader);
        return this;
    }

    public NextRequest body(final InputStream stream) throws IOException {
        this.body = IOUtils.readBytes(stream);
        return this;
    }

    public NextRequest params(final NextParams params) {
        if (params != null) {
            queries(params.queries);
            forms(params.forms);
            parts(params.parts);
        }
        return this;
    }

    public boolean debug() {
        return debug;
    }

    public HttpUrl url() {
        return httpUrl.build();
    }

    public HttpMethod method() {
        return method;
    }

    public String originalUrl() {
        return originalUrl;
    }

    public ProgressListener listener() {
        return listener;
    }

    boolean supportBody() {
        return HttpMethod.supportBody(method);
    }

    NextRequest part(final BodyPart part) {
        this.params.parts.add(part);
        return this;
    }


    NextRequest removeQuery(String key) {
        this.params.queries.remove(key);
        return this;
    }

    NextRequest removeHeader(String key) {
        this.params.headers.remove(key);
        return this;
    }

    NextRequest removeForm(String key) {
        this.params.forms.remove(key);
        return this;
    }

    Map<String, String> headers() {
        return this.params.headers;
    }

    Map<String, String> queries() {
        return this.params.queries;
    }

    Map<String, String> forms() {
        return this.params.forms;
    }

    List<BodyPart> parts() {
        return this.params.parts;
    }

    boolean hasParts() {
        return this.params.parts.size() > 0;
    }

    boolean hasParams() {
        return this.params.forms.size() > 0;
    }

    RequestBody getRequestBody() throws IOException {
        if (!supportBody()) {
            return null;
        }
        if (body != null) {
            final MediaType type = MediaType.parse(HttpConsts.APPLICATION_OCTET_STREAM);
            return RequestBody.create(type, body);
        }
        RequestBody body;
        if (hasParts()) {
            final MultipartBuilder multipart = new MultipartBuilder();
            for (final BodyPart part : parts()) {
                if (part.getBody() != null) {
                    multipart.addFormDataPart(part.getName(), part.getFileName(), part.getBody());
                }
            }
            for (Map.Entry<String, String> entry : forms().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                multipart.addFormDataPart(key, value == null ? "" : value);
            }
            body = multipart.build();
        } else {
            final FormEncodingBuilder bodyBuilder = new FormEncodingBuilder();
            for (Map.Entry<String, String> entry : forms().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                bodyBuilder.add(key, value == null ? "" : value);
            }
            body = bodyBuilder.build();
        }
        return body;
    }

    @Override
    public String toString() {
        return "Request{ HTTP " + method + " " + httpUrl.build().toString() + '}';
    }

    public String dump() {
        return "Request{ HTTP " + method + " " + httpUrl.build().toString()
                + ' ' + params + '}';
    }
}
