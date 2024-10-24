package com.letianpai.robot.audioservice

interface AudioServiceConst {
    companion object {
        const val ROBOT_STATUS_SILENCE: String = "avatar.silence"

        const val ROBOT_STATUS_LISTENING: String = "avatar.listening"

        const val ROBOT_STATUS_UNDERSTANDING: String = "avatar.understanding"

        const val ROBOT_STATUS_SPEAKING: String = "avatar.speaking"

        const val ROBOT_STATUS_DEFAULT: String = "avatar.silence"
    }
}
