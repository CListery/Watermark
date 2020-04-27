package com.yh.watermark.model

import androidx.annotation.FloatRange

/**
 * Created by CYH on 2020/4/26 15:19
 */
class WatermarkPosition {

    @FloatRange(from = 0.0, to = 1.0)
    var positionX = 0.0F
    @FloatRange(from = 0.0, to = 1.0)
    var positionY = 0.0F
    var rotationAngle = 0.0F

}