package com.renhejia.robot.commandlib.consts

interface CommandConsts {
    companion object {
        /**
         * 命令key
         */
        const val COMMAND_KEY: String = "command_key"

        /**
         * 命令类型
         */
        const val COMMAND_TYPE: String = "command_type"

        /**
         * 语音命令
         */
        const val COMMAND_TYPE_VOICE: String = "command_voice"

        /**
         * 足部引擎
         */
        const val COMMAND_TYPE_FOOT_ENGINE: String = "command_foot_engine"

        /**
         * 耳朵引擎
         */
        const val COMMAND_TYPE_EARS_ENGINE: String = "command_ears_engine"

        /**
         * 视觉命令
         */
        const val COMMAND_TYPE_VISUAL: String = "command_visual"

        /**
         * 语音命令
         */
        const val COMMAND_TYPE_SPEECH: String = "command_speech"

        /**
         * 视频通话
         */
        const val COMMAND_TYPE_VIDEO_CALL: String = "command_video_call"

        /**
         * 远程查看
         */
        const val COMMAND_TYPE_REMOTE_VIEW: String = "command_remote_view"

        /**
         * 唤醒词
         */
        const val COMMAND_TYPE_WAKE_UP: String = "command_wake_up"
    }
}
