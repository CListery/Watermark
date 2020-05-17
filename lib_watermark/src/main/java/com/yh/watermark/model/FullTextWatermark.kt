package com.yh.watermark.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextPaint
import com.yh.appinject.logger.ext.libW
import com.yh.watermark.WatermarkMgr
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Created by CYH on 2020/4/27 10:29
 */
class FullTextWatermark(text: String) : TextWatermark(text) {

    private var lineCount: Int = 3

    fun setLineSpace(lineCount: Int): FullTextWatermark {
        this.lineCount = lineCount
        return this
    }

    override fun make(paint: TextPaint, width: Int, height: Int): Bitmap? {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        WatermarkMgr.get().libW("make: bounds- $bounds")

        var boundWidth = bounds.width()
        if (boundWidth <= 0) {
            return null
        }
        val newWidth = getDistanceByPoint(PointF(0F, 0F), PointF(width.toFloat(), height.toFloat())).toInt()
        val newText = if (boundWidth < newWidth) {
            var tmpText = text
            do {
                tmpText = tmpText.plus(" ").plus(text)
                paint.getTextBounds(tmpText, 0, tmpText.length, bounds)
                boundWidth = bounds.width()
            } while (boundWidth < newWidth)
            tmpText
        } else {
            text
        }

        val textWatermarkBitmap = Bitmap.createBitmap(newWidth, height, Bitmap.Config.ARGB_8888)
        WatermarkMgr.get().libW( "make: [${textWatermarkBitmap.width} * ${textWatermarkBitmap.height}]")
        val canvas = Canvas(textWatermarkBitmap)
        canvas.drawColor(backgroundColor)

        val lineH = height.toFloat() / lineCount
        val lineCenterY = (lineH - bounds.bottom) / 2
        var startY: Float
        for (line in 1..lineCount) {
            startY = line * lineH - lineCenterY
            canvas.drawText(newText, 0F, startY, paint)
        }

        return textWatermarkBitmap
    }

    private fun getDistanceByPoint(p0: PointF, p1: PointF): Float {
        return sqrt((p0.y - p1.y).pow(2F) + (p0.x - p1.x).pow(2F))
    }
}