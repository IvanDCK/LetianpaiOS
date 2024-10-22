package com.renhejia.robot.commandlib.parser.displaymodes.logo

class LogoData {
    @JvmField
    var hello_logo: String? = null

    var desktop_logo: String? = null

    override fun toString(): String {
        return "LogoData{" +
                "hello_logo='" + hello_logo + '\'' +
                ", desktop_logo='" + desktop_logo + '\'' +
                '}'
    }
}
