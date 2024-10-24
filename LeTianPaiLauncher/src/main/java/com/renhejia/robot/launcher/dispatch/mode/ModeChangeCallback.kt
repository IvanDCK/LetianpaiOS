package com.renhejia.robot.launcher.dispatch.mode

/**
 * 模式切换状态回调
 * @author liujunbin
 */
class ModeChangeCallback private constructor() {
    private var mModeChangeListener: ModeChangeListener? = null

    private object MModeChangeCallbackHolder {
        val instance: ModeChangeCallback = ModeChangeCallback()
    }

    interface ModeChangeListener {
        fun onViewModeChanged(viewMode: Int)
    }

    fun setViewModeChangeListener(listener: ModeChangeListener?) {
        this.mModeChangeListener = listener
    }

    fun setModeChange(viewMode: Int) {
        if (mModeChangeListener != null) {
            mModeChangeListener!!.onViewModeChanged(viewMode)
        }
    }


    companion object {
        @JvmStatic
        val instance: ModeChangeCallback
            get() = MModeChangeCallbackHolder.instance
    }
}
