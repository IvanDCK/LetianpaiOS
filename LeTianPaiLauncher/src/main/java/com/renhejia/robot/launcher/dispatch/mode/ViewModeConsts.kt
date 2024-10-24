package com.renhejia.robot.launcher.dispatch.mode

/**
 * 模式常量
 * @author liujunbin
 */
interface ViewModeConsts {
    companion object {
        const val VM_AUTO_MODE: Int = 1
        const val VM_STANDBY_MODE: Int = 2
        const val VM_DISPLAY_MODE: Int = 3
        const val VM_CHARGING_MODE: Int = 4
        const val VM_SLEEP_MODE: Int = 5
        const val VM_FUNCTION_MODE: Int = 6
        const val VM_CHAT_GPT_MODE: Int = 7
        const val VM_REMOTE_CONTROL_MODE: Int = 8
        const val VM_DEMOSTRATE_MODE: Int = 9
        const val VM_AUTO_PLAY_MODE: Int = 10
        const val VM_AUTO_NEW_PLAY_MODE: Int = 11

        /**
         * 语音唤醒
         */
        const val VM_AUDIO_WAKEUP_MODE: Int = 12

        /**
         * 一次性执行
         */
        const val VM_ONESHOT_MODE: Int = 13


        const val VIEW_MODE_IN: Int = 1
        const val VIEW_MODE_OUT: Int = 0
    }
}
