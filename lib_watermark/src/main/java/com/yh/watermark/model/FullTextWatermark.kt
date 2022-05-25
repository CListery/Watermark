package com.yh.watermark.model

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.text.TextPaint
import com.yh.appbasic.logger.ext.libW
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
    
    override fun draw(watermarkCanvas: Canvas, paint: TextPaint, width: Int, height: Int) {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        WatermarkMgr.get().libW("make: bounds- $bounds")
        
        var boundWidth = bounds.width()
        if(boundWidth <= 0) {
            return
        }
        val newWidth = getDistanceByPoint(PointF(0F, 0F), PointF(width.toFloat(), height.toFloat())).toInt()
        val newText = if(boundWidth < newWidth) {
            var tmpText = text
            do {
                tmpText = tmpText.plus(" ").plus(text)
                paint.getTextBounds(tmpText, 0, tmpText.length, bounds)
                boundWidth = bounds.width()
            } while(boundWidth < newWidth)
            tmpText
        } else {
            text
        }
        
        val lineH = height.toFloat() / lineCount
        val lineCenterY = (lineH - bounds.bottom) / 2
        setupCanvas(watermarkCanvas, width, height, 0, 0) {
            val bgPaint = Paint()
            bgPaint.color = backgroundColor
            drawRect(Rect(0, 0, newWidth, height), bgPaint)
            for(line in 1..lineCount) {
                this.drawText(newText, (width - newWidth) / 2F, line * lineH - lineCenterY, paint)
            }
        }
    }
    
    private fun getDistanceByPoint(p0: PointF, p1: PointF): Float {
        return sqrt((p0.y - p1.y).pow(2F) + (p0.x - p1.x).pow(2F))
    }
}