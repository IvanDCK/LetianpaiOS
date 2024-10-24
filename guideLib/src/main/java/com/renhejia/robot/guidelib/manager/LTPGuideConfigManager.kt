package com.renhejia.robot.guidelib.manager

import android.content.Context

/**
 * Launcher 偏好设置管理器
 */
class LTPGuideConfigManager private constructor(private val mContext: Context) : GuideConfigConst {
    private val mGuideSharedPreference = GuideSharedPreference(
        mContext,
        GuideSharedPreference.SHARE_PREFERENCE_NAME,
        GuideSharedPreference.ACTION_INTENT_CONFIG_CHANGE
    )

    /**
     * 增加偏好设置初始化逻辑
     * (暂时没有用，预留给将来手表需要初始化一些状态值时使用)
     */
    private fun initKidSmartConfigState() {
    }

    fun commit(): Boolean {
        return mGuideSharedPreference.commit()
    }


    var sSID: String?
        get() {
            val skinPath =
                mGuideSharedPreference.getString(GuideConfigConst.KEY_SSID, "")
            return skinPath
        }
        set(ssid) {
            mGuideSharedPreference.putString(GuideConfigConst.KEY_SSID, ssid!!)
        }

    var password: String?
        get() {
            val skinPath =
                mGuideSharedPreference.getString(GuideConfigConst.KEY_PASSWORD, "")
            return skinPath
        }
        set(ssid) {
            mGuideSharedPreference.putString(GuideConfigConst.KEY_PASSWORD, ssid!!)
        }


    var isActivated: Boolean
        get() = mGuideSharedPreference.getBoolean(
            GuideConfigConst.KEY_ACTIVATED,
            false
        )
        set(activated) {
            mGuideSharedPreference.putBoolean(GuideConfigConst.KEY_ACTIVATED, activated)
        }

    var isRandom: Boolean
        get() {
            val isActivated =
                mGuideSharedPreference.getBoolean(GuideConfigConst.KEY_RANDOM, false)
            return isActivated
        }
        set(activated) {
            mGuideSharedPreference.putBoolean(GuideConfigConst.KEY_RANDOM, activated)
        }


    companion object {
        private var mLTPGuideConfigManager: LTPGuideConfigManager? = null
        @JvmStatic
        fun getInstance(context: Context): LTPGuideConfigManager? {
            if (mLTPGuideConfigManager == null) {
                mLTPGuideConfigManager = LTPGuideConfigManager(context)
                mLTPGuideConfigManager!!.initKidSmartConfigState()
                mLTPGuideConfigManager!!.commit()
            }
            return mLTPGuideConfigManager
        }
    }
}
