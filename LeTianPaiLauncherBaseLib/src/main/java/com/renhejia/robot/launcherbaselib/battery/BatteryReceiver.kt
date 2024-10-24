package com.renhejia.robot.launcherbaselib.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.renhejia.robot.launcherbaselib.info.LauncherInfoManager
import kotlin.math.max
import kotlin.math.min

/**
 * @author liujunbin
 */
class BatteryReceiver : BroadcastReceiver() {
    private var mContext: Context? = null

    override fun onReceive(context: Context, intent: Intent) {
        this.mContext = context
        when (intent.action) {
            Intent.ACTION_BATTERY_CHANGED -> handleBatteryChanged(context, intent)
            Intent.ACTION_POWER_DISCONNECTED -> responseDisconnect()
            Intent.ACTION_POWER_CONNECTED -> responseConnect()
        }
    }

    private fun responseDisconnect() {
        val percent: Int = LauncherInfoManager.getInstance(mContext!!).batteryLevel
        LauncherInfoManager.getInstance(mContext!!).isChargingMode = false
        Log.e("letianpai", "responseDisconnect_responseCharging")
        ChargingUpdateCallback.instance.setChargingStatus(false, percent)
    }

    private fun responseConnect() {
        val percent: Int = LauncherInfoManager.getInstance(mContext!!).batteryLevel
        LauncherInfoManager.Companion.getInstance(mContext!!).isChargingMode = false
        Log.e("letianpai", "responseConnect_responseCharging")
        ChargingUpdateCallback.instance.setChargingStatus(false, percent)
    }

    private fun handleBatteryChanged(context: Context, intent: Intent) {
        val bundle = intent.extras

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        Log.e("letianpai_1213", "chargePlug: $chargePlug")
        if (null == bundle) {
            return
        }

        // 获取当前电量
        val current = bundle.getInt(BatteryManager.EXTRA_LEVEL, 0)
        // 获取总电量
        val total = bundle.getInt(BatteryManager.EXTRA_SCALE, 100)

        if (total == 0) {
            return
        }
        var percent = current * 100 / total
        percent = max(0.0, min(percent.toDouble(), 100.0)).toInt()
        LauncherInfoManager.Companion.getInstance(context).batteryLevel = percent

        //TODO 电量低的时候，需要做关机提醒
//        if (percent < 4
////                && (! LauncherInfoManager.getInstance(context).hadShowCountdownDialog())
////                && (! LauncherInfoManager.getInstance(mContext).isChargingMode())
//        ){
////            LauncherInfoManager.getInstance(context).setHadShowCountdownDialog(true);
//            showCountdownDialog();
//        }
        val status =
            intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        if (percent <= 15) {
            showBatteryLowDialog(context)
        }

        when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> responseCharging(context, percent)
            BatteryManager.BATTERY_STATUS_FULL -> if (isCharging(chargePlug)) {
                responseCharging(context, percent)
            } else {
                responseDisCharging(context, percent)
            }

            BatteryManager.BATTERY_STATUS_DISCHARGING -> {
                responseDisCharging(context, percent)
                responseDisCharging(context, percent, chargePlug)
            }

            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {
                responseDisCharging(context, percent)
                responseDisCharging(context, percent, chargePlug)
            }

            BatteryManager.BATTERY_STATUS_UNKNOWN -> {
                responseDisCharging(context, percent)
                responseDisCharging(context, percent, chargePlug)
            }
        }
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_BATTERY_UPDATE))
    }

    /**
     * 响应断冲
     * @param context
     * @param percent
     * @param chargePlug
     */
    private fun responseDisCharging(context: Context, percent: Int, chargePlug: Int) {
        //TODO 需要增加重复弹窗保护
        //TODO to be check and removed

        LauncherInfoManager.getInstance(mContext!!).isChargingMode = false
        ChargingUpdateCallback.instance.setChargingStatus(false, percent, chargePlug)
        hideChargingView()
    }

    /**
     * 响应断冲
     * @param context
     * @param percent
     */
    private fun responseDisCharging(context: Context, percent: Int) {
        //TODO 需要增加重复弹窗保护
        //TODO to be check and removed

        LauncherInfoManager.Companion.getInstance(context).isChargingMode
        ChargingUpdateCallback.instance.setChargingStatus(false, percent)
        hideChargingView()
    }


    //
    //    /**
    //     * 响应充电
    //     *
    //     * @param context
    //     * @param percent
    //     */
    //    private void responseCharging(Context context, int percent, int chargePlug) {
    //        sendShowChargingDialog(context,percent);
    //        LauncherInfoManager.getInstance(context).setChargingMode(true);
    //        ChargingUpdateCallback.getInstance().setChargingStatus(true, percent);
    //        showChargingView();
    //        killThirdApps();
    //    }
    //
    /**
     * 响应充电
     *
     * @param context
     * @param percent
     */
    private fun responseCharging(context: Context, percent: Int) {
        sendShowChargingDialog(context, percent)
        LauncherInfoManager.getInstance(mContext!!).isChargingMode = true
        ChargingUpdateCallback.instance.setChargingStatus(true, percent)
        showChargingView()
        killThirdApps()
    }

    /**
     * 显示充电弹窗
     * @param context
     * @param percent
     */
    private fun sendShowChargingDialog(context: Context, percent: Int) {
        //TODO 展示通电弹窗，此处通道接口，不做UI展示，只做消息传递，展示层做此逻辑
    }

    /**
     * 显示低电弹窗
     *
     * @param mContext
     */
    private fun showBatteryLowDialog(mContext: Context) {
        //TODO 需要做低点提醒
        // TODO 乐天派需要做分段电量提醒逻辑（20%， 10%， 5%）
    }

    /**
     *
     * @param context
     * @return
     */
    private fun getScreenStatus(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isScreenOn
    }

    private fun isCharging(chargePlug: Int): Boolean {
        return if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC || chargePlug == BatteryManager.BATTERY_PLUGGED_USB || chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
        ) {
            true
        } else {
            false
        }
    }

    private fun showChargingView() {
        //TODO 显示正在弹窗页面
        //TODO 此处通道接口，不做UI展示，只做消息传递，展示层做此逻辑
    }

    private fun hideChargingView() {
        //TODO 此处通道接口，不做UI展示，只做消息传递，展示层做此逻辑
    }

    /**
     * 倒计时关机接口
     */
    fun showCountdownDialog() {
        //TODO 此处通道接口，不做UI展示，只做消息传递，展示层做此逻辑
    }


    /**
     * 杀死限流列表的应用
     */
    private fun killThirdApps() {
        //TODO 给framework层法消息，充电时，杀掉第三方进程
    }


    companion object {
        const val ACTION_BATTERY_UPDATE: String = "com.renhejia.robot.action.battery_update"
    }
}
