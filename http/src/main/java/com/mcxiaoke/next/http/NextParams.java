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

    public NextParams header(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        this.headers.put(key, value);
        return this;
    }

    public NextParams headers(Map<String, String> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public NextParams query(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        this.queries.put(key, value);
        return this;
    }

    public NextParams queries(Map<String, String> queries) {
        if (queries != null) {
            this.queries.putAll(queries);
        }
        return this;
    }

    public NextParams form(String key, String value) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        this.forms.put(key, value);
        return this;
    }

    public NextParams forms(Map<String, String> forms) {
        if (forms != null) {
            this.forms.putAll(forms);
        }
        return this;
    }

    public NextParams file(String key, File file) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        BodyPart part = BodyPart.create(key, file);
        return part(part);
    }

    public NextParams file(String key, File file, String contentType) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        BodyPart part = BodyPart.create(key, file, contentType);
        return part(part);
    }

    public NextParams file(String key, File file, String contentType, String fileName) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        BodyPart part = BodyPart.create(key, file, contentType, fileName);
        return part(part);
    }

    public NextParams file(String key, byte[] bytes) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        BodyPart part = BodyPart.create(key, bytes);
        return part(part);

    }

    public NextParams file(String key, byte[] bytes, String contentType) {
        AssertUtils.notEmpty(key, "key must not be null or empty.");
        BodyPart part = BodyPart.create(key, bytes, contentType);
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
                "queries=" + StringUtils.toString(queries) +
                ", forms=" + StringUtils.toString(forms) +
                ", headers=" + StringUtils.toString(headers) +
                ", parts=" + StringUtils.toString(parts) +
                '}';
    }
}
