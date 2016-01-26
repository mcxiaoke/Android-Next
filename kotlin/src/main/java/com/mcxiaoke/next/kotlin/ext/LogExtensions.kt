package com.mcxiaoke.next.kotlin.ext

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 17:29
 */

import android.util.Log


fun Any.lv(message: String) {
    lv(this.javaClass.simpleName, message)
}

fun Any.ld(message: String) {
    ld(this.javaClass.simpleName, message)
}

fun Any.li(message: String) {
    li(this.javaClass.simpleName, message)
}

fun Any.lw(message: String) {
    lw(this.javaClass.simpleName, message)
}

fun Any.le(message: String) {
    le(this.javaClass.simpleName, message)
}

fun Any.wtf(message: String) {
    wtf(this.javaClass.simpleName, message)
}

fun Any.lv(tag: String, message: String) {
    lv(tag, message, null)
}

fun Any.ld(tag: String, message: String) {
    ld(tag, message, null)
}

fun Any.li(tag: String, message: String) {
    li(tag, message, null)
}

fun Any.lw(tag: String, message: String) {
    lw(tag, message, null)
}

fun Any.le(tag: String, message: String) {
    le(tag, message, null)
}

fun Any.wtf(tag: String, message: String) {
    wtf(tag, message, null)
}

fun Any.lv(tag: String, message: String, exception: Exception?) {
    Log.v(tag, message, exception)
}

fun Any.ld(tag: String, message: String, exception: Exception?) {
    Log.d(tag, message, exception)
}

fun Any.li(tag: String, message: String, exception: Exception?) {
    Log.i(tag, message, exception)
}

fun Any.lw(tag: String, message: String, exception: Exception?) {
    Log.w(tag, message, exception)
}

fun Any.le(tag: String, message: String, exception: Exception?) {
    Log.e(tag, message, exception)
}

fun Any.wtf(tag: String, message: String, exception: Exception?) {
    Log.wtf(tag, message, exception)
}