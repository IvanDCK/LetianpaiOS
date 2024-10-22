package com.renhejia.robot.commandlib.parser.displaymodes.countdown

/**
 * @author liujunbin
 */
class CountDownEventItem {
    @JvmField
    var event_title: String? = null
    var event_time: Long = 0
    @JvmField
    var remain_days: Int = 0

    override fun toString(): String {
        return "{" +
                "event_title='" + event_title + '\'' +
                ", event_time=" + event_time +
                ", remain_days=" + remain_days +
                '}'
    }
}
