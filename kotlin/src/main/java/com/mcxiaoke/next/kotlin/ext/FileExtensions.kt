package com.mcxiaoke.next.kotlin.ext

import com.mcxiaoke.next.kotlin.Const
import java.io.File

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:11
 */

fun File.isNameSafe(): Boolean {
    // Note, we check whether it matches what's known to be safe,
    // rather than what's known to be unsafe.  Non-ASCII, control
    // characters, etc. are all unsafe by default.
    return Const.SAFE_FILENAME_PATTERN.matcher(this.path).matches()
}