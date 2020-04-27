package com.yh.demo

import android.app.Application
import android.util.Log
import com.yh.appinject.IBaseAppInject
import com.yh.watermark.WatermarkMgr

/**
 * Created by CYH on 2020/4/26 15:13
 */
class App : Application(), IBaseAppInject {
    override fun getApplication(): Application {
        return this
    }

    override fun getNotificationIcon(): Int {
        return R.mipmap.ic_launcher
    }

    override fun showTipMsg(errorMsg: String) {
        Log.w("App", "showTip: $errorMsg")
    }

    override fun onCreate() {
        super.onCreate()

        WatermarkMgr.get().register(this)

    }

}