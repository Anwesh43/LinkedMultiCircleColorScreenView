package com.anwesh.uiprojects.multicirclecolorscreenview

/**
 * Created by anweshmishra on 08/09/19.
 */

import android.content.Context
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.view.MotionEvent

val circles : Int = 5
val scGap : Float = 0.01f
val colors : Array<String> = arrayOf("#311B92", "#00C853", "#f44336", "#0D47A1", "#F57F17")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n))
