package me.huntto.brush.content

import android.graphics.Path

data class Point(val x: Float = 0f, val y: Float = 0f, val size: Float = 0f) {
    constructor(point: Point) : this(point.x, point.y, point.size)
}


data class Ink(val type: Type = Type.PENCIL, val points: ArrayList<Point> = ArrayList<Point>()) {
    enum class Type {
        PENCIL,
        PEN,
        ERASER
    }

    var path: Path? = null
}