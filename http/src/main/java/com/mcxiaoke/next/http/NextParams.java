package com.mcxiaoke.next.http;

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
    public final Map<String, String> queries;
    public final Map<String, String> forms;
    public final List<BodyPart> parts;

    public NextParams() {
        queries = new HashMap<String, String>();
        forms = new HashMap<String, String>();
        parts = new ArrayList<BodyPart>();
    }

    public NextParams(final NextParams source) {
        queries = source.queries;
        forms = source.forms;
        parts = source.parts;
    }

    public NextParams copy(final NextParams source) {
        queries.putAll(source.queries);
        forms.putAll(source.forms);
        parts.addAll(source.parts);
        return this;
    }

    public NextParams removeForm(String key) {
        this.forms.remove(key);
        return this;
    }

    public NextParams removeQuery(String key) {
        this.queries.remove(key);
        return this;
    }

    public NextParams query(String key, String value) {
        this.queries.put(key, value);
        return this;
    }

    public NextParams queries(Map<String, String> queries) {
        for (Map.Entry<String, String> entry : queries.entrySet()) {
            query(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public NextParams form(String key, String value) {
        this.forms.put(key, value);
        return this;
    }

    public NextParams form(Map<String, String> forms) {
        for (Map.Entry<String, String> entry : forms.entrySet()) {
            form(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public NextParams file(String key, File file) {
        BodyPart part = BodyPart.create(key, file);
        return part(part);
    }

    public NextParams file(String key, File file, String contentType) {
        BodyPart part = BodyPart.create(key, file, contentType);
        return part(part);
    }

    public NextParams file(String key, File file, String contentType, String fileName) {
        BodyPart part = BodyPart.create(key, file, contentType, fileName);
        return part(part);
    }

    public NextParams file(String key, byte[] bytes) {
        BodyPart part = BodyPart.create(key, bytes);
        return part(part);
    }

    public NextParams file(String key, byte[] bytes, String contentType) {
        BodyPart part = BodyPart.create(key, bytes, contentType);
        return part(part);
    }

    NextParams part(final BodyPart part) {
        this.parts.add(part);
        return this;
    }

}
