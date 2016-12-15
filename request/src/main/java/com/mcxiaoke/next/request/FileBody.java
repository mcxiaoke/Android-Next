package com.mcxiaoke.next.request;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 16/12/14
 * Time: 12:10
 */
class FileBody {
    public final String name;
    public final RequestBody body;
    public final String fileName;

    FileBody(String name, final RequestBody body, final String fileName) {
        this.name = name;
        this.fileName = null;
        this.body = body;
    }

    FileBody(String name, File file, String mediaType, String fileName) {
        this(name, RequestBody.create(MediaType.parse(mediaType), file), fileName);
    }

    FileBody(String name, byte[] content, String mediaType, String fileName) {
        this(name, RequestBody.create(MediaType.parse(mediaType), content), fileName);
    }

    public static FileBody create(String name, RequestBody body) {
        return new FileBody(name, body, null);
    }

    public static FileBody create(String name, RequestBody body,
                                  final String fileName) {
        return new FileBody(name, body, fileName);
    }

    public static FileBody create(String name, File file,
                                  String mediaType) {
        return new FileBody(name, file, mediaType, null);
    }

    public static FileBody create(String name, File file,
                                  String mediaType, String fileName) {
        return new FileBody(name, file, mediaType, fileName);
    }

    public static FileBody create(String name, byte[] content,
                                  String mediaType) {
        return new FileBody(name, content, mediaType, null);
    }

    @Override
    public String toString() {
        return "FileBody{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
