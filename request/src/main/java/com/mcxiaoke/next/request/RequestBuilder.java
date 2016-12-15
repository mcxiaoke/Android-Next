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
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    protected HttpMethod method;
    protected HttpUrl.Builder httpUrl;
    protected Headers.Builder headers;
    protected Map<String, String> forms;
    protected List<FileBody> bodies;
    protected RequestBody rawBody;
    protected ProgressListener listener;
    protected boolean debug;

    public RequestBuilder() {
        this.method = HttpMethod.GET;
        this.httpUrl = new HttpUrl.Builder();
        this.headers = new Headers.Builder();
        this.forms = new HashMap<>();
        this.bodies = new ArrayList<>();
    }

    RequestBuilder(final RequestBuilder builder) {
        this.method = builder.method;
        this.httpUrl = builder.httpUrl.build().newBuilder();
        this.headers = builder.headers.build().newBuilder();
        this.forms = new HashMap<>(builder.forms);
        this.bodies = new ArrayList<>(builder.bodies);
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

    public RequestBuilder header(String name, String value) {
        this.headers.set(name, value);
        return this;
    }

    public RequestBuilder addHeader(String name, String value) {
        this.headers.add(name, value);
        return this;
    }

    public RequestBuilder headers(final Map<String, String> headers) {
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestBuilder userAgent(final String userAgent) {
        return header(HEADER_USER_AGENT, userAgent);
    }

    public RequestBuilder authorization(final String authorization) {
        return header(HEADER_AUTHORIZATION, authorization);
    }

    public RequestBuilder addQuery(String name, String value) {
        this.httpUrl.addQueryParameter(name, value);
        return this;
    }

    public RequestBuilder addQueries(Map<String, String> queries) {
        if (queries != null) {
            for (final Map.Entry<String, String> entry : queries.entrySet()) {
                addQuery(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestBuilder addBody(String name, String value) {
        AssertUtils.notEmpty(name, "name must not be null or empty.");
        AssertUtils.notNull(value, "value must not be null.");
        this.forms.put(name, value);
        return this;
    }

    public RequestBuilder addBody(String name, File file) {
        return addBody(name, file, APPLICATION_OCTET_STREAM, file.getName());
    }

    public RequestBuilder addBody(String name, File file, String mediaType) {
        return addBody(name, file, mediaType, file.getName());
    }

    public RequestBuilder addBody(String name, File file, String mediaType, String fileName) {
        FileBody part = FileBody.create(name, file, mediaType, fileName);
        this.bodies.add(part);
        return this;
    }

    public RequestBuilder addBody(String name, byte[] content) {
        return addBody(name, content, APPLICATION_OCTET_STREAM);
    }

    public RequestBuilder addBody(String name, byte[] content, String mediaType) {
        FileBody part = FileBody.create(name, content, mediaType);
        this.bodies.add(part);
        return this;
    }

    public RequestBuilder addBody(final String name, final RequestBody body) {
        AssertUtils.notNull(body, "body must not be null.");
        FileBody part = FileBody.create(name, body);
        this.bodies.add(part);
        return this;
    }

    public RequestBuilder addBody(final RequestBody body) {
        return addBody(null, body);
    }

    public RequestBuilder setBody(final RequestBody body) {
        this.rawBody = body;
        return this;
    }

    protected boolean hasParts() {
        return this.bodies.size() > 0;
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
            for (final FileBody part : bodies) {
                if (part.body != null) {
                    builder.addFormDataPart(part.name, part.fileName, part.body);
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
