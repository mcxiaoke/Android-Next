package com.mcxiaoke.commons.cache;

import android.content.Context;
import com.mcxiaoke.commons.Charsets;
import com.mcxiaoke.commons.cache.naming.FileNameGenerator;
import com.mcxiaoke.commons.cache.naming.SimpleFileNameGenerator;
import com.mcxiaoke.commons.utils.AndroidUtils;
import com.mcxiaoke.commons.utils.IOUtils;
import com.mcxiaoke.commons.utils.LogUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * User: mcxiaoke
 * Date: 14-3-21
 * Time: 18:19
 */
public class DiscCache implements IDiscCache {
    public static final String TAG = DiscCache.class.getSimpleName();
    public static final String DIR_NAME_DEFAULT = "next_disc_cache";

    private Context mContext;
    private File mCacheDir;
    private String mCacheDirName;
    private FileNameGenerator mGenerator = new SimpleFileNameGenerator();
    private Charset mCharset = Charsets.UTF_8;
    private boolean mDebug;

    public DiscCache(Context context) {
        this(context, DIR_NAME_DEFAULT);
    }

    public DiscCache(Context context, String dirName) {
        if (mDebug) {
            LogUtils.v(TAG, "DiscCache() cacheDirName=" + dirName);
        }
        mContext = context;
        setCacheDir(dirName);
    }

    public void setDebug(boolean debug) {
        LogUtils.v(TAG, "setDebug() debug=" + debug);
        mDebug = debug;
    }

    /**
     * 设置缓存文件夹的名字
     *
     * @param dirName Dir Name
     */
    public void setCacheDir(String dirName) {
        if (mDebug) {
            LogUtils.v(TAG, "setCacheDir() dirName=" + dirName);
        }
        if (dirName == null) {
            mCacheDirName = DIR_NAME_DEFAULT;
        } else {
            mCacheDirName = dirName;
        }
        checkCacheDir(true);
    }

    /**
     * 直接设置完整的缓存路径
     *
     * @param cacheDir Cache Dir
     */
    public void setCacheDir(File cacheDir) {
        if (mDebug) {
            LogUtils.v(TAG, "setCacheDir() cacheDir=" + cacheDir);
        }
        mCacheDir = cacheDir;
        checkCacheDir(false);
    }

    @Override
    public File getCacheDir() {
        checkCacheDir(false);
        return mCacheDir;
    }

    public void setCharset(String charset) {
        if (mDebug) {
            LogUtils.v(TAG, "setCharset() charset=" + charset);
        }
        mCharset = Charsets.toCharset(charset);
    }

    public void setCharset(Charset charset) {
        if (mDebug) {
            LogUtils.v(TAG, "setCharset() charset=" + charset);
        }
        mCharset = Charsets.toCharset(charset);
    }

    public void setFileNameGenerator(FileNameGenerator generator) {
        mGenerator = generator;
    }

    @Override
    public void put(String key, byte[] data) {
        checkCacheDir(false);
        try {
            if (mDebug) {
                LogUtils.v(TAG, "put() bytes key=" + key);
            }
            IOUtils.writeBytes(getFile(key), data);
        } catch (IOException ignored) {
            if (mDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public void put(String key, InputStream stream) {
        checkCacheDir(false);
        try {
            if (mDebug) {
                LogUtils.v(TAG, "put() stream key=" + key);
            }
            IOUtils.writeStream(getFile(key), stream);
        } catch (IOException ignored) {
            if (mDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public void put(String key, String text) {
        checkCacheDir(false);
        try {
            if (mDebug) {
                LogUtils.v(TAG, "put() string key=" + key);
            }
            IOUtils.writeString(getFile(key), text);
        } catch (IOException ignored) {
            if (mDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public String get(String key) {
        try {
            String value = IOUtils.readString(getFile(key), mCharset);
            if (mDebug) {
                LogUtils.v(TAG, "get() key=" + key + " value=" + value);
            }
            return value;
        } catch (IOException ignored) {
            if (mDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
            return null;
        }
    }

    @Override
    public File getFile(String key) {
        File file = getCacheFile(key);
        if (mDebug) {
            LogUtils.v(TAG, "getFile() key=" + key + " file=" + file);
        }
        return file;
    }

    @Override
    public byte[] getBytes(String key) {
        File file = getFile(key);
        try {
            return IOUtils.readBytes(file);
        } catch (IOException ignored) {
            if (mDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
            return null;
        }
    }

    @Override
    public boolean remove(String key) {
        File file = getFile(key);
        if (mDebug) {
            LogUtils.v(TAG, "remove() key=" + key + " file=" + file);
        }
        return IOUtils.delete(file);
    }

    @Override
    public void clear() {
        if (mDebug) {
            LogUtils.v(TAG, "clear()");
        }
        IOUtils.delete(mCacheDir);
        checkCacheDir(false);
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
        if (mDebug) {
            LogUtils.v(TAG, "trim() count=" + count);
        }
        return count;
    }

    private void checkCacheDir(boolean forceSet) {
        if (mCacheDir == null || forceSet) {
            File baseCacheDir;
            if (AndroidUtils.isMediaMounted()) {
                baseCacheDir = mContext.getExternalCacheDir();
            } else {
                baseCacheDir = mContext.getCacheDir();
            }
            mCacheDir = new File(baseCacheDir, mCacheDirName);
        }
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
        if (mDebug) {
            LogUtils.v(TAG, "checkCacheDir() cacheDir=" + mCacheDir + " forceSet=" + forceSet);
        }
    }

    private File getCacheFile(String key) {
        String fileName = mGenerator.generate(key);
        if (mDebug) {
            LogUtils.v(TAG, "getCacheFile() key=" + key + " fileName=" + fileName);
        }
        return new File(mCacheDir, fileName);
    }

}
