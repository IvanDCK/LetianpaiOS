package com.renhejia.robot.launcher.statusbar

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.dispatch.statusbar.StatusBarUpdateCallback
import com.renhejia.robot.launcher.dispatch.statusbar.StatusBarUpdateCallback.StatusBarChangeListener
import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback
import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback.ChargingUpdateListener
import com.renhejia.robot.launcherbaselib.callback.NetworkChangingUpdateCallback
import com.renhejia.robot.launcherbaselib.info.LauncherInfoManager
import com.renhejia.robot.launcherbaselib.timer.TimerKeeperCallback
import com.renhejia.robot.launcherbaselib.timer.TimerKeeperCallback.TimerKeeperUpdateListener
import java.lang.ref.WeakReference

/**
 * 未曾使用，yujianbin24年2月18日记录
 */
@Deprecated("")
class StatusBar : LinearLayout {
    private var mContext: Context? = null
    private var topImage: ImageView? = null
    private var bottomText: TextView? = null
    private var batteryCharging: BatteryCharging? = null
    private var rootStatus: LinearLayout? = null
    private var rlTitlePart: LinearLayout? = null
    private var emptyView: View? = null
    private var mHandler: UpdateViewHandler? = null

    constructor(context: Context) : super(context) {
        init(context)
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        mHandler = UpdateViewHandler(context)
        inflate(mContext, R.layout.robot_status_bar, this)
        initView()
        showWifiStatus()
        //        setDisplayTime();
//        setBatteryLow();
//        setBottomText("请说 '嗨,小乐' 唤醒我");
        addUpdateTextListeners()
        addNetworkChangeListeners()
        addTimerUpdateCallback()
    }

    private fun showWifiStatus() {
        val status = LauncherInfoManager.getInstance(mContext!!).wifiStates
        if (status) {
            hideNoNetworkStatus()
        } else {
            setNoNetworkStatus()
        }
    }

    private fun addUpdateTextListeners() {
        StatusBarUpdateCallback.instance.setStatusBarTextChangeListener(object :
            StatusBarChangeListener {
            override fun onStatusBarTextChanged(content: String?) {
                showText(content)
            }
        })
    }

    private fun addTimerUpdateCallback() {
        TimerKeeperCallback.instance.registerTimerKeeperUpdateListener(object :
            TimerKeeperUpdateListener {
            override fun onTimerKeeperUpdateReceived(hour: Int, minute: Int) {
                updateTime()
            }
        })
    }


    private fun addNetworkChangeListeners() {
//        NetworkChangingUpdateCallback.getInstance().setChargingStatusUpdateListener(new NetworkChangingUpdateCallback.NetworkChangingUpdateListener() {
//            @Override
//            public void onNetworkChargingUpdateReceived(int networkType, int networkStatus) {
//                if (networkType == NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED) {
//                    setNoNetworkStatus();
//                }else{
//                    hideNoNetworkStatus();
//                }
//
//            }
//        });
        NetworkChangingUpdateCallback.instance.registerChargingStatusUpdateListener(object :
            NetworkChangingUpdateCallback.NetworkChangingUpdateListener {
            override fun onNetworkChargingUpdateReceived(networkType: Int, networkStatus: Int) {
                Log.e("letianpai_net", "networkType: $networkType")
                if (networkType == NetworkChangingUpdateCallback.NETWORK_TYPE_DISABLED) {
                    setNoNetworkStatus()
                } else {
                    hideNoNetworkStatus()
                }
            }
        })

        ChargingUpdateCallback.instance.registerChargingStatusUpdateListener(object :
            ChargingUpdateListener {
            override fun onChargingUpdateReceived(changingStatus: Boolean, percent: Int) {
                Log.e("letianpai_1213", "changingStatus: $changingStatus")
                Log.e("letianpai_1213", "percent: $percent")
                if (percent > LOW_BATTERY_STANDARD) {
                    bottomText!!.text = ""
                }
                if (changingStatus) {
                    setCharging(percent)
                } else if (percent < LOW_BATTERY_STANDARD) {
                    setBatteryLow(percent)
                } else {
                    batteryCharging!!.visibility = GONE
                }
            }

            override fun onChargingUpdateReceived(
                changingStatus: Boolean,
                percent: Int,
                chargePlug: Int
            ) {
            }
        })
    }

    private fun initView() {
        rootStatus = findViewById(R.id.root_status)
        //        rootStatus.getBackground().setAlpha(256);
        topImage = findViewById(R.id.title_part)
        bottomText = findViewById(R.id.bottom_part)
        batteryCharging = findViewById(R.id.bcCharging)
        emptyView = findViewById(R.id.empty_view)
        rlTitlePart = findViewById(R.id.rl_title_part)
    }

    fun setNoNetworkStatus() {
        topImage!!.visibility = VISIBLE
        topImage!!.setImageResource(R.drawable.no_network)
    }

    private fun hideNoNetworkStatus() {
        topImage!!.visibility = GONE
        //        bottomText.setVisibility(View.GONE);
    }


    fun setBatteryLow(percent: Int) {
//        topImage.setVisibility(View.GONE);
        batteryCharging!!.visibility = VISIBLE
        batteryCharging!!.setBatteryLow(percent.toFloat())
        bottomText!!.visibility = VISIBLE
        bottomText!!.setText(R.string.battery_low)
    }

    fun setCharging(percent: Int) {
        batteryCharging!!.visibility = VISIBLE
        batteryCharging!!.setBatteryLevel(percent.toFloat())
    }

    fun setDisplayTime() {
        bottomText!!.visibility = VISIBLE
        setBottomText(TimeUtil.correctTime)
    }


    /**
     * 设置底部文案
     *
     * @param content
     */
    fun setBottomText(content: String?) {
//        bottomText.setVisibility(View.VISIBLE);
//        bottomText.setText(content);
    }

    private inner class UpdateViewHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SHOW_TIME -> if (msg.obj != null) {
                    setBottomText((msg.obj) as String)
                }

                UPDATE_TIME -> {}
            }
        }
    }

    fun showText(text: String?) {
        val message = Message()
        message.what = SHOW_TIME
        message.obj = text
        mHandler!!.sendMessage(message)
    }

    fun updateTime() {
        val message = Message()
        message.what = UPDATE_TIME
        mHandler!!.sendMessage(message)
    }


    companion object {
        private const val LOW_BATTERY_STANDARD = 20
        private const val SHOW_TIME = 110
        private const val UPDATE_TIME = 111
    }
}
