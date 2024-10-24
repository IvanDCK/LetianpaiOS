package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.commandlib.parser.displaymodes.countdown.CountDownListInfo


/**
 * Created by liujunbin
 */
class CountDownInfoUpdateCallback private constructor() {
    private var mRobotInfoListener: CountDownInfoListener? = null

    private object RobotInfoUpdateCallbackHolder {
        val instance: CountDownInfoUpdateCallback = CountDownInfoUpdateCallback()
    }

    fun interface CountDownInfoListener {
        fun updateCountDown(countDownListInfo: CountDownListInfo)
    }

    fun seCountDownInfoUpdateListener(listener: CountDownInfoListener?) {
        this.mRobotInfoListener = listener
    }

    fun updateCountDown(countDownListInfo: CountDownListInfo) {
        if (mRobotInfoListener != null) {
            mRobotInfoListener!!.updateCountDown(countDownListInfo)
        }
    }


    companion object {
        @JvmStatic
        val instance: CountDownInfoUpdateCallback
            get() = RobotInfoUpdateCallbackHolder.instance
    }
}
