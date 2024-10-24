package com.renhejia.robot.launcher.displaymode.calendar

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.gson.Gson
import com.renhejia.robot.commandlib.parser.displaymodes.calendar.CalenderInfo
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.getInstance
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.displaymode.callback.CalendarNoticeInfoUpdateCallback
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager.Companion.getInstance
import java.lang.ref.WeakReference

/**
 * 日历提醒
 */
class CalendarNoticeView : RelativeLayout {
    private var mContext: Context? = null
    private var noticeTime1: TextView? = null
    private var noticeName1: TextView? = null
    private var noticeTime2: TextView? = null
    private var noticeName2: TextView? = null
    private var noticeTime3: TextView? = null
    private var noticeName3: TextView? = null
    private var gson: Gson? = null
    private var mHandler: UpdateViewHandler? = null
    private var mCalenderInfo: CalenderInfo? = null


    constructor(context: Context) : super(context) {
        init(context)
        fillData()
        addListeners()
    }

    private fun fillData() {
        val calendar = LauncherConfigManager.getInstance(mContext!!)!!.robotCalendar
        val calenderInfo = gson!!.fromJson(
            calendar,
            CalenderInfo::class.java
        )
        updateViews(calenderInfo)
    }

    private fun updateViews(calenderInfo: CalenderInfo?) {
        if (calenderInfo != null && calenderInfo.data!!.memo_list != null && calenderInfo.data!!.memo_list.isNotEmpty()) {
            if (calenderInfo.data!!.memo_list[0] != null && calenderInfo.data!!.memo_list[0].memo_title != null && calenderInfo.data!!.memo_list[0].memo_time_label != null) {
                Log.e("letianpai_123456", " ====== canlendar 0=========")
                noticeName1!!.text = calenderInfo.data!!.memo_list[0].memo_title
                noticeTime1!!.text = calenderInfo.data!!.memo_list[0].memo_time_label
            }
            if (calenderInfo.data!!.memo_list[1] != null && calenderInfo.data!!.memo_list[1].memo_title != null && calenderInfo.data!!.memo_list[1].memo_time_label != null) {
                Log.e("letianpai_123456", " ====== canlendar 1=========")
                noticeName2!!.text = calenderInfo.data!!.memo_list[1].memo_title
                noticeTime2!!.text = calenderInfo.data!!.memo_list[1].memo_time_label
            }
            if (calenderInfo.data!!.memo_list[2] != null && calenderInfo.data!!.memo_list[2].memo_title != null && calenderInfo.data!!.memo_list[2].memo_time_label != null) {
                Log.e("letianpai_123456", " ====== canlendar 2=========")
                noticeName3!!.text = calenderInfo.data!!.memo_list[2].memo_title
                noticeTime3!!.text = calenderInfo.data!!.memo_list[2].memo_time_label
            }
        }
    }

    private fun updateCalenderInfo(calenderInfo: CalenderInfo) {
        this.mCalenderInfo = calenderInfo
    }


    private fun addListeners() {
        CalendarNoticeInfoUpdateCallback.instance.setCalendarInfoListener { calenderInfo ->
            //CalendarData{event_total=3, has_more=false, memo_list=[CalenderItem{memo_title='去超市买菜，要买萝卜和西红柿。', memo_time=1682582038, memo_time_label='今天 15:00'}, CalenderItem{memo_title='去户外遛狗30分钟', memo_time=1682585638, memo_time_label='今天 18:00'}, CalenderItem{memo_title='起床俯卧撑20个', memo_time=1682589238, memo_time_label='明天 8:00'}]}
            //                updateViews(calenderInfo);
            updateCalenderInfo(calenderInfo)
            update()
            //
        }
    }

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
        mHandler = UpdateViewHandler(context)
        inflate(context, R.layout.robot_display_calendar, this)
        noticeName1 = findViewById(R.id.tv_notice_name1)
        noticeTime1 = findViewById(R.id.tv_notice_time1)
        noticeName2 = findViewById(R.id.tv_notice_name2)
        noticeTime2 = findViewById(R.id.tv_notice_time2)
        noticeName3 = findViewById(R.id.tv_notice_name3)
        noticeTime3 = findViewById(R.id.tv_notice_time3)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }


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
                UPDATE_STATUS -> updateViews(mCalenderInfo)
            }
        }
    } //WeatherData{province='北京', city='北京城区', town='', wea='多云', wea_img='', tem='16', tem_max='22', tem_min='13', win='北', win_speed='≤3', hourWeather=null}

    companion object {
        private const val UPDATE_STATUS = 111
    }
}
