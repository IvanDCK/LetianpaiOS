package com.renhejia.robot.launcherbaselib.init

import android.content.Context

/**
 *
 */
class WatchClockRegManager private constructor(private val mContext: Context) {
    init {
        init(mContext)
    }

    private fun init(context: Context) {
    }

    companion object {
        fun getInstance(context: Context): WatchClockRegManager {
            return WatchClockRegManager(context.applicationContext)
        }
    }
}
