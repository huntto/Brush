package me.huntto.brush.painter

import android.graphics.Canvas
import android.graphics.Rect
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point
import java.util.*

abstract class BrushPainter(protected val canvas: Canvas) {
    abstract val type: Ink.Type
    abstract var color: Int
    abstract var strokeWidth: Float

    abstract fun startStroke(point: Point, dirtyRect: Rect)
    abstract fun continueStroke(point: Point, dirtyRect: Rect)
    abstract fun endStroke(point: Point, dirtyRect: Rect): Ink

    abstract fun draw(ink: Ink)

    companion object {
        fun newInstance(type: Ink.Type, canvas: Canvas): BrushPainter = when (type) {
            Ink.Type.ERASER -> EraserPainter(canvas)
            else -> PencilPainter(canvas)
        }
    }
}

object BrushPainterCache {
    private val brushPainterMap = HashMap<Ink.Type, Deque<BrushPainter>>()

    fun put(brushPainter: BrushPainter) {
        if (brushPainterMap[brushPainter.type] == null) {
            brushPainterMap[brushPainter.type] = LinkedList<BrushPainter>()
        }
        brushPainterMap[brushPainter.type]?.add(brushPainter)
    }

    fun get(type: Ink.Type, canvas: Canvas): BrushPainter {
        return brushPainterMap[type]?.remove() ?: BrushPainter.newInstance(type, canvas)
    }
}
