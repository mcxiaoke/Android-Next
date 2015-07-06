package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.AssertUtils;
import com.mcxiaoke.next.utils.StringUtils;

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
public final class NextParams {
    final Map<String, String> headers;
    final Map<String, String> queries;
    final Map<String, String> forms;
    final List<BodyPart> parts;

    public NextParams() {
        headers = new HashMap<String, String>();
        queries = new HashMap<String, String>();
        forms = new HashMap<String, String>();
        parts = new ArrayList<BodyPart>();
    }

    public NextParams(final NextParams source) {
        headers = new HashMap<String, String>(source.headers);
        queries = new HashMap<String, String>(source.queries);
        forms = new HashMap<String, String>(source.forms);
        parts = new ArrayList<BodyPart>(source.parts);
    }

    String getQuery(final String key) {
        return this.queries.get(key);
    }

    String getForm(final String key) {
        return this.forms.get(key);
    }

    String getHeader(final String key) {
        return this.headers.get(key);
    }

    BodyPart getPart(final String key) {
        for (final BodyPart part : parts) {
            if (part.getName().equals(key)) {
                return part;
            }
        }
        return null;
    }

    boolean hasQuery(final String key) {
        return this.queries.containsKey(key);
    }

    boolean hasForm(final String key) {
        return this.forms.containsKey(key);
    }

    boolean hasHeader(final String key) {
        return this.headers.containsKey(key);
    }

    boolean hasPart(final String key) {
        return getPart(key) != null;
    }

    int queriesSize() {
        return queries.size();
    }

    int formsSize() {
        return forms.size();
    }

    int headersSize() {
        return headers.size();
    }

    int partsSize() {
        return parts.size();
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
            this.queries.put(key, value);
        }
        return this;
    }

    public NextParams queries(Map<String, String> queries) {
        if (queries != null) {
            for (final Map.Entry<String, String> entry : queries.entrySet()) {
                query(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public NextParams form(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.forms.put(key, value);
        }
        return this;
    }

    public NextParams forms(Map<String, String> forms) {
        if (forms != null) {
            for (final Map.Entry<String, String> entry : forms.entrySet()) {
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

    public Map<String, String> forms() {
        return forms;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<BodyPart> parts() {
        return parts;
    }

    public Map<String, String> queries() {
        return queries;
    }

    @Override
    public String toString() {
        return "Params{" +
                "forms:[" + StringUtils.toString(forms) +
                "], parts:[" + StringUtils.toString(parts) +
                "], queries:[" + StringUtils.toString(queries) +
                "], headers:[" + StringUtils.toString(headers) +
                "]}";
    }
}
