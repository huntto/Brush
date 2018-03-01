package me.huntto.brush.painter

import android.graphics.*
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point

class EraserPainter(canvas: Canvas) : BrushPainter(canvas) {
    override val type: Ink.Type = Ink.Type.ERASER
    override var color: Int
        get() = ovalPaint.color
        set(value) {
            ovalPaint.color = value
        }
    override var strokeWidth: Float
        get() = paint.strokeWidth
        set(value) {
            paint.strokeWidth = value
            halfStrokeWidth = value / 2
        }
    private var halfStrokeWidth: Float = 25f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val realTimePath: Path = Path()
    private val maxRealTimePoints: Int = 3
    private val realTimeEndPoint = BezierPathBuilder.VarPoint()

    private val ovalPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var ovalRect: RectF = RectF()
    private val ovalClearXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    private lateinit var ink: Ink

    init {
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = Color.TRANSPARENT
        paint.strokeWidth = halfStrokeWidth * 2
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        ovalPaint.style = Paint.Style.FILL
        ovalPaint.color = Color.YELLOW
    }

    override fun startStroke(point: Point, dirtyRect: Rect) {
        ink = Ink(type)

        ovalRect.set(point.x - halfStrokeWidth, point.y - halfStrokeWidth,
                point.x + halfStrokeWidth, point.y + halfStrokeWidth)
        stroking(dirtyRect, point, Point(point))
    }


    private fun stroking(dirtyRect: Rect, vararg points: Point) {
        ink.points.addAll(points)

        ovalPaint.xfermode = ovalClearXfermode
        canvas.drawOval(ovalRect, ovalPaint)

        BezierPathBuilder.buildPath(ink.points.takeLast(maxRealTimePoints), realTimePath, realTimeEndPoint)
        canvas.drawPath(realTimePath, paint)

        ovalPaint.xfermode = null
        ovalRect.let {
            it.left = realTimeEndPoint.x - halfStrokeWidth
            it.top = realTimeEndPoint.y - halfStrokeWidth
            it.right = realTimeEndPoint.x + halfStrokeWidth
            it.bottom = realTimeEndPoint.y + halfStrokeWidth

            canvas.drawOval(it.left + 1, it.top + 1,
                    it.right - 1, it.bottom - 1, ovalPaint)
        }

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

        stroking(dirtyRect, endPoint)

        ovalPaint.xfermode = ovalClearXfermode
        canvas.drawOval(ovalRect, ovalPaint)
        return ink
    }


    override fun draw(ink: Ink) {
        BezierPathBuilder.buildPath(ink, realTimePath)
        canvas.drawPath(realTimePath, paint)
    }
}

