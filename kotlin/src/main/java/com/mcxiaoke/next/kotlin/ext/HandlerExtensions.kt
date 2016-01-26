package com.mcxiaoke.next.kotlin.ext

import android.os.Handler
import android.os.Message

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 17:58
 */
fun Handler.post(action: () -> Unit): Boolean = post(Runnable(action))

fun Handler.atFrontOfQueue(action: () -> Unit): Boolean = postAtFrontOfQueue(Runnable(action))

fun Handler.atTime(uptimeMillis: Long, action: () -> Unit): Boolean = postAtTime(Runnable(action), uptimeMillis)

fun Handler.delayed(delayMillis: Long, action: () -> Unit): Boolean = postDelayed(Runnable(action), delayMillis)

fun handler(handleMessage: (Message) -> Boolean): Handler {
    return android.os.Handler { p -> if (p == null) false else handleMessage(p) }
}
