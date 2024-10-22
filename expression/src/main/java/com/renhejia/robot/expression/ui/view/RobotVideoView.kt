package com.renhejia.robot.expression.ui.view

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.widget.VideoView
import com.renhejia.robot.commandlib.log.LogUtils.logi
import java.io.FileNotFoundException

/**
 *
 */
class RobotVideoView : VideoView {
    var mAssetsPathName: String? = null
    var mVideoTotal: Int = 0
    private val mPlayIndex = 0
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

    fun setAssetsPath(pathName: String?) {
        mAssetsPathName = pathName
    }

    fun stopVideo() {
        stopPlayback()
    }

    val isPlayVideo: Boolean
        get() = isPlaying

    //    public void playNext() {
    //        if (mVideoTotal > 0) {
    //            mPlayIndex++;
    //
    //            if (mPlayIndex > mVideoTotal) {
    //                mPlayIndex = 1;
    //            }
    //            playVideo(mPlayIndex);
    //        }
    //    }
    //    public void playRand() {
    //        if (mVideoTotal > 0) {
    //            Random random = new Random();
    //            Integer index = random.nextInt(mVideoTotal) + 1;
    //            if (index == mPlayIndex) {
    //                playNext();
    //            }
    //            playVideo(index);
    //
    //        }
    //    }
    fun playVideo(name: String) {
        val holder = this.holder

        if (isSurfaceCreated) {
//                playAssetVideoByHandler1(name);
            playAssetVideo(name)
        } else {
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    isSurfaceCreated = true
                    player.setDisplay(holder)
                    if (mAssetsPathName != null && !TextUtils.isEmpty(name)) {
                        playAssetVideo(name)
                        //                            playAssetVideoByHandler1(name);
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


    private fun playAssetVideoByHandler1(name: String) {
        val message = Message()
        message.obj = name
        message.what = PLAY_VIDEO
        handler.sendMessage(message)
    }


    fun playAssetVideo(name: String) {
        try {
            player.reset()
            //            String filename = mAssetsPathName + "/" + "video_" + name + ".mp4";
//            String filename = "video" + "/" + "bigLaugh.mp4";
            val filename = "video/$name.mp4"

            val afd = this@RobotVideoView.context.assets.openFd(filename)
            player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)

            player.setOnPreparedListener { mp ->
                mp.setOnInfoListener(MediaPlayer.OnInfoListener { mp, what, extra ->
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        //                                RobotVideoView.this.setBackgroundColor(Color.TRANSPARENT);
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
//            playAssetVideo();
            logi("letiabpai", "文件没找到")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //    public void playAssetVideo(int index) {
    //        try {
    //            player.reset();
    //            String filename = mAssetsPathName + "/" + "video_" + String.valueOf(index) + ".mp4";
    //            AssetFileDescriptor afd = RobotVideoView.this.getContext().getAssets().openFd(filename);
    //            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
    //
    //            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
    //                @Override
    //                public void onPrepared(MediaPlayer mp) {
    //                    mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
    //                        @Override
    //                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
    //                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
    //                                RobotVideoView.this.setBackgroundColor(Color.TRANSPARENT);
    //                                return true;
    //                            }
    //                            return false;
    //                        }
    //                    });
    //
    //                    mp.start();
    //                }
    //            });
    //            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
    //                @Override
    //                public void onCompletion(MediaPlayer mp) {
    //                    // mp.start();
    //                }
    //            });
    //            try {
    ////                player.prepareAsync();
    //                player.prepare();
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        } catch (FileNotFoundException e) {
    ////            playAssetVideo(1);
    //            playAssetVideo(1);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //
    //    }
    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                PLAY_VIDEO -> playAssetVideo(msg.obj as String)
            }
        }
    }

    companion object {
        private const val PLAY_VIDEO = 9
    }
}
