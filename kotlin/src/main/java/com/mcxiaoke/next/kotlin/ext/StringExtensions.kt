package com.mcxiaoke.next.kotlin.ext

import com.mcxiaoke.next.kotlin.Const
import com.mcxiaoke.next.kotlin.Encoding
import com.mcxiaoke.next.kotlin.SizeDef
import java.io.File
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLDecoder
import java.util.*

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:35
 */

fun String.quote(): String {
    return "'$this'"
}

fun CharSequence.isBlank(): Boolean {
    val len: Int = this.length
    if (len == 0) {
        return true
    }
    forEach { c ->
        if (!Character.isWhitespace(c)) {
            return false
        }
    }
    return true
}

fun String.toHexBytes(): ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4)
                + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun String.withoutQuery(): String {
    return this.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
}

fun String.toSafeFileName(): String {
    val size = this.length
    val builder = StringBuilder(size * 2)
    forEachIndexed { i, c ->
        var valid = c >= 'a' && c <= 'z'
        valid = valid || c >= 'A' && c <= 'Z'
        valid = valid || c >= '0' && c <= '9'
        valid = valid || c == '_' || c == '-' || c == '.'

        if (valid) {
            builder.append(c)
        } else {
            // Encode the character using hex notation
            builder.append('x')
            builder.append(Integer.toHexString(i))
        }
    }
    return builder.toString()
}

fun String.toQueries(): Map<String, String> {
    var map: Map<String, String> = mapOf()
    if (this.length == 0) {
        return map
    }
    try {
        val queries = HashMap<String, String>()
        for (param in this.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val pair = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val key = URLDecoder.decode(pair[0], Encoding.UTF_8)
            if (pair.size > 1) {
                val value = URLDecoder.decode(pair[1], Encoding.UTF_8)
                queries.put(key, value)
            }
        }
        return queries
    } catch (ex: UnsupportedEncodingException) {
        throw RuntimeException(ex)
    }

}

@JvmOverloads fun String.toStringList(delimiters: String = "?",
                                      trimTokens: Boolean = true,
                                      ignoreEmptyTokens: Boolean = true): List<String> {
    val st = StringTokenizer(this, delimiters)
    val tokens = ArrayList<String>()
    while (st.hasMoreTokens()) {
        var token = st.nextToken()
        if (trimTokens) {
            token = token.trim { it <= ' ' }
        }
        if (!ignoreEmptyTokens || token.length > 0) {
            tokens.add(token)
        }
    }
    return tokens
}

@JvmOverloads fun String.toStringArray(delimiters: String = "?",
                                       trimTokens: Boolean = true,
                                       ignoreEmptyTokens: Boolean = true): Array<String> {
    return toStringList(delimiters, trimTokens, ignoreEmptyTokens).toTypedArray()
}


fun String.trimLeadingCharacter(leadingCharacter: Char): String {
    if (this.isEmpty()) {
        return this
    }
    val sb = StringBuilder(this)
    while (sb.length > 0 && sb[0] == leadingCharacter) {
        sb.deleteCharAt(0)
    }
    return sb.toString()
}

fun String.trimTrailingCharacter(trailingCharacter: Char): String {
    if (this.isEmpty()) {
        return this
    }
    val sb = StringBuilder(this)
    while (sb.length > 0 && sb[sb.length - 1] == trailingCharacter) {
        sb.deleteCharAt(sb.length - 1)
    }
    return sb.toString()
}

fun String.trimAllWhitespace(): String {
    if (this.isEmpty()) {
        return this
    }
    val sb = StringBuilder(this)
    var index = 0
    while (sb.length > index) {
        if (Character.isWhitespace(sb[index])) {
            sb.deleteCharAt(index)
        } else {
            index++
        }
    }
    return sb.toString()
}

fun CharSequence.containsWhitespace(): Boolean {
    if (this.isEmpty()) {
        return false
    }
    forEach { c ->
        if (Character.isWhitespace(c)) {
            return true
        }
    }
    return false
}


fun String.fileNameWithoutExtension(): String {
    if (isEmpty()) {
        return this
    }

    var filePath = this
    val extenPosi = filePath.lastIndexOf(IO.FILE_EXTENSION_SEPARATOR)
    val filePosi = filePath.lastIndexOf(File.separator)
    if (filePosi == -1) {
        return if (extenPosi == -1) filePath else filePath.substring(0, extenPosi)
    }
    if (extenPosi == -1) {
        return filePath.substring(filePosi + 1)
    }
    return if (filePosi < extenPosi) filePath.substring(filePosi + 1, extenPosi)
    else filePath.substring(filePosi + 1)
}

fun String.fileName(): String {
    if (isEmpty()) {
        return this
    }

    var filePath = this
    val filePosi = filePath.lastIndexOf(File.separator)
    return if (filePosi == -1) filePath else filePath.substring(filePosi + 1)
}

fun String.fileExtension(): String {
    if (isEmpty()) {
        return this
    }
    var filePath = this
    val extenPosi = filePath.lastIndexOf(IO.FILE_EXTENSION_SEPARATOR)
    val filePosi = filePath.lastIndexOf(File.separator)
    if (extenPosi == -1) {
        return ""
    }
    return if (filePosi >= extenPosi) "" else filePath.substring(extenPosi + 1)
}

fun BigInteger.readableSize(): String {
    val displaySize: String
    var size = this
    if (size.divide(SizeDef.ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_EB_BI).toString() + " EB"
    } else if (size.divide(SizeDef.ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_PB_BI).toString() + " PB"
    } else if (size.divide(SizeDef.ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_TB_BI).toString() + " TB"
    } else if (size.divide(SizeDef.ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_GB_BI).toString() + " GB"
    } else if (size.divide(SizeDef.ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_MB_BI).toString() + " MB"
    } else if (size.divide(SizeDef.ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
        displaySize = size.divide(SizeDef.ONE_KB_BI).toString() + " KB"
    } else {
        displaySize = size.toString() + " bytes"
    }
    return displaySize
}

fun Long.readableSize(): String {
    return BigInteger.valueOf(this).readableSize()
}