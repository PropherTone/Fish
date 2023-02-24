package com.protone.layout.view.imageRegionLoadingView

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class GestureHandler(
    private val view: View,
    private val coroutineScope: CoroutineScope,
    onGesture: (OnGestureEvent.() -> Unit)? = null
) : GestureDetector.SimpleOnGestureListener() {

    private val onGestureEvent by lazy { OnGestureEvent() }

    init {
        onGesture?.invoke(onGestureEvent)
    }

    private var onFingerUp: (() -> Unit)? = null
    private var doScaleRequest: () -> Boolean = { true }
    private var onScale: ((Float, Float) -> Unit)? = null
    private var onDown: ((MotionEvent?) -> Boolean)? = null
    private var onShowPressed: ((MotionEvent?) -> Unit)? = null
    private var onLongPressed: ((MotionEvent?) -> Unit)? = null
    private var onSingleTapConfirmed: ((MotionEvent?) -> Boolean)? = null
    private var onDoubleTap: ((MotionEvent?) -> Boolean)? = null
    private var onScroll: (() -> Boolean)? = null
    private var doFling: (() -> Boolean)? = null
    private var onFlingEvent: OnFlingEvent? = null
    fun setOnFlyingEvent(onFlingEvent: OnFlingEvent): GestureHandler {
        this.onFlingEvent = onFlingEvent
        return this
    }

    inner class OnGestureEvent {

        fun setOnFingerUp(onFingerUp: () -> Unit) {
            this@GestureHandler.onFingerUp = onFingerUp
        }

        fun setOnScale(scale: (Float, Float) -> Unit) {
            this@GestureHandler.onScale = scale
        }

        fun doScaleRequest(doScale: () -> Boolean) {
            doScaleRequest = doScale
        }

        fun onDown(onDown: (MotionEvent?) -> Boolean) {
            this@GestureHandler.onDown = onDown
        }

        fun onShowPressed(onShowPressed: (MotionEvent?) -> Unit) {
            this@GestureHandler.onShowPressed = onShowPressed
        }

        fun onLongPressed(onLongPressed: (MotionEvent?) -> Unit) {
            this@GestureHandler.onLongPressed = onLongPressed
        }

        fun onSingleTapConfirmed(onSingleTapConfirmed: (MotionEvent?) -> Boolean) {
            this@GestureHandler.onSingleTapConfirmed = onSingleTapConfirmed
        }

        fun onDoubleTap(onDoubleTap: (MotionEvent?) -> Boolean) {
            this@GestureHandler.onDoubleTap = onDoubleTap
        }

        fun onScroll(onScroll: () -> Boolean) {
            this@GestureHandler.onScroll = onScroll
        }

        fun onFling(onFling: () -> Boolean) {
            this@GestureHandler.doFling = onFling
        }

    }

    private val gestureDetector = GestureDetector(view.context, this)
    private val overScroller = OverScroller(view.context, FlingInterpolator())

    //缩放相关
    companion object {
        const val SCALE_MAX = 5.0f
        const val SCALE_MID = 2.5f
        const val SCALE_MIN = 1.0f
    }

    private var mFinger1DownX = 0f
    private var mFinger1DownY = 0f
    private var mFinger2DownX = 0f
    private var mFinger2DownY = 0f
    private var oldDistance = 0.0
    private var clkX = 0f
    private var clkY = 0f
    private var zoomIn = 0

    fun handleTouchEvent(ev: MotionEvent?): Boolean {
        val fingerCounts = ev?.pointerCount
        when (ev?.action?.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                if (doScaleRequest.invoke())
                    view.parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP -> {
                clkX = ev.x
                clkY = ev.y
                view.parent.requestDisallowInterceptTouchEvent(false)
                onFingerUp?.invoke()
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (fingerCounts == 2) {
                    mFinger1DownX = 0f
                    mFinger1DownY = 0f
                    mFinger2DownX = 0f
                    mFinger2DownY = 0f
                    onScale?.invoke(view.scaleX, view.scaleY)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (fingerCounts == 2) {
                    val moveX1 = ev.getX(0)
                    val moveY1 = ev.getY(0)
                    val moveX2 = ev.getX(1)
                    val moveY2 = ev.getY(1)
                    val changeX1: Double = (moveX1 - mFinger1DownX).toDouble()
                    val changeY1: Double = (moveY1 - mFinger1DownY).toDouble()
                    val changeX2: Double = (moveX2 - mFinger2DownX).toDouble()
                    val changeY2: Double = (moveY2 - mFinger2DownY).toDouble()
                    if (view.scaleX > 1) {
                        val lessX = (changeX1 / 2 + changeX2 / 2).toFloat()
                        val lessY = (changeY1 / 2 + changeY2 / 2).toFloat()
                        view.setPivot(-lessX, -lessY)
                    }
                    val newDistance = spacing(ev)
                    val space: Double = newDistance - oldDistance
                    var scale = (view.scaleX + space / view.width).toFloat()
                    if (scale < 1f) {
                        scale = 1f
                    }
                    view.setScale(scale.coerceAtMost(SCALE_MAX))
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                clkX = ev.x
                clkY = ev.y
                if (fingerCounts == 2) {
                    mFinger1DownX = ev.getX(0)
                    mFinger1DownY = ev.getY(0)
                    mFinger2DownX = ev.getX(1)
                    mFinger2DownY = ev.getY(1)
                    oldDistance = spacing(ev)
                    return true
                }
            }
        }
        return gestureDetector.onTouchEvent(ev)
    }

    private fun View.setPivot(x: Float, y: Float) {
        var mPivotX: Float
        var mPivotY: Float
        mPivotX = pivotX + x
        mPivotY = pivotY + y
        if (mPivotX < 0 && mPivotY < 0) {
            mPivotX = 0f
            mPivotY = 0f
        } else if (mPivotX > 0 && mPivotY < 0) {
            mPivotY = 0f
            if (mPivotX > width) {
                mPivotX = width.toFloat()
            }
        } else if (mPivotX < 0 && mPivotY > 0) {
            mPivotX = 0f
            if (mPivotY > height) {
                mPivotY = height.toFloat()
            }
        } else {
            if (mPivotX > width) {
                mPivotX = width.toFloat()
            }
            if (mPivotY > height) {
                mPivotY = height.toFloat()
            }
        }
        pivotX = mPivotX
        pivotY = mPivotY
    }

    private fun View.setScale(scale: Float) {
        elevation = if (scale > 1f) 10f else 0f
        scaleX = scale
        scaleY = scale
    }

    private fun spacing(event: MotionEvent): Double {
        return if (event.pointerCount == 2) {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            sqrt((x * x + y * y).toDouble())
        } else {
            0.0
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return onDown?.invoke(e) == true
    }

    override fun onShowPress(e: MotionEvent?) {
        onShowPressed?.invoke(e)
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
        onLongPressed?.invoke(e)
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return onSingleTapConfirmed?.invoke(e) == true
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        return onDoubleTap?.invoke(e) == true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return super.onDoubleTapEvent(e)
    }

    override fun onContextClick(e: MotionEvent?): Boolean {
        return super.onContextClick(e)
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return onFlingEvent?.let {
            it.calculateScrollHorizontally(distanceX)
            it.calculateScrollVertically(distanceY)
            onScroll?.invoke() ?: true
        } == true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return onFlingEvent?.let {
            if (doFling?.invoke() == false) return false
            coroutineScope.launch {
                overScroller.fling(
                    view.scrollX,
                    view.scrollY,
                    -velocityX.toInt(),
                    -velocityY.toInt(),
                    Int.MIN_VALUE,
                    Int.MAX_VALUE,
                    Int.MIN_VALUE,
                    Int.MAX_VALUE
                )
                while (overScroller.computeScrollOffset()) {
                    it.calculateScrollHorizontally(overScroller.currX.toFloat() - view.scrollX)
                    it.calculateScrollVertically(overScroller.currY.toFloat() - view.scrollY)
                }
            }
            true
        } == true
    }
}