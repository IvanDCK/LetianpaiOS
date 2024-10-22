package com.rhj.audio.observer

/**
 * 初始化状态改变的监听
 */
interface InitCallback {
    fun stateChange(initStatus: Boolean)
}
