package me.huntto.brush.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class BoardView : SurfaceView, IBoardView {

    override var onAvailableListener: IBoardView.OnAvailableListener? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                onAvailableListener?.onAvailable(width, height)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

            override fun surfaceCreated(holder: SurfaceHolder?) {

            }

        })
    }

    override fun lockCanvas(): Canvas? = holder?.lockCanvas()
    override fun lockCanvas(dirtyRect: Rect): Canvas? = holder?.lockCanvas(dirtyRect)

    override fun unlockCanvasAndPost(canvas: Canvas?) {
        holder?.unlockCanvasAndPost(canvas)
    }

}