package com.renhejia.robot.gesturefactory.parser

import com.renhejia.robot.commandlib.parser.antennalight.AntennaLight
import com.renhejia.robot.commandlib.parser.antennamotion.AntennaMotion
import com.renhejia.robot.commandlib.parser.face.Face
import com.renhejia.robot.commandlib.parser.motion.Motion
import com.renhejia.robot.commandlib.parser.sound.Sound
import com.renhejia.robot.commandlib.parser.tts.Tts

/**
 * 姿态数据对象类
 * @author liujunbin
 */
class GestureData {
    var type: String? = null
    var actionData: Any? = null
    var ttsInfo: Tts? = null
    var expression: Face? = null
    var expressionTime: Int = 0
    var footAction: Motion? = null
    var earAction: AntennaMotion? = null
    var antennalight: AntennaLight? = null
    var soundEffects: Sound? = null
    var isEndGesture: Boolean = false
    @JvmField
    var interval: Long = 0

    override fun toString(): String {
        return "GestureData{" +
                "type='" + type + '\'' +
                ", actionData=" + actionData +
                ", ttsInfo=" + ttsInfo +
                ", expression=" + expression +
                ", expressionTime=" + expressionTime +
                ", footAction=" + footAction +
                ", earAction=" + earAction +
                ", antennalight=" + antennalight +
                ", soundEffects=" + soundEffects +
                ", endGesture=" + isEndGesture +
                ", interval=" + interval +
                '}'
    }
}
