package com.protone.layout.view.imageRegionLoadingView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import com.protone.common.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class ImageRegionLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private var regionDecoder: RegionDecoder? = null

    private var widthMode = MeasureSpec.EXACTLY
    private var heightMode = MeasureSpec.EXACTLY

    private val gestureHandler by lazy {
        GestureHandler(context) { gesture ->
            setOnScale { _, _ ->
                invalidate()
            }
            doScaleRequest {
                Log.d(TAG, "doScaleRequest ")
                true
            }
            onDoubleTap {
                Log.d(TAG, "onDoubleTap ")
                gesture.performZoom(this@ImageRegionLoadingView, 100L)
                true
            }
            onDown {
                Log.d(TAG, "onDown ")
                true
            }
            onLongPressed {
                Log.d(TAG, "onLongPressed ")
            }
            onShowPressed {
                Log.d(TAG, "onShowPressed ")
            }
            onSingleTapConfirmed {
                Log.d(TAG, "onSingleTapConfirmed ")
                true
            }
            doFling {
                scaleX > 1f
            }
        }.setOnFlyingEvent(object : OnScrollEvent {
            override fun getStartX(): Int = scrollX
            override fun getStartY(): Int = scrollY

            private var lastScrollX = scrollX
            private var rect = Rect()
            override fun calculateScrollX(scrollValue: Float) {
                //scrollValue > 0 右滑
                Log.d(TAG, "calculateScrollX:scrollValue $scrollValue")
                getLocalVisibleRect(rect)
                Log.d(TAG, "calculateScrollX:local $rect")
                Log.d(TAG, "calculateScrollX:scrollX $scrollX")
                Log.d(TAG, "calculateScrollX:pivotX $pivotX")
                Log.d(TAG, "calculateScrollX:scaleX $scaleX")
                //(next rect.left) =(last rect.left) - scaleX * ((next scrollX) - (last scrollX))
                var distance = scaleX * (scrollX - lastScrollX)
                scrollX += scrollValue.toInt()
            }

            override fun calculateScrollY(scrollValue: Float) {
                scrollY += scrollValue.toInt()
            }

            override fun onScrollReady(): Boolean {
                return true
            }

            override fun onFlingReady() {
                computeScroll()
            }

        })
    }

    fun setImageResource(path: String) {
        initDecoder()
        post {
            regionDecoder?.setImageResource(path, measuredWidth, measuredHeight)
        }
    }

    fun setImageResource(uri: Uri) {
        initDecoder()
        post {
            regionDecoder?.setImageResource(context, uri, measuredWidth, measuredHeight)
        }
    }

    private fun initDecoder() {
        if (regionDecoder != null) return
        regionDecoder = RegionDecoder(this, object : RegionDecoder.OnDecoderListener {

            override fun onResourceReady(resource: Bitmap) {
                when {
                    widthMode != MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY -> updateLayoutParams {
                        height = resource.height
                        width = resource.width
                    }
                    widthMode != MeasureSpec.EXACTLY -> updateLayoutParams {
                        val mix = measuredHeight / resource.height.toFloat()
                        width = (resource.width * mix).toInt()
                    }
                    heightMode != MeasureSpec.EXACTLY -> updateLayoutParams {
                        val mix = measuredWidth / resource.width.toFloat()
                        height = (resource.height * mix).toInt()
                    }
                    else -> invalidate()
                }
            }

        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureHandler.handleTouchEvent(this, event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        widthMode = MeasureSpec.getMode(widthMeasureSpec)
        heightMode = MeasureSpec.getMode(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun computeScroll() {
        gestureHandler.computeScroll(this)
    }

    private val localRect by lazy { Rect() }

    override fun onDrawForeground(canvas: Canvas?) {
        if (regionDecoder == null) {
            super.onDrawForeground(canvas)
            return
        }
        regionDecoder?.drawOriginImage(canvas)
        if (!gestureHandler.isGestureEventEnd()) return
        regionDecoder?.drawScaled(
            scaleX,
            getLocalVisibleRect(localRect).let { localRect },
            canvas
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancel()
        regionDecoder?.release()
    }
}