package com.renhejia.robot.launcher.main.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.renhejia.robot.commandlib.consts.MCUCommandConsts.COMMAND_SET_SHOW_TIME
import com.renhejia.robot.commandlib.consts.RobotRemoteConsts
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.parser.displaymodes.logo.LogoInfo
import com.renhejia.robot.expression.face.FaceConsts
import com.renhejia.robot.expression.ui.view.RobotExpressionViewGif
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager.Companion.getInstance
import com.renhejia.robot.guidelib.utils.SystemUtil
import com.renhejia.robot.guidelib.utils.SystemUtil.get
import com.renhejia.robot.guidelib.utils.SystemUtil.robotActivateStatus
import com.renhejia.robot.guidelib.utils.SystemUtil.setAppLanguage
import com.renhejia.robot.guidelib.utils.SystemUtil.setRobotActivated
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.getInstance
import com.renhejia.robot.launcher.LTPConfigConsts
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.audioservice.AudioCmdResponseManager.Companion.getInstance
import com.renhejia.robot.launcher.callback.CommandResponseCallback
import com.renhejia.robot.launcher.dispatch.mode.ModeChangeCallback
import com.renhejia.robot.launcher.dispatch.mode.ViewModeConsts
import com.renhejia.robot.launcher.dispatch.service.StartServiceCallback
import com.renhejia.robot.launcher.displaymode.DisplayModesView
import com.renhejia.robot.launcher.displaymode.callback.DeviceChannelLogoCallBack
import com.renhejia.robot.launcher.displaymode.callback.DeviceChannelLogoCallBack.DeviceChannelLogoUpdateListener
import com.renhejia.robot.launcher.main.view.RobotTestAnimationView
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager
import com.renhejia.robot.launcher.system.LetianpaiFunctionUtil
import com.renhejia.robot.launcher.system.SystemFunctionUtil
import com.renhejia.robot.launcherbaselib.info.LauncherInfoManager.Companion.getInstance
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager.Companion.getInstance
import java.lang.ref.WeakReference

class LeTianPaiMainActivity : Activity() {
    //开机的hello world GIF
    private var robotExpressionViewGif: RobotExpressionViewGif? = null

    private var mHandler: ChangeExpressionHandler? = null
    private var sleep: ImageView? = null
    private var alertTime: TextView? = null
    private var displayModesView: DisplayModesView? = null
    private var shutdownView: View? = null
    private var robotSleepAnimationView: RobotTestAnimationView? = null
    private var hadSetMainCountdownTimer = false
    private var engineCountDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setAppLanguage(this@LeTianPaiMainActivity)
    }

    override fun onStart() {
        super.onStart()
        init()
        logd(
            "LeTianPaiMainActivity",
            "onStart  LTPGuideConfigManager.getInstance(this).isActivated()::" + LTPGuideConfigManager.getInstance(
                this
            )!!.isActivated
        )
    }

    private fun init() {
        //思必驰语音独立出来了，启动放在RobotService即可
        if (!LetianpaiFunctionUtil.isFactoryRom) {
            val pro = get(SystemUtil.REGION_LANGUAGE, "zh")
            //            if (LTPGuideConfigManager.getInstance(LeTianPaiMainActivity.this).isActivated()){
            if ("zh" == pro) {
                startSpeechAudioService()
            } else if ("en" == pro) {
                startAmazonAudioService()
            } else {
                startSpeechAudioService()
            }

            //            }
            startAppStoreService()
            startMenuService()
        }

        SystemFunctionUtil.Companion.setTimeZone(this@LeTianPaiMainActivity)
        startMainCountDownTimer()
        initEngineCountDownTimer()
    }

    private fun startMenuService() {
        Thread {
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.robot.desktop",
                "com.letianpai.robot.desktop.service.GeeUIDesktopService"
            )
            intent.setComponent(cn)
            startService(intent)
        }.start()
    }

    private fun startAppStoreService() {
        Thread {
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.robot.appstore",
                "com.letianpai.robot.appstore.service.AppStoreService"
            )
            intent.setComponent(cn)
            startService(intent)
        }.start()
    }

    private fun startMainCountDownTimer() {
        Log.e(
            "letianpai_test111",
            "hadSetMainCountdownTimer: $hadSetMainCountdownTimer"
        )
        if (!hadSetMainCountdownTimer) {
            Log.e(
                "letianpai_test111",
                "hadSetMainCountdownTimer1: $hadSetMainCountdownTimer"
            )
            hadSetMainCountdownTimer = true
            Handler().postDelayed({ StartServiceCallback.instance.startService() }, 5000)
        }
    }

    protected fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        checkSteeringEngine()
        logd(
            "LeTianPaiMainActivity",
            "LTPGuideConfigManager.getInstance(this).isActivated()::" + LTPGuideConfigManager.getInstance(
                this
            )!!.isActivated
        )

        //激活设备
        if (!LTPGuideConfigManager.getInstance(this)!!.isActivated && robotActivateStatus) {
            setRobotActivated()
            LTPGuideConfigManager.getInstance(this)!!.isActivated = true
            LTPGuideConfigManager.getInstance(this)!!.commit()
        }
        showChargingView()
        if (robotExpressionViewGif!!.visibility == View.GONE) {
            displayModesView!!.showViews()
        }
    }

    fun showCharging() {
        CommandResponseCallback.instance.setAppCommand(
            RobotRemoteConsts.COMMAND_SET_APP_MODE,
            RobotRemoteConsts.COMMAND_SHOW_CHARGING
        )
    }

    private fun checkSteeringEngine() {
        engineCountDownTimer!!.start()
    }


    private fun initEngineCountDownTimer() {
        engineCountDownTimer = object : CountDownTimer(3000, 300) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                if (LetianpaiFunctionUtil.isLauncherOnTheTop(this@LeTianPaiMainActivity)) {
                    GuideFunctionCallback.instance.shutDownSteeringEngine()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent != null) {
            val action = intent.action
            if ("stop" == action) {
                finish()
                return
            }
        }

        keepScreenOn()
        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_main_letianpai)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        inits()

        if (intent != null) {
            val from = intent.getStringExtra(LTPConfigConsts.START_FROM)
            showViews(from)
        }
        logo
        deviceLogo
    }

    private val logo: Unit
        get() {
            GeeUINetResponseManager.Companion.getInstance(this).logoInfo
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        logd(
            "LeTianPaiMainActivity",
            "onNewIntent action::$action"
        )
        if ("stop" == action) {
            finish()
            return
        }

        val from = intent.getStringExtra(LTPConfigConsts.START_FROM)
        showViews(from)
        deviceLogo
    }

    private fun showViews(from: String?) {
        logd(
            "LeTianPaiMainActivity",
            "onNewIntent from::$from"
        )
        if (!TextUtils.isEmpty(from) && from == LTPConfigConsts.START_FROM_MINI_APP) {
            showDisplayViews(null)
        } else if (!TextUtils.isEmpty(from) && from == LTPConfigConsts.START_FROM_START_APP) {
            showBlack(false)
        } else if (!TextUtils.isEmpty(from) && from == LTPConfigConsts.START_FROM_SLEEP) {
            //这里只是显示view布局，打呼噜声音不在这里
            showRobotSleepMode()
        } else if (!TextUtils.isEmpty(from) && from == LTPConfigConsts.START_FROM_DEEP_SLEEP) {
            showRobotDeepSleepMode()
        } else if (!TextUtils.isEmpty(from) && from == LTPConfigConsts.START_FROM_WIFI_CONNECTOR) {
            showTime()
        }
    }

    private fun showTime() {
        com.renhejia.robot.launcher.dispatch.command.CommandResponseCallback.instance.setLTPCommand(
            COMMAND_SET_SHOW_TIME,
            COMMAND_SET_SHOW_TIME
        )
    }

    private val deviceLogo: Unit
        get() {
            DeviceChannelLogoCallBack.instance.setDeviceChannelLogoUpdateListener(object :
                DeviceChannelLogoUpdateListener {
                override fun onLogoInfoUpdate(logoInfo: LogoInfo?) {
                    logd(
                        "LeTianPaiMainActivity",
                        "onNewIntent action::AA " + logoInfo.toString()
                    )
                    updateViewData(logoInfo!!)
                }
            })
        }

    /***
     * 更新 背景
     * @param logoInfo
     */
    private fun updateViewData(logoInfo: LogoInfo) {
        runOnUiThread { robotExpressionViewGif!!.updateView(logoInfo.data!!.hello_logo!!) }
    }

    override fun onPause() {
        super.onPause()
        displayModesView!!.hideViews()
        showBlackView()
    }

    private fun startSpeechAudioService() {
        logd("LeTianPaiMainActivity", "startSpeechAudioService: ")
        val intent = Intent()
        val cn = ComponentName("com.rhj.speech", "com.rhj.audio.service.LTPAudioService")
        intent.setComponent(cn)
        startService(intent)
    }

    private fun startDownloadService() {
        try {
            logd("LeTianPaiMainActivity", "startDownloadService: ")
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.robot.downloader",
                "com.letianpai.robot.downloader.service.DownloadService"
            )
            intent.setComponent(cn)
            startService(intent)
        } catch (e: Exception) {
        }
    }

    private fun startAmazonAudioService() {
        logd("LeTianPaiMainActivity", "startAmazonAudioService: ")
        val intent = Intent()
        val cn = ComponentName("com.geeui.lex", "com.geeui.lex.services.BotService")
        intent.setComponent(cn)
        startService(intent)
    }

    private fun inits() {
        initView()
    }

    private fun showDisplayViews(viewName: String?) {
        val message = Message()
        message.what = SHOW_DISPLAY_VIEW
        message.obj = viewName
        mHandler!!.sendMessage(message)
    }

    private fun showBlack(isShow: Boolean) {
        val message = Message()
        message.what = SHOW_REMOTE_BLACK
        message.obj = isShow
        mHandler!!.sendMessage(message)
    }

    private fun showChargingView() {
        val message = Message()
        message.what = SHOW_CHARGING
        mHandler!!.sendMessageDelayed(message, 1500)
    }

    private fun showBlackView() {
        robotExpressionViewGif!!.visibility = View.GONE
        alertTime!!.visibility = View.GONE
        sleep!!.visibility = View.GONE
        hideRobotSleepMode()
    }

    private fun hideBlackView() {
        robotExpressionViewGif!!.visibility = View.GONE
        alertTime!!.visibility = View.GONE
        sleep!!.visibility = View.GONE
    }

    private fun showDisplayView(msg: Message) {
        robotExpressionViewGif!!.visibility = View.GONE
        alertTime!!.visibility = View.GONE
        sleep!!.visibility = View.GONE
        hideRobotSleepMode()
        ModeChangeCallback.instance.setModeChange(ViewModeConsts.VM_DISPLAY_MODE)
        showChargingView()
    }


    private fun initView() {
        mHandler = ChangeExpressionHandler(this@LeTianPaiMainActivity)
        robotExpressionViewGif = findViewById(R.id.rview)
        alertTime = findViewById(R.id.tv_alarm_time)
        displayModesView = findViewById(R.id.display_modes_view)

        shutdownView = findViewById(R.id.shutdown)
        sleep = findViewById(R.id.sleep)
        robotSleepAnimationView = findViewById(R.id.robot_sleep_view)
    }

    private inner class ChangeExpressionHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SHOW_MAIN_IMAGE -> {}
                SHOW_DISPLAY_VIEW -> {
                    Log.e("onDisplayViewShow", "onDisplayViewShow ======= 7 =====================")
                    showDisplayView(msg)
                }

                SHOW_REMOTE_CONTROL_VIEW -> showRemoteControlView()
                SHOW_REMOTE_BLACK -> if (msg.obj as Boolean == true) {
                    showBlackView()
                } else {
                    hideBlackView()
                }

                UPDATE_ALARM_TIME -> updateAlarmText(msg.obj as String)
                SHUTDOWN -> showShutdown()
                SHOW_CHARGING -> if ((robotSleepAnimationView!!.visibility != View.VISIBLE) && (robotExpressionViewGif!!.visibility != View.VISIBLE)) {
                    //TODO setChargingShow
                    showCharging()
                }
            }
        }
    }


    private fun updateAlarmText(obj: String) {
        alertTime!!.text = obj
    }

    private fun showRemoteControlView() {
        robotExpressionViewGif!!.visibility = View.GONE
        alertTime!!.visibility = View.GONE
        sleep!!.visibility = View.GONE
        hideRobotSleepMode()
    }

    private fun showRobotSleepMode() {
        //隐藏默认的表情GIF
        displayModesView!!.hideViews()
        //播放睡眠动画表情
        robotSleepAnimationView!!.visibility = View.VISIBLE
        robotSleepAnimationView!!.playAnimationView(FaceConsts.FACE_SLEEP)
    }

    private fun showRobotDeepSleepMode() {
        //隐藏默认的表情GIF
        displayModesView!!.hideViews()
        //播放睡眠动画表情
        robotSleepAnimationView!!.visibility = View.VISIBLE
        robotSleepAnimationView!!.playAnimationView(FaceConsts.FACE_DEEP_SLEEP)
    }

    private fun hideRobotSleepMode() {
        robotSleepAnimationView!!.visibility = View.GONE
        robotSleepAnimationView!!.stopAnimationView()
    }


    private fun showShutdown() {
        shutdownView!!.visibility = View.VISIBLE
        hideRobotSleepMode()
    }

    companion object {
        // private static final int CHANGE_EXPRESSION = 99;
        private const val SHOW_MAIN_IMAGE = 102
        private const val SHOW_DISPLAY_VIEW = 103
        private const val SHOW_REMOTE_CONTROL_VIEW = 105

        // private static final int SHOW_REMOTE_TEXT = 107;
        private const val SHOW_REMOTE_BLACK = 108
        private const val UPDATE_ALARM_TIME = 109
        private const val SHUTDOWN = 111

        // private static final int SHOW_SLEEP = 114;
        // private static final int SHOW_DEEP_SLEEP = 115;
        private const val SHOW_CHARGING = 116
    }
}
