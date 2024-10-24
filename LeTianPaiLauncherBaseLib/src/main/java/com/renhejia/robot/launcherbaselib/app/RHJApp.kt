package com.renhejia.robot.launcherbaselib.app

import android.app.Application

/**
 * 人和家系统app入口
 *
 */
open class RHJApp : Application() {
    override fun onCreate() {
        super.onCreate()
        rhjApp = this
    }

    companion object {
        //TODO 初始化需要的 service
        var rhjApp: RHJApp? = null
            private set

        //TODO 初始化需要的 观察者对象
        //TODO 初始化
    }
}
