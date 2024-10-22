package com.renhejia.robot.commandlib.parser.displaymodes.weather

/**
 * 天气信息
 * @author liujunbin
 */
class WeatherData {
    var province: String? = null
    var city: String? = null
    var town: String? = null
    var wea: String? = null
    var wea_img: String? = null
    var tem: String? = null
    var tem_max: String? = null
    var tem_min: String? = null
    var win: String? = null
    var win_speed: String? = null
    var hourWeather: Array<HourWeather>? = null

    override fun toString(): String {
        return "{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", town='" + town + '\'' +
                ", wea='" + wea + '\'' +
                ", wea_img='" + wea_img + '\'' +
                ", tem='" + tem + '\'' +
                ", tem_max='" + tem_max + '\'' +
                ", tem_min='" + tem_min + '\'' +
                ", win='" + win + '\'' +
                ", win_speed='" + win_speed + '\'' +
                ", hourWeather=" + hourWeather.contentToString() +
                '}'
    }
}
