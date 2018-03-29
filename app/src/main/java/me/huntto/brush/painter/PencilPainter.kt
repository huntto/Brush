/*
 * MIT License
 *
 * Copyright (c) 2018 xiangtao
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.huntto.brush.painter

import android.graphics.*
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point


class PencilPainter : BrushPainter() {
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
        ink.points.add(point)
        ink.points.add(Point(point))
        stroking(dirtyRect)
    }

    private fun stroking(dirtyRect: Rect) {
        BezierPathBuilder.buildPath(ink.points.takeLast(maxRealTimePoints), realTimePath)
        canvas.drawPath(realTimePath, paint)
        realTimePath.computeBounds(dirtyRect, paint.strokeWidth)
    }

    override fun continueStroke(point: Point, dirtyRect: Rect) {
        ink.points.add(point)
        stroking(dirtyRect)
    }

    override fun endStroke(point: Point, dirtyRect: Rect): Ink {
        var endPoint: Point = point
        val lastPoint = ink.points.last()
        if (lastPoint.x == point.x && lastPoint.y == point.y) {
            endPoint = Point(point.x + 0.1f, point.y + 0.1f, point.size)
        }

        ink.points.add(endPoint)
        stroking(tmpDirtyRect)
        ink.points.add(Point(endPoint))
        stroking(tmpDirtyRect)
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