package com.mcxiaoke.next.kotlin.ext

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.mcxiaoke.next.kotlin.Const
import com.mcxiaoke.next.kotlin.HASH
import com.mcxiaoke.next.kotlin.HEX
import com.mcxiaoke.next.kotlin.NetworkType
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:09
 */


fun Context.hideSoftKeyboard(editText: EditText) {
    val imm = this.getSystemService(
            Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(editText.windowToken, 0)
}

fun Context.showSoftKeyboard(editText: EditText) {
    if (editText.requestFocus()) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Context.toggleSoftInput(view: View) {
    if (view.requestFocus()) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, 0)
    }
}

inline fun <reified T : Context> Context.getIntent(): Intent =
        Intent(this, T::class.java)

inline fun <reified T : Context> Context.getIntent(flags: Int): Intent {
    val intent = getIntent<T>()
    intent.setFlags(flags)
    return intent
}

inline fun <reified T : Context> Context.getIntent(extras: Bundle): Intent =
        getIntent<T>(0, extras)

inline fun <reified T : Context> Context.getIntent(flags: Int, extras: Bundle): Intent {
    val intent = getIntent<T>(flags)
    intent.putExtras(extras)
    return intent
}

fun Context.inflateLayout(layoutResId: Int): View =
        inflateView(this, layoutResId, null, false)

fun Context.inflateLayout(layoutResId: Int, parent: ViewGroup): View =
        inflateLayout(layoutResId, parent, true)

fun Context.inflateLayout(layoutResId: Int, parent: ViewGroup, attachToRoot: Boolean): View =
        inflateView(this, layoutResId, parent, attachToRoot)

private fun inflateView(context: Context, layoutResId: Int, parent: ViewGroup?,
                        attachToRoot: Boolean): View =
        LayoutInflater.from(context).inflate(layoutResId, parent, attachToRoot)

fun Context.cacheDir(): File {
    if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val cacheDir = this.externalCacheDir
        val noMedia = File(cacheDir, Const.FILENAME_NOMEDIA)
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return cacheDir
    } else {
        return this.cacheDir
    }
}

internal fun Context.getMediaDataColumn(uri: Uri, selection: String?,
                                        selectionArgs: Array<String>?): String {

    var cursor: Cursor? = null
    val column = MediaStore.MediaColumns.DATA
    try {
        cursor = this.contentResolver.query(uri, arrayOf(column),
                selection, selectionArgs,
                null)
        cursor.moveToFirst()
        return cursor.getString(cursor.getColumnIndexOrThrow(column))
    } finally {
        cursor?.close()
    }
}

/**
 * Get a file path from a Uri. This will get the the path for Storage Access
 * Framework Documents, as well as the _data field for the MediaStore and
 * other file-based ContentProviders.

 * @param context The context.
 * *
 * @param uri     The Uri to query.
 */
@SuppressLint("NewApi")
fun Context.getPath(uri: Uri): String? {

    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
        // ExternalStorageProvider
        if (uri.isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().path + "/" + split[1]
            }
        } else if (uri.isDownloadsDocument()) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/_downloads"), java.lang.Long.valueOf(id)!!)

            return getMediaDataColumn(contentUri, null, null)
        } else if (uri.isMediaDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            if (contentUri != null) {
                return getMediaDataColumn(contentUri, "_id=?", arrayOf(split[1]))
            }
        }// MediaProvider
        // DownloadsProvider
    } else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        if (uri.isGooglePhotosUri())
            return uri.lastPathSegment

        return getMediaDataColumn(uri, null, null)
    } else if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }// File
    // MediaStore (and general)

    return uri.path
}


fun Context.toast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
}

fun Context.toast(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.longToast(resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

fun Context.longToast(text: CharSequence) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.hasCamera(): Boolean {
    val pm = this.packageManager
    return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
            || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
}

fun Context.mediaScan(uri: Uri) {
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.setData(uri)
    this.sendBroadcast(intent)
}

// another media scan way
fun Context.addToMediaStore(file: File) {
    val path = arrayOf(file.path)
    MediaScannerConnection.scanFile(this, path, null, null)
}

fun Context.getBatteryStatus(): Intent {
    val appContext = this.applicationContext
    return appContext.registerReceiver(null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED))
}

fun Context.getResourceValue(resId: Int): Int {
    val value = TypedValue()
    this.resources.getValue(resId, value, true)
    return TypedValue.complexToFloat(value.data).toInt()
}


fun Context.getPackageSignature(): Signature? {
    val pm = this.packageManager
    var info: PackageInfo? = null
    try {
        info = pm.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
    } catch (ignored: Exception) {
    }
    return info?.signatures?.get(0)
}

fun Context.getSignature(context: Context): String {
    val signature = this.getPackageSignature()
    if (signature != null) {
        try {
            return HASH.sha1(signature.toByteArray())
        } catch (e: Exception) {
            return ""
        }
    }
    return ""
}

fun Context.dumpSignature(): String {
    val signature = this.getPackageSignature() ?: return ""
    val builder = StringBuilder()
    try {
        val signatureBytes = signature.toByteArray()
        val certFactory = CertificateFactory.getInstance("X.509")
        val `is` = ByteArrayInputStream(signatureBytes)
        val cert = certFactory.generateCertificate(`is`) as X509Certificate
        val chars = signature.toCharsString()
        val hex = HEX.encodeHex(signatureBytes, false)
        val md5 = HASH.md5(signatureBytes)
        val sha1 = HASH.sha1(signatureBytes)
        builder.append("SignName:").append(cert.sigAlgName).append("\n")
        builder.append("Chars:").append(chars).append("\n")
        builder.append("Hex:").append(hex).append("\n")
        builder.append("MD5:").append(md5).append("\n")
        builder.append("SHA1:").append(sha1).append("\n")
        builder.append("SignNumber:").append(cert.serialNumber).append("\n")
        builder.append("SubjectDN:").append(cert.subjectDN.name).append("\n")
        builder.append("IssuerDN:").append(cert.issuerDN.name).append("\n")
    } catch (e: Exception) {
        builder.append("Error:" + e)
    }
    return builder.toString()
}

fun Context.networkTypeName(): String {

    var result = "(No Network)"

    try {
        val cm: ConnectivityManager = this.connectivityManager()

        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnectedOrConnecting) {
            return result
        }

        result = info.typeName
        if (info.type == ConnectivityManager.TYPE_MOBILE) {
            result += info.subtypeName
        } else {
        }
    } catch (ignored: Throwable) {
    }

    return result
}

fun Context.networkOperator(): String {
    val tm: TelephonyManager = this.telephonyManager()
    return tm.networkOperator
}

fun Context.networkType(): NetworkType {
    val cm: ConnectivityManager = this.connectivityManager()
    val info = cm.activeNetworkInfo
    if (info == null || !info.isConnectedOrConnecting) {
        return NetworkType.NONE
    }
    val type = info.type
    if (ConnectivityManager.TYPE_WIFI == type) {
        return NetworkType.WIFI
    } else if (ConnectivityManager.TYPE_MOBILE == type) {
        return NetworkType.MOBILE
    } else {
        return NetworkType.OTHER
    }
}

fun Context.isWifi(): Boolean {
    return networkType() == NetworkType.WIFI
}

fun Context.isMobile(): Boolean {
    return networkType() == NetworkType.MOBILE
}

fun Context.isConnected(): Boolean {
    val cm: ConnectivityManager = this.connectivityManager()
    val info = cm.activeNetworkInfo
    return info != null && info.isConnectedOrConnecting
}

fun Context.isAppInstalled(packageName: String): Boolean {
    try {
        return this.packageManager.getPackageInfo(packageName, 0) != null
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }

}

fun Context.isMainProcess(): Boolean {
    val am: ActivityManager = this.activityManager()
    val processes = am.runningAppProcesses
    val mainProcessName = this.packageName
    val myPid = android.os.Process.myPid()
    return processes.any { p ->
        p.pid == myPid
                && mainProcessName == p.processName
    }
}

fun Context.isComponentDisabled(clazz: Class<*>): Boolean {
    val componentName = ComponentName(this, clazz)
    val pm = this.packageManager
    return pm.getComponentEnabledSetting(componentName) ==
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}

fun Context.isComponentEnabled(clazz: Class<*>): Boolean {
    val componentName = ComponentName(this, clazz)
    val pm = this.packageManager
    return pm.getComponentEnabledSetting(componentName) !=
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
}

fun Context.enableComponent(clazz: Class<*>) {
    return setComponentState(clazz, true)
}

fun Context.disableComponent(context: Context, clazz: Class<*>) {
    return setComponentState(clazz, false)
}

fun Context.setComponentState(clazz: Class<*>, enable: Boolean) {
    val componentName = ComponentName(this, clazz)
    val pm = this.packageManager
    val oldState = pm.getComponentEnabledSetting(componentName)
    val newState = if (enable)
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    else
        PackageManager.COMPONENT_ENABLED_STATE_DISABLED
    if (newState != oldState) {
        val flags = PackageManager.DONT_KILL_APP
        pm.setComponentEnabledSetting(componentName, newState, flags)
    }
}
