package com.renhejia.robot.commandlib.parser.power

class PowerMotion(var function: Int, var status: Int) {
    override fun toString(): String {
        return "{" +
                "function=" + function +
                ", status=" + status +
                '}'
    }
}
