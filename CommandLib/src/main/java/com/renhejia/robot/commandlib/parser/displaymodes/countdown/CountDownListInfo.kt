package com.renhejia.robot.commandlib.parser.displaymodes.countdown

/**
 * @author liujunbin
 */
class CountDownListInfo {
    var code: Int = 0
    @JvmField
    var data: CountDownListData? = null
    var msg: String? = null

    override fun toString(): String {
        return "{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}
