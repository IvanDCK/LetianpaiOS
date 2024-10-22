package com.rhj.audio.observer

interface TtsStateChangeCallback {
    fun onSpeakBegin()

    fun onSpeakEnd(ttsId: String?, errorCode: Int)

    fun onSpeakProgress(current: Int, total: Int)

    fun error(s: String?)
}
