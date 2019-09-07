package com.anwesh.uiprojects.multicirclecolorscreenview

/**
 * Created by anweshmishra on 08/09/19.
 */

import android.content.Context
import android.app.Activity
import android.graphics.*
import android.view.View
import android.view.MotionEvent

val circles : Int = 5
val scGap : Float = 0.01f
val colors : Array<String> = arrayOf("#311B92", "#00C853", "#f44336", "#0D47A1", "#F57F17")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n))

fun Canvas.drawCircleScreen(i : Int, size : Float, sc1 : Float, sc2 : Float, shouldFill : Boolean, paint : Paint) {
    var w : Float = 0f
    if (sc2 > 0f) {
        w = 2 * size * sc2
    }
    if (shouldFill) {
        w = 2 * size
    }
    save()
    translate(2 * size * i + size, 0f)
    val path : Path = Path()
    path.addCircle(0f, 0f, size, Path.Direction.CW)
    clipPath(path)
    drawRect(RectF(-size + 2 * size * sc1, -size, -size + w, size), paint)
    restore()
}

fun Canvas.drawMultiCircleScreen(i : Int, size : Float, sc1 : Float, sc2 : Float, shouldFill: Boolean, paint : Paint) {
    for (j in 0..(colors.size - 1)) {
        paint.color = Color.parseColor(colors[i])
        drawCircleScreen(j, size, sc1.divideScale(0, 2), sc2.divideScale(1, 2), shouldFill, paint)
    }
}

fun Canvas.drawMSCNode(i : Int, scale : Float, sc : Float, currI : Int, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (colors.size)
    val size = gap / 2
    save()
    translate(0f, h / 2)
    drawMultiCircleScreen(i, size, scale, sc, i == currI, paint)
    restore()
}
