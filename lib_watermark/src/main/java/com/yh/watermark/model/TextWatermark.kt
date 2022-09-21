package com.yh.watermark.model

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.ColorInt
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.yh.appbasic.logger.logW
import com.yh.appbasic.share.AppBasicShare
import com.yh.watermark.WatermarkLogger
import kotlin.math.min

/**
 * Created by CYH on 2020/4/26 13:50
 */
open class TextWatermark(protected val text: String) : AbsWatermark<TextPaint>() {
    
    private var maxW: Int = -1
    private var maxTextSize: Float = 20F
    @ColorInt
    private var textColor = Color.BLACK
    private var textStyle = Paint.Style.FILL
    @FontRes
    private var textFount = 0
    private var textShadowBlurRadius = 0F
    private var textShadowXOffset = 0F
    private var textShadowYOffset = 0F
    @ColorInt
    private var textShadowColor = Color.WHITE
    
    fun setMax(maxW: Int = -1, maxTextSize: Float): TextWatermark {
        this.maxW = maxW
        this.maxTextSize = maxTextSize
        return this
    }
    
    fun setTextStyle(@ColorInt textColor: Int, textStyle: Paint.Style, @FontRes textFount: Int): TextWatermark {
        this.textColor = textColor
        this.textStyle = textStyle
        this.textFount = textFount
        return this
    }
    
    /**
     * Set the shadow of the text watermark.
     */
    fun setTextShadow(blurRadius: Float, shadowXOffset: Float, shadowYOffset: Float, @ColorInt shadowColor: Int): TextWatermark {
        textShadowBlurRadius = blurRadius
        textShadowXOffset = shadowXOffset
        textShadowYOffset = shadowYOffset
        textShadowColor = shadowColor
        return this
    }
    
    override fun createPaint(): TextPaint {
        val paint = TextPaint()
        paint.color = textColor
        paint.style = textStyle
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.LEFT
        
        if(textShadowBlurRadius != 0F || textShadowXOffset != 0F || textShadowYOffset != 0F) {
            paint.setShadowLayer(textShadowBlurRadius, textShadowXOffset, textShadowYOffset, textShadowColor)
        }
        if(textFount != 0) {
            val typeface = ResourcesCompat.getFont(AppBasicShare.context, textFount)
            if(null != typeface) {
                paint.typeface = typeface
            }
        }
        return paint
    }
    
    override fun setupPaintStyle(paint: TextPaint, width: Int, height: Int) {
        super.setupPaintStyle(paint, width, height)
        val textSizePixel = dp2pxByDst(maxTextSize, width, height)
        logW("setupPaintStyle: $textSizePixel", loggable = WatermarkLogger)
        paint.textSize = textSizePixel
    }
    
    override fun draw(watermarkCanvas: Canvas, paint: TextPaint, width: Int, height: Int) {
        val textMaxWidth = if(maxW <= 0) min(paint.measureText(text).toInt(), width)
        else maxW
        logW("make: $textMaxWidth", loggable = WatermarkLogger)
        if(textMaxWidth <= 0) {
            return
        }
        
        val staticLayout = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION") StaticLayout(text, 0, text.length, paint, textMaxWidth, Layout.Alignment.ALIGN_NORMAL, 1.2F, 0F, true)
        } else {
            StaticLayout.Builder.obtain(text, 0, text.length, paint, textMaxWidth).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(0F, 1.2F).setIncludePad(true).build()
        }
        
        val lineCount = staticLayout.lineCount
        logW("text2Bitmap: lineCount- $lineCount", loggable = WatermarkLogger)
        if(lineCount <= 0) {
            return
        }
        
        val fontMetrics = paint.fontMetrics
        val distance = fontMetrics.bottom - fontMetrics.top
        val totalHeight = (distance * staticLayout.spacingMultiplier * lineCount).toInt()
        
        if(textMaxWidth > 0 && totalHeight > 0) {
            setupCanvas(watermarkCanvas, width, height, textMaxWidth, totalHeight) {
                val bgPaint = Paint()
                bgPaint.color = backgroundColor
                drawRect(Rect(0, 0, textMaxWidth, totalHeight), bgPaint)
                staticLayout.draw(this)
            }
        }
    }
}