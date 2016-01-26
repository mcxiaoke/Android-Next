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
import java.util.concurrent.*
import java.util.regex.Pattern

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:13
 */



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


