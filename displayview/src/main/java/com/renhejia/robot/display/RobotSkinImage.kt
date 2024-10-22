package com.renhejia.robot.display

import android.graphics.Rect


open class RobotSkinImage {
    private var origRect: Rect? = null
    private var dispRect: Rect? = null

    private var origTouchRect: Rect? = null
    private var dispTouchRect: Rect? = null

    private var filePrefix: String? = null
    private var align = 0

    fun getOrigRect(): Rect? {
        return origRect
    }

    fun setOrigRect(origRect: Rect?) {
        this.origRect = origRect
    }

    fun getDispRect(): Rect? {
        return dispRect
    }

    fun setDispRect(dispRect: Rect?) {
        this.dispRect = dispRect
    }

    fun getFilePrefix(): String? {
        return filePrefix
    }

    fun setFilePrefix(filePrefix: String?) {
        this.filePrefix = filePrefix
    }


    fun getOnOffFilename(enabled: Boolean): String {
        return if (enabled) {
            filePrefix + "on.png"
        } else {
            filePrefix + "off.png"
        }
    }

    fun getNoticeFileName(): String {
        return filePrefix + "notice.png"
    }

    fun getFansIconFileName(): String {
        return filePrefix + "icon.png"
    }

    fun getFansHeadFileName(): String {
        return filePrefix + "head.png"
    }

    fun getWeatherFilename(weatherId: Int): String {
        var strWeather = "no_info"


        when (weatherId) {
            RobotClockSkin.Companion.WEATHER_TYPE_NO_INFO -> strWeather = "no_info"
            RobotClockSkin.Companion.WEATHER_TYPE_SUNNY -> strWeather = "sunny"
            RobotClockSkin.Companion.WEATHER_TYPE_CLOUDY -> strWeather = "cloudy"
            RobotClockSkin.Companion.WEATHER_TYPE_RAIN -> strWeather = "rain"
            RobotClockSkin.Companion.WEATHER_TYPE_SNOW -> strWeather = "snow"
            RobotClockSkin.Companion.WEATHER_TYPE_HAZE -> strWeather = "haze"
            RobotClockSkin.Companion.WEATHER_TYPE_SAND_DUST -> strWeather = "sand_dust"
            RobotClockSkin.Companion.WEATHER_TYPE_WIND -> strWeather = "wind"
            RobotClockSkin.Companion.WEATHER_TYPE_THUNDER -> strWeather = "thunder"
            RobotClockSkin.Companion.WEATHER_TYPE_HAIL -> strWeather = "hail"
            RobotClockSkin.Companion.WEATHER_TYPE_FOG -> strWeather = "smog"
            RobotClockSkin.Companion.WEATHER_TYPE_RAIN_HAIL -> strWeather = "rain_hail"
            RobotClockSkin.Companion.WEATHER_TYPE_RAIN_SNOW -> strWeather = "rain_snow"
            RobotClockSkin.Companion.WEATHER_TYPE_RAIN_THUNDER -> strWeather = "rain_thunder"
        }

        return "$filePrefix$strWeather.png"
    }

    fun getVolumeFilename(volume: Int): String {
        return "$filePrefix$volume.png"
    }


    fun getBackgroundFilename(): String {
        return if (filePrefix == null) {
            "background.png"
        } else {
            filePrefix + "background.png"
        }
    }

    fun getCustomizedBackgroundFilename(): String {
        return "customized_background.png"
    }

    fun getBatteryFilename(batteryLevel: Int): String {
        val level = batteryLevel / 10 * 10
        return "$filePrefix$level.png"
    }


    fun getForeground(): String {
        return if (filePrefix == null) {
            "foreground.png"
        } else {
            filePrefix + "foreground.png"
        }
    }

    fun getMiddle(): String {
        return if (filePrefix == null) {
            "middle.png"
        } else {
            filePrefix + "middle.png"
        }
    }

    fun getBatterySquaresFull(): String {
        return "battery_squares_full.png"
    }

    fun getBatterySquaresEmpty(): String {
        return "battery_squares_empty.png"
    }

    fun getStepSquaresFull(): String {
        return "step_squares_full.png"
    }

    fun getStepSquaresEmpty(): String {
        return "step_squares_empty.png"
    }

    fun getBatteryFanFull(): String {
        return "battery_fan_full.png"
    }

    fun getBatteryFanEmpty(): String {
        return "battery_fan_empty.png"
    }


    fun getAlign(): Int {
        return align
    }

    fun setAlign(align: Int) {
        this.align = align
    }

    fun getOrigTouchRect(): Rect? {
        return origTouchRect
    }

    fun setOrigTouchRect(origTouchRect: Rect?) {
        this.origTouchRect = origTouchRect
    }

    fun getDispTouchRect(): Rect? {
        return dispTouchRect
    }

    fun setDispTouchRect(dispTouchRect: Rect?) {
        this.dispTouchRect = dispTouchRect
    }
}
