package com.mcxiaoke.commons.cache;

import android.content.Context;
import com.mcxiaoke.commons.Charsets;
import com.mcxiaoke.commons.cache.naming.FileNameGenerator;
import com.mcxiaoke.commons.cache.naming.SimpleFileNameGenerator;
import com.mcxiaoke.commons.utils.AndroidUtils;
import com.mcxiaoke.commons.utils.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 18:19
 */
public class DiscCache implements IDiscCache {
    public static final String CACHE_DIR_NAME = "disc_cache_store";

    private Context mContext;
    private File mCacheDir;
    private FileNameGenerator mGenerator = new SimpleFileNameGenerator();
    private Charset mCharset = Charsets.UTF_8;

    public DiscCache(Context context) {
        mContext = context;
        setCacheDir(null);
    }

    public DiscCache(Context context, File cacheDir) {
        mContext = context;
        setCacheDir(cacheDir);
    }

    public void setCacheDir(File cacheDir) {
        mCacheDir = cacheDir;
        checkCacheDir();
    }

    public void setCharset(String charset) {
        mCharset = Charsets.toCharset(charset);
    }

    public void setCharset(Charset charset) {
        mCharset = Charsets.toCharset(charset);
    }

    public void setFileNameGenerator(FileNameGenerator generator) {
        mGenerator = generator;
    }

    @Override
    public void put(String key, byte[] data) {
        checkCacheDir();
        try {
            IOUtils.writeBytes(getFile(key), data);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void put(String key, InputStream stream) {
        checkCacheDir();
        try {
            IOUtils.writeStream(getFile(key), stream);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void put(String key, String text) {
        checkCacheDir();
        try {
            IOUtils.writeString(getFile(key), text);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void put(String key, Collection<?> collection) {
        checkCacheDir();
        try {
            IOUtils.writeList(collection, getFile(key), mCharset);
        } catch (IOException ignored) {
        }
    }

    @Override
    public String get(String key) {
        try {
            return IOUtils.readString(getFile(key), mCharset);
        } catch (IOException ignored) {
            return null;
        }
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
        try {
            return IOUtils.readBytes(file);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public List<String> getList(String key) {
        try {
            return IOUtils.readStringList(getFile(key), mCharset);
        } catch (IOException ignored) {
            return null;
        }
    }

    @Override
    public boolean remove(String key) {
        File file = getFile(key);
        return IOUtils.delete(file);
    }

    @Override
    public void clear() {
        IOUtils.delete(mCacheDir);
        checkCacheDir();
    }

    @Override
    public int trim(FileFilter filter) {
        File[] files = mCacheDir.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }
        int count = 0;
        for (File file : files) {
            if (filter.accept(file)) {
                IOUtils.delete(file);
            }
        }
        return count;
    }

    private void checkCacheDir() {
        if (mCacheDir == null) {
            File baseCacheDir;
            if (AndroidUtils.isMediaMounted()) {
                baseCacheDir = mContext.getExternalCacheDir();
            } else {
                baseCacheDir = mContext.getCacheDir();
            }
            mCacheDir = new File(baseCacheDir, CACHE_DIR_NAME);
        }
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
    }

    private File getCacheFile(String key) {
        String fileName = mGenerator.generate(key);
        return new File(mCacheDir, fileName);
    }

}
