package com.renhejia.robot.commandlib.parser.time

/**
 * @author liujunbin
 */
class ServerTime {
    @JvmField
    var code: Int = 0
    var msg: String? = null

    // {"code":0,"data":{"timestamp":1688544743},"msg":"success"}
    @JvmField
    var data: TimeData? = null
}

