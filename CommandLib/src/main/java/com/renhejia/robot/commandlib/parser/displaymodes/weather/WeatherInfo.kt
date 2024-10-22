package com.renhejia.robot.commandlib.parser.displaymodes.weather

/**
 * 天气信息
 * @author liujunbin
 */
class WeatherInfo {
    var code: Int = 0
    var data: WeatherData? = null
    var msg: String? = null

    override fun toString(): String {
        return "{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}'
    }
}

