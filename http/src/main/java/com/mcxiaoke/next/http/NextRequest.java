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
    private final HttpUrl httpUrl;
    private NextParams params;
    private byte[] body;
    private ProgressListener listener;
    private boolean debug;

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
        final HttpUrl hUrl = HttpUrl.parse(url);
        AssertUtils.notNull(hUrl, "invalid url:" + url);
        this.method = method;
        this.httpUrl = HttpUrl.parse(url);
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
            this.params.headers(headers);
        }
        return this;
    }

    public NextRequest query(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        this.params.query(key, value);
        return this;
    }

    public NextRequest queries(Map<String, String> queries) {
        this.params.queries(queries);
        return this;
    }

    private void throwIfNotSupportBody() {
        if (!supportBody()) {
            throw new IllegalStateException("HTTP " + method.name() + " not support http body");
        }
    }

    public NextRequest form(String key, String value) {
        throwIfNotSupportBody();
        this.params.form(key, value);
        return this;
    }

    public NextRequest forms(Map<String, String> forms) {
        throwIfNotSupportBody();
        this.params.forms(forms);
        return this;
    }

    public NextRequest parts(Collection<BodyPart> parts) {
        throwIfNotSupportBody();
        for (final BodyPart part : parts) {
            part(part);
        }
        return this;
    }

    public NextRequest file(String key, File file) {
        throwIfNotSupportBody();
        this.params.file(key, file);
        return this;
    }

    public NextRequest file(String key, File file, String contentType) {
        throwIfNotSupportBody();
        this.params.file(key, file, contentType);
        return this;
    }

    public NextRequest file(String key, File file, String contentType, String fileName) {
        throwIfNotSupportBody();
        this.params.file(key, file, contentType, fileName);
        return this;
    }

    public NextRequest file(String key, byte[] bytes) {
        throwIfNotSupportBody();
        this.params.file(key, bytes);
        return this;
    }

    public NextRequest file(String key, byte[] bytes, String contentType) {
        throwIfNotSupportBody();
        this.params.file(key, bytes, contentType);
        return this;
    }

    public NextRequest body(final byte[] body) {
        throwIfNotSupportBody();
        this.body = body;
        return this;
    }

    public NextRequest body(final String content, final Charset charset) {
        throwIfNotSupportBody();
        this.body = content.getBytes(charset);
        return this;
    }

    public NextRequest body(final File file) throws IOException {
        throwIfNotSupportBody();
        this.body = IOUtils.readBytes(file);
        return this;
    }

    public NextRequest body(final Reader reader) throws IOException {
        throwIfNotSupportBody();
        this.body = IOUtils.readBytes(reader);
        return this;
    }

    public NextRequest body(final InputStream stream) throws IOException {
        throwIfNotSupportBody();
        this.body = IOUtils.readBytes(stream);
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

    public boolean debug() {
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

    NextRequest removeHeader(String key) {
        this.params.headers.remove(key);
        return this;
    }

    NextRequest removeQuery(String key) {
        this.params.queries.remove(key);
        return this;
    }

    NextRequest removeForm(String key) {
        this.params.forms.remove(key);
        return this;
    }

    NextRequest removePart(BodyPart part) {
        this.params.parts.remove(part);
        return this;
    }

    String getHeader(String key) {
        return this.params.getHeader(key);
    }

    String getQuery(String key) {
        return this.params.getQuery(key);
    }

    String getForm(String key) {
        return this.params.getForm(key);
    }

    BodyPart getPart(String key) {
        return this.params.getPart(key);
    }

    boolean hasHeader(String key) {
        return getHeader(key) != null;
    }

    boolean hasQuery(String key) {
        return getQuery(key) != null;
    }

    boolean hasForm(String key) {
        return getForm(key) != null;
    }

    boolean hasPart(String key) {
        return getPart(key) != null;
    }


    int queriesSize() {
        return queries().size();
    }

    int formsSize() {
        return form().size();
    }

    int headersSize() {
        return headers().size();
    }

    int partsSize() {
        return parts().size();
    }

    Map<String, String> headers() {
        return this.params.headers;
    }

    Map<String, String> queries() {
        return this.params.queries;
    }

    Map<String, String> form() {
        return this.params.forms;
    }

    List<BodyPart> parts() {
        return this.params.parts;
    }

    boolean hasParts() {
        return this.params.parts.size() > 0;
    }

    boolean hasForms() {
        return this.params.forms.size() > 0;
    }


    HttpUrl buildUrlWithQueries() {
        final HttpUrl.Builder builder = httpUrl.newBuilder();
        for (final Map.Entry<String, String> entry : params.queries().entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    void copy(final NextRequest source) {
        this.params = source.params;
        this.body = source.body;
        this.listener = source.listener;
        this.debug = source.debug;
    }

    RequestBody getRequestBody() throws IOException {
        if (!supportBody()) {
            return null;
        }
        if (body != null) {
            return RequestBody.create(HttpConsts.MEDIA_TYPE_OCTET_STREAM, body);
        }
        RequestBody requestBody;
        if (hasParts()) {
            final MultipartBuilder multipart = new MultipartBuilder();
            for (final BodyPart part : parts()) {
                if (part.getBody() != null) {
                    multipart.addFormDataPart(part.getName(), part.getFileName(), part.getBody());
                }
            }
            for (Map.Entry<String, String> entry : form().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                multipart.addFormDataPart(key, value == null ? "" : value);
            }
            requestBody = multipart.build();
        } else if (hasForms()) {
            final FormEncodingBuilder bodyBuilder = new FormEncodingBuilder();
            for (Map.Entry<String, String> entry : form().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                bodyBuilder.add(key, value == null ? "" : value);
            }
            requestBody = bodyBuilder.build();
        } else {
            //FIXME workaround for null body, waiting OkHttp release
            requestBody = RequestBody.create(null, HttpConsts.NO_BODY);
        }
        return requestBody;
    }

    @Override
    public String toString() {
        return "Request{HTTP " + method + " " + httpUrl + '}';
    }

    public String dump() {
        return "Request{HTTP " + method + " " + httpUrl + ' ' + params + '}';
    }
}
