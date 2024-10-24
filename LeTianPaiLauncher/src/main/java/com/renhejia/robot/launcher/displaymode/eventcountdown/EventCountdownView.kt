package com.renhejia.robot.launcher.displaymode.eventcountdown

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.renhejia.robot.commandlib.parser.displaymodes.countdown.CountDownListInfo
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.getInstance
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.displaymode.callback.CountDownInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.callback.CountDownInfoUpdateCallback.CountDownInfoListener
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager.Companion.getInstance
import java.lang.ref.WeakReference

class EventCountdownView : RelativeLayout {
    private var mContext: Context? = null
    private var countdownTime: TextView? = null
    private var countdownTitle: TextView? = null
    private var gson: Gson? = null
    private var mHandler: UpdateViewHandler? = null
    private var mCountDownListInfo: CountDownListInfo? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context,  attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
         attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(
        context: Context,
         attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
        mHandler = UpdateViewHandler(context)
        inflate(context, R.layout.robot_display_countdown, this)
        countdownTime = findViewById(R.id.countdown_time)
        countdownTitle = findViewById(R.id.countdown_title)
        fillData()
        addEventCountListeners()
    }

    private fun fillData() {
//        String countDown = LauncherConfigManager.getInstance(mContext).getRobotCountDown();
//        CountDownListInfo countDownListInfo = gson.fromJson(countDown,CountDownListInfo.class);
//        updateViews(countDownListInfo);
    }

    private fun updateCountDownList(countDownListInfo: CountDownListInfo) {
        this.mCountDownListInfo = countDownListInfo
    }

    private fun updateViews(countDownListInfo: CountDownListInfo?) {
        if (countDownListInfo?.data != null && countDownListInfo.data!!.event_list != null && countDownListInfo.data!!.event_list.isNotEmpty()) {
            if (countDownListInfo.data!!.event_list[0].event_title != null) {
//                if (countDownListInfo.getData().getEvent_list()[0].getEvent_title().length() >10){
//                    countdownTitle.setText((countDownListInfo.getData().getEvent_list()[0].getEvent_title()).substring(10));
//                }
                countdownTitle!!.text = countDownListInfo.data!!.event_list[0].event_title
                if (countDownListInfo.data!!.event_list[0].remain_days === 0) {
                    countdownTime!!.text = "今天"
                } else if (countDownListInfo.data!!.event_list[0].remain_days === 1) {
                    countdownTime!!.text = "明天"
                } else if (countDownListInfo.data!!.event_list[0].remain_days === 2) {
                    countdownTime!!.text = "后天"
                } else {
                    countdownTime?.text = "${countDownListInfo.data!!.event_list[0].remain_days}  天后"
                }
            }
        }
    }

    private fun addEventCountListeners() {
        CountDownInfoUpdateCallback.instance.seCountDownInfoUpdateListener { countDownListInfo ->
            updateCountDownList(countDownListInfo)
            update()
        }
    }

    //event_list=[CountDownEventItem{event_title='明天是个好日子', event_time=-62135596800, remain_days=0}]}
    private fun update() {
        val message = Message()
        message.what = UPDATE_STATUS
        mHandler!!.sendMessage(message)
    }

    private inner class UpdateViewHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_STATUS -> updateViews(mCountDownListInfo)
            }
        }
    }


    companion object {
        private const val UPDATE_STATUS = 111
    }
}
