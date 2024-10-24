package com.renhejia.robot.launcher.callback

/**
 * 模式切换状态回调
 *
 * @author liujunbin
 */
class CommandResponseCallback private constructor() {
    private val mLTPAppCmdResponseListener: LTPAppCmdResponseListener? = null

    private object CommandResponseCallbackHolder {
        val instance: CommandResponseCallback = CommandResponseCallback()
    }

    interface LTPAppCmdResponseListener {
        fun onAppCmdReceived(command: String?, data: String?)
    }

    fun setAppCommand(command: String?, data: String?) {
        mLTPAppCmdResponseListener?.onAppCmdReceived(command, data)
    }


    companion object {
        @JvmStatic
        val instance: CommandResponseCallback
            get() = CommandResponseCallbackHolder.instance
    }
}
