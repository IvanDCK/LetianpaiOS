package com.renhejia.robot.commandlib.parser.wakeup

import com.google.gson.annotations.SerializedName

class WakeUp {
    @SerializedName("xiaole_switch")
    var xiaoleSwitch: Int = 0

    @SerializedName("xiaopai_switch")
    var xiaopaiSwitch: Int = 0

    @SerializedName("selected_voice_id")
    var selectedVoiceId: String? = null

    @SerializedName("selected_voice_name")
    var selectedVoiceName: String? = null

    @SerializedName("boy_voice_switch")
    var boyVoiceSwitch: Int = 0

    @SerializedName("girl_voice_switch")
    var girlVoiceSwitch: Int = 0

    @SerializedName("boy_child_voice_switch")
    var boyChildVoiceSwitch: Int = 0

    @SerializedName("girl_child_voice_switch")
    var girlChildVoiceSwitch: Int = 0

    @SerializedName("robot_voice_switch")
    var robotVoiceSwitch: Int = 0

    override fun toString(): String {
        return "WakeUp{" +
                "xiaoleSwitch=" + xiaoleSwitch +
                ", xiaopaiSwitch=" + xiaopaiSwitch +
                ", selectedVoiceId='" + selectedVoiceId + '\'' +
                ", selectedVoiceName='" + selectedVoiceName + '\'' +
                ", boyVoiceSwitch=" + boyVoiceSwitch +
                ", girlVoiceSwitch=" + girlVoiceSwitch +
                ", boyChildVoiceSwitch=" + boyChildVoiceSwitch +
                ", girlChildVoiceSwitch=" + girlChildVoiceSwitch +
                ", robotVoiceSwitch=" + robotVoiceSwitch +
                '}'
    }
}
