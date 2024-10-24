package com.renhejia.robot.display

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.widget.VideoView
import java.io.FileNotFoundException
import java.lang.ref.WeakReference
import java.util.Random

class SpineVideoView : VideoView {
    var mAssetsPathName: String? = null
    var mVideoTotal: Int = 0
    private var mPlayIndex = 0
    private var isSurfaceCreated = false
    private var mContext: Context
    var player: MediaPlayer = MediaPlayer()

    constructor(context: Context) : super(context) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
    }

    fun setAssetsPath(pathName: String?, videoTotal: Int) {
        mAssetsPathName = pathName
        mVideoTotal = videoTotal
        mPlayIndex = 0
    }

    fun stopVideo() {
        stopPlayback()
    }

    fun isPlayVideo(): Boolean {
        return isPlaying
    }

    fun playNext() {
        if (mVideoTotal > 0) {
            mPlayIndex++

            if (mPlayIndex > mVideoTotal) {
                mPlayIndex = 1
            }
            playVideo(mPlayIndex)
        }
    }

    fun playRand() {
        if (mVideoTotal > 0) {
            val random = Random()
            val index = random.nextInt(mVideoTotal) + 1
            if (index == mPlayIndex) {
                playNext()
            }
            playVideo(index)
        }
    }

    fun playVideo(index: Int) {
        mPlayIndex = index

        if (mAssetsPathName != null && mVideoTotal > 0) {
            val holder = this.holder

            if (isSurfaceCreated) {
                playAssetVideoByHandler(index)
                //playAssetVideo(index);
            } else {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        isSurfaceCreated = true
                        player.setDisplay(holder)
                        if (mAssetsPathName != null && mVideoTotal > 0) {
//                            playAssetVideo(index);
                            playAssetVideoByHandler(index)
                        }
                    }

                    override fun surfaceChanged(
                        holder: SurfaceHolder,
                        format: Int,
                        width: Int,
                        height: Int
                    ) {
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                    }
                })
            }
        }
    }

    private fun playAssetVideoByHandler(index: Int) {
        val message = Message()
        message.arg1 = index
        message.what = PLAY_VIDEO
        handler.sendMessage(message)
    }


    fun playAssetVideo(index: Int) {
        try {
            player.reset()
            val filename = "$mAssetsPathName/video_$index.mp4"
            val afd = this@SpineVideoView.context.assets.openFd(filename)
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            player.setOnPreparedListener { mp ->
                mp.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        this@SpineVideoView.setBackgroundColor(Color.TRANSPARENT)
                        return@OnInfoListener true
                    }
                    false
                })
                mp.start()
            }
            player.setOnCompletionListener {
                // mp.start();
            }
            try {
//                player.prepareAsync();
                player.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
//            playAssetVideo(1);
            playAssetVideo(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private val handler: Handler = SafeHandler(WeakReference(this))


    private class SafeHandler(spinevideView: WeakReference<SpineVideoView>) : Handler(Looper.getMainLooper()) {
        private val outer: WeakReference<SpineVideoView> = spinevideView

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                PLAY_VIDEO -> outer.get()?.playAssetVideo(msg.arg1)
            }
        }
    }

    companion object {
        private const val PLAY_VIDEO = 9
    }
}
