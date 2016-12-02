package com.mcxiaoke.next.request;

import com.mcxiaoke.next.utils.AssertUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okio.ByteString;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
class RequestBuilder {
    protected HttpMethod method;
    protected HttpUrl.Builder httpUrl;
    protected Headers.Builder headers;
    protected Map<String, String> forms;
    protected List<FileBody> parts;
    protected RequestBody rawBody;
    protected ProgressListener listener;
    protected boolean debug;

    public RequestBuilder() {
        this.method = HttpMethod.GET;
        this.httpUrl = new HttpUrl.Builder();
        this.headers = new Headers.Builder();
        this.forms = new HashMap<>();
        this.parts = new ArrayList<>();
    }

    RequestBuilder(final RequestBuilder builder) {
        this.method = builder.method;
        this.httpUrl = builder.httpUrl.build().newBuilder();
        this.headers = builder.headers.build().newBuilder();
        this.forms = new HashMap<>(builder.forms);
        this.parts = new ArrayList<>(builder.parts);
    }

    public RequestBuilder method(final HttpMethod method) {
        this.method = method;
        return this;
    }

    public RequestBuilder url(final String url) {
        this.httpUrl = HttpUrl.parse(url).newBuilder();
        return this;
    }

    public RequestBuilder url(final HttpUrl url) {
        this.httpUrl = url.newBuilder();
        return this;
    }

    public RequestBuilder get(final String url) {
        return method(HttpMethod.GET).url(url);
    }

    public RequestBuilder post(final String url) {
        return method(HttpMethod.POST).url(url);
    }

    public RequestBuilder header(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        AssertUtils.notNull(value, "value must not be null.");
        this.headers.set(key, value);
        return this;
    }

    public RequestBuilder addHeader(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        AssertUtils.notNull(value, "value must not be null.");
        this.headers.add(key, value);
        return this;
    }

    public RequestBuilder headers(Map<String, String> headers) {
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestBuilder userAgent(final String userAgent) {
        return header(HttpConsts.USER_AGENT, userAgent);
    }

    public RequestBuilder authorization(final String authorization) {
        return header(HttpConsts.AUTHORIZATION, authorization);
    }

    public RequestBuilder query(String name, String value) {
        AssertUtils.notEmpty(name, "name must not be null or empty.");
        this.httpUrl.addQueryParameter(name, value == null ? "" : String.valueOf(value));
        return this;
    }

    public RequestBuilder queries(Map<String, String> queries) {
        if (queries != null) {
            for (final Map.Entry<String, String> entry : queries.entrySet()) {
                query(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestBuilder form(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        AssertUtils.notNull(value, "value must not be null.");
        this.forms.put(key, value);
        return this;
    }

    public RequestBuilder forms(Map<String, String> forms) {
        if (forms != null) {
            for (final Map.Entry<String, String> entry : forms.entrySet()) {
                form(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestBuilder file(String name, File file) {
        return file(name, file, HttpConsts.APPLICATION_OCTET_STREAM, file.getName());
    }

    public RequestBuilder file(String name, File file, String contentType) {
        return file(name, file, contentType, file.getName());
    }

    public RequestBuilder file(String name, File file, String contentType, String fileName) {
        AssertUtils.notEmpty(name, "name must not be null or empty.");
        AssertUtils.notNull(file, "file must not be null.");
        FileBody part = FileBody.create(name, file, contentType, fileName);
        this.parts.add(part);
        return this;
    }

    public RequestBuilder file(String name, byte[] bytes) {
        return file(name, bytes, HttpConsts.APPLICATION_OCTET_STREAM);

    }

    public RequestBuilder file(String name, byte[] bytes, String contentType) {
        return file(name, bytes, contentType, null);
    }

    public RequestBuilder file(String name, byte[] bytes, String contentType,
                               String fileName) {
        AssertUtils.notEmpty(name, "name must not be null or empty.");
        AssertUtils.notNull(bytes, "bytes must not be null.");
        FileBody part = FileBody.create(name, bytes, contentType, fileName);
        this.parts.add(part);
        return this;
    }

    public RequestBuilder rawBody(final RequestBody body) {
        this.rawBody = body;
        return this;
    }

    protected boolean hasParts() {
        return this.parts.size() > 0;
    }

    protected boolean hasForms() {
        return this.forms.size() > 0;
    }

    protected boolean supportBody() {
        return HttpMethod.supportBody(method);
    }

    protected RequestBody createBody() {
        if (!supportBody()) {
            return null;
        }
        if (rawBody != null) {
            return rawBody;
        }
        RequestBody requestBody;
        if (hasParts()) {
            final MultipartBody.Builder builder = new MultipartBody.Builder();
            for (final FileBody part : parts) {
                if (part.getBody() != null) {
                    builder.addFormDataPart(part.getName(), part.getFileName(), part.getBody());
                }
            }
            for (Map.Entry<String, String> entry : forms.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                builder.addFormDataPart(key, value == null ? "" : value);
            }
            requestBody = builder.setType(MultipartBody.FORM).build();
        } else if (hasForms()) {
            final FormBody.Builder builder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : forms.entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                builder.add(key, value == null ? "" : value);
            }
            requestBody = builder.build();
        } else {
            requestBody = null;
        }
        if (requestBody == null) {
            return RequestBody.create(null, ByteString.EMPTY);
        }
        if (listener != null) {
            requestBody = new ProgressRequestBody(requestBody, listener);
        }
        return requestBody;
    }

    protected Request toOkRequest() {
        return new Builder().url(httpUrl.build())
                .headers(headers.build())
                .method(method.name(), createBody())
                .build();
    }

    public OkRequest build() {
        return new OkRequest(this);
    }


}
