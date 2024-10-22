package com.renhejia.robot.display

//        currentTemp = NO_TEMP;
class RobotPlatformState {
    var batteryLevel: Int = 80 // 电池电量
    var stepNumber: Int = 12345 // 记步
    var mediaVolume: Int = 3 // 媒体音量
    var bluetoothEnabled: Boolean = false // 蓝牙开关
    var wifiEnabled: Boolean = true // wifi开关
    var batteryCharging: Boolean = true // 充电状态
    var weatherState: Int = 1 // 天气情况
    var currentTemp: Int = 26 // 实时温度
    var tempRange: String = "32° /10°" // 区间
    var airQuality: Int = 256 // 空气质量

    companion object {
        var NO_TEMP: Int = -99 // 无温度信息
        var NO_WEATHER: Int = -99 // 无温度信息
    }
}
