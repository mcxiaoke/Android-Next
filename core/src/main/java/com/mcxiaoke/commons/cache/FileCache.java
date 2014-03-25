package com.mcxiaoke.commons.cache;

import android.content.Context;
import com.mcxiaoke.commons.Charsets;
import com.mcxiaoke.commons.cache.naming.FileNameGenerator;
import com.mcxiaoke.commons.cache.naming.HashCodeFileNameGenerator;
import com.mcxiaoke.commons.utils.AndroidUtils;
import com.mcxiaoke.commons.utils.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 18:19
 */
public class FileCache implements IDiscCache {
    public static final String CACHE_DIR_NAME = "file_cache";
    public static final String ENCODING_UTF8 = Charsets.ENCODING_UTF_8;

    private Context mContext;
    private File mCacheDir;
    private FileNameGenerator mGenerator = new HashCodeFileNameGenerator();

    public FileCache(Context context) {
        mContext = context;
        File baseCacheDir;
        if (AndroidUtils.isMediaMounted()) {
            baseCacheDir = context.getExternalCacheDir();
        } else {
            baseCacheDir = context.getCacheDir();
        }
        mCacheDir = new File(baseCacheDir, CACHE_DIR_NAME);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    public FileCache(Context context, File cacheDir) {
        mContext = context;
        mCacheDir = cacheDir;
    }

    public void setFileNameGenerator(FileNameGenerator generator) {
        mGenerator = generator;
    }

    @Override
    public void put(String key, byte[] data) {
        IOUtils.writeBytes(getCacheFile(key), data);
    }

    @Override
    public void put(String key, InputStream stream) {
        IOUtils.writeStream(getCacheFile(key), stream);
    }

    @Override
    public void put(String key, String text) {
        IOUtils.writeString(getCacheFile(key), text);
    }

    @Override
    public String get(String key) {
        File file = getFile(key);
        if (file == null) {
            return null;
        }
        return IOUtils.readString(getCacheFile(key), ENCODING_UTF8);
    }

    @Override
    public File getFile(String key) {
        File file = getCacheFile(key);
        if (file == null || !file.exists()) {
            return null;
        }
        return file;
    }

    @Override
    public byte[] getBytes(String key) {
        File file = getFile(key);
        if (file == null) {
            return null;
        }
        try {
            return IOUtils.readBytes(new FileInputStream(file));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File remove(String key) {
        File file = getFile(key);
        if (file != null) {
            file.delete();
        }
        return file;
    }

    @Override
    public void clear() {

    }

    private File getCacheFile(String key) {
        String fileName = mGenerator.generate(key);
        return new File(mCacheDir, fileName);
    }
}
