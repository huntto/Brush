package me.huntto.brush.view

import android.graphics.Canvas
import android.graphics.Rect

interface IBoardView {
    fun lockCanvas(): Canvas?
    fun lockCanvas(dirtyRect: Rect): Canvas?
    fun unlockCanvasAndPost(canvas: Canvas?)

    var onAvailableListener: OnAvailableListener?

    interface OnAvailableListener {
        fun onAvailable(width: Int, height: Int)
    }
}
