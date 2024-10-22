package com.renhejia.robot.display.manager

import android.content.Context

/**
 * @author liujunbin
 */
class RobotSkinInfoManager private constructor(private val mContext: Context) {

    companion object {
        @Volatile private var instance: RobotSkinInfoManager? = null

        fun getInstance(context: Context): RobotSkinInfoManager {
            return instance ?: synchronized(this) {
                instance ?: RobotSkinInfoManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
