package com.renhejia.robot.launcherbaselib.init

import android.content.Context
import com.renhejia.robot.launcherbaselib.broadcast.LauncherBroadcastReceiverManager

class LauncherInitManager private constructor(private val mContext: Context) {
    init {
        init(mContext)
    }

    private fun init(context: Context) {
        LauncherBroadcastReceiverManager.Companion.getInstance(context)


        //TODO 其他的初始化
    }


    companion object {
        fun getInstance(context: Context): LauncherInitManager {
            return LauncherInitManager(context.applicationContext)
        }
    }
}
