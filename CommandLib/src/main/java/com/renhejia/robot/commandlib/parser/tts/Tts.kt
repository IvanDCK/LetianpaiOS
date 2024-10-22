package com.renhejia.robot.commandlib.parser.tts

class Tts(var tts: String) {
    override fun toString(): String {
        return "{" +
                '\"' + "tts" + '\"' + ":" + '\"' + tts + '\"' +
                '}'
    }
}
