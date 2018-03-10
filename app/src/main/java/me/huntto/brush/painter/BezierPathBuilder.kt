package me.huntto.brush.painter

import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point


private val exactlyBounds: RectF = RectF()

fun Path.computeBounds(bounds: Rect, expandSize: Float) {
    computeBounds(exactlyBounds, true)
    bounds.set((exactlyBounds.left - expandSize).toInt(),
            (exactlyBounds.top - expandSize).toInt(),
            (exactlyBounds.right + expandSize + 0.5f).toInt(),
            (exactlyBounds.bottom + expandSize + 0.5f).toInt())
}

object BezierPathBuilder {
    class VarPoint(var x: Float = 0f, var y: Float = 0f, var size: Float = 0f)

    internal fun buildPath(points: List<Point>, path: Path, endPoint: VarPoint = VarPoint()) {
        path.reset()
        if (points.isEmpty()) return
        var prevPoint: Point = points.first()
        for (index in 1 until points.size) {
            endPoint.x = (prevPoint.x + points[index].x) / 2
            endPoint.y = (prevPoint.y + points[index].y) / 2
            endPoint.size = (prevPoint.size + points[index].size) / 2
            if (index == 1) {
                path.moveTo(endPoint.x, endPoint.y)
            } else {
                path.quadTo(prevPoint.x, prevPoint.y, endPoint.x, endPoint.y)
            }
            prevPoint = points[index]
        }
    }

    fun buildPath(ink: Ink, path: Path) {
        buildPath(ink.points, path)
    }
}