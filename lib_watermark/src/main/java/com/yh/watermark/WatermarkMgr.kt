package com.yh.watermark

import com.yh.appinject.IBaseAppInject
import com.yh.appinject.InjectHelper

/**
 * Created by CYH on 2020/4/26 13:52
 */
class WatermarkMgr private constructor() : InjectHelper<IBaseAppInject>() {

    companion object {
        @JvmStatic
        private val mInstances by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { WatermarkMgr() }

        @JvmStatic
        fun get() = mInstances
    }

}