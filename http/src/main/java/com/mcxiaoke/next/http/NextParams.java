package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: mcxiaoke
 * Date: 14-2-8
 * Time: 11:22
 */
public class NextParams {
    final Map<String, String> headers;
    final List<KeyValue> queries;
    final List<KeyValue> forms;
    final List<BodyPart> parts;

    public NextParams() {
        headers = new ConcurrentHashMap<>();
        queries = new ArrayList<>();
        forms = new ArrayList<>();
        parts = new ArrayList<>();
    }

    // internal use
    NextParams(final NextParams source) {
        headers = source.headers;
        queries = source.queries;
        forms = source.forms;
        parts = source.parts;
    }

    public NextParams header(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.headers.put(key, value);
        }
        return this;
    }

    public NextParams headers(Map<String, String> headers) {
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public NextParams query(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.queries.add(KeyValue.of(key, value));
        }
        return this;
    }

    public NextParams queries(List<KeyValue> queries) {
        if (queries != null) {
            for (final KeyValue entry : queries) {
                query(entry.first, entry.second);
            }
        }
        return this;
    }

    public NextParams queries(Map<String, String> queries) {
        if (queries != null) {
            for (final Entry<String, String> entry : queries.entrySet()) {
                query(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public NextParams form(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.forms.add(KeyValue.of(key, value));
        }
        return this;
    }

    public NextParams forms(List<KeyValue> forms) {
        if (forms != null) {
            for (final KeyValue entry : forms) {
                form(entry.first, entry.second);
            }
        }
        return this;
    }

    public NextParams forms(Map<String, String> forms) {
        if (forms != null) {
            for (final Entry<String, String> entry : forms.entrySet()) {
                form(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public NextParams file(String key, File file) {
        return file(key, file, HttpConsts.APPLICATION_OCTET_STREAM, file.getName());
    }

    public NextParams file(String key, File file, String contentType) {
        return file(key, file, contentType, file.getName());
    }

    public NextParams file(String key, File file, String contentType, String fileName) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        AssertUtils.notNull(file, "file must not be null.");
        BodyPart part = BodyPart.create(key, file, contentType, fileName);
        return part(part);
    }

    public NextParams file(String key, byte[] bytes) {
        return file(key, bytes, HttpConsts.APPLICATION_OCTET_STREAM);

    }

    public NextParams file(String key, byte[] bytes, String contentType) {
        return file(key, bytes, contentType, null);
    }

    public NextParams file(String key, byte[] bytes, String contentType, String fileName) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        AssertUtils.notNull(bytes, "bytes must not be null.");
        BodyPart part = BodyPart.create(key, bytes, contentType, fileName);
        return part(part);
    }

    public NextParams part(final BodyPart part) {
        AssertUtils.notNull(part, "part must not be null.");
        this.parts.add(part);
        return this;
    }

    public List<KeyValue> forms() {
        return forms;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<BodyPart> parts() {
        return parts;
    }

    public List<KeyValue> queries() {
        return queries;
    }

    @Override
    public String toString() {
        return "{" +
                "queries:[" + StringUtils.toString(queries) +
                "], forms:[" + StringUtils.toString(forms) +
                "], parts:[" + StringUtils.toString(parts) +
                "], headers:[" + StringUtils.toString(headers) +
                "]}";
    }
}
