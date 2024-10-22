package com.renhejia.robot.commandlib.consts

object RobotRemoteConsts {
    /**
     * OTA升级
     */
    const val COMMAND_TYPE_OTA: String = "otaUpgrade"

    /**
     * 更新wifi配置
     */
    const val COMMAND_TYPE_UPDATE_WIFI_CONFIG: String = "updateWifiConfig"

    /**
     * 更新ble配置
     */
    const val COMMAND_TYPE_UPDATE_BLE_CONFIG: String = "updateBleConfig"

    /**
     * 更新显示模式配置
     */
    const val COMMAND_TYPE_UPDATE_SHOW_MODE_CONFIG: String = "updateShowModeConfig"

    /**
     * 绑定到米家
     */
    const val COMMAND_TYPE_BIND_MIIOT: String = "startBindMijia"


    /**
     * 更新睡眠模式配置
     */
    const val COMMAND_TYPE_UPDATE_SLEEP_MODE_CONFIG: String = "updateSleepModeConfig"

    /**
     * 更新睡眠模式配置
     */
    const val COMMAND_TYPE_UPDATE_AWAKE_CONFIG: String = "updateAwakeConfig"

    /**
     * 更新通用配置
     */
    const val COMMAND_TYPE_UPDATE_GENERAL_CONFIG: String = "updateGeneralConfig"

    /**
     * 更新日期配置
     */
    const val COMMAND_TYPE_UPDATE_DATE_CONFIG: String = "updateDateConfig"

    /**
     * 更新日历配置
     */
    const val COMMAND_TYPE_UPDATE_CALENDAR_CONFIG: String = "updateCalendarConfig"

    /**
     * 更新粉丝配置
     */
    const val COMMAND_TYPE_UPDATE_FANS_CONFIG: String = "updateFansConfig"

    /**
     * 更新倒计时配置
     */
    const val COMMAND_TYPE_UPDATE_COUNT_DOWN_CONFIG: String = "updateCountDownConfig"

    /**
     * 更新显示模式
     */
    const val COMMAND_TYPE_APP_DISPLAY_SWITCH_CONFIG: String = "updateDisplaySwitchConfig"

    /**
     * 更新显示模式 --天气页面
     */
    const val COMMAND_TYPE_UPDATE_WEATHER_CONFIG: String = "updateWeatherConfig"

    /**
     * 更换显示模块
     */
    const val COMMAND_TYPE_CHANGE_SHOW_MODULE: String = "changeShowModule"

    /**
     * 更换显示模块
     */
    const val COMMAND_VALUE_CHANGE_SHOW_MODULE_EVENT: String = "event"

    /**
     * 更换显示模块
     */
    const val COMMAND_VALUE_CHANGE_SHOW_MODULE_WEATHER: String = "weather"

    const val COMMAND_VALUE_CHANGE_SHOW_MODULE_TIME: String = "time"

    const val COMMAND_VALUE_CHANGE_SHOW_MODULE_FANS: String = "fans"

    /**
     * 闹钟信息
     */
    const val COMMAND_TYPE_UPDATE_CLOCK_DATA: String = "updateClockData"

    /**
     * 悬空开始
     */
    const val COMMAND_TYPE_CONTROL_PRECIPICE_START_DATA: String = "controlStartPrecipice"

    /**
     * 悬空结束
     */
    const val COMMAND_TYPE_CONTROL_PRECIPICE_STOP_DATA: String = "controlStopPrecipice"

    /**
     * 倒下开始
     */
    const val COMMAND_TYPE_CONTROL_FALL_DOWN_START_DATA: String = "controlStartFallDown"

    /**
     * 倒下结束
     */
    const val COMMAND_TYPE_CONTROL_FALL_DOWN_STOP_DATA: String = "controlStopFallDown"

    /**
     * 单击
     */
    const val COMMAND_TYPE_CONTROL_TAP_DATA: String = "controlTap"

    /**
     * 双击
     */
    const val COMMAND_TYPE_CONTROL_DOUBLE_TAP_DATA: String = "controlDoubleTap"

    /**
     * 长按
     */
    const val COMMAND_TYPE_CONTROL_LONG_PRESS_DATA: String = "controlLongPressTap"

    /**
     * 防跌落，往后退
     */
    const val COMMAND_TYPE_CONTROL_FALL_BACKEND: String = "fallBackend"

    /**
     * 防跌落，往前走
     */
    const val COMMAND_TYPE_CONTROL_FALL_FORWARD: String = "fallForward"
    const val COMMAND_TYPE_CONTROL_FALL_LEFT: String = "fallLeft"
    const val COMMAND_TYPE_CONTROL_FALL_RIGHT: String = "fallRight"

    /**
     * 选择图片
     */
    const val COMMAND_TYPE_CONTROL_SEND_PIC: String = "controlSendPic"

    /**
     * 文字
     */
    const val COMMAND_TYPE_CONTROL_SEND_WORD: String = "controlSendWord"

    /**
     * 重置机器
     */
    const val COMMAND_TYPE_RESET_DEVICE_INFO: String = "resetDeviceInfo"

    var ROBOT_UPDATE_VERSION_IS_READY: String = "latest"

    /**
     * 机器人模式状态切换
     */
    const val COMMAND_TYPE_CHANGE_MODE: String = "changeMode"

    const val COMMAND_VALUE_CHANGE_MODE_TRANSFORM: String = "transform"
    const val COMMAND_VALUE_CHANGE_MODE_SHOW: String = "show"
    const val COMMAND_VALUE_CHANGE_MODE_SLEEP: String = "sleep"
    const val COMMAND_VALUE_CHANGE_MODE_AUTO: String = "auto"
    const val COMMAND_VALUE_CHANGE_MODE_DEMO: String = "demo"
    const val COMMAND_VALUE_CHANGE_MODE_RESET: String = "reset" // 恢复
    const val COMMAND_VALUE_CHANGE_MODE_STATIC: String = "static" // 静止模式
    const val COMMAND_VALUE_CHANGE_MODE_ROBOT: String = "robot" // 机器人模式

    /**
     * 机器人模式状态切换
     */
    const val COMMAND_TYPE_ADD_FACE_FEATURE: String = "addFaceFeature"

    /**
     * 控制音量
     */
    const val COMMAND_TYPE_CONTROL_SOUND_VOLUME: String = "controlSoundVolume"

    /**
     * 演示模式显示切换
     */
    const val COMMAND_TYPE_CONTROL_DISPLAY_MODE: String = "controlDisplayMode"

    const val COMMAND_VALUE_CONTROL_DISPLAY_TIME: String = "time"
    const val COMMAND_VALUE_CONTROL_DISPLAY_WEATHER: String = "weather"
    const val COMMAND_VALUE_CONTROL_DISPLAY_COUNTDOWN: String = "countdown"
    const val COMMAND_VALUE_CONTROL_DISPLAY_FANS: String = "fans"
    const val COMMAND_VALUE_CONTROL_DISPLAY_SCHEDULE: String = "schedule"
    const val COMMAND_VALUE_CONTROL_DISPLAY_EMPTY: String = "empty"
    const val COMMAND_VALUE_CONTROL_DISPLAY_BLACK: String = "darkScreen"
    const val COMMAND_VALUE_CONTROL_DISPLAY_EXT_BLACK: String = "exitDarkScreen"

    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_TIME = "display/time";
    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_WEATHER = "display/weather";
    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_COUNTDOWN = "display/countdown";
    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_FANS = "display/fans";
    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_SCHEDULE = "display/notice";
    // public static final  String LOCAL_COMMAND_VALUE_CONTROL_DISPLAY_EMPTY = "display/empty";
    const val LOCAL_COMMAND_VALUE_IDENT_FACE_RESULT: String = "identFaceResult"

    /**
     * 演示模式自动动作
     */
    const val COMMAND_TYPE_CONTROL_AUTO_MODE: String = "controlAutoMode"
    const val COMMAND_VALUE_CONTROL_AUTO_MODE_FOLLOW: String = "follow"
    const val COMMAND_VALUE_CONTROL_AUTO_MODE_EXT_FOLLOW: String = "exitFollow"
    const val COMMAND_VALUE_CONTROL_AUTO_MODE_RANDOM: String = "random"

    /**
     * 删除设备
     */
    const val COMMAND_VALUE_REMOVE_DEVICE: String = "removeDevice"

    /**
     * 引导完成按钮点击
     */
    const val COMMAND_VALUE_DEVICE_GUIDE_FINISH: String = "deviceGuideFinish"

    /**
     * 更新时区
     */
    const val COMMAND_UPDATE_DEVICE_TIME_ZONE: String = "updateDeviceTimeZone"

    /**
     *
     */
    const val COMMAND_SET_APP_MODE: String = "set_app_mode"

    /**
     *
     */
    const val COMMAND_SHOW_TEXT: String = "show_text"

    /**
     *
     */
    const val COMMAND_SHOW_CHARGING: String = "show_charging"

    /**
     *
     */
    const val COMMAND_SHOW_ALL: String = "show_all"

    /**
     *
     */
    const val COMMAND_HIDE_TEXT: String = "hide_text"

    const val COMMAND_GET_DEVICE_CHANNEL_LOGO: String = "get_channel_logo"
}
