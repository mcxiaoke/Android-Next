package com.mcxiaoke.next.kotlin.ext

import com.mcxiaoke.next.kotlin.Const

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:42
 */

fun Long.readableByteCount(): String {
    val unit = 1024
    if (this < unit) return "${this}B"
    val exp = (Math.log(this.toDouble()) / Math.log(unit.toDouble())).toInt()
    val pre = "KMGTPE"[exp - 1] + ""
    return "%.1f %sB".format(this / Math.pow(unit.toDouble(), exp.toDouble()), pre)
}

fun ByteArray.hexString(): String {
    val hexChars = CharArray(this.size * 2)
    forEachIndexed { i, byte ->
        val v = byte.toInt() and 255
        hexChars[i * 2] = Const.HEX_DIGITS[v.ushr(4)]
        hexChars[i * 2 + 1] = Const.HEX_DIGITS[v and 15]
    }
    return String(hexChars)
}
