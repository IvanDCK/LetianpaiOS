package com.renhejia.robot.display.manager

import android.content.Context

/**
 * Launcher 偏好设置管理器
 */
class RobotClockConfigManager private constructor(private val mContext: Context) :
    ClockConfigConst {
    private val mKidSharedPreference: WatchSharedPreference


    init {
        this.mKidSharedPreference = WatchSharedPreference(
            mContext,
            WatchSharedPreference.Companion.SHARE_PREFERENCE_NAME,
            WatchSharedPreference.Companion.ACTION_INTENT_CONFIG_CHANGE
        )
    }

    /**
     * 增加偏好设置初始化逻辑
     * (暂时没有用，预留给将来手表需要初始化一些状态值时使用)
     */
    private fun initKidSmartConfigState() {
    }

    fun commit(): Boolean {
        return mKidSharedPreference.commit()
    }

    var lTPClockSkinPath: String?
        /**
         *
         * @return
         */
        get() {
            val skinPath: String? = mKidSharedPreference.getString(
                ClockConfigConst.Companion.KEY_LTP_CLOCK_SKIN_PATH,
                ""
            )
            return skinPath
        }
        /**
         */
        set(skinPath) {
            mKidSharedPreference.putString(
                ClockConfigConst.Companion.KEY_LTP_CLOCK_SKIN_PATH,
                skinPath!!
            )
        }

    var watchHeight: Int
        /**
         * 获取手表高度
         *
         * @return
         */
        get() = mKidSharedPreference.getInt(
            ClockConfigConst.Companion.KEY_WATCH_HEIGHT,
            ClockConfigConst.Companion.VALUE_WATCH_HEIGHT_DEFAULT
        )
        /**
         * 设置手表高度
         *
         * @return
         */
        set(height) {
            mKidSharedPreference.putInt(ClockConfigConst.Companion.KEY_WATCH_HEIGHT, height)
        }

    var watchWidth: Int
        /**
         * 获取手表宽度
         *
         * @return
         */
        get() {
            return mKidSharedPreference.getInt(
                ClockConfigConst.Companion.KEY_WATCH_WIDTH,
                ClockConfigConst.Companion.VALUE_WATCH_WIDTH_DEFAULT
            )
        }
        /**
         * 设置手表宽度
         *
         * @return
         */
        set(width) {
            mKidSharedPreference.putInt(ClockConfigConst.Companion.KEY_WATCH_WIDTH, width)
        }


    companion object {
        private var mLauncherConfigManager: RobotClockConfigManager? = null
        fun getInstance(context: Context): RobotClockConfigManager? {
            if (mLauncherConfigManager == null) {
                mLauncherConfigManager = RobotClockConfigManager(context)
                mLauncherConfigManager!!.initKidSmartConfigState()
                mLauncherConfigManager!!.commit()
            }
            return mLauncherConfigManager
        }
    }
}
