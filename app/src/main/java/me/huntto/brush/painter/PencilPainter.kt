package me.huntto.brush.painter

import android.graphics.*
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point


class PencilPainter(canvas: Canvas) : BrushPainter(canvas) {
    override val type: Ink.Type = Ink.Type.PENCIL

    override var color: Int
        get() = paint.color
        set(value) {
            paint.color = value
        }

    override var strokeWidth: Float
        get() = paint.strokeWidth
        set(value) {
            paint.strokeWidth = value
        }

    private val realTimePath: Path = Path()
    private val maxRealTimePoints: Int = 3

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private lateinit var ink: Ink
    private val tmpDirtyRect = Rect()

    init {
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 4f
    }

    override fun startStroke(point: Point, dirtyRect: Rect) {
        ink = Ink(type)
        stroking(dirtyRect, point, Point(point))
    }

    private fun stroking(dirtyRect: Rect, vararg points: Point) {
        ink.points.addAll(points)
        BezierPathBuilder.buildPath(ink.points.takeLast(maxRealTimePoints), realTimePath)
        canvas.drawPath(realTimePath, paint)
        realTimePath.computeBounds(dirtyRect, paint.strokeWidth)
    }

    override fun continueStroke(point: Point, dirtyRect: Rect) {
        stroking(dirtyRect, point)
    }

    override fun endStroke(point: Point, dirtyRect: Rect): Ink {
        var endPoint: Point = point
        val lastPoint = ink.points.last()
        if (lastPoint.x == point.x && lastPoint.y == point.y) {
            endPoint = Point(point.x + 0.1f, point.y + 0.1f, point.size)
        }

        stroking(tmpDirtyRect, endPoint)
        stroking(dirtyRect, Point(endPoint))
        dirtyRect.union(tmpDirtyRect)
        return ink
    }

    override fun draw(ink: Ink) {
        if (ink.path == null) {
            val path = Path()
            BezierPathBuilder.buildPath(ink, path)
            ink.path = path
        }
        canvas.drawPath(ink.path, paint)
    }
}