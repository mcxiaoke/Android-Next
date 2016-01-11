package com.mcxiaoke.next.http.transformer;

import com.mcxiaoke.next.http.NextResponse;

import java.io.File;
import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 14:46
 */
public class FileTransformer implements HttpTransformer<File> {

    private File file;

    public FileTransformer(final File file) {
        this.file = file;
    }


    @Override
    public File transform(final NextResponse response) throws IOException {
        boolean saved = response.writeTo(file);
        if (saved) {
            return file;
        }
        throw new IOException("can not save file: " + file);
    }
}
