package com.renhejia.robot.launcher.dispatch.command

import android.util.Log

/**
 * 模式切换状态回调
 *
 * @author liujunbin
 */
class CommandResponseCallback private constructor() {
    private var mModeChangeListener: CommandResponseListener? = null
    private var mLTPCommandResponseListener: LTPCommandResponseListener? = null

    private object CommandResponseCallbackHolder {
        val instance: CommandResponseCallback = CommandResponseCallback()
    }

    interface CommandResponseListener {
        fun onCommandReceived(commandFrom: String?, commandType: String?, commandData: Any?)
    }

    interface LTPCommandResponseListener {
        fun onLTPCommandReceived(command: String?, data: String?)
    }

    fun setCommandReceivedListener(listener: CommandResponseListener?) {
        this.mModeChangeListener = listener
    }

    fun setLTPCommandResponseListener(listener: LTPCommandResponseListener?) {
        this.mLTPCommandResponseListener = listener
    }


    fun setCommand(commandFrom: String?, commandType: String?, commandData: Any?) {
        if (mModeChangeListener != null) {
            mModeChangeListener!!.onCommandReceived(commandFrom, commandType, commandData)
        }
    }

    //    public void setLTPCommand(LtpCommand ltpCommand) {
    //        Log.e("letianpai_test_control","switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.1 ============mode: ");
    //        if (mLTPCommandResponseListener != null) {
    //            Log.e("letianpai_test_control","switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.2 ============mode: ");
    //            mLTPCommandResponseListener.onLTPCommandReceived(ltpCommand);
    //        }else{
    //            Log.e("letianpai_test_control","switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.3 ============mode: mLTPCommandResponseListener is null");
    //        }
    //    }
    fun setLTPCommand(command: String?, data: String?) {
        Log.e(
            "letianpai_test_control",
            "switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.1 ============mode: "
        )
        if (mLTPCommandResponseListener != null) {
            Log.e(
                "letianpai_test_control",
                "switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.2 ============mode: "
            )
            mLTPCommandResponseListener!!.onLTPCommandReceived(command, data)
        } else {
            Log.e(
                "letianpai_test_control",
                "switchToNewAutoPlayMode === COMMAND_VALUE_CHANGE_MODE_ROBOT ======= 9.3 ============mode: mLTPCommandResponseListener is null"
            )
        }
    }


    companion object {
        @JvmStatic
        val instance: CommandResponseCallback
            get() = CommandResponseCallbackHolder.instance
    }
}
