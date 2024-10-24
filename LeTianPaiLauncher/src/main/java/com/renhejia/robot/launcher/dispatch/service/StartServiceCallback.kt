package com.renhejia.robot.launcher.dispatch.service

/**
 * 模式切换状态回调
 *
 * @author liujunbin
 */
class StartServiceCallback private constructor() {
    private var mStartServiceListener: StartServiceListener? = null

    private object StartServiceCallbackHolder {
        val instance: StartServiceCallback = StartServiceCallback()
    }

    interface StartServiceListener {
        fun onServiceStart()
    }

    fun setStartServiceListener(listener: StartServiceListener?) {
        this.mStartServiceListener = listener
    }

    fun startService() {
        if (mStartServiceListener != null) {
            mStartServiceListener!!.onServiceStart()
        }
    }

    companion object {
        @JvmStatic
        val instance: StartServiceCallback
            get() = StartServiceCallbackHolder.instance
    }
}
