package com.renhejia.robot.launcherbaselib.info

import android.content.Context
import com.renhejia.robot.launcherbaselib.broadcast.LauncherBroadcastReceiverManager

/**
 * 手表状态持有
 */
class LauncherInfoManager private constructor(mContext: Context) {
    /**
     * 获取充电状态
     *
     * @return
     */
    /**
     * 设置充电状态
     *
     * @param isCharging
     */
    var isChargingMode: Boolean = false
    /**
     * 获取电池电量
     *
     * @return
     */
    /**
     * 设置电量等级
     *
     * @param mBatteryLevel
     */
    var batteryLevel: Int = 0
    var wifiStates: Boolean = false

    init {
        init(mContext)
    }

    private fun init(context: Context) {
    }


    companion object {
        private var instance: LauncherInfoManager? = null
        @JvmStatic
        fun getInstance(context: Context): LauncherInfoManager {
            synchronized(LauncherBroadcastReceiverManager::class.java) {
                if (instance == null) {
                    instance = LauncherInfoManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}
