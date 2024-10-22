package com.renhejia.robot.commandlib.parser.displaymodes.weather

/**
 * 天气信息
 * @author liujunbin
 */
class HourWeather {
    var hour: String? = null
    var wea: String? = null
    var wea_img: String? = null
    var tem: String? = null

    override fun toString(): String {
        return "{" +
                "hour='" + hour + '\'' +
                ", wea='" + wea + '\'' +
                ", wea_img='" + wea_img + '\'' +
                ", tem='" + tem + '\'' +
                '}'
    }
}
