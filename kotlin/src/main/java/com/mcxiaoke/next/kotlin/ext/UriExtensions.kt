package com.mcxiaoke.next.kotlin.ext

import android.net.Uri

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:10
 */

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == this.authority
}

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == this.authority
}

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is MediaProvider.
 */
fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == this.authority
}

/**
 * @param uri The Uri to check.
 * *
 * @return Whether the Uri authority is Google Photos.
 */
fun Uri.isGooglePhotosUri(): Boolean {
    return "com.google.android.apps.photos.content" == this.authority
}
