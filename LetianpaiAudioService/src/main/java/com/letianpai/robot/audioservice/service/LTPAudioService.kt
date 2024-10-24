package com.letianpai.robot.audioservice.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.letianpai.robot.audioservice.player.LetianpaiPlayer
import com.letianpai.robot.letianpaiservice.LtpAudioEffectCallback
import com.renhejia.robot.commandlib.consts.MCUCommandConsts
import com.renhejia.robot.commandlib.consts.SoundEffect
import com.renhejia.robot.commandlib.log.LogUtils
import com.renhejia.robot.commandlib.parser.face.Face
import com.renhejia.robot.letianpaiservice.ILetianpaiService
import java.util.Locale

/**
 * @author liujunbin
 */
class LTPAudioService : Service() {
    private var mPlayer: LetianpaiPlayer? = null
    private var isConnectService = false
    private var iLetianpaiService: ILetianpaiService? = null
    private var mGson: Gson? = null
    var isCommandWork: Boolean = true

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        mGson = Gson()
        initPlayer()
        addListeners()
        connectService()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        createNotificationChannel();
        return super.onStartCommand(intent, flags, startId)
    }

    private fun addListeners() {
        mPlayer?.setVoicePlayerCallback(object : LetianpaiPlayer.VoicePlayerCallback {
            override fun onPlayCompletion(laveLoop: Int) {
                LogUtils.logi("letianpai", "Audio_onPlayCompletion ========== ")
            }

            override fun onPlayError() {
            }
        })
    }

    private fun initPlayer() {
        mPlayer = LetianpaiPlayer(this@LTPAudioService)
    }

    fun pause() {
        mPlayer?.pause()
    }

    fun play(res: Int, name: String) {
        mPlayer?.setLoop(1)
        mPlayer?.play(res)
        if (name == SoundEffect.COMMAND_VALUE_SOUND_CLOCK) {
            mPlayer?.setLoop(26)
        }
        mPlayer?.setVoicePlayerCallback(object : LetianpaiPlayer.VoicePlayerCallback {
            override fun onPlayCompletion(laveLoop: Int) {
                if (name == MCUCommandConsts.COMMAND_VALUE_SOUND_BIRTHDAY) {
                    try {
    //                        iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_FACE, (new Face(MCUCommandConsts.COMMAND_VALUE_FACE_HAPPY)).toString()));
                        iLetianpaiService?.setExpression(
                            MCUCommandConsts.COMMAND_TYPE_FACE,
                            Face(MCUCommandConsts.COMMAND_VALUE_FACE_HAPPY).toString()
                        )
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onPlayError() {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.stop()
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "乐天派 音频服务 完成AIDLService服务")
            iLetianpaiService = ILetianpaiService.Stub.asInterface(service)
            try {
                iLetianpaiService!!.registerAudioEffectCallback(ltpAudioEffectCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            isConnectService = true
        }


        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "乐天派 音频服务 无法绑定aidlserver的AIDLService服务")
            isConnectService = false
        }
    }

    //链接服务端
    private fun connectService() {
        val intent = Intent()
        intent.setPackage("com.renhejia.robot.letianpaiservice")
        intent.setAction("android.intent.action.LETIANPAI")
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }


    private val ltpAudioEffectCallback = object : LtpAudioEffectCallback.Stub() {
        override fun onAudioEffectCommand(command: String, data: String) {
            try {
                when (command) {
                    COMMAND_OPEN_AE -> {
                        isCommandWork = true
                    }
                    COMMAND_CLOSE_AE -> {
                        isCommandWork = false
                    }
                    else -> {
                        if (isCommandWork) {
                            playSoundEffect(data)
                        }
                    }
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun playSoundEffect(name: String) {
        var name = name
        if (TextUtils.isEmpty(name)) {
            return
        }
        name = name.lowercase(Locale.getDefault())
        LogUtils.logd(
            "LTPAudioService",
            "playSoundEffect: 播放音效文件：$name"
        )
        if (name == SoundEffect.COMMAND_FUNCTION_STOP) {
            mPlayer?.stop()
        } else {
//            play(R.raw.mood_circumgyration, MCUCommandConsts.COMMAND_VALUE_SOUND_LOSE);
            Log.i(TAG, "LTPAudioService playSoundEffect: 直接播放名字对应的文件")
            val resId = resources.getIdentifier(name, "raw", "com.letianpai.robot.audioservice")
            play(resId, name)
        }
    }


    companion object {
        private const val TAG = "letianpai"
        const val COMMAND_TYPE_SOUND: String = "controlSound"
        const val COMMAND_OPEN_AE: String = "ltp123"
        const val COMMAND_CLOSE_AE: String = "ltp456"
    }
}
