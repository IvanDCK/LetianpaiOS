package com.renhejia.robot.commandlib.parser.displaymodes.logo

class LogoInfo {
    var code: Int = 0
    @JvmField
    var data: LogoData? = null
    var msg: String? = null

    override fun toString(): String {
        return "LogoInfo{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}
