package com.renhejia.robot.launcher.mode

class DeviceBindInfo {
    var code: Int = 0
    var data: DeviceBindInfoData? = null
    var msg: String? = null


    override fun toString(): String {
        return "{" +
                "code:" + code +
                ", data:" + data +
                ", msg:'" + msg + '\'' +
                '}'
    }
}


