package com.renhejia.robot.gesturefactory.util

/**
 * 命令定义常量
 */
interface CommandConst {
    companion object {
        const val CMD_WALK_FORWARD: String = "walk_forward"
        const val CMD_WALK_BACK: String = "walk_back"


        const val CMD_FROM: String = "cmd_from"
        const val CMD_FROM_VOICE: String = "voice"
        const val CMD_FROM_SENSOR: String = "sensor"
        const val CMD_FROM_APP: String = "app"
        const val CMD_FROM_S_APP: String = "sapp"
        const val CMD_FROM_CAMERA: String = "camera"
    }
}
