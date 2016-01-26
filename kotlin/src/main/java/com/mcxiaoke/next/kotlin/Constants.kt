package com.mcxiaoke.next.kotlin

import java.math.BigInteger
import java.nio.charset.Charset
import java.util.regex.Pattern

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 16:50
 */


enum class NetworkType {
    WIFI, MOBILE, OTHER, NONE
}


object SizeDef {
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

object Encoding {

    val ISO_8859_1 = "ISO-8859-1"
    val US_ASCII = "US-ASCII"
    val UTF_16 = "UTF-16"
    val UTF_16BE = "UTF-16BE"
    val UTF_16LE = "UTF-16LE"
    val UTF_8 = "UTF-8"
    val CHARSET_ISO_8859_1 = Charset.forName(ISO_8859_1)
    val CHARSET_US_ASCII = Charset.forName(US_ASCII)
    val CHARSET_UTF_16 = Charset.forName(UTF_16)
    val CHARSET_UTF_16BE = Charset.forName(UTF_16BE)
    val CHARSET_UTF_16LE = Charset.forName(UTF_16LE)
    val CHARSET_UTF_8 = Charset.forName(UTF_8)
}

object Const {
    val FILENAME_NOMEDIA = ".nomedia"
    val HEAP_SIZE_LARGE = 48 * 1024 * 1024
    val SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+")
    val HEX_DIGITS = "0123456789ABCDEF".toCharArray()
}