package com.renhejia.robot.launcher.mode

import android.content.Context
import android.media.AudioManager
import com.renhejia.robot.commandlib.log.LogUtils.logi

/**
 * 睡眠模式管理器
 * @author liujunbin
 */
class SleepModeManager private constructor(private val mContext: Context) {
    private var audioManager: AudioManager? = null
    private val vTag = "volume1111"

    init {
        init(mContext)
    }

    private fun init(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //TODO 屏蔽消息提醒
//        test();
    }

    private fun test() {
        val currentSystem = audioManager!!.getStreamVolume(AudioManager.STREAM_SYSTEM)
        val currentAccessibility = audioManager!!.getStreamVolume(AudioManager.STREAM_ACCESSIBILITY)
        val currentAlarm = audioManager!!.getStreamVolume(AudioManager.STREAM_ALARM)
        val currentRing = audioManager!!.getStreamVolume(AudioManager.STREAM_RING)
        val currentMusic = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        val currentVoiceCall = audioManager!!.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val currentDTMF = audioManager!!.getStreamVolume(AudioManager.STREAM_DTMF)
        val currentNotification = audioManager!!.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
        logi(vTag, "currentSystem: $currentSystem")
        logi(
            vTag,
            "currentAccessibility: $currentAccessibility"
        )
        logi(vTag, "currentAlarm: $currentAlarm")
        logi(vTag, "currentRing: $currentRing")
        logi(vTag, "currentMusic: $currentMusic")
        logi(vTag, "currentVoiceCall: $currentVoiceCall")
        logi(vTag, "currentDTMF: $currentDTMF")
        logi(
            vTag,
            "currentNotification: $currentNotification"
        )

        //        2023-03-31 17:39:41.591  9423-9423  volume                  com.renhejia.robot.launcher          E  currentSystem: 5
//        2023-03-31 17:39:41.591  9423-9423  volume                  com.renhejia.robot.launcher          E  currentAccessibility: 13
//        2023-03-31 17:39:41.591  9423-9423  volume                  com.renhejia.robot.launcher          E  currentAlarm: 6
//        2023-03-31 17:39:41.592  9423-9423  volume                  com.renhejia.robot.launcher          E  currentRing: 5
//        2023-03-31 17:39:41.592  9423-9423  volume                  com.renhejia.robot.launcher          E  currentMusic: 13
//        2023-03-31 17:39:41.592  9423-9423  volume                  com.renhejia.robot.launcher          E  currentVoiceCall: 3
//        2023-03-31 17:39:41.592  9423-9423  volume                  com.renhejia.robot.launcher          E  currentDTMF: 13
//        2023-03-31 17:39:41.592  9423-9423  volume                  com.renhejia.robot.launcher          E  currentNotification: 5
        audioManager!!.setStreamVolume(AudioManager.STREAM_ACCESSIBILITY, 5, 0)
        logi(vTag, "currentSystem1: $currentSystem")
        logi(
            vTag,
            "currentAccessibility1: $currentAccessibility"
        )
        logi(vTag, "currentAlarm1: $currentAlarm")
        logi(vTag, "currentRing1: $currentRing")
        logi(vTag, "currentMusic1: $currentMusic")
        logi(
            vTag,
            "currentVoiceCall1: $currentVoiceCall"
        )
        logi(vTag, "currentDTMF: $currentDTMF")
        logi(
            vTag,
            "currentNotification: $currentNotification"
        )
    }

    val currentVolume: Int
        get() {
            val currentAccessibility =
                audioManager!!.getStreamVolume(AudioManager.STREAM_ACCESSIBILITY)
            return currentAccessibility
        }

    fun setRobotVolume(volume: Int) {
        audioManager!!.setStreamVolume(AudioManager.STREAM_ACCESSIBILITY, volume, 0)
    }


    fun setRobotVolumeTo20() {
//        setRobotVolume(20);
        setRobotVolume(20)
    }

    companion object {
        private var instance: SleepModeManager? = null
        fun getInstance(context: Context): SleepModeManager {
            synchronized(SleepModeManager::class.java) {
                if (instance == null) {
                    instance = SleepModeManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}
