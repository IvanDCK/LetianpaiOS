package com.renhejia.robot.guidelib.ble.ancs

/**
 *
 */
object GlobalDefine {
    const val NotificationAttributeIDBundleId: Int = 0 // , (Needs to be followed by a 2-bytes
    const val NotificationAttributeIDTitle: Int = 1 // , (Needs to be followed by a 2-bytes

    // max length parameter)
    const val NotificationAttributeIDSubtitle: Int = 2 // , (Needs to be followed by a

    // 2-bytes max length parameter)
    const val NotificationAttributeIDMessage: Int = 3 // , (Needs to be followed by a

    // 2-bytes max length parameter)
    const val NotificationAttributeIDMessageSize: Int = 4 // ,
    const val NotificationAttributeIDDate: Int = 5

    const val BLUETOOTH_ON: Int = 1001
    const val BLUETOOTH_OFF: Int = 1002
    const val BLUETOOTH_CONNECT: Int = 1003
    const val BLUETOOTH_DISCONNECT: Int = 1004
    const val BLUETOOTH_BONDED: Int = 1005
    const val BLUETOOTH_BONDNONE: Int = 1006
    const val BLUETOOTH_BONDING: Int = 1007

    const val BLUETOOTH_GET_MORE_INFO: Int = 1008
    const val BLUETOOTH_DISPLAY_INFO: Int = 1009

    const val service_ancs: String = "7905f431-b5ce-4e99-a40f-4b1e122d00d0"
    const val characteristics_notification_source: String = "9fbf120d-6301-42d9-8c58-25e699a21dbd"
    const val characteristics_data_source: String = "22eac6e9-24d6-4bb5-be44-b36ace7c7bfb"
    const val characteristics_control_point: String = "69d1d8f3-45e1-49a8-9821-9bbdfdaad9d9"
    const val descriptor_config: String = "00002902-0000-1000-8000-00805f9b34fb"

    const val destoryActionString: String = "com.zhangbh.billchang.service.destory"
    const val androidResponseAction: String = "com.zhangbh.billchang.NotifyActionString"
    const val androidResponseActionCode: String = "com.zhangbh.billchang.androidResponseActionCode"
}
