package com.yh.watermark

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.text.TextUtils
import androidx.annotation.FloatRange
import androidx.core.graphics.applyCanvas
import com.yh.appbasic.logger.logW
import com.yh.appbasic.share.AppBasicShare
import com.yh.watermark.model.AbsWatermark
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by CYH on 2020/4/26 18:05
 */
class Watermark {
    
    companion object {
        /**
         * create watermark by file path
         */
        @JvmStatic
        fun create(sourcePath: String): Watermark {
            return Watermark(sourcePath)
        }

        /**
         * create watermark by file url
         */
        @JvmStatic
        fun create(uri: Uri): Watermark {
            return Watermark(uri)
        }
    }

    // file path
    private var mSourcePath: String? = null
    // file url
    private var mSourceUri: Uri? = null

    // watermark layers
    private var mWatermarks = arrayListOf<AbsWatermark<*>?>()

    // Ratio of output watermark image to original image
    @FloatRange(from = 0.0, to = 1.0)
    private var outRatio: Float = 1F
    // Color channel of output watermark image to original image
    private var outColorChannel: Bitmap.Config = Bitmap.Config.ARGB_8888
    
    constructor(sourcePath: String) {
        mSourcePath = sourcePath
    }
    
    constructor(uri: Uri) {
        mSourceUri = uri
    }

    /**
     * config output ratio and color channel
     */
    fun setOutConfigure(@FloatRange(from = 0.0, to = 1.0) outRatio: Float, colorChannel: Bitmap.Config): Watermark {
        this.outRatio = outRatio
        this.outColorChannel = colorChannel
        return this
    }

    /**
     * load watermark layers
     */
    fun loadWatermark(vararg watermark: AbsWatermark<*>?): Watermark {
        mWatermarks.addAll(watermark)
        return this
    }

    /**
     * get watermark output bitmap
     */
    fun getWatermarkBitmap(): Bitmap? {
        if(TextUtils.isEmpty(mSourcePath) && null == mSourceUri) {
            return null
        }
        
        val watermarks = mWatermarks.filterNotNull()
        if(watermarks.isEmpty()) {
            return null
        }
        
        val originOption = BitmapFactory.Options()
        originOption.inJustDecodeBounds = true
        
        getSourceInputStream()?.use {
            BitmapFactory.decodeStream(it, null, originOption)
        }
        
        val originW = originOption.outWidth
        val originH = originOption.outHeight
        
        val outW = (originW * outRatio).toInt()
        val outH = (originH * outRatio).toInt()
        logW("getWatermarkBitmap: [w:$outW * h:$outH]", loggable = WatermarkLogger)
        
        return createWatermark(outW, outH, originW) watermarkCanvas@{
            watermarks.forEach { watermark ->
                drawWatermark(this@watermarkCanvas, watermark, outW, outH)
            }
        }
    }

    /**
     * get input stream from original image
     */
    private fun getSourceInputStream(): InputStream? {
        val sourcePath = mSourcePath
        val sourceUri = mSourceUri
        val sourceInputStream: InputStream? = when {
            null != sourcePath -> FileInputStream(File(sourcePath))
            
            null != sourceUri  -> AppBasicShare.context.contentResolver.openInputStream(sourceUri)
            
            else               -> null
            
        }
        if(null != sourceInputStream) {
            return BufferedInputStream(sourceInputStream)
        }
        return null
    }

    /**
     * create watermark output bitmap
     */
    private fun createWatermark(outW: Int, outH: Int, originW: Int, block: Canvas.() -> Unit): Bitmap? {
        val newOption = BitmapFactory.Options()
        newOption.inJustDecodeBounds = false
        newOption.inPreferredConfig = outColorChannel
        newOption.outWidth = outW
        newOption.outHeight = outH
        newOption.inSampleSize = originW / outW
        newOption.inMutable = true
        logW("createWatermark: ${newOption.inSampleSize}", loggable = WatermarkLogger)
        
        return getSourceInputStream()?.use {
            BitmapFactory.decodeStream(it, null, newOption)?.applyCanvas(block)
        }
    }

    /**
     * draw watermark layers
     */
    private fun drawWatermark(watermarkCanvas: Canvas, watermark: AbsWatermark<*>, outW: Int, outH: Int) {
        val watermarkW = outW - watermark.getPaddingStart() - watermark.getPaddingEnd()
        val watermarkH = outH - watermark.getPaddingTop() - watermark.getPaddingBottom()
        watermark.setup(watermarkCanvas, watermarkW, watermarkH)
    }
}