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