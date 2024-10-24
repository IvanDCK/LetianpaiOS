package com.letianpai.robot.notice.calendar.parser

class CalenderInfo {
    var code: Int = 0
    var data: CalendarData? = null
    var msg: String? = null

    override fun toString(): String {
        return "CalenderInfo{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}
