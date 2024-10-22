package com.renhejia.robot.commandlib.parser.clock

class ClockInfo {
    var clock_id: Int = 0
    var clock_hour: Int = 0
    var clock_time: String? = null
    var clock_title: String? = null
    var is_on: Int = 0
    var repeat_method: List<Int> = emptyList()
    var repeat_method_label: String? = null


    override fun toString(): String {
        return "ClockInfo{" +
                "clock_id=" + clock_id +
                ", clock_hour=" + clock_hour +
                ", clock_time='" + clock_time + '\'' +
                ", clock_title='" + clock_title + '\'' +
                ", is_on=" + is_on +
                ", repeat_method=" + repeat_method +
                ", repeat_method_label='" + repeat_method_label + '\'' +
                '}'
    }
}
