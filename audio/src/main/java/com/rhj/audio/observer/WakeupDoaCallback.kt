package com.rhj.audio.observer

/**
 * 唤醒角度回掉
 * {"doa":355,"wakeupWord":"嗨，小乐","wakeupType":"major"}
 */
interface WakeupDoaCallback {
    fun onDoa(doaData: String?)
}
