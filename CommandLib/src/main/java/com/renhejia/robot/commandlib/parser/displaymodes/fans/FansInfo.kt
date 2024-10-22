package com.renhejia.robot.commandlib.parser.displaymodes.fans

/**
 * @author liujunbin
 */
class FansInfo {
    var code: Int = 0
    @JvmField
    var data: Array<FansData>? = null
    var msg: String? = null

    override fun toString(): String {
        return "{" +
                "code=" + code +
                ", data=" + data.contentToString() +
                ", msg='" + msg + '\'' +
                '}'
    }
}
