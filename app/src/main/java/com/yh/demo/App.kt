package com.yh.demo

import android.app.Application
import android.util.Log
import com.yh.appinject.IBaseAppInject
import com.yh.appbasic.logger.LogsManager
import com.yh.appbasic.logger.logW
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
        logW("showTip: $errorMsg")
    }

    override fun onCreate() {
        super.onCreate()

        LogsManager.get().setDefLoggerConfig(false to 0, true to 0)

        WatermarkMgr.get().apply {
            loggerConfig(true to Log.VERBOSE)
            register(this@App)
        }

    }

}