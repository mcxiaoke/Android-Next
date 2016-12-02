package com.mcxiaoke.next.request;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;

/**
 * User: mcxiaoke
 * Date: 16/12/14
 * Time: 12:10
 */
public class FileBody {

    public final String name;
    private RequestBody body;
    public final String fileName;

    private FileBody(String name, final RequestBody body, final String fileName) {
        this.name = name;
        this.fileName = null;
        this.body = body;
    }

    private FileBody(String name, File file, String contentType, String fileName) {
        this(name, RequestBody.create(MediaType.parse(contentType), file), fileName);
    }

    private FileBody(String name, byte[] content, String contentType, String fileName) {
        this(name, RequestBody.create(MediaType.parse(contentType), content), fileName);
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public RequestBody getBody() {
        return body;
    }

    public static FileBody create(String name, RequestBody body, final String fileName) {
        return new FileBody(name, body, fileName);
    }

    public static FileBody create(String name, File file,
                                  String contentType, String fileName) {
        return new FileBody(name, file, contentType, fileName);
    }

    public static FileBody create(String name, byte[] bytes,
                                  String contentType, final String fileName) {
        return new FileBody(name, bytes, contentType, fileName);
    }

    @Override
    public String toString() {
        return "BodyPart{" +
                "name='" + name + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
