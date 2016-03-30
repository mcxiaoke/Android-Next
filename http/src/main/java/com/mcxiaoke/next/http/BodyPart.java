package com.mcxiaoke.next.http;

import com.mcxiaoke.next.utils.AssertUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.io.File;
import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/2
 * Time: 13:15
 */
public class BodyPart {


    public final String name;
    public final String contentType;
    public final File file;
    public final byte[] bytes;
    public final String fileName;
    public final long length;
    private RequestBody body;

    private BodyPart(String name, File file, String mimeType, String fileName) {
        AssertUtils.notNull(name, "name can not be null.");
        AssertUtils.notNull(file, "file can not be null.");
        AssertUtils.notNull(mimeType, "mimeType can not be null.");
        this.name = name;
        this.contentType = mimeType;
        this.file = file;
        this.bytes = null;
        this.length = file.length();
        this.fileName = fileName == null ? HttpConsts.DEFAULT_NAME : fileName;
        this.body = RequestBody.create(MediaType.parse(contentType), file);
    }

    private BodyPart(String name, byte[] bytes, String mimeType, String fileName) {
        AssertUtils.notNull(name, "name can not be null.");
        AssertUtils.notNull(bytes, "bytes can not be null.");
        AssertUtils.notNull(mimeType, "mimeType can not be null.");
        this.name = name;
        this.contentType = mimeType;
        this.file = null;
        this.bytes = bytes;
        this.length = bytes.length;
        this.fileName = fileName == null ? HttpConsts.DEFAULT_NAME : fileName;
        this.body = RequestBody.create(MediaType.parse(contentType), bytes);
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public RequestBody getBody() throws IOException {
        return body;
    }

    public static BodyPart create(String name, File file,
                                  String mimeType, String fileName) {
        return new BodyPart(name, file, mimeType, fileName);
    }

    public static BodyPart create(String name, byte[] bytes,
                                  String mimeType, final String fileName) {
        return new BodyPart(name, bytes, mimeType, fileName);
    }

    @Override
    public String toString() {
        return "BodyPart{" +
                "name='" + name + '\'' +
                ", contentType='" + contentType + '\'' +
                ", file=" + file +
                ", fileName='" + fileName + '\'' +
                ", length=" + length +
                '}';
    }
}
