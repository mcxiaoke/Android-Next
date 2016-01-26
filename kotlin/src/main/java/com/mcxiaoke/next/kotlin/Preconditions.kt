package com.mcxiaoke.next.kotlin

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 16:50
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