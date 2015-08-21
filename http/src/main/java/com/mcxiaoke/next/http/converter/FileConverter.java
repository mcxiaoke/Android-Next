package com.mcxiaoke.next.http.converter;

import com.mcxiaoke.next.http.NextResponse;

import java.io.File;
import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 14:46
 */
public class FileConverter implements ResponseConverter<File> {

    private File file;

    public FileConverter(final File file) {
        this.file = file;
    }


    @Override
    public File convert(final NextResponse response) throws IOException {
        boolean saved = response.writeTo(file);
        if (saved) {
            return file;
        }
        throw new IOException("can not save file: " + file);
    }
}
