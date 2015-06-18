package com.mcxiaoke.next.utils;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.mcxiaoke.next.core.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class BitmapUtils {
    public static final String TAG = BitmapUtils.class.getSimpleName();
    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean DEBUG_FLAG = false;
    private static final int DEFAULT_BLUR_RADIUS = 12;

    private BitmapUtils() {
    }

    /**
     * 旋转图片
     *
     * @param bmp   原始图片
     * @param angle 旋转的角度
     * @return
     */
    public static Bitmap rotate(Bitmap bmp, float angle) {
        Matrix matrixRotateLeft = new Matrix();
        matrixRotateLeft.setRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixRotateLeft, true);
    }

    /**
     * 按原比例缩放图片
     *
     * @param contentResolver
     * @param uri             图片的URI地址
     * @param maxWidth        缩放后的宽度
     * @param maxHeight       缩放后的高度
     * @return
     */
    public static Bitmap scale(ContentResolver contentResolver, Uri uri, int maxWidth, int maxHeight) {
        String tag = "SCALE";
        LogUtils.v(tag, "uri=" + uri.toString());
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);

            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;

            LogUtils.v(tag, "sourceWidth=" + sourceWidth + ", sourceHeight=" + sourceHeight);
            LogUtils.v(tag, "maxWidth=" + maxWidth + ", maxHeight=" + maxHeight);

            input.close();

            float rate = Math.max(sourceWidth / (float) maxWidth, sourceHeight / (float) maxHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            LogUtils.v(tag, "rate=" + rate + ", inSampleSize=" + options.inSampleSize);

            input = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();

            LogUtils.v(tag, "w0=" + w0 + ", h0=" + h0);


            float scaleWidth = maxWidth / (float) w0;
            float scaleHeight = maxHeight / (float) h0;
            float maxScale = Math.min(scaleWidth, scaleHeight);
            LogUtils.v(tag, "scaleWidth=" + scaleWidth + ", scaleHeight=" + scaleHeight);

            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1)
                matrix.postScale(maxScale, maxScale);

            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);


            input.close();
            // bitmap.recycle();

            return resizedBitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Drawable转换为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable == null)
            return null;

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 　为指定图片增加阴影
     *
     * @param map    　图片
     * @param radius 　阴影的半径
     * @return
     */
    public static Bitmap drawShadow(Bitmap map, int radius) {
        if (map == null)
            return null;

        BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY);
        shadowImage = shadowImage.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage);
        c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);
        return shadowImage;
    }

    /**
     * 获得圆角的bitmap
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int getExifOrientation(ContentResolver cr, Uri contentUri) {
        int returnValue = 0;
        String uriString = contentUri.toString();

        if (ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())) {
            // can post image
            String[] proj = {MediaStore.Images.Media.ORIENTATION};
            Cursor cursor = cr.query(contentUri, proj, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    returnValue = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                }
                cursor.close();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(contentUri.getScheme())) {
            returnValue = getExifOrientation(contentUri.getPath());
        } else if (uriString.startsWith("/")) {
            returnValue = getExifOrientation(contentUri.getPath());
        }
        return returnValue;
    }

    public static int getExifOrientation(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return 0;
        }
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileName);
        } catch (IOException ex) {
            Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    public static byte[] generateBitstream(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }

    public static Bitmap getReflectBitmap(Bitmap originalImage, float rate) {
        if (originalImage == null || originalImage.isRecycled())
            return null;
        //The gap we want between the reflection and the original image
        final int reflectionGap = 4;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int reflectHeight = Math.round(rate * height);


        //This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        //Create a Bitmap with the flip matrix applied to it.
        //We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectHeight, width, reflectHeight, matrix, false);

        //Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + reflectHeight, Config.ARGB_8888);

        //Create a new Canvas with the bitmap that's big enough for
        //the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        //Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        //Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        //Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        //Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        //Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        //Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        //Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width,
                bitmapWithReflection.getHeight() + reflectionGap, paint);

        if (!originalImage.isRecycled()) {
            originalImage.recycle();
        }

        return bitmapWithReflection;
    }

    public static Bitmap getSquareBitmap(Bitmap src) {
        return getSquareBitmap(src, 0.1f);
    }

    public static Bitmap getSquareBitmap(Bitmap src, float rate) {
        if (src == null || src.isRecycled())
            return null;
        Bitmap ret = src;
        int w = src.getWidth();
        int h = src.getHeight();
        int min = Math.min(w, h);
        int max = Math.max(w, h);
        float r = (float) (max - min) / (float) min;
        if (w != h && r > rate) {
            max = Math.round((1.0f + rate) * min);
            if (w > h) {
                ret = Bitmap.createBitmap(src, (w - max) / 2, 0, max, min);
            } else {
                ret = Bitmap.createBitmap(src, 0, (h - max) / 2, min, max);
            }
        }
        return ret;
    }

    public static Bitmap clipCircleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled())
            return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top = 0, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        width = width - 2;
        height = height - 2;
        if (width <= height) {
            roundPx = width / 2;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);


        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return output;
    }

    public static Bitmap getBluredBitmap(Bitmap sentBitmap) {
        return getBluredBitmap(sentBitmap, DEFAULT_BLUR_RADIUS);
    }

    public static Bitmap getBluredBitmap(Bitmap sentBitmap, int radius) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to addPart one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please addPart
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static File compress(String src,
                                String dest, int maxWidth, int quality) throws IOException {
        Bitmap bitmap = getCompressedBitmap(src, maxWidth);
        if (src == null || dest == null || bitmap == null) {
            return null;
        }
        if (DEBUG) {
            LogUtils.v(TAG, "compress() maxWidth=" + maxWidth + " quality=" + quality);
            LogUtils.v(TAG, "compress() src=" + src);
            LogUtils.v(TAG, "compress() dest=" + dest);
            LogUtils.v(TAG, "compress() bitmap=(" + bitmap.getWidth() + ","
                    + bitmap.getHeight() + ")");
        }
        FileOutputStream fos = null;
        try {
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            if (src.toLowerCase().lastIndexOf("png") != -1) {
                format = Bitmap.CompressFormat.PNG;
            }
            fos = new FileOutputStream(dest);
            bitmap.compress(format, quality, fos);
            bitmap.recycle();
            return new File(dest);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }


    private static Bitmap getCompressedBitmap(String path, int maxWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1;
        for (int w = options.outWidth; w > maxWidth * 2 - 10; w /= 2) {
            inSampleSize++;
        }
        if (DEBUG) {
            LogUtils.v(TAG, "getCompressedBitmap() original bitmap=(" + options.outWidth + ","
                    + options.outHeight + ")");
            LogUtils.v(TAG, "getCompressedBitmap() inSampleSize=" + inSampleSize);
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            if (DEBUG) {
                int length = bitmap.getRowBytes() * bitmap.getHeight();
                LogUtils.v(TAG, "getCompressedBitmap() decode bitmap size=(" + bw + "," + bh + ")");
                LogUtils.v(TAG, "getCompressedBitmap() decode bitmap length=(" + length / 1000 + "k)");
            }
            Matrix m = new Matrix();
            if (bw > maxWidth) {
                float scale = (float) maxWidth / (float) bw;
                m.postScale(scale, scale);
                if (DEBUG) {
                    LogUtils.v(TAG, "getCompressedBitmap() matrix scale=" + scale);
                }
            }
            int rotation = getExifOrientation(path);
            if (getExifOrientation(path) != 0) {
                m.postRotate(rotation);
            }
            if (DEBUG) {

                LogUtils.v(TAG, "getCompressedBitmap() matrix rotation=" + rotation);
            }
            Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, m, true);
            if (resultBitmap != bitmap) {
                bitmap.recycle();
            }
            if (DEBUG) {
                int sw = resultBitmap.getWidth();
                int sh = resultBitmap.getHeight();
                int length = resultBitmap.getRowBytes() * resultBitmap.getHeight();
                LogUtils.v(TAG, "getCompressedBitmap() final bitmap size=(" + sw + "," + sh + ")");
                LogUtils.v(TAG, "getCompressedBitmap() final bitmap length=(" + length / 1000 + "k)");
            }
            return resultBitmap;
        }
        return null;

    }


    public static Bitmap decodeImage(final ContentResolver resolver, final Uri uri,
                                     final int maxDim) {

        // Get original dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.v(TAG, "decodeImage() ex=" + e);
                e.printStackTrace();
            }
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }

        final int origWidth = options.outWidth;
        final int origHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        options.inScaled = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inDither = true;
        options.inPreferredConfig = Config.RGB_565;

        if (origWidth > maxDim || origHeight > maxDim) {
            int k = 1;
            int tmpHeight = origHeight, tmpWidth = origWidth;
            while ((tmpWidth / 2) >= maxDim || (tmpHeight / 2) >= maxDim) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                k *= 2;
            }
            options.inSampleSize = k;
        }

        Bitmap bitmap = null;
        try {
            is = resolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            if (DEBUG) {
                LogUtils.v(TAG, "decodeImage() ex=" + e);
                e.printStackTrace();
            }
        } finally {
            IOUtils.closeQuietly(is);
        }

        if (null != bitmap) {
            if (DEBUG) {
                LogUtils.v(TAG,
                        "decodeImage() " + bitmap.getWidth() + "x" + bitmap.getHeight());
            }
        }

        return bitmap;
    }

    public static Bitmap rotate(Bitmap original, final int angle) {
        if ((angle % 360) == 0) {
            return original;
        }

        final boolean dimensionsChanged = angle == 90 || angle == 270;
        final int oldWidth = original.getWidth();
        final int oldHeight = original.getHeight();
        final int newWidth = dimensionsChanged ? oldHeight : oldWidth;
        final int newHeight = dimensionsChanged ? oldWidth : oldHeight;

        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, original.getConfig());
        Canvas canvas = new Canvas(bitmap);

        Matrix matrix = new Matrix();
        matrix.preTranslate((newWidth - oldWidth) / 2f, (newHeight - oldHeight) / 2f);
        matrix.postRotate(angle, bitmap.getWidth() / 2f, bitmap.getHeight() / 2);
        canvas.drawBitmap(original, matrix, null);

        original.recycle();

        return bitmap;
    }

    public static Bitmap getImage(Context context, final Uri uri, int maxDimen) {
        final int size = maxDimen;
        Bitmap bitmap = decodeImage(context.getContentResolver(), uri, size);
        Bitmap rotatedBitmap = rotate(bitmap, getExifOrientation(context.getContentResolver(), uri));
        if (bitmap != rotatedBitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }

}