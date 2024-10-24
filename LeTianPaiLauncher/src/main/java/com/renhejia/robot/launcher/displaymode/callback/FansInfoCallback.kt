package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.commandlib.parser.displaymodes.fans.FansInfo

/**
 * @author liujunbin
 */
class FansInfoCallback private constructor() {
    private var mFansInfoUpdateListener: FansInfoUpdateListener? = null

    private object FansInfoCallbackHolder {
        val instance: FansInfoCallback = FansInfoCallback()
    }

    fun interface FansInfoUpdateListener {
        fun onFansInfoUpdate(fansInfo: FansInfo)
    }

    fun setFansInfoUpdateListener(listener: FansInfoUpdateListener?) {
        this.mFansInfoUpdateListener = listener
    }

    fun setFansInfo(fansInfo: FansInfo) {
        if (mFansInfoUpdateListener != null) {
            mFansInfoUpdateListener!!.onFansInfoUpdate(fansInfo)
        }
    }

    companion object {
        @JvmStatic
        val instance: FansInfoCallback
            get() = FansInfoCallbackHolder.instance
    }
}
