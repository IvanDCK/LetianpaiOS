package com.renhejia.robot.launcher.dispatch.remote

/**
 *
 * @author liujunbin
 */
class RemoteCmdCallback private constructor() {
    private var mRemoteCmdListener: RemoteCmdListener? = null

    private object RemoteCmdCallbackHolder {
        val instance: RemoteCmdCallback = RemoteCmdCallback()
    }

    interface RemoteCmdListener {
        fun onRemoteCmdReceived(commandType: String?, commandData: Any?)
    }

    fun setRemoteCmdReceivedListener(listener: RemoteCmdListener?) {
        this.mRemoteCmdListener = listener
    }

    fun setRemoteCmd(commandType: String?, commandData: Any?) {
        if (mRemoteCmdListener != null) {
            mRemoteCmdListener!!.onRemoteCmdReceived(commandType, commandData)
        }
    }


    companion object {
        @JvmStatic
        val instance: RemoteCmdCallback
            get() = RemoteCmdCallbackHolder.instance
    }
}
