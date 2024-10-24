package com.letianpai.robot.audioservice.callback


/**
 * 播放器回调
 * Created by liujunbin
 */
class PlayerStatusUpdateCallback private constructor() {
    private var mPlayerStatusUpdateListener: PlayerStatusUpdateListener? = null

    private object PlayerStatusUpdateCallbackHolder {
        val instance: PlayerStatusUpdateCallback = PlayerStatusUpdateCallback()
    }

    interface PlayerStatusUpdateListener {
        fun onPlayStatusUpdate(playStatus: Int)
        fun onPlayCompletion(laveLoop: Int)
        fun onPlayError()
    }

    fun setPlayerStatusUpdateListener(listener: PlayerStatusUpdateListener?) {
        this.mPlayerStatusUpdateListener = listener
    }

    fun setPlayStatusUpdate(playStatus: Int) {
        if (mPlayerStatusUpdateListener != null) {
            mPlayerStatusUpdateListener!!.onPlayStatusUpdate(playStatus)
        }
    }

    fun setPlayComplete(laveLoop: Int) {
        if (mPlayerStatusUpdateListener != null) {
            mPlayerStatusUpdateListener!!.onPlayCompletion(laveLoop)
        }
    }

    fun setPlayError() {
        if (mPlayerStatusUpdateListener != null) {
            mPlayerStatusUpdateListener!!.onPlayError()
        }
    }


    companion object {
        val instance: PlayerStatusUpdateCallback
            get() = PlayerStatusUpdateCallbackHolder.instance
    }
}
