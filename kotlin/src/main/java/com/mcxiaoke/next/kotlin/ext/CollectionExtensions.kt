package com.mcxiaoke.next.kotlin.ext

import java.util.*

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:46
 */

@JvmOverloads fun <T> Array<T>.toString(delim: String = ","): String {
    if (this.isEmpty()) {
        return ""
    }
    val sb = StringBuilder()
    forEachIndexed { i, t ->
        if (i > 0) {
            sb.append(delim)
        }
        sb.append(t)
    }
    return sb.toString()
}

@JvmOverloads fun <T> Collection<T>.toString(delim: String = ","): String {
    if (this.isEmpty()) {
        return ""
    }
    val sb = StringBuilder()
    forEachIndexed { i, t ->
        if (i > 0) {
            sb.append(delim)
        }
        sb.append(t)
    }
    return sb.toString()
}

@JvmOverloads fun <K, V> Map<K, V>.toString(delim: String = ","): String {
    if (this.isEmpty()) {
        return ""
    }
    val strings = ArrayList<String>()
    for ((k, v) in this) {
        strings.add("$k=$v")
    }
    return strings.toString(delim)
}


