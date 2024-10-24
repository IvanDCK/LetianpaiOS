package com.renhejia.robot.guidelib.wifi

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.wifi.callback.GuideWifiConnectCallback

/**
 * @author liujunbin
 */
class WIFIAutoConnectionService : Service() {
    /**
     * wifi名
     */
    var ssid: String? = ""
        private set

    /**
     * 密码
     */
    var password: String? = ""
        private set


    interface HandlerCallback {
        fun handleMessage(msg: Message): Boolean
    }
    private val mHandler = Handler(Looper.getMainLooper())

    /**
     * 负责不断尝试连接指定wifi
     */
    private val handlerCallback = object : HandlerCallback {
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                SCAN_RESULT -> {
                    if (mWifiManager == null) {
                        return false
                    }
                    var scanResults: List<ScanResult> = mWifiManager!!.getScanResults()

                    if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        scanResults = mWifiManager!!.scanResults
                    } else {
                        logd("WIFIConnectionManager", "Permission not granted for accessing WiFi scan results")
                    }


                    var isSSIDAvailable: Boolean = false
                    for (scanResult: ScanResult in scanResults) {
                        logd("WIFIConnectionManager", "发现的 ssid 有: " + scanResult.SSID)
                        if (scanResult.SSID == ssid) {
                            // 找到了指定的 SSID
                            isSSIDAvailable = true
                            break
                        }
                    }

                    // WIFIConnectionManager.getInstance(WIFIAutoConnectionService.this).connect(mSsid, mPwd);
                    if (isSSIDAvailable) {
                        //没有扫描到指定的wifi ssid 连接错误
                        logd("WIFIConnectionManager", "found ssid: $ssid")
                        if (ssid != null && password != null) {
                            WIFIConnectionManager.getInstance(this@WIFIAutoConnectionService)
                                .connect(
                                    ssid!!,
                                    password!!
                                )
                        }
                    } else {
                        logd("WIFIConnectionManager", "not found ssid: $ssid")
                        //如果没有扫描到WiFi，都一直扫描
                        // mHandler.sendEmptyMessageAtTime(SCAN_RESULT, 3000);
                        //如果这里执行下面的代码，在没有扫描到的时候，就会提示配网失败。
                        BleConnectStatusCallback.instance
                            .setBleConnectStatus(BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_FAILED)
                    }
                }

                START_CONNECT -> {
                    logi("auto_connect", "onClick_4_mSsid: $ssid")
                    logi("auto_connect", "onClick_4_mPwd: $password")
                    if (TextUtils.isEmpty(ssid)) {
                        return false
                    }
                    if (ssid != null && password != null) {
                        WIFIConnectionManager.getInstance(this@WIFIAutoConnectionService)
                            .connect(
                                ssid!!,
                                password!!
                            )
                    }


                    val connected: Boolean = (WIFIConnectionManager.getInstance(
                        this@WIFIAutoConnectionService
                    ).isConnected(
                        ssid
                    )) && (WIFIConnectionManager.isNetworkAvailable(this@WIFIAutoConnectionService))
                    if (connected) {
                        GuideWifiConnectCallback.instance.setNetworkStatus(
                            GuideWifiConnectCallback.NETWORK_TYPE_WIFI,
                            true
                        )
                        BleConnectStatusCallback.instance
                            .setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS)
                        mHandler.removeCallbacksAndMessages(null)
                        stopSelf()
                    }
                    if (!connected && (!WIFIConnectionManager.getInstance(this@WIFIAutoConnectionService)
                            .isSetIncorrectPassword)
                    ) {
                        mHandler.sendEmptyMessageDelayed(0, 5000) //5s循环
                    }
                }
            }

            return true
        }
    }

    private var mWifiManager: WifiManager? = null

    override fun onCreate() {
        super.onCreate()
    }

    /**
     * @return always null
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        logi("auto_connect", "onClick_3")
        ssid = intent.getStringExtra(KEY_SSID)
        password = intent.getStringExtra(KEY_PWD)
        handleConnect()
        return START_NOT_STICKY
    }

    private fun handleConnect() {
        if (TextUtils.isEmpty(ssid)) {
            logd("WIFIAutoConnectionService", "handleConnect: wifi 名为空，不连接")
            BleConnectStatusCallback.instance
                .setBleConnectStatus(BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_FAILED)
            return
        }
        mWifiManager = getApplicationContext().getSystemService(WIFI_SERVICE) as WifiManager?
        if (!mWifiManager!!.isWifiEnabled()) {
            mWifiManager!!.setWifiEnabled(true)
        }
        mWifiManager!!.startScan()
        mHandler.sendEmptyMessageAtTime(SCAN_RESULT, 4000)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    companion object {
        private const val TAG: String = "www_wifi"
        private const val KEY_SSID: String = "KEY_SSID"
        private const val KEY_PWD: String = "KEY_PWD"
        const val SCAN_RESULT: Int = 0x03
        const val START_CONNECT: Int = 0x04

        /**
         * 连接指定wifi热点, 失败后5s循环
         *
         * @param context 用于启动服务的上下文
         * @param ssid    默认HUD-WIFI
         * @param pwd     (WPA加密)默认12345678
         */
        fun start(context: Context, ssid: String, pwd: String) {
            logi("auto_connect", "onClick_2")
            logi("auto_connect", "ssid: " + ssid)
            logi("auto_connect", "pwd: " + pwd)
            val starter: Intent = Intent(
                context,
                WIFIAutoConnectionService::class.java
            )
            starter.putExtra(KEY_SSID, ssid).putExtra(KEY_PWD, pwd)
            context.startService(starter)
        }

        fun stop(context: Context) {
            val starter: Intent = Intent(
                context,
                WIFIAutoConnectionService::class.java
            )
            context.stopService(starter)
            Log.d(TAG, "stop: ")
        }
    }
}
