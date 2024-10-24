package com.renhejia.robot.launcher.displaymode.callback

import com.letianpai.robot.notice.general.parser.GeneralInfo

/**
 * @author liujunbin
 */
class GeneralInfoCallback private constructor() {
    private var mGeneralInfoUpdateListener: GeneralInfoUpdateListener? = null

    private object GeneralInfoCallbackHolder {
        val instance: GeneralInfoCallback = GeneralInfoCallback()
    }

    fun interface GeneralInfoUpdateListener {
        fun onAtCmdResultReturn(generalInfo: GeneralInfo?)
    }

    fun setGeneralInfoUpdateListener(listener: GeneralInfoUpdateListener?) {
        this.mGeneralInfoUpdateListener = listener
    }

    fun setGeneralInfo(generalInfo: GeneralInfo?) {
        if (mGeneralInfoUpdateListener != null) {
            mGeneralInfoUpdateListener!!.onAtCmdResultReturn(generalInfo)
        }
    }


    companion object {
        @JvmStatic
        val instance: GeneralInfoCallback
            get() = GeneralInfoCallbackHolder.instance
    }
}
