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
val backColor : Int = Color.parseColor("#BDBDBD")

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
    for (j in 0..(circles - 1)) {
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

class MultiScreenColorView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += dir * scGap
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class MSCNode(var i : Int, val state : State = State()) {

        private var next : MSCNode? = null
        private var prev : MSCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = MSCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, sc : Float, currI : Int, paint : Paint) {
            canvas.drawMSCNode(i, state.scale, sc, currI, paint)
            if (state.scale > 0f) {
                next?.draw(canvas, state.scale, currI, paint)
            }
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MSCNode {
            var curr : MSCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class MultiCircleColorScreen(var i : Int) {

        private val root : MSCNode = MSCNode(0)
        private var curr : MSCNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, 0f, curr.i, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }

    }

    data class Renderer(var view : MultiScreenColorView) {

        private val animator : Animator = Animator(view)
        private val mccs : MultiCircleColorScreen = MultiCircleColorScreen(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            mccs.draw(canvas, paint)
            animator.animate {
                mccs.update {
                    animator.start()
                }
            }
        }

        fun handleTap() {
            mccs.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : MultiScreenColorView {
            val view : MultiScreenColorView = MultiScreenColorView(activity)
            activity.setContentView(view)
            return view
        }
    }
}