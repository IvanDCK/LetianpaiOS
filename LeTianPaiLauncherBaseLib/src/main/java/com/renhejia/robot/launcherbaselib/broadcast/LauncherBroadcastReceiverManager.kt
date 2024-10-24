package com.renhejia.robot.launcherbaselib.broadcast

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.renhejia.robot.launcherbaselib.battery.BatteryReceiver
import com.renhejia.robot.launcherbaselib.timer.TimerReceiver

/**
 * 广播管理器
 *
 * @author liujunbin
 */
class LauncherBroadcastReceiverManager(var mContext: Context) {
    init {
        init(mContext)
    }

    private fun init(context: Context) {
        // TODO 增加需要监听的广播进行初始化
        // TODO 此处为需要监听状态的统一入口，唯一的监听位置，后续需要状态的，统一在此处进行监听后进行分发
        // setBatteryListener();
        setNetWorkChangeListener()
        setWifiChangeListener()
        // setTimeListener();
    }

    //电池监听
    private fun setBatteryListener() {
        val batteryReceiver = BatteryReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        mContext.registerReceiver(batteryReceiver, intentFilter)
    }

    private fun setNetWorkChangeListener() {
        val netChangeReceiver = NetWorkChangeReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        //        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)

        mContext.registerReceiver(netChangeReceiver, intentFilter)
    }

    private fun setWifiChangeListener() {
        val wifiReceiver = WifiReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        mContext.registerReceiver(wifiReceiver, intentFilter)
    }

    private fun setTimeListener() {
        val mTimeReceiver = TimerReceiver()
        val timeFilter = IntentFilter()
        timeFilter.addAction(Intent.ACTION_TIME_TICK)
        mContext.registerReceiver(mTimeReceiver, timeFilter)
    }


    companion object {
        private var instance: LauncherBroadcastReceiverManager? = null

        fun getInstance(context: Context): LauncherBroadcastReceiverManager {
            synchronized(LauncherBroadcastReceiverManager::class.java) {
                if (instance == null) {
                    instance = LauncherBroadcastReceiverManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}
