package com.renhejia.robot.launcher.displaymode.manager

import android.content.Context
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.isNetworkAvailable
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager

/**
 * @author liujunbin
 */
class DisplayModeInfoManager private constructor(private var mContext: Context) {
    init {
        init(mContext)
    }

    private fun init(context: Context) {
        this.mContext = context
        displayInfo
    }

    private val displayInfo: Unit
        get() {
            if (isNetworkAvailable(mContext)) {
//            getCalendarList();
//            getCountDownList();
//            getFansInfos();
                GeeUINetResponseManager.getInstance(mContext).updateGeneralInfo()
                //            getClockList();
//            getWeather();
            }
        }


    companion object {
        private var instance: DisplayModeInfoManager? = null
        fun getInstance(context: Context): DisplayModeInfoManager {
            synchronized(DisplayModeInfoManager::class.java) {
                if (instance == null) {
                    instance = DisplayModeInfoManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}