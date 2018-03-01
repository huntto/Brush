package me.huntto.brush

import android.annotation.SuppressLint
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point
import me.huntto.brush.log.logD
import me.huntto.brush.painter.BrushPainter
import me.huntto.brush.painter.EraserPainter
import me.huntto.brush.painter.PencilPainter
import me.huntto.brush.view.IBoardView

class Brush(contentWidth: Int, contentHeight: Int, private val boardView: IBoardView) : View.OnTouchListener {

    var type: Ink.Type = Ink.Type.PENCIL
    var bgColor: Int = Color.LTGRAY
    var maxPointer: Int = 1
    var onGenerateInkListener: OnGenerateInkListener? = null
    var color: Int = Color.RED

    private val contentBitmap: Bitmap = Bitmap.createBitmap(contentWidth, contentHeight, Bitmap.Config.ARGB_8888)
    private val contentCanvas: Canvas = Canvas(contentBitmap)

    private val brushPainters = arrayOfNulls<BrushPainter>(20)


    private var pointerCount = 0
    private val dirtyRect: Rect = Rect()

    init {
        clean()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> actionDown(event)
            MotionEvent.ACTION_POINTER_DOWN -> actionPointerDown(event)
            MotionEvent.ACTION_MOVE -> actionMove(event)
            MotionEvent.ACTION_POINTER_UP -> actionPointerUp(event)
            MotionEvent.ACTION_UP -> actionUp(event)
            else -> cancel()
        }
        return true
    }

    private fun actionDown(event: MotionEvent) {
        this logD "actionDown"
        pointerCount = 0
        event.actionIndex.let {
            val point = Point(event.getX(it), event.getY(it), event.getSize(it))
            val pointerId = event.getPointerId(it)
            start(pointerId, point)
        }
    }

    private fun start(pointerId: Int, point: Point) {
        if (pointerCount < maxPointer) {
            pointerCount++
            if (brushPainters[pointerId] == null || brushPainters[pointerId]?.type != type) {
                brushPainters[pointerId] = BrushPainter.newInstance(type, contentCanvas)
            }
            brushPainters[pointerId]?.let {
                it.startStroke(point, dirtyRect)
                boardView.lockCanvas(dirtyRect)?.let {
                    it.drawColor(bgColor)
                    it.drawBitmap(contentBitmap, dirtyRect, dirtyRect, null)
                    boardView.unlockCanvasAndPost(it)
                }
            }
        }
    }


    private fun actionPointerDown(event: MotionEvent) {
        logD("actionPointerDown")
        event.actionIndex.let {
            val point = Point(event.getX(it), event.getY(it), event.getSize(it))
            val pointerId = event.getPointerId(it)
            start(pointerId, point)
        }
    }

    private fun actionMove(event: MotionEvent) {
        logD("actionMove")
        for (index in 0 until event.pointerCount) {
            val pointerId = event.getPointerId(index)
            val point = Point(event.getX(index), event.getY(index), event.getSize(index))
            brushPainters[pointerId]?.let {
                it.continueStroke(point, dirtyRect)
                boardView.lockCanvas(dirtyRect)?.let {
                    it.drawColor(bgColor)
                    it.drawBitmap(contentBitmap, dirtyRect, dirtyRect, null)
                    boardView.unlockCanvasAndPost(it)
                }
            }
        }
    }

    private fun actionPointerUp(event: MotionEvent) {
        logD("actionPointerUp")
        event.actionIndex.let {
            val point = Point(event.getX(it), event.getY(it), event.getSize(it))
            val pointerId = event.getPointerId(it)
            end(pointerId, point)
        }
    }

    private fun end(pointerId: Int, point: Point) {
        brushPainters[pointerId]?.let {
            val ink = it.endStroke(point, dirtyRect)
            boardView.lockCanvas(dirtyRect)?.let {
                it.drawColor(bgColor)
                it.drawBitmap(contentBitmap, dirtyRect, dirtyRect, null)
                boardView.unlockCanvasAndPost(it)
            }
            onGenerateInkListener?.onGenerate(ink)
            pointerCount--
        }
    }

    private fun actionUp(event: MotionEvent) {
        logD("actionUp")
        event.actionIndex.let {
            val point = Point(event.getX(it), event.getY(it), event.getSize(it))
            val pointerId = event.getPointerId(it)
            end(pointerId, point)
        }
    }

    private fun cancel() {
        logD("cancel")
    }

    fun clean() {
        contentCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        boardView.lockCanvas()?.let {
            it.drawColor(bgColor)
            it.drawBitmap(contentBitmap, 0f, 0f, null)
            boardView.unlockCanvasAndPost(it)
        }
    }

    interface OnGenerateInkListener {
        fun onGenerate(ink: Ink)
    }

    fun draw(inks: ArrayList<Ink>) {
        contentCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        inks.forEach {
            when (it.type) {
                Ink.Type.ERASER -> {
                    EraserPainter(contentCanvas).draw(it)
                }
                else -> {
                    PencilPainter(contentCanvas).draw(it)
                }
            }
        }
        boardView.lockCanvas()?.let {
            it.drawColor(bgColor)
            it.drawBitmap(contentBitmap, 0f, 0f, null)
            boardView.unlockCanvasAndPost(it)
        }
    }
}