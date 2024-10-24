package com.renhejia.robot.launcher.statusbar.service

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.RemoteException
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.letianpai.robot.letianpaiservice.LtpLongConnectCallback
import com.letianpai.robot.letianpaiservice.LtpRobotStatusCallback
import com.letianpai.robot.notice.alarm.receiver.AlarmCallback
import com.renhejia.robot.commandlib.consts.MCUCommandConsts
import com.renhejia.robot.commandlib.consts.RobotRemoteConsts
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.commandlib.parser.power.PowerMotion
import com.renhejia.robot.commandlib.parser.time.ServerTime
import com.renhejia.robot.commandlib.parser.timezone.TimeZone
import com.renhejia.robot.gesturefactory.parser.GestureData
import com.renhejia.robot.gesturefactory.util.GestureConsts
import com.renhejia.robot.guidelib.ble.BleService
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback.RobotShutDownListener
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager
import com.renhejia.robot.guidelib.utils.SystemUtil.hasHardCode
import com.renhejia.robot.guidelib.utils.SystemUtil.isChinese
import com.renhejia.robot.guidelib.utils.SystemUtil.setRobotInactive
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.isNetworkAvailable
import com.renhejia.robot.launcher.audioservice.AudioCmdResponseManager
import com.renhejia.robot.launcher.audioservice.AudioCmdResponseManager.Companion.commandResponse
import com.renhejia.robot.launcher.dispatch.command.CommandResponseCallback
import com.renhejia.robot.launcher.dispatch.command.CommandResponseCallback.CommandResponseListener
import com.renhejia.robot.launcher.dispatch.command.CommandResponseCallback.LTPCommandResponseListener
import com.renhejia.robot.launcher.dispatch.gesture.GestureCallback
import com.renhejia.robot.launcher.dispatch.gesture.GestureCallback.GestureResponseListener
import com.renhejia.robot.launcher.dispatch.remote.RemoteCmdCallback
import com.renhejia.robot.launcher.dispatch.remote.RemoteCmdCallback.RemoteCmdListener
import com.renhejia.robot.launcher.dispatch.service.StartServiceCallback
import com.renhejia.robot.launcher.dispatch.service.StartServiceCallback.StartServiceListener
import com.renhejia.robot.launcher.main.activity.LauncherActivity
import com.renhejia.robot.launcher.main.activity.LeTianPaiMainActivity
import com.renhejia.robot.launcher.mode.DeviceBindInfo
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager
import com.renhejia.robot.launcher.nets.GeeUiNetManager
import com.renhejia.robot.launcher.system.LetianpaiFunctionUtil
import com.renhejia.robot.launcher.system.SystemFunctionUtil
import com.renhejia.robot.letianpaiservice.ILetianpaiService
import com.renhejia.robot.commandlib.consts.MCUCommandConsts.COMMAND_SET_SHOW_TIME
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * @author liujunbin
 */
class DispatchService : Service() {
    private var isConnectService = false
    private var iLetianpaiService: ILetianpaiService? = null
    private var gson: Gson? = null
    private var mHandler: GestureHandler? = null
    private var mainCountDownTimer: CountDownTimer? = null

    private var robotShutDownListener: RobotShutDownListener? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        // Your service logic here
        return START_STICKY
    }

    private fun startForegroundService() {
        val notificationChannelId = "com.example.notification.channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "Background Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(notificationChannel)
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Service is running")
            .setContentText("Foreground service is active")
            .build()

        startForeground(1, notification)
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }


    private fun init() {



        gson = Gson()
        mHandler = GestureHandler(this@DispatchService)
        initConnectService()
        addCommandListener()
        //addModeChangeListeners();
        initCountDownTimer()
        netInfo
        initShutDownListener()
        registerRobotShutDownListener()
        registerNetChangeListener()

        //调用接口判断设备是否绑定
        Thread { deviceBindInfo }.start()
    }

    private val deviceBindInfo: Unit
        get() {
            if (!isNetworkAvailable(this@DispatchService)) {
                return
            }
            GeeUiNetManager.getDeviceBindInfo(
                this@DispatchService,
                isChinese,
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.body != null) {
                            var deviceBindInfo: DeviceBindInfo? = null
                            val info = response.body!!.string()
                            try {
                                if (info != null) {
                                    deviceBindInfo = Gson().fromJson(
                                        info,
                                        DeviceBindInfo::class.java
                                    )
                                    Log.e(
                                        "----",
                                        "response11: $deviceBindInfo"
                                    )
                                    val data = deviceBindInfo.data
                                    if (data != null && data.bindStatus == 0) {
                                        if (LTPGuideConfigManager.getInstance(this@DispatchService)!!.isActivated) {
                                            //设备已解绑
                                            removeDevice()
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
        }

    private fun registerNetChangeListener() {
        BleConnectStatusCallback.instance.registerBleConnectStatusListener { connectStatus ->
            if (connectStatus == BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS) {
                Log.e("letianpai_network", "networkStatus11111: ====== 1 ")
                updateRobotTime()
            }
        }
    }

    private fun updateRobotTime() {
        Thread {
            Log.e("letianpai_network", "networkStatus11111: ====== 2 ")
            updateTime()
        }.start()
    }


    private fun updateTime() {
        GeeUiNetManager.getTimeStamp(this@DispatchService, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body != null) {
                    var serverTime: ServerTime? = null
                    var info = ""
                    if (response.body != null) {
                        info = response.body!!.string()
                        Log.e(
                            "letianpai_network",
                            "networkStatus11111: ====== 3: $info"
                        )
                    }
                    try {
                        if (info != null) {
                            serverTime = Gson().fromJson(info, ServerTime::class.java)
                            if (serverTime != null && serverTime.code == 0 && serverTime.data != null && serverTime.data!!.timestamp != 0L) {
                                changeTime(serverTime.data!!.timestamp * 1000)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun changeTime(time: Long) {
        val status = SystemClock.setCurrentTimeMillis(time * 1000)
        Log.i(
            TAG,
            "changeTime---status:" + status + "---System.currentTimeMillis()::" + System.currentTimeMillis()
        )
    }

    private fun initShutDownListener() {
        robotShutDownListener = object : RobotShutDownListener {
            override fun onShutDown() {
                sendShutdownCmd()
            }

            override fun onShutDownSteeringEngine() {
                sendShutDownSteeringEngineCmd()
            }
        }
    }

    private fun registerRobotShutDownListener() {
        GuideFunctionCallback.instance.registerRobotShutDownListener(robotShutDownListener)
    }

    private fun unregisterRobotShutDownListener() {
        if (robotShutDownListener != null) {
            GuideFunctionCallback.instance.unregisterRobotShutDownListener(robotShutDownListener)
        }
    }

    private fun sendShutdownCmd() {
        try {
            iLetianpaiService!!.setAppCmd(COMMAND_TYPE_SHUTDOWN, COMMAND_TYPE_SHUTDOWN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendShutDownSteeringEngineCmd() {
        try {
            iLetianpaiService!!.setAppCmd(
                COMMAND_TYPE_SHUTDOWN_STEERING_ENGINE,
                COMMAND_TYPE_SHUTDOWN_STEERING_ENGINE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private val netInfo: Unit
        get() {
            logi("letianpai_1234", "getNetInfo: ~~~~~0")
            if (isNetworkAvailable(this@DispatchService) && hasHardCode()) {
                GeeUINetResponseManager.getInstance(this@DispatchService)
                    .displayInfo
            } else if (isNetworkAvailable(this@DispatchService) && !hasHardCode()) {
            }
        }

    private fun cancelAll() {
        cancelMainCountDownTimer()
    }

    private fun initCountDownTimer() {
        mainCountDownTimer = object : CountDownTimer((10 * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                //设备未激活，返回
                if (!LTPGuideConfigManager.getInstance(this@DispatchService)!!.isActivated) {
                    return
                }
                if (LTPGuideConfigManager.getInstance(this@DispatchService)!!.isActivated) {
                    sendPowerOnCharging()
                    //思必驰语音独立出来了，启动放在RobotService即可
                    if (!LetianpaiFunctionUtil.isFactoryRom) {
                        startGeeUIOtaService()
                    }
                }
            }
        }
    }

    //等待设备起来之后，再去打开otaservice
    private fun startGeeUIOtaService() {
        Thread {
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.otaservice",
                "com.letianpai.otaservice.ota.GeeUpdateService"
            )
            intent.setComponent(cn)
            startService(intent)
        }.start()
    }

    private fun sendPowerOnCharging() {
        try {
            iLetianpaiService!!.setAppCmd(
                COMMAND_TYPE_POWER_ON_CHARGING,
                COMMAND_TYPE_POWER_ON_CHARGING
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun closeSteeringEngine() {
        try {
            iLetianpaiService!!.setAppCmd(
                COMMAND_TYPE_CLOSE_STEERING_ENGINE,
                COMMAND_TYPE_CLOSE_STEERING_ENGINE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startCountDownTimer() {
        cancelMainCountDownTimer()
    }

    fun startMainCountDownTimer() {
        mainCountDownTimer!!.start()
    }

    fun cancelMainCountDownTimer() {
        mainCountDownTimer!!.cancel()
    }

    private fun addCommandListener() {
        CommandResponseCallback.instance.setCommandReceivedListener(object :
            CommandResponseListener {
            override fun onCommandReceived(
                commandFrom: String?,
                commandType: String?,
                commandData: Any?
            ) {
                Log.e("letianpai_test", "========= addCommandListener 0 ========")
                commandResponse(
                    this@DispatchService, commandFrom,
                    commandType!!,
                    commandData!!, iLetianpaiService
                )
            }
        })

        CommandResponseCallback.instance.setLTPCommandResponseListener(object :
            LTPCommandResponseListener {
            override fun onLTPCommandReceived(command: String?, data: String?) {
                if (command == MCUCommandConsts.COMMAND_SET_APP_MODE) {
                    Log.e("letianpai_test111", "========= 0 ========")
                    try {
                        iLetianpaiService!!.setAppCmd(MCUCommandConsts.COMMAND_SET_APP_MODE, data)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                } else if (command == COMMAND_SET_SHOW_TIME) {
                    if (LTPGuideConfigManager.getInstance(this@DispatchService)!!.isActivated) {
                        sendPowerOnCharging()
                        //思必驰语音独立出来了，启动放在RobotService即可
                        if (!LetianpaiFunctionUtil.isFactoryRom) {
                            startGeeUIOtaService()
                        }
                    }
                } else {
                    try {
                        iLetianpaiService!!.setMcuCommand(command, data)
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        })

        GestureCallback.instance.setGestureListener(object : GestureResponseListener {
            override fun onGestureReceived(gesture: String?) {
                showGesture(gesture)
            }

            override fun onGestureReceived(gesture: String?, gestureId: Int) {
                showGesture(gesture, gestureId)
            }

            override fun onGesturesReceived(list: ArrayList<GestureData?>?, taskId: Int) {
                showGestures(list, taskId)
            }

            override fun onGesturesReceived(gestureData: GestureData?) {
                showGesture(gestureData)
            }
        })

        AlarmCallback.getInstance().registerAlarmTimeListener { hour, minute ->
            Log.i("letianpai_timer", "updateAlarm: " + hour + "_" + minute)
            //                RobotModeManager.getInstance(DispatchService.this).setAlarming(true);
            GestureCallback.instance.setGesture(GestureConsts.GESTURE_CLOCK)
        }

        StartServiceCallback.instance.setStartServiceListener(object : StartServiceListener {
            override fun onServiceStart() {
                startMainCountDownTimer()
            }
        })

        RemoteCmdCallback.instance.setRemoteCmdReceivedListener(object : RemoteCmdListener {
            override fun onRemoteCmdReceived(commandType: String?, commandData: Any?) {
                Log.e("letianpai123456789", "commandType: $commandType")
                Log.e("letianpai123456789", "commandData: " + commandData.toString())
                GeeUINetResponseManager.Companion.getInstance(this@DispatchService)
                    .dispatchTask(commandType, commandData)
            }
        })
    }

    private fun initConnectService() {
        connectService()
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "乐天派 Launcher 完成AIDLService服务")
            iLetianpaiService = ILetianpaiService.Stub.asInterface(service)
            try {
                iLetianpaiService!!.registerLCCallback(ltpLongConnectCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            isConnectService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "乐天派 无法绑定aidlserver的AIDLService服务")
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

    private val ltpLongConnectCallback: LtpLongConnectCallback.Stub =
        object : LtpLongConnectCallback.Stub() {
            @Throws(RemoteException::class)
            override fun onLongConnectCommand(command: String, data: String) {
                Log.e("letianpai0508_2", "command: $command /data: $data")
                if (TextUtils.isEmpty(command)) {
                    return
                }
                if (RobotRemoteConsts.COMMAND_VALUE_REMOVE_DEVICE == command) {
                    removeDevice()
                } else if (RobotRemoteConsts.COMMAND_TYPE_APP_DISPLAY_SWITCH_CONFIG == command) {
                    GeeUINetResponseManager.Companion.getInstance(this@DispatchService)
                        .updateGeneralInfo()
                } else if (RobotRemoteConsts.COMMAND_VALUE_DEVICE_GUIDE_FINISH == command) {
                    // deviceGuideFinish();
                } else if (RobotRemoteConsts.COMMAND_UPDATE_DEVICE_TIME_ZONE == command) {
                    updateDeviceTimeZone(data)
                }
            }
        }

    private fun updateDeviceTimeZone(data: String) {
        val timeZone = gson!!.fromJson(
            data,
            TimeZone::class.java
        )
        if (timeZone != null && !TextUtils.isEmpty(timeZone.zone)) {
            SystemFunctionUtil.Companion.setTimeZone(this@DispatchService, timeZone.zone)
        }
    }


    /**
     * 小程序点击了引导的完成按钮
     * 这边跳转到mainactivity
     */
    private fun deviceGuideFinish() {
        GuideFunctionCallback.instance.closeGuide()
    }

    /**
     * 解除绑定
     */
    private fun removeDevice() {
        LTPGuideConfigManager.getInstance(this@DispatchService)!!.isActivated = false
        LTPGuideConfigManager.getInstance(this@DispatchService)!!.commit()
        setRobotInactive()
        logd(
            "DispatchService", "removeDevice: " + LTPGuideConfigManager.getInstance(
                this@DispatchService
            )!!.isActivated
        )
        // 关闭机器人
        try {
            iLetianpaiService!!.setAppCmd(
                COMMAND_VALUE_KILL_PROCESS,
                ROBOT_PACKAGE_NAME + PACKAGE_NAME_SPLIT + PACKAGE_NAME_IDENT
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        //舵机卸力
        if (iLetianpaiService != null) {
            try {
                iLetianpaiService!!.setMcuCommand(
                    MCUCommandConsts.COMMAND_TYPE_POWER_CONTROL,
                    PowerMotion(3, 0).toString()
                )
                iLetianpaiService!!.setMcuCommand(
                    MCUCommandConsts.COMMAND_TYPE_POWER_CONTROL,
                    PowerMotion(5, 0).toString()
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        //关闭语音助手
        stopSpeechAudioService()
        stopAmazonAudioService()
        //解绑的时候，不需要清除WiFi 11月21日加
        // try {
        //     WIFIAutoConnectionService.stop(DispatchService.this);
        //     WIFIConnectionManager.getInstance(DispatchService.this).clearWifiPasswords();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        Handler(mainLooper).postDelayed({
            val intent = Intent(this@DispatchService, LauncherActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

            //关闭上一个页面
            val stopIntent = Intent(
                this@DispatchService,
                LeTianPaiMainActivity::class.java
            )
            stopIntent.setAction("stop")
            stopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(stopIntent)
            rebootRobot()
        }, 1500)

        //启动蓝牙service，
        startService(Intent(this, BleService::class.java))
    }

    private fun rebootRobot() {
        SystemFunctionUtil.Companion.reboot(this@DispatchService)
    }

    // 获取所有正在运行的进程信息
    private fun getRunningAppProcesses(context: Context): List<RunningAppProcessInfo> {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses
    }

    // 根据进程名获取应用程序信息
    private fun getAppInfo(context: Context, processName: String): ApplicationInfo? {
        val packageManager = context.packageManager
        val appInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (appInfo in appInfos) {
            if (appInfo.processName == processName) {
                return appInfo
            }
        }

        return null
    }

    private fun stopSpeechAudioService() {
        val intent = Intent()
        val cn = ComponentName("com.rhj.speech", "com.rhj.audio.service.LTPAudioService")
        intent.setComponent(cn)
        stopService(intent)
    }

    private fun stopAmazonAudioService() {
        val intent = Intent()
        val cn = ComponentName("com.geeui.lex", "com.geeui.lex.services.BotService")
        intent.setComponent(cn)
        stopService(intent)
    }

    private val ltpRobotStatusCallback: LtpRobotStatusCallback.Stub =
        object : LtpRobotStatusCallback.Stub() {
            @Throws(RemoteException::class)
            override fun onRobotStatusChanged(command: String, data: String) {
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        unregisterRobotShutDownListener()
    }

    fun showGesture(gesture: String?) {
        val message = Message()
        message.what = SHOW_GESTURE_STR
        message.obj = gesture
        mHandler!!.sendMessage(message)
    }

    fun showGesture(gesture: String?, gId: Int) {
        val message = Message()
        message.what = SHOW_GESTURES_WITH_ID
        message.obj = gesture
        message.arg1 = gId
        mHandler!!.sendMessage(message)
    }

    fun showGestures(list: ArrayList<GestureData?>?, taskId: Int) {
        val message = Message()
        message.what = SHOW_GESTURES_STR
        message.obj = list
        message.arg1 = taskId
        mHandler!!.sendMessage(message)
    }

    fun showGesture(gestureData: GestureData?) {
        val message = Message()
        message.what = SHOW_GESTURE_STR_OBJECT
        message.obj = gestureData
        mHandler!!.sendMessage(message)
    }

    private inner class GestureHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == SHOW_GESTURE_STR) {
                if (msg.obj != null) {
                    AudioCmdResponseManager.getInstance(this@DispatchService)
                        .responseGestures(((msg.obj as String)), iLetianpaiService)
                }
            } else if (msg.what == SHOW_GESTURES_STR) {
                if (msg.obj != null) {
                    AudioCmdResponseManager.getInstance(this@DispatchService).responseGestures(
                        (msg.obj as ArrayList<GestureData>),
                        msg.arg1,
                        iLetianpaiService
                    )
                }
            } else if (msg.what == SHOW_GESTURE_STR_OBJECT) {
                if (msg.obj != null) {
                    AudioCmdResponseManager.getInstance(this@DispatchService)
                        .responseGesture((msg.obj as GestureData), iLetianpaiService)
                }
            } else if (msg.what == SHOW_GESTURES_WITH_ID) {
                if (msg.obj != null && msg.arg1 != 0) {
                    AudioCmdResponseManager.getInstance(this@DispatchService)
                        .responseGestures(((msg.obj as String)), msg.arg1, iLetianpaiService)
                }
            }
        }
    }

    companion object {
        private const val TAG = "letianpai"
        private const val SHOW_GESTURE_STR = 2
        private const val SHOW_GESTURES_STR = 3
        private const val SHOW_GESTURES_WITH_ID = 4
        private const val SHOW_GESTURE_STR_OBJECT = 5
        const val COMMAND_TYPE_SHUTDOWN: String = "shutdown"
        const val COMMAND_TYPE_SHUTDOWN_STEERING_ENGINE: String = "ShutDownSteeringEngine"
        const val COMMAND_TYPE_POWER_ON_CHARGING: String = "power_on_charging"
        const val COMMAND_TYPE_CLOSE_STEERING_ENGINE: String = "power_on_charging"

        const val ROBOT_PACKAGE_NAME: String = "com.geeui.face"
        const val PACKAGE_NAME_IDENT: String = "com.ltp.ident"
        const val PACKAGE_NAME_SPLIT: String = "__"

        const val COMMAND_VALUE_KILL_PROCESS: String = "killProcess"
    }
}
