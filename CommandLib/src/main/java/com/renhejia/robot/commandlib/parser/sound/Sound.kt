package com.renhejia.robot.commandlib.parser.sound

class Sound {
    var sound: String
    var id: Int = 0
    var desc: String? = null

    constructor(sound: String) {
        this.sound = sound
    }

    constructor(sound: String, desc: String?) {
        this.sound = sound
        this.desc = desc
    }

    fun showLog(): String {
        return " $sound"
    }

    override fun toString(): String {
        return "{" +
                "sound='" + sound + '\'' +
                ", id=" + id +
                ", desc='" + desc + '\'' +
                '}'
    }
}
