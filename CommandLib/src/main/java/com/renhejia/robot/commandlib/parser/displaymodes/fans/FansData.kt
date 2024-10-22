package com.renhejia.robot.commandlib.parser.displaymodes.fans

class FansData {
    @JvmField
    var platform: String? = null
    @JvmField
    var avatar: String? = null
    @JvmField
    var nick_name: String? = null
    @JvmField
    var fans_count: String? = null

    override fun toString(): String {
        return "{" +
                "platform='" + platform + '\'' +
                ", avatar='" + avatar + '\'' +
                ", nick_name='" + nick_name + '\'' +
                ", fans_count='" + fans_count + '\'' +
                '}'
    }
}
