package com.rhj.audio.observer

import com.rhj.message.MessageBean

interface MessageCallback {
    fun onMessage(messageBean: MessageBean?)
}
