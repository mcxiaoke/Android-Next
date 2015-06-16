package com.mcxiaoke.next.utils;

import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 10:47
 */
public class ImageUtils {

    public static final String PREFIX_IMAGE = "IMG_";
    public static final String EXTENSION_JPEG = ".jpg";
    public static final String EXTENSION_PNG = ".png";

    public static final DateFormat IMG_FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);


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
}
