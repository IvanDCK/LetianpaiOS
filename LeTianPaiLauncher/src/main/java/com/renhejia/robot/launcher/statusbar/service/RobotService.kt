package com.renhejia.robot.launcher.statusbar.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager.Companion.getInstance
import com.renhejia.robot.guidelib.utils.SystemUtil
import com.renhejia.robot.guidelib.utils.SystemUtil.get
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.getInstance
import com.renhejia.robot.launcher.audioservice.AudioCmdResponseManager.Companion.getInstance
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager
import com.renhejia.robot.launcher.system.LetianpaiFunctionUtil
import com.renhejia.robot.launcherbaselib.callback.NetworkChangingUpdateCallback
import com.renhejia.robot.launcherbaselib.info.LauncherInfoManager.Companion.getInstance
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager.Companion.getInstance

class RobotService : Service() {
    private var mContext: Context? = null
    private val TAG = "ble_viewpager"

    private var isWifiConnected = true
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var handler: Handler? = null

    override fun onCreate() {
        super.onCreate()
        this.mContext = this@RobotService
        handler = Handler(mainLooper)
        addListeners()
        startServices()
    }

    private fun startServices() {
        if (LetianpaiFunctionUtil.isFactoryRom) {
            LetianpaiFunctionUtil.openFactoryApp(this@RobotService)
        } else {
            startResourceService()
            handler!!.postDelayed({
                startLongConnectService()
                startLogService()
                val pro = get(
                    SystemUtil.REGION_LANGUAGE,
                    "zh"
                )
                if ("en" != pro) {
                    startMIotService()
                }

                startMcuService()
                startTaskService()
                //                    if (WIFIConnectionManager.getInstance(mContext).isConnected()){
                //                        startAppStoreService();
                //                    }
                startDispatchService()
                startAlarmService()
            }, 1000)

            handler!!.postDelayed({ startSoundService() }, 200)
        }
    }

    private fun startAlarmService() {
        try {
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.robot.alarmnotice",
                "com.letianpai.robot.alarmnotice.service.AlarmService"
            )
            intent.setComponent(cn)
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTaskService() {
        try {
            val intent = Intent()
            val cn = ComponentName(
                "com.letianpai.robot.taskservice",
                "com.letianpai.robot.control.service.DispatchService"
            )
            intent.setComponent(cn)
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addListeners() {
        //        BleConnectStatusCallback.getInstance().registerBleConnectStatusListener(new BleConnectStatusCallback.BleConnectStatusChangedListener() {
//            @Override
//            public void onBleConnectStatusChanged(int connectStatus) {
//                LogUtils.logi(TAG, " ====== RobotService 0 ======= BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS");
//                if (connectStatus == BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtils.logi(TAG, " ====== RobotService ======= BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS");
//                            Intent intent = new Intent(mContext, LeTianPaiMainActivity.class);
//                            startActivity(intent);
//                        }
//                    }, 50);
//                }
//
//            }
//        });

//        NetworkChangingUpdateCallback.getInstance().setchangingStatusUpdateListener((networkType, networkStatus) -> {
//            LogUtils.logd("RobotService", "addListeners: networkStatus: "+networkStatus);
//            if (networkType == NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED) {
//                LogUtils.logd("RobotService", "addListeners: 网络if ");
//            } else {
//                LogUtils.logd("RobotService", "addListeners: 网络 else");
//            }
//        });

        //监听网络变化

        NetworkChangingUpdateCallback.instance.registerChargingStatusUpdateListener(object :
            NetworkChangingUpdateCallback.NetworkChangingUpdateListener {
            override fun onNetworkChargingUpdateReceived(networkType: Int, networkStatus: Int) {
                Log.e("RobotService", "networkType: $networkType")
                //网络变化断网了2分钟，在重新连接
                if (networkType == NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED) {
                    isWifiConnected = false
                    handler!!.postDelayed({ //WiFi没有链接成功再去链接
                        if (!isWifiConnected) {
                            WIFIConnectionManager.getInstance(mContext!!).connect()
                        }
                    }, (60000 * 2).toLong())
                } else {
                    isWifiConnected = true
                    logo
                    Log.e(
                        "RobotService",
                        "----网络连接- networkType ----: $networkType"
                    )
                }
            }
        })
    }


    private val logo: Unit
        get() {
            GeeUINetResponseManager.Companion.getInstance(this).logoInfo
        }

    override fun onDestroy() {
        super.onDestroy()
    }

    //链接服务端
    private fun startService() {
        val intent = Intent()
        intent.setPackage("com.renhejia.robot.launcher")
        intent.setAction("android.intent.action.LETIANPAI")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startService(intent)
        }
    }

    private fun startLongConnectService() {
        val intent = Intent()
        val cn = ComponentName("com.letianpai.emqxservice", "com.letianpai.emqxservice.EmqxService")
        intent.setComponent(cn)
        startService(intent)
    }

    private fun startSpeechRecognitionService() {
        val intent = Intent()
        intent.setAction("android.intent.action.LTPAUDIO")
        intent.setPackage("com.letianpai.robot.audioservice")
        startService(intent)
    }

    private fun startLogService() {
        try {
            val intent = Intent()
            intent.setAction("android.intent.action.BUGREPORT")
            intent.setPackage("com.letianpai.bugreportservice")
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startResourceService() {
        try {
            val intent = Intent()
            intent.setAction("android.intent.action.LETIANPAI.RESOURCE")
            intent.setPackage("com.letianpai.robot.geeuiresources")
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMcuService() {
        val intent = Intent()
        val cn = ComponentName(
            "com.letianpai.robot.mcuservice",
            "com.letianpai.robot.mcuservice.service.LTPMcuService"
        )
        intent.setComponent(cn)
        startService(intent)
    }


    private fun startMIotService() {
        val intent = Intent()
        val cn = ComponentName("com.geeui.miiot", "com.geeui.miiot.MiIotService")
        intent.setComponent(cn)
        startService(intent)
    }

    private fun startSoundService() {
        val intent = Intent()
        val cn = ComponentName(
            "com.letianpai.robot.audioservice",
            "com.letianpai.robot.audioservice.service.LTPAudioService"
        )
        intent.setComponent(cn)
        startService(intent)
    }

    private fun startAppStoreService() {
        val intent = Intent()
        val cn = ComponentName(
            "com.letianpai.robot.appstore",
            "com.letianpai.robot.appstore.service.AppStoreService"
        )
        intent.setComponent(cn)
        startService(intent)
    }

    private fun startDispatchService() {
        val intent = Intent(this@RobotService, DispatchService::class.java)
        startService(intent)
    }
}
