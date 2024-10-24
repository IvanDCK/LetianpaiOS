package com.renhejia.robot.launcher.statusbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback
import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback.ChargingUpdateListener

/**
 * 充电状态
 * @author liujunbin
 */
class BatteryCharging : RelativeLayout {
    private var progress: View? = null
    private var empty: View? = null
    private var redProgress: View? = null
    private var ivCharging: ImageView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    private fun init(context: Context) {
        inflate(context, R.layout.robot_charge, this)
        progress = findViewById(R.id.battery_progress)
        empty = findViewById(R.id.battery_empty)
        redProgress = findViewById(R.id.battery_low)
        ivCharging = findViewById(R.id.ivcharging)
        //addChargingCallback();
    }


    private fun addChargingCallback() {
        ChargingUpdateCallback.instance.registerChargingStatusUpdateListener(object :
            ChargingUpdateListener {
            override fun onChargingUpdateReceived(changingStatus: Boolean, percent: Int) {
                logi(
                    "changingStatus",
                    "changingStatus: $changingStatus"
                )
                logi(
                    "changingStatus",
                    "percent: $percent"
                )
                setBatteryLevel(percent.toFloat())
                if (changingStatus) {
                    ivCharging!!.visibility = VISIBLE
                } else {
                    ivCharging!!.visibility = INVISIBLE
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

    fun setBatteryLevel(batteryLevel: Float) {
        ivCharging!!.visibility = VISIBLE
        redProgress!!.visibility = GONE
        progress!!.visibility = VISIBLE
        val params = LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT)
        params.weight = batteryLevel //在此处设置weight
        progress!!.layoutParams = params

        val params1 = LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT)
        params1.weight = 100 - batteryLevel //在此处设置weight
        empty!!.layoutParams = params1
    }

    //    public void setBatteryLow() {
    //        ivCharging.setVisibility(View.GONE);
    //        redProgress.setVisibility(View.VISIBLE);
    //        progress.setVisibility(View.GONE);
    //
    //        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT);
    //        params.weight = 10;//在此处设置weight
    //        redProgress.setLayoutParams(params);
    //
    //        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT);
    //        params1.weight = 90;//在此处设置weight
    //        empty.setLayoutParams(params1);
    //
    //    }
    fun setBatteryLow(batteryLevel: Float) {
        ivCharging!!.visibility = GONE
        redProgress!!.visibility = VISIBLE
        progress!!.visibility = GONE

        val params = LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT)
        params.weight = batteryLevel //在此处设置weight
        redProgress!!.layoutParams = params

        val params1 = LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT)
        params1.weight = 100 - batteryLevel //在此处设置weight
        empty!!.layoutParams = params1
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

    companion object {
        private const val SHOW_TIME = 110
        private const val SHOW_HI_XIAOLE = 111
        private const val UPDATE_ICON = 110
        private const val HIDE_TEXT = 112
    }
}
