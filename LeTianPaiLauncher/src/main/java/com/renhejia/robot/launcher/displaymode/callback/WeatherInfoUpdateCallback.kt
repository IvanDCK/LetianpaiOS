package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.commandlib.parser.displaymodes.weather.WeatherInfo


/**
 * Created by liujunbin
 */
class WeatherInfoUpdateCallback private constructor() {
    private var mWeatherUpdateListener: WeatherUpdateListener? = null

    private object RobotInfoUpdateCallbackHolder {
        val instance: WeatherInfoUpdateCallback = WeatherInfoUpdateCallback()
    }

    interface WeatherUpdateListener {
        fun updateWeather(weatherInfo: WeatherInfo?)
    }

    fun setWeatherUpdateListener(listener: WeatherUpdateListener?) {
        this.mWeatherUpdateListener = listener
    }


    fun updateWeather(weatherInfo: WeatherInfo?) {
        if (mWeatherUpdateListener != null) {
            mWeatherUpdateListener!!.updateWeather(weatherInfo)
        }
    }


    companion object {
        @JvmStatic
        val instance: WeatherInfoUpdateCallback
            get() = RobotInfoUpdateCallbackHolder.instance
    }
}
