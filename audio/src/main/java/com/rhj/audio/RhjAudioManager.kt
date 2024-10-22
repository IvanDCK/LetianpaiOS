package com.rhj.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import com.aispeech.dui.dds.DDS
import com.aispeech.dui.dds.DDSAuthListener
import com.aispeech.dui.dds.DDSConfig
import com.aispeech.dui.dds.DDSInitListener
import com.aispeech.dui.dds.agent.ASREngine
import com.aispeech.dui.dds.agent.DMTaskCallback
import com.aispeech.dui.dds.agent.tts.TTSEngine.CallbackOptimize
import com.aispeech.dui.dds.agent.wakeup.word.WakeupWord
import com.aispeech.dui.dds.exceptions.DDSNotInitCompleteException
import com.demon.fmodsound.FmodSound.playTts
import com.google.gson.Gson
import com.rhj.audio.observer.AuthStatusCallback
import com.rhj.audio.observer.CommandCallback
import com.rhj.audio.observer.InitCallback
import com.rhj.audio.observer.MessageCallback
import com.rhj.audio.observer.RhjCommandObserver
import com.rhj.audio.observer.RhjDMTaskCallback
import com.rhj.audio.observer.RhjMessageObserver
import com.rhj.audio.observer.TtsStateChangeCallback
import com.rhj.audio.observer.WakeupDoaCallback
import com.rhj.audio.observer.WakeupStateChangeCallback
import com.rhj.audio.utils.LogUtils
import com.rhj.audio.utils.SPUtils
import com.rhj.message.MessageBean
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID
import kotlin.concurrent.Volatile

class RhjAudioManager {
    private val TAG = "RhjAudioManager"
    private val mMessageObserver = RhjMessageObserver() // 消息监听器
    private val mCommandObserver = RhjCommandObserver() // 命令监听器

    /**
     * 获取是否初始化成功
     *
     * @return
     */
    var isInitComplete: Boolean = false
        private set

    /**
     * 获取是否授权成功
     *
     * @return
     */
    var isAuthSuccess: Boolean = false
        private set
    private var authStatusCallback: AuthStatusCallback? = null
    private var initCallback: InitCallback? = null

    // dds初始状态监听器,监听init是否成功
    private val mInitListener: DDSInitListener = object : DDSInitListener {
        override fun onInitComplete(isFull: Boolean) {
            LogUtils.logi(
                "LTPAudioService",
                "RhjAudioManager_onInitComplete() ========== 5.4 ========="
            )
            LogUtils.logd("RhjAudioManager", "onInitComplete $isFull")
            if (isFull) {
                isInitComplete = true
                if (initCallback != null) {
                    initCallback!!.stateChange(true)
                }
                LogUtils.logi(
                    "LTPAudioService",
                    "RhjAudioManager_onInitComplete() ========== 5.5 ========="
                )
                initComplete()
            }
        }

        override fun onError(what: Int, msg: String) {
            isInitComplete = false
            if (initCallback != null) {
                initCallback!!.stateChange(false)
            }
            LogUtils.logi("LTPAudioService", "RhjAudioManager_onError() ========== 5.6 =========")
            LogUtils.logd("RhjAudioManager", "Init onError: $what, error: $msg")
        }
    }

    // dds认证状态监听器,监听auth是否成功
    private val mAuthListener: DDSAuthListener = object : DDSAuthListener {
        override fun onAuthSuccess() {
            isAuthSuccess = true
            if (authStatusCallback != null) {
                authStatusCallback!!.onAuthStausStateChange(true)
            }
            LogUtils.logd("RhjAudioManager", "onAuthSuccess")
        }

        override fun onAuthFailed(errId: String, error: String) {
            isAuthSuccess = false
            if (authStatusCallback != null) {
                authStatusCallback!!.onAuthStausStateChange(false)
            }
            LogUtils.logd(
                "RhjAudioManager",
                "onAuthFailed: $errId, error:$error"
            )
        }
    }

    private val messageCallbackList: MutableList<MessageCallback> = ArrayList()
    private val wakeupStateChangeCallbackList: MutableList<WakeupStateChangeCallback> = ArrayList()
    private val wakeupDoaCallbackList: MutableList<WakeupDoaCallback> = ArrayList()
    private val ttsStateChangeCallbackList: MutableList<TtsStateChangeCallback> = ArrayList()
    private val commandCallbackList: MutableList<CommandCallback> = ArrayList()
    private var rhjDMTaskCallback: RhjDMTaskCallback? = null
    private val resultHistoryList: MutableList<DmTaskResultBean?> = ArrayList()
    private val mGson = Gson()

    /**
     * 是否使用FMod 变声
     */
    private var useFmod = false
    private var enableWakeupWordXiaole = true
    private var enableWakeupWordXiaoPai = false
    private var context: Context? = null

    fun init(context: Context, apiKey: String?) {
        LogUtils.logi("LTPAudioService", "RhjAudioManager_init() ========== 5.3 =========")
        //        registerVolumeChange(context);
        this.context = context
        DDS.getInstance().init(
            context.applicationContext,
            createConfig(context, apiKey),
            mInitListener,
            mAuthListener
        )
    }

    fun test() {
        try {
            DDS.getInstance().agent.asrEngine.startListening(object : ASREngine.Callback {
                override fun beginningOfSpeech() {
                }

                override fun endOfSpeech() {
                }

                override fun bufferReceived(bytes: ByteArray) {
                }

                override fun partialResults(s: String) {
                }

                override fun finalResults(s: String) {
                    Log.i(TAG, "RhjAudioManager finalResults: $s")
                }

                override fun error(s: String) {
                }

                override fun rmsChanged(v: Float) {
                }
            })
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    fun unInit(context: Context?) {
//        unRegisterVolumeChange(context);
        messageCallbackList.clear()
        wakeupStateChangeCallbackList.clear()
        wakeupDoaCallbackList.clear()
        ttsStateChangeCallbackList.clear()
        commandCallbackList.clear()
        mMessageObserver.unregister()
        mCommandObserver.unregister()
        DDS.getInstance().release()
    }

    private fun initComplete() {
        setTtsListener()
        setMessageObserver()
        setCommandObserver()
        setResultWithoutSpeak()
        enableWakeup()
        //        disableWakeup();
        try {
            DDS.getInstance().agent.wakeupEngine.setWakeupCallback { jsonObject ->
                LogUtils.logd("RhjAudioManager", "onWakeup: $jsonObject")
                val result = JSONObject()
                try {
                    result.put("greeting", "")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                result
            }
            //https://www.duiopen.com/docs/ct_cloud_TTS_Voice
//            DDS.getInstance().getAgent().getTTSEngine().setSpeaker("xijunmv4");//臻品男声小军
//            DDS.getInstance().getAgent().getTTSEngine().setSpeaker("yyqiaf");//臻品女声悦悦
//            DDS.getInstance().getAgent().getTTSEngine().setSpeaker("lzliafp");//精品可爱男童连连
//            DDS.getInstance().getAgent().getTTSEngine().setSpeaker("xbekef");//女孩-精品女童贝壳
//            DDS.getInstance().getAgent().getTTSEngine().setSpeaker("robot");//变声
            DDS.getInstance().agent.ttsEngine.setStreamType(AudioAttributes.CONTENT_TYPE_MUSIC) //可以设置TTS播报通道，默认使用ALARM通道。
            DDS.getInstance().agent.ttsEngine.volume = 300 //可以调整TTS音量，建议可以初始化授权成功后调用该接口设置TTS音量。
            DDS.getInstance().agent.ttsEngine.speed = 1.0f
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    /**
     * 打开唤醒
     */
    fun enableWakeup() {
        try {
            DDS.getInstance().agent.wakeupEngine.enableWakeupWhenAsr(true)
            DDS.getInstance().agent.asrEngine.enableVolume(true)
            DDS.getInstance().agent.wakeupEngine.enableWakeup()

            //设置音色
            val speakerName = context?.let { SPUtils.getInstance(it)?.getString(SPEAKER_NAME) ?: "" }
            if (speakerName != null) {
                setSpeaker(context, speakerName)
            }
            //设置唤醒词
            enableWakeupWordXiaole = context?.let { SPUtils.getInstance(it)?.getBoolean(ENABLE_WORDUP_XIAOLE)
                ?: false  } == true
            enableWakeupWordXiaoPai = context?.let { SPUtils.getInstance(it)?.getBoolean(ENABLE_WORDUP_XIAOPAI)
                ?: false  } == true
            setWakeupWord()

            val result = DDS.getInstance().agent.wakeupEngine.wakeupWords
            Log.i(TAG, "RhjAudioManager 初始唤醒词 " + result.contentToString())
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    /**
     * 关闭唤醒
     */
    fun disableWakeup() {
        try {
            DDS.getInstance().agent.wakeupEngine.disableWakeup()
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    fun setWakeupWord(context: Context?, enableXiaoLe: Boolean, enableXiaoPai: Boolean) {
        if (context != null) {
            SPUtils.getInstance(context)?.putBoolean(ENABLE_WORDUP_XIAOLE, enableXiaoLe)
            SPUtils.getInstance(context)?.putBoolean(ENABLE_WORDUP_XIAOPAI, enableXiaoPai)
        }
        enableWakeupWordXiaole = enableXiaoLe
        enableWakeupWordXiaoPai = enableXiaoPai
        setWakeupWord()
    }

    fun setSpeaker(context: Context?, speakerName: String) {
        if (context != null) {
            SPUtils.getInstance(context)?.putString(SPEAKER_NAME, speakerName)
        }
        if (isInitComplete) {
            if (speakerName == "robot") {
                //https://www.duiopen.com/docs/ct_cloud_TTS_Voice
                try {
                    //默认机器人的底色是小军
                    DDS.getInstance().agent.ttsEngine.speaker = "xijunmv4"
                    useFmod = true
                } catch (e: DDSNotInitCompleteException) {
                    throw RuntimeException(e)
                }
            } else {
                useFmod = false
                //https://www.duiopen.com/docs/ct_cloud_TTS_Voice
                try {
                    LogUtils.logd(
                        "RhjAudioManager",
                        "设置音色setSpeaker: $speakerName"
                    )
                    DDS.getInstance().agent.ttsEngine.speaker = speakerName
                    LogUtils.logd(
                        "RhjAudioManager",
                        "setSpeaker: " + DDS.getInstance().agent.ttsEngine.speaker
                    )
                } catch (e: DDSNotInitCompleteException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    private fun setWakeupWord() {
        try {
            val list = ArrayList<WakeupWord>()
            if (enableWakeupWordXiaole) {
                val mainWord =
                    WakeupWord().setPinyin("hai xiao le").setWord("嗨，小乐").setThreshold("0.32")
                        .addGreeting("")
                list.add(mainWord)
            }
            if (enableWakeupWordXiaoPai) {
                val mainWord =
                    WakeupWord().setPinyin("hai xiao pai").setWord("嗨，小派").setThreshold("0.32")
                        .addGreeting("")
                list.add(mainWord)
            }
            if (list.isEmpty()) {
                return
            }
            DDS.getInstance().agent.wakeupEngine.clearMainWakeupWord()
            DDS.getInstance().agent.wakeupEngine.addMainWakeupWords(list)


            val result = DDS.getInstance().agent.wakeupEngine.wakeupWords
            Log.i(TAG, "RhjAudioManager 唤醒词为 " + result.contentToString())
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    /**
     * 让当前唤醒状态进入下一个状态
     * 点击唤醒/停止识别/打断播报 操作接口
     */
    fun avatarClick() {
        try {
            DDS.getInstance().agent.avatarClick()
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    /**
     * 停止所有播报
     */
    fun shutupTts() {
        try {
            DDS.getInstance().agent.ttsEngine.shutup(null)
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    private fun setTtsListener() {
        try {
            DDS.getInstance().agent.ttsEngine.setListener(object : CallbackOptimize() {
                override fun beginning(s: String) {
                    LogUtils.logd("RhjAudioManager", "beginning: ")
                    for (ttsStateChangeCallback in ttsStateChangeCallbackList) {
                        ttsStateChangeCallback.onSpeakBegin()
                    }
                }

                override fun end(ttsId: String, i: Int) {
                    LogUtils.logd("RhjAudioManager", "end: ")
                    for (ttsStateChangeCallback in ttsStateChangeCallbackList) {
                        ttsStateChangeCallback.onSpeakEnd(ttsId, i)
                    }
                }

                override fun error(s: String) {
                    LogUtils.logd("RhjAudioManager", "error: ")
                    for (ttsStateChangeCallback in ttsStateChangeCallbackList) {
                        ttsStateChangeCallback.error(s)
                    }
                }

                override fun onSpeechProgress(
                    ttsId: String,
                    currentFrame: Int,
                    totalFrame: Int,
                    isDataReady: Boolean
                ) {
                    super.onSpeechProgress(ttsId, currentFrame, totalFrame, isDataReady)
                    LogUtils.logd("RhjAudioManager", "onSpeechProgress: ")
                    for (ttsStateChangeCallback in ttsStateChangeCallbackList) {
                        ttsStateChangeCallback.onSpeakProgress(currentFrame, totalFrame)
                    }
                }

                override fun phoneReturnReceived(s: String) {
                    LogUtils.logd("RhjAudioManager", "phoneReturnReceived: $s")
                }
            })
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    private fun setMessageObserver() {
        val messageCallback = object : MessageCallback {
            override fun onMessage(messageBean: MessageBean?) {
                messageCallbackList.forEach { it.onMessage(messageBean) }
            }
        }

        val wakeupStateChangeCallback = object : WakeupStateChangeCallback {
            override fun onState(stateData: String?) {
                wakeupStateChangeCallbackList.forEach { it.onState(stateData) }
            }
        }

        val wakeupDoaCallback = object : WakeupDoaCallback {
            override fun onDoa(doaData: String?) {
                wakeupDoaCallbackList.forEach { it.onDoa(doaData) }
            }
        }

        mMessageObserver.register(messageCallback, wakeupStateChangeCallback, wakeupDoaCallback)
    }

    private fun setCommandObserver() {
        val commandCallback = object: CommandCallback {
            override fun onCommand(command: String?, data: String?) {
                commandCallbackList.forEach { it.onCommand(command, data) }
            }
        }
        mCommandObserver.register(commandCallback)
    }

    /**
     * 添加监听的命令项
     *
     * @param strings
     */
    fun addCommandSubscribeArray(strings: Array<String?>?) {
        mCommandObserver.addSubscribe(strings)
    }

    /**
     * 添加消息监听项
     *
     * @param strings
     */
    fun addMessageSubscribeArray(strings: Array<String?>?) {
        mMessageObserver.addSubscribe(strings)
    }

    /**
     * 获取语音处理结果所有内容
     * 如果需要白名单，可以在此处处理
     * {
     * "from":"dm.output",
     * "sessionId":"8489c5640ebc42e680c4e7dc951f38a2",
     * "recordId":"ecc7bca5ed6d4a94a6e5cfe641f47d70:f6070dd46d44498e88e2d39c57a6a967",
     * "skillId":"2019042500000544",
     * "skillName":false,
     * "taskId":"",
     * "shouldEndSession":true,
     * "intentName":"查询天气",
     * "task":"天气",
     * "nlg":"北京市今天全天多云，气温-8~2℃，和昨天差不多，有西南风转南风1级",
     * "ssml":"",
     * "speakUrl":"https:\/\/dds-ack.dui.ai\/runtime\/v1\/longtext\/ecc7bca5ed6d4a94a6e5cfe641f47d70:f6070dd46d44498e88e2d39c57a6a967?productId=279614681&aispeech-da-env=hd-ack",
     * "widget":Object{...},
     * "dmInput":"天气",
     * "endSessionReason":Object{...},
     * "display":"北京市今天全天多云，气温-8~2℃，和昨天差不多，有西南风转南风1级",
     * "watchId":"03e5f2552579412f8b3616accb510c8a"
     * }
     */
    private fun setResultWithoutSpeak() {
        DDS.getInstance().agent.setDMTaskCallback(DMTaskCallback { dmTaskResult, type ->
            Log.i(
                TAG,
                "RhjAudioManager onDMTaskResult: " + (rhjDMTaskCallback != null) + dmTaskResult
            )
            val dmTaskResultBean = mGson.fromJson(
                dmTaskResult.toString(),
                DmTaskResultBean::class.java
            )
            if (resultHistoryList.size > RESULT_HISTORY_MAX_NUMBER) {
                resultHistoryList.removeAt(0)
            }

            //                try {
            //                    DDS.getInstance().getAgent().stopDialog();
            //                } catch (DDSNotInitCompleteException e) {
            //                    throw new RuntimeException(e);
            //                }
            /*new Thread(() -> {
                        FmodSound.INSTANCE.playTts(dmTaskResultBean.getSpeakUrl());
                        LogUtils.logd("<<<","进入launcher-----"+dmTaskResultBean.getSpeakUrl());
                    }).start();
    
                    try {
                        //语音不播报，唤醒的回复会在。
                        dmTaskResult.put("nlg", "");
                        dmTaskResult.put("speakUrl", null); // 不设置为null的话，播放的还是原来的音频
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return dmTaskResult;*/
            if (rhjDMTaskCallback != null) {
                try {
                    val input = dmTaskResult.getString("dmInput")
                    if (input.contains("退出智能模式")) {
                        return@DMTaskCallback dmTaskResult
                    }
                    try {
                        DDS.getInstance().agent.stopDialog() //结束对话，不会走后续的处理命令的流程
                        Thread {
                            val result = rhjDMTaskCallback!!.dealResult(dmTaskResultBean)
                            Log.i(
                                TAG,
                                "RhjAudioManager onDMTaskResult: result= $result"
                            )
                            speak(result)
                        }.start()
                    } catch (e: DDSNotInitCompleteException) {
                        throw RuntimeException(e)
                    }
                    return@DMTaskCallback dmTaskResult
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Log.i(
                    TAG,
                    "RhjAudioManager onDMTaskResult: null $dmTaskResultBean"
                )
                resultHistoryList.add(dmTaskResultBean)

                //TODO 如果有白名单，可以在此处理
                if (type == DMTaskCallback.Type.DM_OUTPUT) {
                    if (rhjDMTaskCallback != null) {
                        rhjDMTaskCallback?.dealResult(dmTaskResultBean)
                        try {
                            //语音不播报，唤醒的回复会在。
                            dmTaskResult.put("nlg", "")
                            dmTaskResult.put("speakUrl", null) // 不设置为null的话，播放的还是原来的音频
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else if (useFmod) {
                        LogUtils.logd(
                            "RhjAudioManager",
                            "onDMTaskResult: useFmod:$useFmod"
                        )
                        Thread {
                            LogUtils.logd(
                                "<<<",
                                "进入fmod变声-----" + dmTaskResultBean.speakUrl
                            )
                            dmTaskResultBean.speakUrl?.let { playTts(it) }
                        }.start()

                        try {
                            //语音不播报，唤醒的回复会在。
                            dmTaskResult.put("nlg", "")
                            dmTaskResult.put("speakUrl", null) // 不设置为null的话，播放的还是原来的音频
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                        LogUtils.logd(
                            "RhjAudioManager",
                            "onDMTaskResult: useFmod:$useFmod"
                        )
                    }
                } else if (type == DMTaskCallback.Type.CDM_ERROR) {
                    rhjDMTaskCallback?.dealErrorResult()
                }
            }
            dmTaskResult
        })
    }

    // 创建dds配置信息
    private fun createConfig(context: Context, apiKey: String?): DDSConfig {
        var apiKey = apiKey
        if (apiKey == null || apiKey.isEmpty()) {
//            apiKey = "fe6fd8c58e3c1b1b25e3cdaf63bd69a1";
//            apiKey = "e012c990cb64e012c990cb64641827a8";
            apiKey = "2ade05a72d062ade05a72d06641864b8"

            //            0bafb81e5f8e0bafb81e5f8e641924ce
        }
        // 用户设置自己实现的单个功能，目前支持 wakeup 和 vad。WakeupII.class 是一个使用示例
//         DDS.getInstance().setOutsideEngine(IEngine.Name.WAKEUP_SINGLE_MIC, WakeupII.class);
        val config = DDSConfig()

        // 基础配置项
//        config.addConfig("TTS_HEAD_NULL_WAV", "30"); // 产品ID -- 必填

//        config.addConfig("TTS_TAIL_NULL_WAV", "30"); // 产品ID -- 必填
        config.addConfig(DDSConfig.K_PRODUCT_ID, "279614681") // 产品ID -- 必填
        config.addConfig(DDSConfig.K_USER_ID, "aispeech") // 用户ID -- 必填
        config.addConfig(DDSConfig.K_ALIAS_KEY, "test") // 产品的发布分支 -- 必填
        config.addConfig(
            DDSConfig.K_PRODUCT_KEY,
            "12e4c9cdffa62009662bff42d6858372"
        ) // Product Key -- 必填
        config.addConfig(
            DDSConfig.K_PRODUCT_SECRET,
            "54f665b47df0b43b357cd8e09243bdb6"
        ) // Product Secre -- 必填
        config.addConfig(DDSConfig.K_DEVICE_ID, getDeviceId(context)) //填入唯一的deviceId -- 选填

        config.addConfig(DDSConfig.K_API_KEY, apiKey) // 产品授权秘钥，服务端生成，用于产品授权 -- 必填


        // 更多高级配置项,请参考文档: https://www.dui.ai/docs/ct_common_Andriod_SDK 中的 --> 四.高级配置项
        config.addConfig(
            DDSConfig.K_DUICORE_ZIP,
            "duicore.zip"
        ) // 预置在指定目录下的DUI内核资源包名, 避免在线下载内核消耗流量, 推荐使用
        config.addConfig(
            DDSConfig.K_USE_UPDATE_DUICORE,
            "false"
        ) //设置为false可以关闭dui内核的热更新功能，可以配合内置dui内核资源使用

        // 资源更新配置项
        config.addConfig(
            DDSConfig.K_CUSTOM_ZIP,
            "product.zip"
        ) // 预置在指定目录下的DUI产品配置资源包名, 避免在线下载产品配置消耗流量, 推荐使用

        // config.addConfig(DDSConfig.K_USE_UPDATE_NOTIFICATION, "false"); // 是否使用内置的资源更新通知栏

        // 录音配置项
        // config.addConfig(DDSConfig.K_IS_REVERSE_AUDIO_CHANNEL, "false"); // 录音机通道是否反转，默认不反转
        // config.addConfig(DDSConfig.K_AUDIO_SOURCE, AudioSource.DEFAULT); // 内置录音机数据源类型
        // config.addConfig(DDSConfig.K_AUDIO_BUFFER_SIZE, (16000 * 1 * 16 * 100 / 1000)); // 内置录音机读buffer的大小

        // TTS配置项
        config.addConfig(DDSConfig.K_STREAM_TYPE, AudioManager.STREAM_MUSIC) // 内置播放器的STREAM类型
        config.addConfig(DDSConfig.K_AUDIO_USAGE, AudioAttributes.USAGE_MEDIA)
        config.addConfig(DDSConfig.K_CONTENT_TYPE, AudioAttributes.CONTENT_TYPE_MUSIC)


        config.addConfig(
            DDSConfig.K_TTS_MODE,
            "internal"
        ) // TTS模式：external（使用外置TTS引擎，需主动注册TTS请求监听器）、internal（使用内置DUI TTS引擎）

        // config.addConfig(DDSConfig.K_CUSTOM_TIPS, "{\"71304\":\"请讲话\",\"71305\":\"不知道你在说什么\",\"71308\":\"咱俩还是聊聊天吧\"}"); // 指定对话错误码的TTS播报。若未指定，则使用产品配置。

        //唤醒配置项
        config.addConfig(
            DDSConfig.K_WAKEUP_ROUTER,
            "dialog"
        ) //唤醒路由：partner（将唤醒结果传递给partner，不会主动进入对话）、dialog（将唤醒结果传递给dui，会主动进入对话）
        //        config.addConfig(DDSConfig.K_CUSTOM_AUDIO,"");
        config.addConfig(DDSConfig.K_WAKEUP_DISABLE_CUSTOM_GREETING, "false")

        // config.addConfig(DDSConfig.K_WAKEUP_BIN, "/sdcard/wakeup.bin"); //商务定制版唤醒资源的路径。如果开发者对唤醒率有更高的要求，请联系商务申请定制唤醒资源。
        // config.addConfig(DDSConfig.K_ONESHOT_MIDTIME, "500");// OneShot配置：
        // config.addConfig(DDSConfig.K_ONESHOT_ENDTIME, "2000");// OneShot配置：

        //识别配置项识别配置项
        // config.addConfig(DDSConfig.K_ASR_ENABLE_PUNCTUATION, "false"); //识别是否开启标点
        // config.addConfig(DDSConfig.K_ASR_ROUTER, "dialog"); //识别路由：partner（将识别结果传递给partner，不会主动进入语义）、dialog（将识别结果传递给dui，会主动进入语义）
        // config.addConfig(DDSConfig.K_VAD_TIMEOUT, 5000); // VAD静音检测超时时间，默认8000毫秒
        // config.addConfig(DDSConfig.K_ASR_ENABLE_TONE, "true"); // 识别结果的拼音是否带音调
        // config.addConfig(DDSConfig.K_ASR_TIPS, "true"); // 识别完成是否播报提示音
        // config.addConfig(DDSConfig.K_VAD_BIN, "/sdcard/vad.bin"); // 商务定制版VAD资源的路径。如果开发者对VAD有更高的要求，请联系商务申请定制VAD资源。

        // 麦克风阵列配置项
        config.addConfig("PING_TIMEOUT", "20000") //单位ms,SDK初始化时，修改PING_TIMEOUT字段的值即可。默认10s


        // config.addConfig(DDSConfig.K_MIC_ARRAY_AEC_CFG, "/data/aec.bin"); // 麦克风阵列aec资源的磁盘绝对路径,需要开发者确保在这个路径下这个资源存在
        // config.addConfig(DDSConfig.K_MIC_ARRAY_BEAMFORMING_CFG, "/data/beamforming.bin"); // 麦克风阵列beamforming资源的磁盘绝对路径，需要开发者确保在这个路径下这个资源存在

        // 全双工/半双工配置项
        config.addConfig(DDSConfig.K_DUPLEX_MODE, "HALF_DUPLEX") // 半双工模式


        //TODO 自己的板子，设配hal 之后使用
        config.addConfig(
            DDSConfig.K_MIC_TYPE,
            "6"
        ) // （根据麦克风实际类型进行配置）设置硬件采集模组的类型 0：无。默认值。 1：单麦回消 2：线性四麦 3：环形六麦 4：车载双麦 5：家具双麦 6: 环形四麦  7: 新车载双麦 8: 线性6麦
        config.addConfig(
            DDSConfig.K_AEC_MODE,
            "external"
        ) //AEC模式，HAL层未集成AEC算法时，选择"internal"。HAL已经集成AEC算法时，选择"external"
        config.addConfig(DDSConfig.K_USE_SSPE, "true") //如果资源是SSPE资源，则需要将此配置置为true

        config.addConfig(
            DDSConfig.K_RECORDER_MODE,
            "internal"
        ) //（适配了hal之后选内部，或者不写这一条，SDK默认是内部---录音机模式：external（使用外置录音机，需主动调用拾音接口）、internal（使用内置录音机，DDS自动录音）
        config.addConfig(
            DDSConfig.K_MIC_ARRAY_SSPE_BIN,
            "sspe_aec-uca-wkp_46mm_ch6_4mic_1ref_release-v2.0.0.130.bin"
        ) //SSPE资源（放在test/src/main/assert文件夹下，或放到机器上指定绝对路径）(已包含aec算法)绝对路径，请务必保证绝对路径有可读写权限

        //config.addConfig(DDSConfig.K_WAKEUP_BIN, "wakeup_s20_zhihuijingling_20230103.bin"); //商务定制版唤醒资源的路径。如果开发者对唤醒率有更高的要求，请联系商务申请定制唤醒资源。
        val wakeAudioJson = "[{" +
                "\"name\":\"我在，有什么可以帮你\"," +
                "\"type\":\"mp3\"," +
                "\"path\":\"/storage/emulated/0/error.mp3\"" +
                "}]"
        config.addConfig(DDSConfig.K_CUSTOM_AUDIO, wakeAudioJson)

        if (BuildConfig.DEBUG) {
            config.addConfig(
                DDSConfig.K_CACHE_PATH,
                "/sdcard/cache"
            ) // 调试信息保存路径,如果不设置则保存在默认路径"/sdcard/Android/data/包名/cache"
            config.addConfig(
                DDSConfig.K_CACHE_SIZE,
                "1024"
            ) // 调试信息保存路径,如果不设置则保存在默认路径"/sdcard/Android/data/包名/cache"
            config.addConfig(
                DDSConfig.K_WAKEUP_DEBUG,
                "true"
            ) // 用于唤醒音频调试, 开启后在 "/sdcard/Android/data/包名/cache" 目录下会生成唤醒音频
            config.addConfig(
                DDSConfig.K_VAD_DEBUG,
                "true"
            ) // 用于过vad的音频调试, 开启后在 "/sdcard/Android/data/包名/cache" 目录下会生成过vad的音频
            config.addConfig(
                DDSConfig.K_ASR_DEBUG,
                "true"
            ) // 用于识别音频调试, 开启后在 "/sdcard/Android/data/包名/cache" 目录下会生成识别音频
            config.addConfig(
                DDSConfig.K_TTS_DEBUG,
                "true"
            ) // 用于tts音频调试, 开启后在 "/sdcard/Android/data/包名/cache/tts/" 目录下会自动生成tts音频
        }

        //        https://iot-sz.aispeech.com/doc/cicada-doc/#/ProjectDocking/?id=%e5%8d%8a%e5%8f%8c%e5%b7%a5%e6%a8%a1%e5%bc%8f%e4%b8%8b%ef%bc%8c%e6%92%ad%e6%8a%a5%e5%94%a4%e9%86%92%e5%9b%9e%e5%a4%8d%e8%af%ad%e7%9a%84%e5%90%8c%e6%97%b6%ef%bc%8c%e8%bf%9b%e8%a1%8c%e8%af%ad%e9%9f%b3%e8%af%86%e5%88%ab%e7%9a%84%e6%96%b9%e6%a1%88
//        config.addConfig(DDSConfig.K_ONESHOT_MIDTIME, -1);
//        config.addConfig(DDSConfig.K_ONESHOT_ENDTIME, -1);
        LogUtils.logd("RhjAudioManager", "config->$config")
        return config
    }

    // 获取手机的唯一标识符: deviceId
    @SuppressLint("MissingPermission")
    private fun getDeviceId(context: Context): String {
        val telephonyMgr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var imei: String? = null
        try {
//            imei = telephonyMgr.getDeviceId();
            imei = SystemUtil.ltpSn
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var serial = Build.SERIAL
        if (TextUtils.isEmpty(imei)) {
            imei = "unkown"
        } else if (TextUtils.isEmpty(serial)) {
            serial = "unkown"
        }
        val uuid =
            UUID.nameUUIDFromBytes((imei + serial).toByteArray()).toString()
        return uuid
    }

    fun setMessageCallback(messageCallback: MessageCallback) {
        messageCallbackList.add(messageCallback)
    }

    fun setWakeupStateChangeCallback(wakeupStateChangeCallback: WakeupStateChangeCallback) {
        wakeupStateChangeCallbackList.add(wakeupStateChangeCallback)
    }

    fun setWakeupDoaCallback(wakeupDoaCallback: WakeupDoaCallback) {
        wakeupDoaCallbackList.add(wakeupDoaCallback)
    }

    fun setTtsStateChangeCallback(ttsStateChangeCallback: TtsStateChangeCallback) {
        ttsStateChangeCallbackList.add(ttsStateChangeCallback)
    }

    fun setCommandCallback(commandCallback: CommandCallback) {
        commandCallbackList.add(commandCallback)
    }

    fun setRhjDMTaskCallback(rhjDMTaskCallback: RhjDMTaskCallback?) {
        this.rhjDMTaskCallback = rhjDMTaskCallback
    }

    fun setAuthStatusCallback(authStatusCallback: AuthStatusCallback?) {
        this.authStatusCallback = authStatusCallback
    }

    fun setInitCallback(initCallback: InitCallback?) {
        this.initCallback = initCallback
    }

    fun speak(text: String?, ttsId: String?) {
        speak(text, 0, ttsId, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE, "0")
    }

    /**
     * @param text       播报文本，支持SSML。
     * @param priority   优先级
     * ①优先级0：保留，与DDS语音交互同级，仅限内部使用；
     * ②优先级1：正常，默认选项，同级按序播放；
     * ③优先级2：重要，可以插话<优先级1>，同级按序播放，播报完毕后继续播报刚才被插话的<优先级1>；
     * ④优先级3：紧急，可以打断当前正在播放的<优先级1></优先级1>|优先级2>，同级按序播放，播报完毕后不再继续播报刚才被打断的<优先级1></优先级1>｜优先级2>。
     * @param ttsId      用于追踪该次播报的id，建议使用UUID。
     * @param audioFocus 该次播报的音频焦点，默认值:
     * ①优先级0：android.media.AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
     * ②优先级非0：android.media.AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
     * @param type       语音合成格式 云端合成，建议是mp3，本地合成，可以用wav
    </优先级1></优先级1> */
    /**
     * @param text
     * @param priority 建议是用 0 ，否则影响播放的同时，语音唤醒
     */
    /**
     * String text, int priority, String ttsId, int audioFocus, String type
     *
     * @param text
     */
    @JvmOverloads
    fun speak(
        text: String?,
        priority: Int = 0,
        ttsId: String? = UUID.randomUUID().toString(),
        audioFocus: Int = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE,
        type: String? = "0"
    ) {
        try {
            DDS.getInstance().agent.ttsEngine.speak(text, priority, ttsId, audioFocus, type)
        } catch (e: DDSNotInitCompleteException) {
            e.printStackTrace()
        }
    }

    fun speakAndStartDialog(text: String?) {
        val jsonObject = JSONObject()
        try {
            DDS.getInstance().agent.stopDialog()
            jsonObject.put("speakText", text)
            DDS.getInstance().agent.startDialog(jsonObject)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        } catch (e: DDSNotInitCompleteException) {
            throw RuntimeException(e)
        }
    }

    fun stopDialogWithText(text: String?) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("speakText", text)
            DDS.getInstance().agent.stopDialog(jsonObject)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        } catch (e: DDSNotInitCompleteException) {
            throw RuntimeException(e)
        }
    }

    fun stopDialog() {
        try {
            DDS.getInstance().agent.stopDialog()
        } catch (e: DDSNotInitCompleteException) {
            throw RuntimeException(e)
        }
    }

    /**
     * 获取最近的十条处理结果
     *
     * @return
     */
    fun getResultHistoryList(): List<DmTaskResultBean?> {
        return resultHistoryList
    }


    companion object {
        const val ENABLE_WORDUP_XIAOLE: String = "enable_xiao_le"
        const val ENABLE_WORDUP_XIAOPAI: String = "enable_xiao_pai"
        const val SPEAKER_NAME: String = "speakerName"

        @Volatile
        private var rhjAudioManager: RhjAudioManager? = null
        const val RESULT_HISTORY_MAX_NUMBER: Int = 10
        val instance: RhjAudioManager?
            get() {
                if (rhjAudioManager == null) {
                    rhjAudioManager = RhjAudioManager()
                }
                return rhjAudioManager
            }
    }
}
