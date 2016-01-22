package com.mcxiaoke.next.kotlin

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.mcxiaoke.next.kotlin.ext.CounterThreadFactory
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:13
 */

fun <T : Any> notNull(obj: T?, message: String? = "argument is null") {
    if (obj == null) {
        throw IllegalArgumentException(message)
    }
}

fun notEmpty(obj: String?, message: String? = "argument is empty") {
    if (obj == null || obj.length == 0) {
        throw IllegalArgumentException(message)
    }
}

fun isTrue(condition: Boolean, message: String? = "argument is false") {
    if (condition) {
        throw IllegalArgumentException(message)
    }
}

enum class NetworkType {
    WIFI, MOBILE, OTHER, NONE
}

object Const {

    val ENCODING_ISO_8859_1 = "ISO-8859-1"
    val ENCODING_US_ASCII = "US-ASCII"
    val ENCODING_UTF_16 = "UTF-16"
    val ENCODING_UTF_16BE = "UTF-16BE"
    val ENCODING_UTF_16LE = "UTF-16LE"
    val ENCODING_UTF_8 = "UTF-8"
    val CHARSET_ISO_8859_1 = Charset.forName("ISO-8859-1")
    val CHARSET_US_ASCII = Charset.forName("US-ASCII")
    val CHARSET_UTF_16 = Charset.forName("UTF-16")
    val CHARSET_UTF_16BE = Charset.forName("UTF-16BE")
    val CHARSET_UTF_16LE = Charset.forName("UTF-16LE")
    val CHARSET_UTF_8 = Charset.forName("UTF-8")

    val FILENAME_NOMEDIA = ".nomedia"

    val HEAP_SIZE_LARGE = 48 * 1024 * 1024
    val SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+")

    val HEX_DIGITS = "0123456789ABCDEF".toCharArray()

    object Size {
        val ONE_KB: Long = 1024
        val ONE_KB_BI = BigInteger.valueOf(ONE_KB)
        val ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI)
        val ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI)
        val ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI)
        val ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI)
        val ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI)
        val ONE_MB = ONE_KB * ONE_KB
        val ONE_GB = ONE_KB * ONE_MB
        val ONE_TB = ONE_KB * ONE_GB
        val ONE_PB = ONE_KB * ONE_TB
        val ONE_EB = ONE_KB * ONE_PB
        val ONE_ZB = BigInteger.valueOf(ONE_KB).multiply(BigInteger.valueOf(ONE_EB))
        val ONE_YB = ONE_KB_BI.multiply(ONE_ZB)
    }


}

object Android {

    fun Context.dpToPx(dp: Int): Int {
        return (dp * this.resources.displayMetrics.density + 0.5).toInt()
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / this.resources.displayMetrics.density + 0.5).toInt()
    }

    fun Float.pxToDp(): Int {
        val metrics = Resources.getSystem().displayMetrics
        val dp = this / (metrics.densityDpi / 160f)
        return Math.round(dp)
    }

    fun Float.dpToPx(): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = this * (metrics.densityDpi / 160f)
        return Math.round(px)
    }

    val isLargeHeap: Boolean
        get() = Runtime.getRuntime().maxMemory() > Const.HEAP_SIZE_LARGE


    fun noSdcard(): Boolean {
        return Environment.MEDIA_MOUNTED != Environment.getExternalStorageState()
    }

    /**
     * check if free size of SDCARD and CACHE dir is OK

     * @param needSize how much space should release at least
     * *
     * @return true if has enough space
     */
    fun noFreeSpace(needSize: Long): Boolean {
        val freeSpace = freeSpace()
        return freeSpace < needSize * 3
    }

    fun freeSpace(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    fun getBatteryLevel(batteryIntent: Intent): Float {
        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        return level / scale.toFloat()
    }

    fun getBatteryInfo(batteryIntent: Intent): String {
        val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
        val chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC

        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val batteryPct = level / scale.toFloat()
        return "Battery Info: isCharging=$isCharging usbCharge=$usbCharge  acCharge=$acCharge  batteryPct=$batteryPct"
    }


    val isMediaMounted: Boolean
        get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    fun hasIceCreamSandwich(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
    }

    val isPreIceCreamSandwich: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH

    fun hasJellyBean(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
    }

    fun hasKitkat(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    }

    val isPreLollipop: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP

    fun hasLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }


    fun newCachedThreadPool(name: String): ThreadPoolExecutor {
        return ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                SynchronousQueue<Runnable>(),
                CounterThreadFactory(name))
    }

    fun newFixedThreadPool(name: String, nThreads: Int): ThreadPoolExecutor {
        return ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue<Runnable>(),
                CounterThreadFactory(name))
    }

    fun newSingleThreadExecutor(name: String): ThreadPoolExecutor {
        return newFixedThreadPool(name, 1)
    }
}


