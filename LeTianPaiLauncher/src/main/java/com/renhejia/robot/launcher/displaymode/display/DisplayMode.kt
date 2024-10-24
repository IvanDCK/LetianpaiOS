package com.renhejia.robot.launcher.displaymode.display

/**
 * 显示模式状态
 */
class DisplayMode {
    var general_display_switch: Int = 0
    var weather_display_switch: Int = 0
    var calendar_display_switch: Int = 0
    var countdown_display_switch: Int = 0
    var fans_display_switch: Int = 0

    override fun toString(): String {
        return "DisplayMode{" +
                "general_display_switch=" + general_display_switch +
                ", weather_display_switch=" + weather_display_switch +
                ", calendar_display_switch=" + calendar_display_switch +
                ", countdown_display_switch=" + countdown_display_switch +
                ", fans_display_switch=" + fans_display_switch +
                '}'
    }
}
