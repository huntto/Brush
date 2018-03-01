package me.huntto.brush.log

import android.util.Log

infix fun Any.logD(msg: String) {
    Log.d(this::class.java.simpleName, msg)
}

infix fun Any.logE(msg: String) {
    Log.e(this::class.java.simpleName, msg)
}