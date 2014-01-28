package com.mcxiaoke.commons.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import com.mcxiaoke.commons.BuildConfig;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * User: mcxiaoke
 * Date: 13-7-5
 * Time: 下午4:38
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public final class IoUtils {
    public static final String TAG = IoUtils.class.getSimpleName();
    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String PREFIX_IMAGE = "IMG_";
    public static final String EXTENSION_JPEG = ".jpg";
    public static final String EXTENSION_PNG = ".png";
    public static final String FILENAME_NOMEDIA = ".nomedia";

    public static final DateFormat IMG_FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    /**
     * Regular expression for safe filenames: no spaces or metacharacters
     */
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");
    public static final String ENCODING_UTF8 = "UTF-8";

    private IoUtils() {
    }

    /**
     * 保存在 /sdcard/Pictures/xxx，用于普通的保存图片
     */
    public static File createPictureFile(String dirName) {
        String timeStamp = IMG_FILE_NAME_FORMAT.format(new Date());
        String imageFileName = PREFIX_IMAGE + timeStamp + EXTENSION_JPEG;
        return new File(getPicturesDir(dirName), imageFileName);
    }

    /**
     * 保存在 /sdcard/DCIM/xxx，用于拍照图片保存
     */
    public static File createMediaFile(String dirName) {
        String timeStamp = IMG_FILE_NAME_FORMAT.format(new Date());
        String imageFileName = PREFIX_IMAGE + timeStamp + EXTENSION_JPEG;
        return new File(getMediaDir(dirName), imageFileName);
    }

    public static File getPicturesDir(String dirName) {
        File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dirName);
        if (!picturesDir.exists()) {
            picturesDir.mkdirs();
        }
        return picturesDir;
    }

    public static File getMediaDir(String dirName) {
        File dcim = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        File mediaDir = new File(dcim, dirName);
        if (!mediaDir.exists()) {
            mediaDir.mkdirs();
        }
        return mediaDir;
    }

    public static File getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File cacheDir = context.getExternalCacheDir();
            File noMedia = new File(cacheDir, FILENAME_NOMEDIA);
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cacheDir;
        } else {
            return context.getCacheDir();
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (DEBUG) {
            LogUtils.v(TAG, "getRealPath() uri=" + uri + " isKitKat=" + isKitKat);
        }

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static void copyFile(File src, File dest) throws IOException {
        FileChannel srcChannel = new FileInputStream(src).getChannel();
        FileChannel destChannel = new FileOutputStream(dest).getChannel();
        srcChannel.transferTo(0, srcChannel.size(), destChannel);
        srcChannel.close();
        destChannel.close();
    }

    public static void copyStream(InputStream in, OutputStream out,
                                  int bufferSize) throws IOException {
        byte[] buf = new byte[bufferSize];
        int len = 0;
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Check if a filename is "safe" (no metacharacters or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe.  Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    /**
     * Read a text file into a String, optionally limiting the length.
     *
     * @param file     to read (will not seek, so things like /proc files are OK)
     * @param max      length (positive for head, negative of tail, 0 for no limit)
     * @param ellipsis to add of the file was truncated (can be null)
     * @return the contents of the file, possibly truncated
     * @throws java.io.IOException if something goes wrong reading the file
     */
    public static String readTextFile(File file, int max, String ellipsis) throws IOException {
        InputStream input = new FileInputStream(file);
        // wrapping a BufferedInputStream around it because when reading /proc with unbuffered
        // input stream, bytes read not equal to buffer size is not necessarily the correct
        // indication for EOF; but it is true for BufferedInputStream due to its implementation.
        BufferedInputStream bis = new BufferedInputStream(input);
        try {
            long size = file.length();
            if (max > 0 || (size > 0 && max == 0)) {  // "head" mode: read the first N bytes
                if (size > 0 && (max == 0 || size < max)) max = (int) size;
                byte[] data = new byte[max + 1];
                int length = bis.read(data);
                if (length <= 0) return "";
                if (length <= max) return new String(data, 0, length);
                if (ellipsis == null) return new String(data, 0, max);
                return new String(data, 0, max) + ellipsis;
            } else if (max < 0) {  // "tail" mode: keep the last N
                int len;
                boolean rolled = false;
                byte[] last = null;
                byte[] data = null;
                do {
                    if (last != null) rolled = true;
                    byte[] tmp = last;
                    last = data;
                    data = tmp;
                    if (data == null) data = new byte[-max];
                    len = bis.read(data);
                } while (len == data.length);

                if (last == null && len <= 0) return "";
                if (last == null) return new String(data, 0, len);
                if (len > 0) {
                    rolled = true;
                    System.arraycopy(last, len, last, 0, last.length - len);
                    System.arraycopy(data, 0, last, last.length - len, len);
                }
                if (ellipsis == null || !rolled) return new String(last);
                return ellipsis + new String(last);
            } else {  // "cat" mode: size unknown, read it all in streaming fashion
                ByteArrayOutputStream contents = new ByteArrayOutputStream();
                int len;
                byte[] data = new byte[1024];
                do {
                    len = bis.read(data);
                    if (len > 0) contents.write(data, 0, len);
                } while (len == data.length);
                return contents.toString();
            }
        } finally {
            bis.close();
            input.close();
        }
    }

    public static void forceClose(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void forceClose(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void forceClose(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void forceClose(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File writeListToFile(Context context, String fileName, List<String> strings) {
        if (fileName == null || strings == null || strings.isEmpty()) {
            return null;
        }
        String data = StringUtils.getPrintString(strings, "\n");
        return writeStringToFile(context, fileName, data);
    }

    public static List<String> readListFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            return readStreamAsList(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            forceClose(fis);
        }
        return null;
    }

    public static File writeStringToFile(Context context, String fileName, String string) {
        if (DEBUG) {
            LogUtils.v(TAG, "writeStringToFile() string=" + string);
        }
        if (fileName == null || string == null) {
            return null;
        }
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(string.getBytes(ENCODING_UTF8));
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            forceClose(fos);
        }
        return context.getFileStreamPath(fileName);
    }

    public static String readStringFromFile(Context context, String fileName) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
            return readStreamAsString(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            forceClose(fis);
        }
        return null;
    }

    public static String readStreamAsString(InputStream is) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder builder = new StringBuilder();
        try {
            isr = new InputStreamReader(is, ENCODING_UTF8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } finally {
            forceClose(isr);
            forceClose(br);
        }
        if (DEBUG) {
            LogUtils.v(TAG, "readStringFromFile() data=" + builder.toString());
        }
        return builder.toString();
    }

    public static List<String> readStreamAsList(InputStream is) throws IOException {
        InputStreamReader isr = null;
        BufferedReader br = null;
        List<String> strings = new ArrayList<String>();
        try {
            isr = new InputStreamReader(is, ENCODING_UTF8);
            br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                strings.add(line);
            }
        } finally {
            forceClose(isr);
            forceClose(br);
        }
        if (DEBUG) {
            LogUtils.v(TAG, "readStreamAsList() data=" + strings);
        }
        return strings;
    }
}
