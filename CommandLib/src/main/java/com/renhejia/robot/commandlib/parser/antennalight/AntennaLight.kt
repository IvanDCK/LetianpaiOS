package com.renhejia.robot.commandlib.parser.antennalight

class AntennaLight(var antenna_light: String, var antenna_light_color: Int) {
    override fun toString(): String {
        return "{" +
                '\"' + "antenna_light" + '\"' + ":" + '\"' + antenna_light + '\"' +
                "," + '\"' + "antenna_light_color" + '\"' + ":" + antenna_light_color +
                '}'
    }
}
