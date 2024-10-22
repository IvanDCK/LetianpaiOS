package com.renhejia.robot.commandlib.parser.antennamotion

/**
 * ear_cmd:
 * 1 双耳左转 90
 * 2 双耳右转 90
 * 3 双耳左右转 90 (默认参数：step=3,delay=60)
 *
 *
 *
 *
 * ear_step: 1 表示动作指令连续执行次数1次
 *
 *
 * speed: 1 表示动作连续间的时间间隔 单位1ms
 * angle:30 表示动作幅度单位°（0°~90°，>90° 默认90°；不传此参数 幅度默认90°）
 * 举例：
 * AT+EARW,3,3,60,30\r\n 双耳左右转3次，速度60ms,幅度30°
 */
class AntennaMotion(var cmd: Int) {
    var step: Int = 0
    @JvmField
    var speed: Int = 0
    @JvmField
    var angle: Int = 0


    override fun toString(): String {
        return "{" +
                '\"' + "cmd" + '\"' + ":" + cmd +
                "," + '\"' + "step" + '\"' + ":" + step +
                "," + '\"' + "speed" + '\"' + ":" + speed +
                "," + '\"' + "angle" + '\"' + ":" + angle +
                '}'
    } //    @Override
    //    public String toString() {
    //        return "{" +
    //                '\"' + "antenna_motion" + '\"' + ":" + '\"' + antenna_motion + '\"' +
    //                "," + '\"' + "number" + '\"' + ":" + number +
    //                "," + '\"' + "step" + '\"' + ":" + step +
    //                "," + '\"' + "delay" + '\"' + ":" + delay +
    //                '}';
    //    }
    //
}
