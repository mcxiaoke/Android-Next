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
package com.mcxiaoke.next.request;

import com.mcxiaoke.next.utils.LogUtils;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;

import java.util.List;
import java.util.Map;

public class OkRequest {
    private static final String TAG = "OkRequest";
    RequestBuilder builder;

    public OkRequest(final OkRequest source) {
        this.builder = source.builder;
    }

    public OkRequest(final RequestBuilder builder) {
        this.builder = builder;
    }

    public OkRequest(final HttpMethod method, String url) {
        this.builder = new RequestBuilder().method(method).url(url);
    }

    public RequestBuilder newBuilder() {
        return new RequestBuilder(this.builder);
    }

    public boolean isDebug() {
        return builder.debug;
    }

    public HttpMethod getMethod() {
        return builder.method;
    }

    public HttpUrl getUrl() {
        return builder.httpUrl.build();
    }

    public Headers getHeaders() {
        return builder.headers.build();
    }

    public Map<String, String> getForms() {
        return builder.forms;
    }

    public List<FileBody> getParts() {
        return builder.bodies;
    }

    public Request toOkRequest() {
        return builder.toOkRequest();
    }

    private void logHttpCurl(final OkRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append("http");
        builder.append(" -f");
        builder.append(" ").append(request.getMethod().name());
        builder.append(" ").append(request.getUrl());

        for (Map.Entry<String, String> entry : request.getForms().entrySet()) {
            builder.append(" ").append(entry.getKey()).append("=\"")
                    .append(entry.getValue()).append("\"");
        }

        for (FileBody part : request.getParts()) {
            builder.append(" ").append(part.name).append("@").append(part.fileName);
        }
        final Map<String, List<String>> headers = request.getHeaders().toMultimap();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.append(" ").append(entry.getKey()).append(":\"")
                    .append(entry.getValue().get(0)).append("\"");
        }

        LogUtils.i(TAG, "Curl Command: [ " + builder.toString() + " ]");

    }

}
