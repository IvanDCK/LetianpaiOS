package com.renhejia.robot.commandlib.parser.displaymodes.general

class GeneralData {
    var wea: String? = null
    var wea_img: String? = null
    var tem: String? = null
    var calender_total: Int = 0

    override fun toString(): String {
        return "GeneralData{" +
                "wea='" + wea + '\'' +
                ", wea_img='" + wea_img + '\'' +
                ", tem='" + tem + '\'' +
                ", calender_total=" + calender_total +
                '}'
    }
}
