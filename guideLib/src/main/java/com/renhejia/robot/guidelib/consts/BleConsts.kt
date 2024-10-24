package com.renhejia.robot.guidelib.consts

object BleConsts {
    /**
     * 消息提醒
     */
    const val BLE_CMD_MESSAGE_TIPS: String = "003"

    /**
     * 常规的控制指令
     */
    const val BLE_CMD_COMMON_CONTROL: String = "004"

    /**
     * 控制动作
     */
    const val BLE_CMD_CONTROL_MOTION: String = "005"

    /**
     * 切换ROM的地区，0061代表国内，0062代表海外
     */
    const val BLE_CMD_CHANGE_ROM_REGION: String = "006"
    const val ROM_REGION_CHINA: String = "1"
    const val ROM_REGION_GLOBAL: String = "2"
}
