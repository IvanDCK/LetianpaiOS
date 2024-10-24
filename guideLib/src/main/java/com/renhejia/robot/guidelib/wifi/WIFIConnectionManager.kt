package com.renhejia.robot.guidelib.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkInfo
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.utils.SystemUtil
import com.renhejia.robot.guidelib.wifi.callback.GuideWifiConnectCallback
import java.lang.reflect.Method

/**
 * @author liujunbin
 */
class WIFIConnectionManager(private val mContext: Context) {
    val wifiManager: WifiManager
    private var networkId: Int = 0
    var currentSsid: String? = null
    private var currentPassword: String? = null
    var isSetIncorrectPassword: Boolean = false


    init {
        wifiManager =
            mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    /**
     * 尝试连接指定wifi
     *
     * @param ssid     wifi名
     * @param password 密码
     * @return 是否连接成功
     */
    fun connect(ssid: String, password: String): Boolean {
        //将WiFi和密码存储起来
        // SystemUtil.set("persist.sys.ssid", ssid);
        // SystemUtil.set("persist.sys.password", password);

        this.currentSsid = ssid
        this.currentPassword = password
        logd(
            "auto_connect",
            "connect() called with: ssid = [" + ssid + "], password = [" + password + "]"
        )
        logd("auto_connect", "connect: wifi opened = " + openWifi())

        networkId = wifiManager.addNetwork(newWifiConfig(ssid, password, true))
        val result: Boolean = wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()
        val isConnected: Boolean = isConnected(ssid) //当前已连接至指定wifi
        val isNetWork: Boolean = isNetworkAvailable(mContext)
        logd(TAG, "connect: is already connected = " + isConnected)
        if (isConnected && isNetWork) {
            GuideWifiConnectCallback.instance
                .setNetworkStatus(GuideWifiConnectCallback.NETWORK_TYPE_WIFI, true)
            BleConnectStatusCallback.instance
                .setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS)
            return true
        }
        logd("auto_connect", "connect: network enabled = " + result)
        return result
    }

    /**
     * 尝试连接指定wifi
     *
     * @return 是否连接成功
     */
    fun connect(): Boolean {
        Log.e(
            "WIFIConnectionManager",
            "currentSsid: " + currentSsid + "---currentPassword::" + currentPassword
        )
        if (TextUtils.isEmpty(currentSsid) || TextUtils.isEmpty(currentPassword)) {
            currentSsid = SystemUtil.get("persist.sys.ssid", "")
            currentPassword = SystemUtil.get("persist.sys.password", "")
            if (TextUtils.isEmpty(currentSsid)) {
                return false
            } else {
                return connect(currentSsid!!, currentPassword!!)
            }
        } else {
            return connect(currentSsid!!, currentPassword!!)
        }
    }

    /**
     * 根据wifi名与密码配置 WiFiConfiguration, 每次尝试都会先断开已有连接
     *
     * @param isClient 当前设备是作为客户端,还是作为服务端, 影响SSID和PWD
     */
    private fun newWifiConfig(
        ssid: String,
        password: String,
        isClient: Boolean
    ): WifiConfiguration {
        val config: WifiConfiguration = WifiConfiguration()
        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        if (isClient) { //作为客户端, 连接服务端wifi热点时要加双引号
            config.SSID = "\"" + ssid + "\""
            config.preSharedKey = "\"" + password + "\""
        } else { //作为服务端, 开放wifi热点时不需要加双引号
            config.SSID = ssid
            config.preSharedKey = password
        }
        config.hiddenSSID = true
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
        config.status = WifiConfiguration.Status.ENABLED
        return config
    }

    val isWifiEnabled: Boolean
        /**
         * @return 热点是否已开启
         */
        get() {
            try {
                val methodIsWifiApEnabled: Method =
                    WifiManager::class.java.getDeclaredMethod("isWifiApEnabled")
                return methodIsWifiApEnabled.invoke(wifiManager) as Boolean
            } catch (e: Exception) {
                logi(
                    TAG,
                    "isWifiEnabled: " + e.message
                )
                return false
            }
        }

    /**
     * 是否已连接指定wifi
     */
    fun isConnected(ssid: String?): Boolean {
        val wifiInfo: WifiInfo? = wifiManager.getConnectionInfo()

        if (wifiInfo == null) {
            return false
        }

        logd("WIFIConnectionManager", "isConnected: state: " + wifiInfo.getSupplicantState())
        when (wifiInfo.getSupplicantState()) {
            SupplicantState.AUTHENTICATING, SupplicantState.ASSOCIATING, SupplicantState.ASSOCIATED, SupplicantState.FOUR_WAY_HANDSHAKE, SupplicantState.GROUP_HANDSHAKE, SupplicantState.COMPLETED -> {
                logi(
                    "auto_connect",
                    " wifiInfo.getSSID(): " + wifiInfo.getSSID().replace("\"", "").toString()
                )
                return wifiInfo.getSSID().replace("\"", "") == ssid
            }

            else -> return false
        }
    }

    val isConnected: Boolean
        /**
         * 是否已连接指定wifi
         */
        get() {
            if (TextUtils.isEmpty(currentSsid)) {
                return false
            } else {
                val isC: Boolean = isConnected(currentSsid)
                val isN: Boolean =
                    isNetworkAvailable(mContext)
                logd(
                    "WIFIConnectionManager",
                    "isConnected:isC " + isC + " isN " + isN
                )
                return isC
            }
        }

    /**
     * 打开WiFi
     *
     * @return
     */
    fun openWifi(): Boolean {
        var opened: Boolean = true
        if (!wifiManager.isWifiEnabled()) {
            opened = wifiManager.setWifiEnabled(true)
        }
        return opened
    }

    /**
     * 关闭wifi
     *
     * @return
     */
    fun closeWifi(): Boolean {
        var closed: Boolean = true
        if (wifiManager.isWifiEnabled()) {
            closed = wifiManager.setWifiEnabled(false)
        }
        return closed
    }

    /**
     * 断开连接
     *
     * @return
     */
    fun disconnect(): WIFIConnectionManager {
        if (networkId != 0) {
            wifiManager.disableNetwork(networkId)
        }
        wifiManager.disconnect()
        return this
    }

    /**
     * 删除网络
     *
     * @return
     */
    fun removeNetwork(): Boolean {
        if (networkId != 0) {
            return wifiManager.removeNetwork(networkId)
        }
        return false
    }

    /**
     * 是否连接过指定Wifi
     */
    fun everConnected(ssid: String): WifiConfiguration? {
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
////            return TODO;
//        }

        var ssid: String = ssid
        var existingConfigs: List<WifiConfiguration>? = wifiManager.getConfiguredNetworks()
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            existingConfigs = wifiManager.configuredNetworks
        }
        if (existingConfigs == null || existingConfigs.isEmpty()) {
            return null
        }
        ssid = "\"" + ssid + "\""
        for (existingConfig: WifiConfiguration in existingConfigs) {
            if (existingConfig.SSID == ssid) {
                return existingConfig
            }
        }
        return null
    }

    val localIp: String?
        /**
         * 获取本机的ip地址
         */
        get() {
            return convertIp(wifiManager.getConnectionInfo().getIpAddress())
        }

    private fun convertIp(ipAddress: Int): String? {
        if (ipAddress == 0) return null
        return (((ipAddress and 0xff).toString() + "." + (ipAddress shr 8 and 0xff) + "."
                + (ipAddress shr 16 and 0xff) + "." + (ipAddress shr 24 and 0xff)))
    }

    fun getConnectState(context: Context, SSID: String): Int {
        var connectState: Int = WIFI_STATE_NONE

        if (Build.VERSION.SDK_INT >= 26) {
            //高通8.0 GO
            val wifiInfo: WifiInfo? = wifiManager.getConnectionInfo()
            val manager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkInfo: NetworkInfo? = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

            if (wifiInfo != null) {
                logi(
                    "",
                    "getConnectState()..connectState: " + connectState + "；ossid: " + SSID + ":wifiinfo ssid: " + wifiInfo.getSSID()
                )
                if (wifiInfo.getSSID() == "\"" + SSID + "\"" || wifiInfo.getSSID() == SSID) {
                    if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        logi("", "getConnectState().. network type: " + networkInfo.getType())
                        val detailedState: NetworkInfo.DetailedState =
                            networkInfo.getDetailedState()
                        //LogUtils.logd(TAG, ".SSID = " + wifiConfiguration.SSID + " " + detailedState + " status = " + wifiConfiguration.status + " rssi = " + rssi);
                        if (detailedState == NetworkInfo.DetailedState.CONNECTING
                            || detailedState == NetworkInfo.DetailedState.AUTHENTICATING
                            || detailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR
                        ) {
                            connectState = WIFI_STATE_CONNECTED
                        } else if (detailedState == NetworkInfo.DetailedState.CONNECTED
                            || detailedState == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK
                        ) {
                            connectState = WIFI_STATE_CONNECTED
                        }

                        logi("", "getConnectState().. detailedState: " + detailedState)
                    }
                }
            }
        }
        return connectState
    }

    private fun findNetworkidBySsid(ssid: String): Int {

        var wifiConfigs: List<WifiConfiguration>? = wifiManager.getConfiguredNetworks()
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            wifiConfigs = wifiManager.configuredNetworks
        }
            var curNetworkId: Int = -1
        if (wifiConfigs != null) {
            for (existingConfig: WifiConfiguration in wifiConfigs) {
                logd(TAG, "removeNetwork() wifiConfigs.. networkId: " + existingConfig.networkId)
                if (existingConfig.SSID == "\"" + ssid + "\"" || existingConfig.SSID == ssid) {
                    logd(TAG, "removeNetwork().. networkId: " + existingConfig.networkId)
                    curNetworkId = existingConfig.networkId
                    break
                }
            }
        }
        logd(TAG, "removeNetwork().. return networkId: " + curNetworkId)
        return curNetworkId
    }

    fun removeNetwork(ssid: String) {
        var curNetworkId: Int = -1

        curNetworkId = findNetworkidBySsid(ssid)
        if (curNetworkId != -1) {
            wifiManager.disconnect()
            val removeResult: Boolean = wifiManager.removeNetwork(curNetworkId)
            logd("auto_connect", "removeResult = " + removeResult)
        }
    }

    fun clearWifiPasswords() {
        // 获取 WifiManager 实例
        val wifiManager: WifiManager =
            mContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val wifiConfigurations: List<WifiConfiguration>? =
                wifiManager.configuredNetworks // 获取保存的 Wi-Fi 配置列表


            // 遍历 Wi-Fi 配置列表，移除每个网络的密码
            if (wifiConfigurations != null) {
                for (wifiConfiguration: WifiConfiguration in wifiConfigurations) {
                    wifiManager.disableNetwork(wifiConfiguration.networkId)
                    wifiManager.disconnect()
                    wifiManager.removeNetwork(wifiConfiguration.networkId)
                }
            }
        }
        // 保存配置更改
        wifiManager.saveConfiguration()
    }

    companion object {
        private val TAG: String = WIFIConnectionManager::class.java.getName()
        private var sInstance: WIFIConnectionManager? = null
        const val WIFI_STATE_NONE: Int = 0
        const val WIFI_STATE_CONNECTING: Int = 2
        const val WIFI_STATE_CONNECTED: Int = 4
        @JvmStatic
        fun getInstance(context: Context): WIFIConnectionManager {
            if (sInstance == null) {
                synchronized(WIFIConnectionManager::class.java) {
                    if (sInstance == null) {
                        sInstance = WIFIConnectionManager(context)
                    }
                }
            }
            return sInstance!!
        }

        fun getSSID(ctx: Context): String {
            val wifiManager: WifiManager =
                ctx.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = wifiManager.getConnectionInfo()
            val ssid: String = wifiInfo.getSSID()
            return ssid.replace("\"".toRegex(), "")
        }

        @JvmStatic
        fun isNetworkAvailable(context: Context): Boolean {
            val cm: ConnectivityManager? = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (cm != null) {
                //如果仅仅是用来判断网络连接
                val info: Array<Network> = cm.getAllNetworks()
                if (info != null) {
                    for (network: Network? in info) {
                        val networkInfo: NetworkInfo? = cm.getNetworkInfo(network)
                        if (networkInfo!!.getState() == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}
