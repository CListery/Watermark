package com.yh.watermark

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import androidx.annotation.FloatRange
import com.yh.watermark.model.AbsWatermark
import com.yh.watermark.utils.FileUtils

/**
 * Created by CYH on 2020/4/26 18:05
 */
class Watermark {

    companion object {
        private const val TAG = "Watermark"

        @JvmStatic
        fun create(sourcePath: String): Watermark {
            return Watermark(sourcePath)
        }
    }

    private var mSourcePath: String? = null
    private var mSourceUri: Uri? = null

    private var mWatermarks = arrayListOf<AbsWatermark<*>?>()

    @FloatRange(from = 0.0, to = 1.0)
    private var outRatio: Float = 1F
    private var outColorChannel: Bitmap.Config = Bitmap.Config.ARGB_8888

    constructor(sourcePath: String) {
        mSourcePath = sourcePath
    }

    constructor(uri: Uri) {
        mSourceUri = uri
    }

    fun setOutConfigure(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) outRatio: Float, colorChannel: Bitmap.Config
    ): Watermark {
        this.outRatio = outRatio
        this.outColorChannel = colorChannel
        return this
    }

    fun loadWatermark(vararg watermark: AbsWatermark<*>?): Watermark {
        mWatermarks.addAll(watermark)
        return this
    }

    fun getWatermarkBitmap(): Bitmap? {
        if (TextUtils.isEmpty(mSourcePath) && null == mSourceUri) {
            return null
        }
        var sourcePath = mSourcePath
        if (null == sourcePath) {
            sourcePath = FileUtils.getPath(WatermarkMgr.get().ctx(), mSourceUri)
        }
        if (TextUtils.isEmpty(sourcePath)) {
            return null
        }

        val watermarks = mWatermarks.filterNotNull()
        if (watermarks.isEmpty()) {
            return null
        }

        val originOption = BitmapFactory.Options()
        originOption.inJustDecodeBounds = true

        BitmapFactory.decodeFile(sourcePath, originOption)

        val originW = originOption.outWidth
        val originH = originOption.outHeight

        val outW = (originW * outRatio).toInt()
        val outH = (originH * outRatio).toInt()
        Log.w(TAG, "getWatermarkBitmap: [w:$outW * h:$outH]")

        val newBitmap = Bitmap.createBitmap(outW, outH, outColorChannel)

        val watermarkCanvas = Canvas(newBitmap)

        drawBackground(outW, outH, originW, watermarkCanvas, sourcePath)

        watermarks.forEach { watermark ->
            drawWatermark(watermarkCanvas, watermark, outW, outH)
        }

        return newBitmap
    }

    private fun drawWatermark(
        watermarkCanvas: Canvas,
        watermark: AbsWatermark<*>,
        outW: Int,
        outH: Int
    ) {
        val watermarkW = outW - watermark.getPaddingStart() - watermark.getPaddingEnd()
        val watermarkH = outH - watermark.getPaddingTop() - watermark.getPaddingBottom()
        val wmBitmap = watermark.createWatermarkBitmap(watermarkW, watermarkH)
        if (null != wmBitmap) {
            val dst = Rect()
            val src = Rect(0, 0, wmBitmap.width, wmBitmap.height)

            if (watermark.getGravity().and(Gravity.LEFT) == Gravity.LEFT) {
                dst.left = watermark.getPaddingStart()
                dst.right = dst.left + wmBitmap.width
            }
            if (watermark.getGravity().and(Gravity.TOP) == Gravity.TOP) {
                dst.top = watermark.getPaddingTop()
                dst.bottom = dst.top + wmBitmap.height
            }
            if (watermark.getGravity().and(Gravity.RIGHT) == Gravity.RIGHT) {
                dst.right = outW - watermark.getPaddingEnd()
                dst.left = dst.right - wmBitmap.width
            }
            if (watermark.getGravity().and(Gravity.BOTTOM) == Gravity.BOTTOM) {
                dst.bottom = outH - watermark.getPaddingBottom()
                dst.top = dst.bottom - wmBitmap.height
            }

            if (dst.isEmpty) {
                dst.left = watermark.getPaddingStart()
                dst.right = dst.left + wmBitmap.width
                dst.top = watermark.getPaddingTop()
                dst.bottom = dst.top + wmBitmap.height
            }
            if (src.width() > outW || src.height() > outH) {
                dst.left = (outW - src.width()) / 2
                dst.top = (outH - src.height()) / 2
                dst.right = src.width()
                dst.bottom = src.height()
            }
            if (watermark.getRotationAngle() != 0F) {
                watermarkCanvas.save()
                watermarkCanvas.rotate(
                    watermark.getRotationAngle(),
                    outW.toFloat() / 2,
                    outH.toFloat() / 2
                )
                watermarkCanvas.drawBitmap(wmBitmap, src, dst, null)
                watermarkCanvas.restore()
            } else {
                watermarkCanvas.drawBitmap(wmBitmap, src, dst, null)
            }
        }
    }

    private fun drawBackground(
        outW: Int,
        outH: Int,
        originW: Int,
        watermarkCanvas: Canvas,
        sourcePath: String?
    ) {
        val newOption = BitmapFactory.Options()
        newOption.inJustDecodeBounds = false
        newOption.inPreferredConfig = outColorChannel
        newOption.outWidth = outW
        newOption.outHeight = outH
        newOption.inSampleSize = originW / outW
        Log.d(TAG, "drawBackground: ${newOption.inSampleSize}")
        watermarkCanvas.drawBitmap(
            BitmapFactory.decodeFile(sourcePath, newOption),
            0F,
            0F,
            null
        )
    }

}