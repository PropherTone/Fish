package com.protone.layout.view.imageRegionLoadingView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
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
                    widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED -> updateLayoutParams {
                        height = resource.height
                        width = resource.width
                    }
                    widthMode == MeasureSpec.UNSPECIFIED -> updateLayoutParams {
                        val mix = measuredHeight / resource.height
                        width = resource.width * mix
                    }
                    heightMode == MeasureSpec.UNSPECIFIED -> updateLayoutParams {
                        val mix = measuredWidth / resource.width.toFloat()
                        height = (resource.height * mix).toInt()
                    }
                }
            }

        })
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        widthMode = MeasureSpec.getMode(widthMeasureSpec)
        heightMode = MeasureSpec.getMode(heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        regionDecoder.fullImage?.let {
            canvas?.drawBitmap(it, null, Rect(0, 0, measuredWidth, measuredHeight), null)
        }
    }
}