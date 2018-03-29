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

import android.graphics.Canvas
import android.graphics.Rect
import me.huntto.brush.content.Ink
import me.huntto.brush.content.Point
import java.util.*

abstract class BrushPainter {
    abstract val type: Ink.Type
    abstract var color: Int
    abstract var strokeWidth: Float

    abstract fun startStroke(point: Point, dirtyRect: Rect)
    abstract fun continueStroke(point: Point, dirtyRect: Rect)
    abstract fun endStroke(point: Point, dirtyRect: Rect): Ink

    abstract fun draw(ink: Ink)

    var canvas: Canvas = Canvas()

    companion object {
        fun newInstance(type: Ink.Type): BrushPainter = when (type) {
            Ink.Type.ERASER -> EraserPainter()
            else -> PencilPainter()
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

    fun get(type: Ink.Type): BrushPainter {
        return brushPainterMap[type]?.remove() ?: BrushPainter.newInstance(type)
    }
}
