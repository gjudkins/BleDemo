package com.bledemo.bledemo

import android.util.Log


object DebugLogger {
    private val TAG = "DEBUG LOGGER"

    fun log(text: String) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, text)
    }

    fun v(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG)
            Log.v(tag, text)
    }

    fun d(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, text)
        }
    }

    fun i(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG)
            Log.i(tag, text)
    }

    fun w(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, text)
        }
    }

    fun e(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text)
    }

    fun e(tag: String = TAG, text: String, e: Throwable) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text, e)
    }

    fun wtf(tag: String = TAG, text: String) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, text)
        }
    }

    fun wtf(tag: String = TAG, text: String, e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, text, e)
        }
    }

}