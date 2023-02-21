package com.protone.layout.view.imageRegionLoadingView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
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
    var fullImage: Bitmap? = null

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

    fun release() {
        bitmapRegionDecoder?.recycle()
        bitmapRegionDecoder = null
    }

    private suspend fun InputStream.initDecoder(w: Int, h: Int) {
        bitmapRegionDecoder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            BitmapRegionDecoder.newInstance(this)
        } else {
            @Suppress("DEPRECATION")
            BitmapRegionDecoder.newInstance(this, false)
        }
        bitmapRegionDecoder?.let {
            var width = w
            var height = h
            if (width == 0) {
                val mix = height / it.height.toFloat()
                width = (it.width * mix).toInt()
            }
            if (height == 0) {
                val mix = width / it.width.toFloat()
                height = (it.height * mix).toInt()
            }
            it.decodeRegion(Rect(0, 0, it.width, it.height), BitmapFactory.Options().apply {
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