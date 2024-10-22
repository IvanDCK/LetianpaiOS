package com.renhejia.robot.commandlib.parser.displaymodes.calendar

class CalenderInfo {
    var code: Int = 0
    @JvmField
    var data: CalendarData? = null
    var msg: String? = null

    override fun toString(): String {
        return "{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}
