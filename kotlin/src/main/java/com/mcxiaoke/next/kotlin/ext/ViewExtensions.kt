package com.mcxiaoke.next.kotlin.ext

import android.app.Activity
import android.app.Fragment
import android.view.View

/**
 * User: mcxiaoke
 * Date: 16/1/26
 * Time: 17:38
 */

inline fun <reified T : View> View.find(id: Int): T = this.findViewById(id) as T

inline fun <reified T : View> Activity.find(id: Int): T = this.findViewById(id) as T

inline fun <reified T : View> Fragment.find(id: Int): T = this.view.findViewById(id) as T