package com.rhj.player

import android.media.MediaPlayer
import android.util.Log
import com.rhj.message.MessageMusicBean
import java.util.Locale
import java.util.Random

class Player private constructor() {
    var musicList: List<MessageMusicBean>? = null
        private set

    private var mPlayer: MediaPlayer?

    var currentMusic: MessageMusicBean? = null
        private set

    var size: Int = 0
        private set

    private var currentIndex = 0

    var playMode: Int = 0

    var duration: Int = 0
        private set
    private var currentPosition = 0
    private var onFinishListener: OnFinishListener? = null

    internal interface PlayMode {
        companion object {
            /**
             * 顺序播放
             */
            const val PLAY_MODE_LIST: Int = 0

            /**
             * 列表循环
             */
            const val PLAY_MODE_REPEAT_LIST: Int = 1

            /**
             * 单曲循环
             */
            const val PLAY_MODE_REPEAT_ONE: Int = 2

            /**
             * 随机播放
             */
            const val PLAY_MODE_SHUFFLE: Int = 3
        }
    }

    init {
        mPlayer = MediaPlayer()
    }

    fun init(musics: List<MessageMusicBean>) {
        this.musicList = musics
        size = musics.size
        currentMusic = musics[0]
        currentIndex = 0
        mPlayer!!.setOnCompletionListener { mp: MediaPlayer? ->
            if (currentPosition / 1000 - duration / 1000 <= 1) {
                when (playMode) {
                    PlayMode.PLAY_MODE_LIST -> if (currentIndex == size - 1) {
                        if (onFinishListener != null) {
                            onFinishListener!!.onFinish()
                        } else {
                            mPlayer!!.stop()
                            mPlayer!!.reset()
                        }
                        return@setOnCompletionListener
                    } else {
                        currentIndex++
                    }

                    PlayMode.PLAY_MODE_REPEAT_LIST -> if (currentIndex == size - 1) {
                        currentIndex = 0
                    } else {
                        currentIndex++
                    }

                    PlayMode.PLAY_MODE_REPEAT_ONE -> {}
                    PlayMode.PLAY_MODE_SHUFFLE -> {
                        val tempIndex = currentIndex
                        var index = Random().nextInt(size)
                        while (index == tempIndex) {
                            index = Random().nextInt(size)
                        }
                        currentIndex = index
                    }

                    else -> {}
                }
                currentMusic = musics[currentIndex]
            }
            //            mPlayer.reset();
            setAndPrepared()
        }
        mPlayer!!.setOnErrorListener { mp: MediaPlayer?, what: Int, extra: Int ->
            Log.i(
                TAG,
                "抱歉，当前资源找不到，即将播放下一个"
            )
            Log.i(TAG, "onError: $what,$extra")
            next()
            false
        }
        setAndPrepared()
    }

    fun setOnPreparedListener(onPreparedListener: MediaPlayer.OnPreparedListener) {
        mPlayer!!.setOnPreparedListener { mp: MediaPlayer ->
            Log.i(
                TAG,
                "Player setOnPreparedListener: "
            )
            duration = mp.duration
            onPreparedListener.onPrepared(mp)
        }
    }

    fun getCurrentDuration(): Int {
        try {
            if (mPlayer != null) {
                currentPosition = mPlayer!!.currentPosition
            }
        } catch (e: Exception) {
            return 0
        }
        return currentPosition
    }

    val progress: Int
        get() {
            try {
                if (mPlayer != null) {
                    return mPlayer!!.currentPosition / 1000
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
            return 0
        }


    fun seekTo(progress: Int) {
        mPlayer!!.seekTo(progress)
    }

    fun start(): Boolean {
        if (mPlayer != null) {
            mPlayer!!.start()
            return true
        } else {
            return false
        }
    }

    fun playByIndex(index: Int) {
        Log.d(TAG, "playByIndex: $index")
        currentIndex = index
        currentMusic = musicList!![currentIndex]
        if (mPlayer != null) {
            mPlayer!!.pause()
            mPlayer!!.reset()
            setAndPrepared()
        }
    }

    fun stop(): Boolean {
        if (mPlayer != null) {
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
            sInstance = null
            return true
        } else {
            return false
        }
    }

    fun rePlay(): Boolean {
        if (mPlayer != null) {
            mPlayer!!.seekTo(0)
            return true
        } else {
            return false
        }
    }

    fun pause(): Boolean {
        if (mPlayer != null) {
            mPlayer!!.pause()
            return true
        } else {
            return false
        }
    }

    fun resume(): Boolean {
        if (mPlayer != null) {
            mPlayer!!.start()
            return true
        } else {
            return false
        }
    }

    fun prev(): Boolean {
        if (mPlayer != null) {
            when (playMode) {
                PlayMode.PLAY_MODE_LIST -> if (currentIndex == 0) {
                    return false
                } else {
                    currentIndex--
                }

                PlayMode.PLAY_MODE_REPEAT_ONE, PlayMode.PLAY_MODE_REPEAT_LIST -> if (currentIndex == 0) {
                    currentIndex = size - 1
                } else {
                    currentIndex--
                }

                PlayMode.PLAY_MODE_SHUFFLE -> {
                    val tempIndex = currentIndex
                    var index = Random().nextInt(size)
                    while (index == tempIndex) {
                        index = Random().nextInt(size)
                    }
                    currentIndex = index
                }

                else -> {}
            }
            mPlayer!!.pause()
            mPlayer!!.reset()
            currentMusic = musicList!![currentIndex]
            setAndPrepared()
            return true
        } else {
            return false
        }
    }

    fun next(): Boolean {
        if (mPlayer != null) {
            when (playMode) {
                PlayMode.PLAY_MODE_LIST -> if (currentIndex == size - 1) {
                    return false
                } else {
                    currentIndex++
                }

                PlayMode.PLAY_MODE_REPEAT_ONE, PlayMode.PLAY_MODE_REPEAT_LIST -> if (currentIndex == size - 1) {
                    currentIndex = 0
                } else {
                    currentIndex++
                }

                PlayMode.PLAY_MODE_SHUFFLE -> {
                    val tempIndex = currentIndex
                    var index = Random().nextInt(size)
                    while (index == tempIndex) {
                        index = Random().nextInt(size)
                    }
                    currentIndex = index
                }

                else -> {}
            }
            mPlayer!!.pause()
            mPlayer!!.reset()
            currentMusic = musicList!![currentIndex]
            setAndPrepared()
            return true
        } else {
            return false
        }
    }

    private fun setAndPrepared() {
        try {
            Log.i(
                TAG,
                "Player setAndPrepared: $currentMusic"
            )
            mPlayer!!.reset()
            mPlayer!!.setDataSource(currentMusic!!.linkUrl)
            mPlayer!!.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isPlaying: Boolean
        get() = mPlayer!!.isPlaying

    fun favorite(): Boolean {
        if (currentMusic != null) {
            currentMusic!!.isFavorite = true
            return true
        } else {
            return false
        }
    }

    fun fastForward(relativeTime: Int, absoluteTime: Int): Boolean {
        var relativeTime = relativeTime
        var absoluteTime = absoluteTime
        Log.d(
            TAG,
            "fastForward: $relativeTime,$absoluteTime"
        )
        relativeTime = relativeTime * 1000
        absoluteTime = absoluteTime * 1000
        if (mPlayer != null) {
            val duration = mPlayer!!.duration
            val position = mPlayer!!.currentPosition
            if (absoluteTime != 0) {
                mPlayer!!.seekTo(absoluteTime)
                return true
            } else if (relativeTime != 0) {
                if (relativeTime + position > duration) {
                    return false
                } else {
                    mPlayer!!.seekTo(relativeTime + position)
                    return true
                }
            } else {
                if (position + 10 > duration) {
                    return false
                } else {
                    mPlayer!!.seekTo(position + 10000)
                    return true
                }
            }
        } else {
            return false
        }
    }

    fun backForward(relativeTime: Int, absoluteTime: Int): Boolean {
        var relativeTime = relativeTime
        var absoluteTime = absoluteTime
        Log.d(
            TAG,
            "backForward: $relativeTime,$absoluteTime"
        )
        relativeTime = relativeTime * 1000
        absoluteTime = absoluteTime * 1000
        if (mPlayer != null) {
            val duration = mPlayer!!.duration
            val position = mPlayer!!.currentPosition
            if (absoluteTime != 0) {
                mPlayer!!.seekTo(absoluteTime)
                return true
            } else if (relativeTime != 0) {
                if (position - relativeTime < 0) {
                    mPlayer!!.seekTo(0)
                    return false
                } else {
                    mPlayer!!.seekTo(position - relativeTime)
                    return true
                }
            } else {
                if (position - 10 < 0) {
                    return false
                } else {
                    mPlayer!!.seekTo(position - 10000)
                    return true
                }
            }
        } else {
            return false
        }
    }

    fun unFavorite(): Boolean {
        if (currentMusic != null) {
            currentMusic!!.isFavorite = false
            return true
        } else {
            return false
        }
    }

    val isFavorite: Boolean
        get() = currentMusic!!.isFavorite

    fun setOnFinishListener(listener: OnFinishListener?) {
        this.onFinishListener = listener
    }

    interface OnFinishListener {
        fun onFinish()
    }

    companion object {
        private const val TAG = "MusicPlayer"

        private var sInstance: Player? = null

        @get:Synchronized
        val instance: Player
            get() {
                if (sInstance == null) {
                    sInstance = Player()
                }
                return sInstance!!
            }

        fun formatDuration(duration: Int): String {
            // milliseconds into seconds
            var duration = duration
            duration /= 1000
            var minute = duration / 60
            val hour = minute / 60
            minute %= 60
            val second = duration % 60
            return if (hour != 0) {
                String.format(Locale.CHINA, "%2d:%02d:%02d", hour, minute, second)
            } else {
                String.format(Locale.CHINA, "%02d:%02d", minute, second)
            }
        }

        fun getIntTime(time: String): Int {
            try {
                val my = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (my.size == 3) {
                    val hour = my[0].toInt()
                    val min = my[1].toInt()
                    val sec = my[2].toInt()
                    return hour * 3600 + min * 60 + sec
                } else if (my.size == 2) {
                    val min = my[0].toInt()
                    val sec = my[1].toInt()
                    return min * 60 + sec
                } else {
                    return my[0].toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return 0
            }
        }
    }
}
