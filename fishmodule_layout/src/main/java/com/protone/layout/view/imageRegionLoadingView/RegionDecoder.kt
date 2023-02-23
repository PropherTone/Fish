package com.protone.layout.view.imageRegionLoadingView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.util.Log
import com.protone.common.utils.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class RegionDecoder(private val onDecoderListener: OnDecoderListener) :
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    interface OnDecoderListener {
        fun onResourceReady(resource: Bitmap)
    }

    private var bitmapRegionDecoder: BitmapRegionDecoder? = null

    private val bitmapPaint: Paint = Paint().apply { flags = Paint.FILTER_BITMAP_FLAG }
    private var fullImage: Bitmap? = null

    private val imageOriginalRect = Rect(0, 0, 0, 0)
    private val scaledRect = Rect(0, 0, 0, 0)
    private val sampleRect = Rect(0, 0, 0, 0)

    private var srcScaledW = 0f
    private var srcScaledH = 0f

    fun setImageResource(path: String, w: Int, h: Int) {
        launch {
            release()
            val file = File(path)
            if (!file.isFile) return@launch
            if (file.isDirectory) return@launch
            file.inputStream().initDecoder(w, h)
        }
    }

    @SuppressLint("Recycle")
    fun setImageResource(context: Context, uri: Uri, w: Int, h: Int) {
        launch {
            release()
            context.contentResolver?.openInputStream(uri)?.initDecoder(w, h)
        }
    }

    fun drawOriginImage(canvas: Canvas?) {
        fullImage?.let {
            canvas?.drawBitmap(it, null, imageOriginalRect, bitmapPaint)
        }
    }

    fun drawScaled(
        scaleValue: Float,
        globalRect: Rect,
        localRect: Rect,
        canvas: Canvas?
    ) {
        if (scaleValue <= 1f) return
        bitmapRegionDecoder?.let { decoder ->
            val scaledW =
                ((scaledRect.right - imageOriginalRect.left) * scaleValue).toInt()
            val scaledH =
                ((scaledRect.bottom - imageOriginalRect.top) * scaleValue).toInt()
            val options = BitmapFactory.Options()
            options.inSampleSize = calculateInSampleSize(
                decoder.width,
                decoder.height,
                scaledW,
                scaledH
            )
//            if (options.inSampleSize <= 1) return

            Log.d(TAG, "ScaleValue: $scaleValue")
            Log.d(TAG, "GlobalRect: $globalRect")
            Log.d(TAG, "LocalRect: $localRect")
            Log.d(TAG, "ImageOriginalRect: $imageOriginalRect")
//            canvas?.drawRect(localRect, Paint().apply {
//                color = Color.BLUE
//            })
            imageOriginalRect.apply {

                scaledRect.left = (localRect.left / scaleValue).toInt()
                scaledRect.top = (localRect.top / scaleValue).toInt()
                scaledRect.right = (localRect.right / scaleValue).toInt()
                scaledRect.bottom = (localRect.bottom / scaleValue).toInt()

                val l = scaledRect.left + left
                val t = scaledRect.top + top
                val r = scaledRect.right - scaledRect.left - left
                val b = scaledRect.bottom - scaledRect.top - top
                sampleRect.set(
                    (l * srcScaledW).toInt(),
                    (t * srcScaledH).toInt(),
                    (r * srcScaledW).toInt(),
                    (b * srcScaledH).toInt()
                )
            }

            canvas?.drawRect(scaledRect, Paint().apply {
                color = Color.RED
                strokeWidth = 5f
                style = Paint.Style.STROKE
                isAntiAlias = true
                isDither = true
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            })

            try {
                decoder.decodeRegion(sampleRect, options)
            } catch (_: IllegalArgumentException) {
                null
            }?.let {
                canvas?.drawBitmap(it, null, scaledRect, bitmapPaint)
            }
        }

    }

    fun release() {
        bitmapRegionDecoder?.recycle()
        bitmapRegionDecoder = null
    }

    private suspend fun InputStream.initDecoder(viewWidth: Int, viewHeight: Int) {
        bitmapRegionDecoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(this)
        } else {
            @Suppress("DEPRECATION")
            BitmapRegionDecoder.newInstance(this, false)
        }
        bitmapRegionDecoder?.let {
            var width = viewWidth
            var height = viewHeight
            when {
                viewWidth == 0 && viewHeight == 0 -> {
                    width = it.width
                    height = it.height
                    imageOriginalRect.right = width
                    imageOriginalRect.bottom = height
                }
                viewWidth == 0 -> {
                    val mix = height / it.height.toFloat()
                    width = (it.width * mix).toInt()
                    imageOriginalRect.right = width
                    imageOriginalRect.bottom = height
                }
                viewHeight == 0 -> {
                    val mix = width / it.width.toFloat()
                    height = (it.height * mix).toInt()
                    imageOriginalRect.right = width
                    imageOriginalRect.bottom = height
                }
                else -> {
                    if (viewHeight >= viewWidth) {
                        val widthCompare = viewWidth / it.width.toFloat()
                        val rectHeight = (it.height * widthCompare).toInt()
                        if (rectHeight > viewHeight) {
                            val heightCompare = viewHeight / it.height.toFloat()
                            val rectWidth = (it.width * heightCompare).toInt()
                            imageOriginalRect.left = (viewWidth / 2) - (rectWidth / 2)
                            imageOriginalRect.right = rectWidth + imageOriginalRect.left
                            imageOriginalRect.top = 0
                            imageOriginalRect.bottom = viewHeight
                            width = rectWidth
                            height = viewHeight
                        } else {
                            imageOriginalRect.left = 0
                            imageOriginalRect.right = viewWidth
                            imageOriginalRect.top = (viewHeight / 2) - (rectHeight / 2)
                            imageOriginalRect.bottom = rectHeight + imageOriginalRect.top
                            width = viewWidth
                            height = rectHeight
                        }
                    } else {
                        val heightCompare = viewHeight / it.height.toFloat()
                        val rectWidth = (it.width * heightCompare).toInt()
                        if (rectWidth > viewWidth) {
                            val widthCompare = viewWidth / it.width.toFloat()
                            val rectHeight = (it.height * widthCompare).toInt()
                            imageOriginalRect.left = 0
                            imageOriginalRect.right = viewWidth
                            imageOriginalRect.top = (viewHeight / 2) - (rectHeight / 2)
                            imageOriginalRect.bottom = rectHeight + imageOriginalRect.top
                            width = viewWidth
                            height = rectHeight
                        } else {
                            imageOriginalRect.left = (viewWidth / 2) - (rectWidth / 2)
                            imageOriginalRect.right = rectWidth + imageOriginalRect.left
                            imageOriginalRect.top = 0
                            imageOriginalRect.bottom = viewHeight
                            width = rectWidth
                            height = viewHeight
                        }
                    }
                }
            }
            srcScaledW = it.width / viewWidth.toFloat()
            srcScaledH = it.height / viewHeight.toFloat()
            it.decodeRegion(Rect(0, 0, width, height), BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(it.width, it.height, width, height)
            })
        }?.also {
            fullImage = it
            this.close()
            withContext(Dispatchers.Main) {
                onDecoderListener.onResourceReady(it)
            }
        }
    }

    //采样率算法
    private fun calculateInSampleSize(
        outWidth: Int,
        outHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        var inSampleSize = 1
        if (outHeight > reqHeight || outWidth > reqWidth) {
            val halfHeight = outHeight / 2
            val halfWidth = outWidth / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            try {
                while (halfHeight / inSampleSize >= reqHeight
                    || halfWidth / inSampleSize >= reqWidth
                ) {
                    inSampleSize *= 2
                }
            } catch (e: ArithmeticException) {
                Log.e(TAG, "half$halfHeight,$halfWidth,inSampleSize$inSampleSize")
                Log.e(TAG, "reqHeight$reqHeight,reqWidth$reqWidth")
            }
        }
        return inSampleSize
    }
}