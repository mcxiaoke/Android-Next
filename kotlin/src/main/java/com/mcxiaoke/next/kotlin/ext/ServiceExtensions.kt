package com.mcxiaoke.next.kotlin.ext

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.os.Bundle

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 17:26
 */
inline fun <reified T : Service> Context.startService(): ComponentName =
        this.startService(getIntent<T>())

inline fun <reified T : Service> Context.startService(flags: Int): ComponentName =
        this.startService(getIntent<T>(flags))

inline fun <reified T : Service> Context.startService(extras: Bundle): ComponentName =
        this.startService(getIntent<T>(extras))

inline fun <reified T : Service> Context.startService(extras: Bundle,
                                                      flags: Int): ComponentName = this.startService(getIntent<T>(flags, extras))
