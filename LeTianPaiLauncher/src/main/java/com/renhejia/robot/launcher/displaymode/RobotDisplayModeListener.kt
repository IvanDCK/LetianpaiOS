package com.renhejia.robot.launcher.displaymode

import com.renhejia.robot.commandlib.parser.displaymodes.calendar.CalenderInfo
import com.renhejia.robot.commandlib.parser.displaymodes.countdown.CountDownListInfo
import com.renhejia.robot.commandlib.parser.displaymodes.fans.FansInfo
import com.renhejia.robot.commandlib.parser.displaymodes.general.GeneralInfo
import com.renhejia.robot.commandlib.parser.displaymodes.weather.WeatherInfo

interface RobotDisplayModeListener {
    fun updateGeneralInfo(generalInfo: GeneralInfo?)
    fun updateWeather(weatherInfo: WeatherInfo?)
    fun updateCountDown(countDownListInfo: CountDownListInfo?)
    fun updateNotice(calenderInfo: CalenderInfo?)
    fun updateFansInfo(fansInfo: FansInfo?)
}
