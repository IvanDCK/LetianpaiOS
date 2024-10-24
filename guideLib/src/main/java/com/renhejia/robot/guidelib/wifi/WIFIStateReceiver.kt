package com.renhejia.robot.guidelib.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.wifi.callback.GuideWifiConnectCallback

/**
 * Wi-Fi 自动连接广播
 */
class WIFIStateReceiver(private val mContext: Context) : BroadcastReceiver() {
    private val mWifiManager: WifiManager
    private var isConnected: Boolean = false
    private val handler: Handler? = null
    private val isTimeout: Boolean = false

    init {
        mWifiManager = mContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    override fun onReceive(context: Context, intent: Intent) {
        logd("WIFIStateReceiver", "onReceive---: " + intent.getAction())
        if (isConnected) {
            return
        }
        if (intent.getAction() == WifiManager.NETWORK_STATE_CHANGED_ACTION || intent.getAction() == ConnectivityManager.CONNECTIVITY_ACTION) {
            // 网络状态变化
            val networkInfo: NetworkInfo? =
                intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)
            if (networkInfo != null) {
                val wifiManager: WifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo: WifiInfo = wifiManager.getConnectionInfo()
                var ssid: String = wifiInfo.getSSID() // 获取当前连接的 WiFi 网络的 SSID
                ssid = ssid.replace("\"", "")
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    // 已连接到 WiFi 网络
                    Log.e("letianpai_net", "net_Connect:" + ssid)
                    val currentSsid: String? =
                        WIFIConnectionManager.getInstance(mContext).currentSsid
                    Log.e("letianpai_net", "conneced-ssid:" + ssid + "--currentSsid:" + currentSsid)
                    if (ssid == currentSsid) {
                        updateNetworkStatus(context, intent)
                    } else {
                        if (currentSsid != "") {
                            //切换网络失败
                            BleConnectStatusCallback.instance
                                .setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_FAILED)
                        }
                    }
                } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                    Log.e("letianpai_net", "net_Disconnect:" + ssid)
                    // WiFi 网络已断开连接
                    // BleConnectStatusCallback.getInstance().setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_FAILED);
                }
            }

            // if (WIFIConnectionManager.getInstance(mContext).isConnected()) {
            //     LogUtils.logd("WIFIStateReceiver", "onReceive: isConnected true");

            // } else {
            //     LogUtils.logd("WIFIStateReceiver", "onReceive: isConnected flase");
            // }
        } else if (intent.getAction() == WifiManager.SUPPLICANT_STATE_CHANGED_ACTION) {
            val linkWifiResult: Int = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123)
            logd("WIFIStateReceiver", "onReceive:linkWifiResult= " + linkWifiResult)
            if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
//                    密码错误时  清空networkId的相关信息
//                WIFIConnectionManager.getInstance(mContext).removeNetwork();
                if ((BleConnectStatusCallback.instance
                        .status != BleConnectStatusCallback.BLE_STATUS_DEFAULT) && (!WIFIConnectionManager.getInstance(
                        mContext
                    ).isSetIncorrectPassword)
                ) {
                    BleConnectStatusCallback.instance
                        .setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_FAILED)
                    WIFIConnectionManager.getInstance(mContext)
                        .isSetIncorrectPassword  = true
                }
            }
        }
    }

    private fun updateNetworkStatus(context: Context, intent: Intent) {
        if (mWifiManager.isWifiEnabled() && isWifiConnected(context)) {
            isConnected = true
            GuideWifiConnectCallback.instance
                .setNetworkStatus(GuideWifiConnectCallback.NETWORK_TYPE_WIFI, true)
            BleConnectStatusCallback.instance
                .setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS)
        }
    }

    private fun isWifiConnected(context: Context?): Boolean {
        if (context != null) {
            val mConnectivityManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWiFiNetworkInfo: NetworkInfo? = mConnectivityManager.getActiveNetworkInfo()
            if (mWiFiNetworkInfo != null) {
                logd(
                    "WIFIStateReceiver",
                    "isWifiConnected: " + (mWiFiNetworkInfo != null) + "  " + mWiFiNetworkInfo.isAvailable() + "  " + mWiFiNetworkInfo.isConnected()
                )
                return mWiFiNetworkInfo.isAvailable() && mWiFiNetworkInfo.isConnected()
            }
        }
        logd("WIFIStateReceiver", "isWifiConnected: mWiFiNetworkInfo null")
        return false
    }

    companion object {
        private val TAG: String = WIFIStateReceiver::class.java.getName()
    }
}
