package com.rhj.player

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.rhj.message.MessageMediaListBean
import com.rhj.message.MessageMusicBean
import org.json.JSONObject

/**
 * 播放音乐技能
 */
class PlayerService : Service() {
    private var player: Player? = null
    private var mGson: Gson? = null
    private var musicList: List<MessageMusicBean>? = null
    private var am: AudioManager? = null
    private var onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener? = null

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "PlayerService onCreate: ")
        mGson = Gson()
        init()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        onNewDataLoad(intent.getStringExtra(INTENT_EXTRA_DATA)!!)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun init() {
        Log.i(TAG, "PlayerService init: ")
        player = Player.instance
        player!!.setOnPreparedListener { mp: MediaPlayer? ->
            Log.i("PlayerService", "init: ")
            mp?.start()
        }
        registerFocus()
    }

    private fun registerFocus() {
        am = getSystemService(AUDIO_SERVICE) as AudioManager
        onAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange: Int ->
            Log.i(
                TAG,
                "PlayerService registerFocus: $focusChange"
            )
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {}
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> player!!.pause()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {}
                AudioManager.AUDIOFOCUS_GAIN -> player!!.start()
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            am!!.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    fun unRegisterFocus() {
        am!!.abandonAudioFocus(onAudioFocusChangeListener)
    }

    private fun onNewDataLoad(json: String) {
        musicList = loadMusic(json)
        player!!.init(musicList!!)
    }

    fun setNewMusic(json: String) {
        onNewDataLoad(json)
    }

    private fun loadMusic(data: String): List<MessageMusicBean>? {
        try {
            Log.d(TAG, "loadMusic: $data")
            val `object` = JSONObject(data)
            val musics = mGson!!.fromJson(
                data,
                MessageMediaListBean::class.java
            )
            return musics.list
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun play() {
        if (player != null) {
            registerFocus()
            player!!.start()
        }
    }

    fun prev() {
        if (player != null) {
            player!!.prev()
        }
    }

    fun next() {
        if (player != null) {
            player!!.next()
        }
    }

    fun pause() {
        if (player != null) {
            unRegisterFocus()
            player!!.pause()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.i(TAG, "PlayerService onBind: ")
        onNewDataLoad(intent.getStringExtra(INTENT_EXTRA_DATA)!!)
        if (myBinder == null) {
            myBinder = MyBinder()
        }
        return myBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterFocus()
    }

    var myBinder: MyBinder? = null


    inner class MyBinder : Binder() {
        val service: PlayerService
            get() = this@PlayerService
    }

    companion object {
        private const val TAG = "PlayerService"
        const val INTENT_EXTRA_DATA: String = "data"
    }
}