package com.mcxiaoke.next.kotlin.ext

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager

/**
 * User: mcxiaoke
 * Date: 16/1/22
 * Time: 13:14
 */


fun Activity.setFullScreen(fullscreen: Boolean) {
    if (fullscreen) {
        this.window.addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.window.clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    } else {
        this.window.addFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
        this.window.clearFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}

fun Activity.setPortraitOrientation(portrait: Boolean) {
    if (portrait) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}

fun Activity.lockScreenOrientation(portrait: Boolean) {
    if (portrait) {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}

fun Activity.unlockScreenOrientation() {
    this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
}

fun Activity.restart() {
    val intent = this.intent
    this.overridePendingTransition(0, 0)
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
    this.finish()
    this.overridePendingTransition(0, 0)
    this.startActivity(intent)
}
