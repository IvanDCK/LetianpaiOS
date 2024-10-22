package com.renhejia.robot.commandlib.consts

/**
 * 分发命令
 * @author liujunbin
 */
interface DistributeCommands {
    companion object {
        /**
         * 启动视频通话
         */
        const val START_VIDEO_CALL: String = "start_video_call"

        /**
         * 关闭机器人
         */
        const val SHUTDOWN_REBOOT: String = "REBOOT_SHUTDOWN"

        /**
         * 启动
         */
        const val ACTION_START_COMPLETED: String = "android.intent.action.START_COMPLETED"
    }
}
