package com.rhj.audio.observer

interface CommandCallback {
    fun onCommand(command: String?, data: String?)
}
