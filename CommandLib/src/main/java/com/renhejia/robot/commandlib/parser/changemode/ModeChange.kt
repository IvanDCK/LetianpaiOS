package com.renhejia.robot.commandlib.parser.changemode

class ModeChange {
    var mode: String? = null
    var mode_status: Int = 0

    override fun toString(): String {
        return "ModeChange{" +
                "mode='" + mode + '\'' +
                ", mode_status=" + mode_status +
                '}'
    }
}
