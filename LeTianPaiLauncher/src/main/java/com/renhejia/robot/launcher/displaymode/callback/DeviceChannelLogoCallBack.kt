package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.commandlib.parser.displaymodes.logo.LogoInfo

class DeviceChannelLogoCallBack private constructor() {
    private var mDeviceChannelLogoUpdateListener: DeviceChannelLogoUpdateListener? = null


    private object DeviceChannelLogoCallBackHolder {
        val instance: DeviceChannelLogoCallBack = DeviceChannelLogoCallBack()
    }

    interface DeviceChannelLogoUpdateListener {
        fun onLogoInfoUpdate(logoInfo: LogoInfo?)
    }

    fun getmDeviceChannelLogoUpdateListener(): DeviceChannelLogoUpdateListener? {
        return mDeviceChannelLogoUpdateListener
    }

    fun setDeviceChannelLogoUpdateListener(mDeviceChannelLogoUpdateListener: DeviceChannelLogoUpdateListener?) {
        this.mDeviceChannelLogoUpdateListener = mDeviceChannelLogoUpdateListener
    }


    fun setDeviceChannelLogo(logoInfo: LogoInfo?) {
        if (mDeviceChannelLogoUpdateListener != null) {
            mDeviceChannelLogoUpdateListener!!.onLogoInfoUpdate(logoInfo)
        }
    }

    companion object {
        @JvmStatic
        val instance: DeviceChannelLogoCallBack
            get() = DeviceChannelLogoCallBackHolder.instance
    }
}
