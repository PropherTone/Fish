package com.protone.layout.view.imageRegionLoadingView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.updateLayoutParams
import com.protone.common.utils.TAG

class ImageRegionLoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val regionDecoder by lazy {
        RegionDecoder(object : RegionDecoder.OnDecoderListener {

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

    private val gestureHandler by lazy {
        GestureHandler(this) {
            doScaleRequest {
                Log.d(TAG, "doScaleRequest ")
                true
            }
            onDoubleTap {
                Log.d(TAG, "onDoubleTap ")
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
        }
    }

    private var widthMode = MeasureSpec.EXACTLY
    private var heightMode = MeasureSpec.EXACTLY

    fun setImageResource(path: String) {
        post {
            regionDecoder.setImageResource(path, measuredWidth, measuredHeight)
        }
    }

    fun setImageResource(uri: Uri) {
        post {
            regionDecoder.setImageResource(context, uri, measuredWidth, measuredHeight)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureHandler.handleTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        widthMode = MeasureSpec.getMode(widthMeasureSpec)
        heightMode = MeasureSpec.getMode(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private val bitmapPaint: Paint = Paint().apply {
        flags = Paint.FILTER_BITMAP_FLAG
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        regionDecoder.fullImage?.let {
            canvas?.drawBitmap(it, null, regionDecoder.imageOriginalRect, bitmapPaint)
        }
    }
}