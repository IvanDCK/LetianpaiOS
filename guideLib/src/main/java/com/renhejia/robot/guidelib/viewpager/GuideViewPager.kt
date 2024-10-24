package com.renhejia.robot.guidelib.viewpager

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.guidelib.R
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback.BleConnectStatusChangedListener
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback
import com.renhejia.robot.guidelib.qrcode.QRCodeView
import com.renhejia.robot.guidelib.view.AutoConnectWifiView

/**
 * 引导页ViewPager
 */
class GuideViewPager : ViewPager {
    private var adapter: GuideViewpagerAdapter? = null
    private var mContext: Context? = null
    private val guideViews: MutableList<View> = ArrayList()
    private var bleConnectStatusChangedListener: BleConnectStatusChangedListener? = null
    private val TAG: String = "ble_viewpager"
    private val chargeBroadCast: ChargeBroadCast = ChargeBroadCast()
    private var isCharging: Boolean = false
    private val uiHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            logd("GuideViewPager", "handleMessage: 关机")
            if (msg.what == CLOSE_DEVICE && isLauncherOnTheTop) {
                logd("GuideViewPager", "handleMessage: 关机1")
                GuideFunctionCallback.instance.onShutDown()
            }
        }
    }

    val isLauncherOnTheTop: Boolean
        get() {
            val activityName: String? = topActivityName
            if (activityName != null && activityName.contains(getContext().getPackageName())) {
                return true
            } else {
                return false
            }
        }

    val topActivityName: String?
        get() {
            val am: ActivityManager =
                getContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningTasks: List<ActivityManager.RunningTaskInfo>? =
                am.getRunningTasks(1)
            if (runningTasks != null && runningTasks.size > 0) {
                val taskInfo: ActivityManager.RunningTaskInfo = runningTasks.get(0)
                val componentName: ComponentName? = taskInfo.topActivity
                if (componentName != null && componentName.getClassName() != null) {
                    return componentName.getClassName()
                }
            }
            return null
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        this.setOverScrollMode(View.OVER_SCROLL_NEVER)
        logd("GuideViewPager", "init: ")
        initView()
        initBleConnectStatusListener()
        registerBleConnectStatusListener()
        addListeners()
        initCloseHandler()
        //        LogUtils.logi("auto_connect"," SystemUtils.getWlanMacAddress(): "+ SystemUtils.getWlanMacAddress());
    }

    private fun initCloseHandler() {
        val ifilter: IntentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = mContext!!.registerReceiver(null, ifilter)
        val status: Int = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
        chargeStatusChange()

        val intentFilter: IntentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        mContext!!.registerReceiver(chargeBroadCast, intentFilter)
    }

    private fun chargeStatusChange() {
        if (!isCharging && this@GuideViewPager.getCurrentItem() == 0) {
            uiHandler.sendEmptyMessageDelayed(CLOSE_DEVICE, CLOSE_TIME.toLong())
        } else {
            uiHandler.removeMessages(CLOSE_DEVICE)
        }
    }


    private fun initBleConnectStatusListener() {
        bleConnectStatusChangedListener = object : BleConnectStatusChangedListener {
            override fun onBleConnectStatusChanged(connectStatus: Int) {
                if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECT_TO_CLIENT) {
                    logi(
                        TAG,
                        "1__BleConnectStatusCallback.BLE_STATUS_CONNECT_TO_CLIENT" + Thread.currentThread()
                    )
                    uiHandler.removeMessages(CLOSE_DEVICE)
                    uiHandler.post(Runnable { this@GuideViewPager.setCurrentItem(1) })
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_SUCCESS) {
                    logi(TAG, "2__BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_SUCCESS")
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_FAILED) {
                    logi(TAG, "3__BleConnectStatusCallback.BLE_STATUS_CONNECTED_NET_FAILED")
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_DISCONNECT_FROM_CLIENT) {
                    logi(TAG, "4__BleConnectStatusCallback.BLE_STATUS_DISCONNECT_FROM_CLIENT")
                }
            }
        }
    }

    private fun addListeners() {
        BleConnectStatusCallback.instance
            .registerBleConnectStatusListener(object : BleConnectStatusChangedListener {
                override fun onBleConnectStatusChanged(connectStatus: Int) {
                    if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_SUCCESS) {
                        //配网成功 TODO
                        // uiHandler.post(() -> GuideViewPager.this.setCurrentItem(2));
                        uiHandler.post(Runnable {
                            GuideFunctionCallback.instance.closeGuide()
                        })
                    }
                }
            })
    }

    private fun registerBleConnectStatusListener() {
        BleConnectStatusCallback.instance
            .registerBleConnectStatusListener(bleConnectStatusChangedListener)
    }

    private fun unregisterBleConnectStatusListener() {
        if (bleConnectStatusChangedListener != null) {
            BleConnectStatusCallback.instance
                .unregisterBleConnectStatusListener(bleConnectStatusChangedListener)
        }
    }

    private fun initView() {
        val finishView: QRCodeView = QRCodeView(mContext)
        finishView.setQrcodeVisible(View.GONE)
        finishView.setTipsDescVisible(View.GONE)
        finishView.setAddDeviceTvVisible(View.GONE)
        //        finishView.setTvScan("已绑定成功\n请用微信扫码学习教程");
        finishView.setTvScan(getContext().getString(R.string.guide_tips))
        guideViews.add(QRCodeView(mContext))
        guideViews.add(AutoConnectWifiView(mContext))
        guideViews.add(finishView)
        adapter = GuideViewpagerAdapter(mContext, guideViews)
        setAdapter(adapter)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mContext!!.unregisterReceiver(chargeBroadCast)
        unregisterBleConnectStatusListener()
    }

    internal inner class ChargeBroadCast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getAction()) {
                Intent.ACTION_POWER_DISCONNECTED -> {
                    isCharging = false
                    chargeStatusChange()
                }

                Intent.ACTION_POWER_CONNECTED -> {
                    isCharging = true
                    chargeStatusChange()
                }
            }
        }
    }

    companion object {
        const val CLOSE_DEVICE: Int = 0x201
        val CLOSE_TIME: Int = 5 * 60 * 1000
    }
}
