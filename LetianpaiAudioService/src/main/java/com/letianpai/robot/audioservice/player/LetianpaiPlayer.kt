package com.letianpai.robot.audioservice.player

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import java.io.File
import java.lang.ref.WeakReference

class LetianpaiPlayer @JvmOverloads constructor(
    private val context: Context,
    audioStreamType: Int = -1
) {
    private var audioStreamType = -1
    private var mediaPlayer: MediaPlayer? = null

    private var path: String? = null
    private var fileRes = 0

    private var loop = 0

    private var hadGotAudioFocus = false

    private var voicePlayerCallback: VoicePlayerCallback? = null

    private val audioFocusChangeListener: VoicePlayFocusChangeListener

    fun play() {
        if (mediaPlayer != null) {
            stop()
        }

        try {
            if (path == null) {
                mediaPlayer = MediaPlayer.create(context, fileRes)
                if (audioStreamType != -1) {
                    mediaPlayer!!.setAudioStreamType(audioStreamType)
                }
            } else {
                logd(
                    "LetianpaiPlayer",
                    "play: Play sound=：$path"
                )
                mediaPlayer = MediaPlayer.create(context, Uri.fromFile(path?.let { File(it) }))
                if (audioStreamType != -1) {
                    mediaPlayer!!.setAudioStreamType(audioStreamType)
                }
            }

            //            mediaPlayer.setOnErrorListener(errorListener);
            mediaPlayer!!.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
                Log.i(
                    "TAG",
                    "LetianpaiPlayer onError: VoicePlayer onError-- what->$what,extra->$extra"
                )
                mediaPlayer = null
                false
            })
            mediaPlayer!!.setOnCompletionListener(completionListener)

            requestAudioFocus()
            mediaPlayer!!.setVolume(1f, 1f)
            mediaPlayer!!.start()
        } catch (e: Exception) {
            cancelAudioFocus()

            if (voicePlayerCallback != null) {
                voicePlayerCallback!!.onPlayError()
            }
        }
    }

    fun play(fileRaw: Int) {
        if (mediaPlayer != null) {
            stop()
        }
        Log.i("TAG", "LetianpaiPlayer play:fileRaw: $fileRaw")
        try {
            if (fileRaw == 0) {
                return
            }
            mediaPlayer = MediaPlayer.create(context, fileRaw)
            if (audioStreamType != -1) {
                mediaPlayer!!.setAudioStreamType(audioStreamType)
            }
            mediaPlayer!!.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
                Log.i("TAG", " VoicePlayer onErro===r-- what->$what,extra->$extra")
                mediaPlayer = null
                false
            })
            mediaPlayer!!.setOnCompletionListener(completionListener)

            requestAudioFocus()
            mediaPlayer!!.setVolume(1f, 1f)
            mediaPlayer!!.start()
        } catch (e: Exception) {
            cancelAudioFocus()

            if (voicePlayerCallback != null) {
                voicePlayerCallback!!.onPlayError()
            }
        }
    }

    fun pause() {
        cancelAudioFocus()
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
        }
    }

    fun stop() {
        cancelAudioFocus()
        if (mediaPlayer != null) {
            try {
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer = null
        }
    }

    private fun innerStop() {
        stop()
        if (null != voicePlayerCallback) {
            voicePlayerCallback!!.onPlayError()
        }
    }

    fun setPath(path: String?) {
        this.path = path
    }

    fun setFileRes(fileRes: Int) {
        this.fileRes = fileRes
    }

    fun setLoop(loop: Int) {
        this.loop = loop
    }

    private val completionListener = MediaPlayer.OnCompletionListener {
        stop()
        if (loop > 1) {
            loop--
            play()
        }
        if (voicePlayerCallback != null) {
            voicePlayerCallback!!.onPlayCompletion(loop)
        }
    }

    private val errorListener =
        MediaPlayer.OnErrorListener { mediaPlayer, what, extra ->
            logi(
                "",
                "VoicePlayer onError-- what->$what,extra->$extra"
            )
            cancelAudioFocus()
            true
        }

    init {
        this.audioStreamType = audioStreamType
        this.audioFocusChangeListener = VoicePlayFocusChangeListener(this)
    }

    private fun requestAudioFocus() {
        if (!hadGotAudioFocus) {
//			hadGotAudioFocus = AudioFocusHelper.getInstance().requestAudioFocus(context,
//					AudioManager.STREAM_MUSIC, audioFocusChangeListener);
        }
    }

    private fun cancelAudioFocus() {
        if (hadGotAudioFocus) {
//			AudioFocusHelper.getInstance().abandonAudioFocus(context, audioFocusChangeListener);
            hadGotAudioFocus = false
        }
    }

    val duration: Int
        get() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                return mediaPlayer!!.duration
            }
            return 0
        }

    val currentPosition: Int
        get() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                return mediaPlayer!!.currentPosition
            }
            return 0
        }

    fun setVoicePlayerCallback(voicePlayerCallback: VoicePlayerCallback?) {
        this.voicePlayerCallback = voicePlayerCallback
    }


    interface VoicePlayerCallback {
        fun onPlayCompletion(laveLoop: Int)

        fun onPlayError()
    }

    private class VoicePlayFocusChangeListener(player: LetianpaiPlayer) :
        AudioManager.OnAudioFocusChangeListener {
        private val voicePlayRefs =
            WeakReference(player)

        private fun getPlayer(): LetianpaiPlayer? {
            return if (null != voicePlayRefs) voicePlayRefs.get() else null
        }

        override fun onAudioFocusChange(focusChange: Int) {
            val player: LetianpaiPlayer? = getPlayer()
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    //pause
                    logi("", "VoicePlayFocusChangeListener AUDIOFOCUS_LOSS_TRANSIENT")
                    player?.innerStop()
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    //stop
                    logi("", "VoicePlayFocusChangeListener AUDIOFOCUS_LOSS")
                    player?.innerStop()
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    //play
                    logi("", "VoicePlayFocusChangeListener AUDIOFOCUS_GAIN")
                }
            }
        }
    }
}
