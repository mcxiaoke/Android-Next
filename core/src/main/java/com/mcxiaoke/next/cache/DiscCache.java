package com.mcxiaoke.next.cache;

import android.content.Context;
import com.mcxiaoke.next.Charsets;
import com.mcxiaoke.next.io.NameGenerator;
import com.mcxiaoke.next.io.SafeFileNameGenerator;
import com.mcxiaoke.next.utils.AndroidUtils;
import com.mcxiaoke.next.utils.IOUtils;
import com.mcxiaoke.next.utils.LogUtils;

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

    /**
     * 指定使用内部存储，外部存储，还是自动选择
     */
    public static final int MODE_INTERNAL = 0;
    public static final int MODE_EXTERNAL = 1;
    public static final int MODE_AUTO = 2;
    private int mMode = MODE_AUTO;
    public static final String DIR_NAME_DEFAULT = ".disc";
    private static boolean sDebug;
    private Context mContext;
    private File mCacheDir;
    private String mCacheDirName;
    private NameGenerator mGenerator = new SafeFileNameGenerator();
    private Charset mCharset = Charsets.UTF_8;

    public DiscCache(Context context) {
        this(context, DIR_NAME_DEFAULT);
    }

    public DiscCache(Context context, String dirName) {
        this(context, dirName, MODE_AUTO);
    }

    public DiscCache(Context context, String dirName, int mode) {
        if (sDebug) {
            LogUtils.v(TAG, "DiscCache() cacheDirName=" + dirName);
        }
        mContext = context;
        setCacheDir(dirName, mode);
    }

    public static void setDebug(boolean debug) {
        LogUtils.v(TAG, "setDebug() debug=" + debug);
        DiscCache.sDebug = debug;
    }

    /**
     * 设置缓存文件夹的名字
     *
     * @param dirName Dir Name
     */
    public void setCacheDir(String dirName, int mode) {
        if (sDebug) {
            LogUtils.v(TAG, "setCacheDir() dirName=" + dirName + " mode=" + mode);
        }
        if (dirName == null) {
            mCacheDirName = DIR_NAME_DEFAULT;
        } else {
            mCacheDirName = dirName;
        }
        mMode = mode;
        checkCacheDir(true);
    }

    /**
     * 直接设置完整的缓存路径，调试用
     *
     * @param cacheDir Cache Dir
     */
    public void setDebugCacheDir(File cacheDir) {
        if (sDebug) {
            LogUtils.v(TAG, "setCacheDir() cacheDir=" + cacheDir);
        }
        mCacheDir = cacheDir;
        checkCacheDir(false);
    }

    public void setCharset(String charset) {
        if (sDebug) {
            LogUtils.v(TAG, "setCharset() charset=" + charset);
        }
        mCharset = Charsets.toCharset(charset);
    }

    public void setCharset(Charset charset) {
        if (sDebug) {
            LogUtils.v(TAG, "setCharset() charset=" + charset);
        }
        mCharset = Charsets.toCharset(charset);
    }

    public void setFileNameGenerator(NameGenerator generator) {
        mGenerator = generator;
    }

    @Override
    public void put(String key, byte[] data) {
        checkCacheDir(false);
        try {
            if (sDebug) {
                LogUtils.v(TAG, "put() bytes key=" + key);
            }
            IOUtils.writeBytes(getFile(key), data);
        } catch (IOException ignored) {
            if (sDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public void put(String key, InputStream stream) {
        checkCacheDir(false);
        try {
            if (sDebug) {
                LogUtils.v(TAG, "put() stream key=" + key);
            }
            IOUtils.writeStream(getFile(key), stream);
        } catch (IOException ignored) {
            if (sDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public void put(String key, String text) {
        checkCacheDir(false);
        try {
            if (sDebug) {
                LogUtils.v(TAG, "put() string key=" + key);
            }
            IOUtils.writeString(getFile(key), text);
        } catch (IOException ignored) {
            if (sDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
        }
    }

    @Override
    public String get(String key) {
        try {
            String value = IOUtils.readString(getFile(key), mCharset);
            if (sDebug) {
                LogUtils.v(TAG, "get() key=" + key + " value=" + value);
            }
            return value;
        } catch (IOException ignored) {
            if (sDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
            return null;
        }
    }

    @Override
    public File getFile(String key) {
        File file = getCacheFile(key);
        if (sDebug) {
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
            if (sDebug) {
                ignored.printStackTrace();
                LogUtils.e(TAG, "put() key=" + key + " error=" + ignored);
            }
            return null;
        }
    }

    @Override
    public boolean remove(String key) {
        File file = getFile(key);
        if (sDebug) {
            LogUtils.v(TAG, "remove() key=" + key + " file=" + file);
        }
        return IOUtils.delete(file);
    }

    @Override
    public void clear() {
        if (sDebug) {
            LogUtils.v(TAG, "clear()");
        }
        IOUtils.delete(mCacheDir);
        checkCacheDir(false);
    }

    @Override
    public int delete(FileFilter filter) {
        File cacheDir = getCacheDir();
        File[] files = cacheDir.listFiles();
        if (files == null || files.length == 0) {
            return 0;
        }
        int count = 0;
        for (File file : files) {
            if (filter.accept(file)) {
                if (sDebug) {
                    LogUtils.v(TAG, "trim() file=" + file.getPath());
                }
                // no recursion
                file.delete();
            }
        }
        if (sDebug) {
            LogUtils.v(TAG, "trim() count=" + count);
        }
        return count;
    }

    @Override
    public File getCacheDir() {
        checkCacheDir(false);
        return mCacheDir;
    }

    public void setCacheDir(String dirName) {
        setCacheDir(dirName, MODE_AUTO);
    }

    @Override
    public long getCacheSize() {
        return IOUtils.sizeOf(getCacheDir());
    }

    private void checkCacheDir(boolean forceSet) {
        if (mCacheDir == null || forceSet) {
            mCacheDir = new File(getBaseCacheDir(), mCacheDirName);
        }
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }
        if (sDebug) {
            LogUtils.v(TAG, "checkCacheDir() cacheDir=" + mCacheDir + " forceSet=" + forceSet);
        }
    }

    private File getCacheFile(String key) {
        String fileName = mGenerator.generate(key);
        if (sDebug) {
            LogUtils.v(TAG, "getCacheFile() key=" + key + " fileName=" + fileName);
        }
        return new File(mCacheDir, fileName);
    }

    private File getBaseCacheDir() {
        File baseCacheDir;
        switch (mMode) {
            case MODE_INTERNAL: {
                baseCacheDir = mContext.getCacheDir();
            }
            break;
            case MODE_EXTERNAL: {
                baseCacheDir = mContext.getExternalCacheDir();
            }
            break;
            case MODE_AUTO:
            default: {
                if (AndroidUtils.isMediaMounted()) {
                    baseCacheDir = mContext.getExternalCacheDir();
                } else {
                    baseCacheDir = mContext.getCacheDir();
                }
                break;
            }
        }
        return baseCacheDir;
    }

}
