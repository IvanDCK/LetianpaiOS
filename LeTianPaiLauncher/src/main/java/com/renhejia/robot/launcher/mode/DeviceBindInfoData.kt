package com.renhejia.robot.launcher.mode

import com.google.gson.annotations.SerializedName

class DeviceBindInfoData {
    //0:未绑定，1:已绑定
    @SerializedName("bind_status")
    var bindStatus: Int = 0

    @SerializedName("bind_time")
    var bindTime: Int = 0

    @SerializedName("client_id")
    var clientId: String? = null

    @SerializedName("user_id")
    var userId: Int = 0

    override fun toString(): String {
        return "{" +
                "bindStatus:" + bindStatus +
                ", bindTime:" + bindTime +
                ", clientId:'" + clientId + '\'' +
                ", userId:'" + userId + '\'' +
                '}'
    }
}
