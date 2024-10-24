package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.launcher.displaymode.display.DisplayMode


/**
 * Created by liujunbin
 */
class DisplayInfoUpdateCallback private constructor() {
    private var mDisplayInfoUpdateListener: DisplayInfoUpdateListener? = null

    private object DisplayInfoUpdateCallbackHolder {
        val instance: DisplayInfoUpdateCallback = DisplayInfoUpdateCallback()
    }

    interface DisplayInfoUpdateListener {
        fun updateDisplayList(displayMode: DisplayMode?)
    }

    fun seDisplayInfoUpdateListener(listener: DisplayInfoUpdateListener?) {
        this.mDisplayInfoUpdateListener = listener
    }

    fun updateDisplayViewInfo(displayMode: DisplayMode?) {
        if (mDisplayInfoUpdateListener != null) {
            mDisplayInfoUpdateListener!!.updateDisplayList(displayMode)
        }
    }


    companion object {
        @JvmStatic
        val instance: DisplayInfoUpdateCallback
            get() = DisplayInfoUpdateCallbackHolder.instance
    }
}
