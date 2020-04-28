package com.yh.watermark.model

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import com.yh.watermark.WatermarkMgr

/**
 * Created by CYH on 2020/4/26 18:04
 */
abstract class AbsWatermark<P : Paint> {

    @IntRange(from = 0, to = 255)
    protected var alpha: Int = 0xFF
    @ColorInt
    protected var backgroundColor = Color.TRANSPARENT
    private var padding = arrayOf(0, 0, 0, 0)
    private var gravity: Int = Gravity.START or Gravity.TOP
    protected val position: WatermarkPosition = WatermarkPosition()

    fun setRotationAngle(rotationAngle: Float): AbsWatermark<P> {
        position.rotationAngle = rotationAngle
        return this
    }

    fun getRotationAngle() = position.rotationAngle

    fun setXY(x: Float, y: Float): AbsWatermark<P> {
        position.positionX = x
        position.positionY = y
        return this
    }

    fun getX() = position.positionX
    fun getY() = position.positionY

    fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int): AbsWatermark<P> {
        this.alpha = alpha
        return this
    }

    fun setGravity(gravity: Int): AbsWatermark<P> {
        this.gravity = gravity
        return this
    }

    fun setPadding(start: Int = 0, top: Int = 0, end: Int = 0, bottom: Int = 0): AbsWatermark<P> {
        padding[0] = start
        padding[1] = top
        padding[2] = end
        padding[3] = bottom
        return this
    }

    fun getPaddingStart(): Int = dip2Pixel(padding[0].toFloat()).toInt()
    fun getPaddingTop(): Int = dip2Pixel(padding[1].toFloat()).toInt()
    fun getPaddingEnd(): Int = dip2Pixel(padding[2].toFloat()).toInt()
    fun getPaddingBottom(): Int = dip2Pixel(padding[3].toFloat()).toInt()

    fun getGravity() = gravity

    protected fun dip2Pixel(dip: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            WatermarkMgr.get().ctx().resources.displayMetrics
        )
    }

    fun createWatermarkBitmap(width: Int, height: Int): Bitmap? {
        val paint = createPaint()
        setupPaintStyle(paint, width, height)
        return make(paint, width, height)
    }

    protected abstract fun createPaint(): P
    protected open fun setupPaintStyle(paint: P, width: Int, height: Int) {
        paint.alpha = alpha
    }

    protected abstract fun make(paint: P, width: Int, height: Int): Bitmap?
}