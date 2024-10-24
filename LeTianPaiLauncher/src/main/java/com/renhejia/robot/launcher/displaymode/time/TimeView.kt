package com.renhejia.robot.launcher.displaymode.time

import android.content.Context
import android.database.ContentObserver
import android.os.Build
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.letianpai.robot.notice.general.parser.GeneralInfo
import com.renhejia.robot.guidelib.utils.SystemUtil.isRobotInChinese
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.displaymode.callback.GeneralInfoCallback
import com.renhejia.robot.launcher.net.GeeUINetConsts
import com.renhejia.robot.launcher.nets.GeeUINetResponseManager
import com.renhejia.robot.launcher.system.LetianpaiFunctionUtil
import com.renhejia.robot.launcherbaselib.timer.TimerKeeperCallback.Companion.instance
import com.renhejia.robot.launcherbaselib.timer.TimerKeeperCallback.TimerKeeperUpdateListener
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeView : LinearLayout {
    private var mContext: Context? = null
    private val title: TextView? = null
    private var date: TextView? = null
    private var tvHour: TextView? = null
    private val time: Long = 0
    private var weather: TextView? = null
    private var ivWeather: ImageView? = null
    private val notice: TextView? = null
    private var gson: Gson? = null
    private var tvMin: TextView? = null
    private var location: TextView? = null
    private var ivLocation: ImageView? = null


    constructor(context: Context) : super(context) {
        init(context)
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

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
        mHandler = UpdateViewHandler(context)
        inflate(context, R.layout.robot_display_time, this)
        initView()
        generalInfo
        fillData()
        addListeners()
        addTimerUpdateCallback()
    }

    private val generalInfo: Unit
        get() {
            mContext?.let { GeeUINetResponseManager.getInstance(it).generalInfo }
            //        GeeUINetResponseManager.getInstance(mContext).getWeatherInfo();
        }

    //    private void fillData() {
    //        String weather = LauncherConfigManager.getInstance(mContext).getRobotGeneralInfo();
    //        GeneralInfo generalInfo = gson.fromJson(weather,GeneralInfo.class);
    //        updateViews(generalInfo);
    //
    //    }
    private fun fillData() {
//        String weather = LauncherConfigManager.getInstance(mContext).getRobotGeneralInfo();
        val generalInfo = mContext?.let { GeeUINetResponseManager.getInstance(it).generalInfo }
        if (generalInfo != null) {
            updateViews(generalInfo)
        }
    }

    private fun updateViews(generalInfo: GeneralInfo?) {
        if (generalInfo?.data == null || generalInfo.data!!.tem == null || generalInfo.data!!.wea == null || generalInfo.data!!.tem == null) {
            return
        }
        Log.e("letianpai_1234", "generalInfo.toString(): $generalInfo")
        //GeneralData{wea='阴', wea_img='', tem='18', calender_total=3}
        val weatherInfo = generalInfo.data!!.wea + " " + generalInfo.data!!.tem
        val notices = generalInfo.data!!.calender_total
        tvHour!!.text = hourTime

        tvMin!!.text = minTime
        if (!TextUtils.isEmpty(generalInfo.data!!.wea)) {
            weather!!.text = weatherInfo + DU
            ivWeather!!.visibility = VISIBLE
            fillWeatherIcon(generalInfo)
        } else {
            weather!!.text = ""
            ivWeather!!.visibility = INVISIBLE
        }

        val city = generalInfo.data!!.city

        //        if (!TextUtils.isEmpty(city)){
//            location.setText(city);
//            ivLocation.setVisibility(View.VISIBLE);
//        }else{
//            location.setText("");
//            ivLocation.setVisibility(View.INVISIBLE);
//        }
        location!!.text = ""
        ivLocation!!.visibility = GONE

        //        notice.setText(notices +"个日历提醒");
    }

    private fun fillWeatherIcon(generalInfo: GeneralInfo) {
        if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_1_FOG) {
            ivWeather!!.setImageResource(R.drawable.wea1)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_2_CLOUDY) {
            ivWeather!!.setImageResource(R.drawable.wea2)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_3_WIND) {
            ivWeather!!.setImageResource(R.drawable.wea3)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_4_SUNNY) {
            ivWeather!!.setImageResource(R.drawable.wea4)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_5_DUST) {
            ivWeather!!.setImageResource(R.drawable.wea5)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_6_SMOG) {
            ivWeather!!.setImageResource(R.drawable.wea6)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_7_SNOW) {
            ivWeather!!.setImageResource(R.drawable.wea7)
        } else if (generalInfo.data!!.wea_img == GeeUINetConsts.WEATHER_CONSTS_WEATHER_8_RAIN) {
            ivWeather!!.setImageResource(R.drawable.wea8)
        } else {
            ivWeather!!.setImageResource(R.drawable.wea4)
        }
    }

    private var mGeneralInfo: GeneralInfo? = null
    private fun updateViewData(generalInfo: GeneralInfo?) {
        this.mGeneralInfo = generalInfo
    }

    private fun addTimerUpdateCallback() {
        instance.registerTimerKeeperUpdateListener(object : TimerKeeperUpdateListener {
            override fun onTimerKeeperUpdateReceived(hour: Int, minute: Int) {
                updateTime()
            }
        })
    }

    private fun initView() {
        date = findViewById(R.id.tv_date)
        tvHour = findViewById(R.id.tv_time_hour)
        tvMin = findViewById(R.id.tv_time_min)
        weather = findViewById(R.id.tv_weather)
        ivWeather = findViewById(R.id.iv_weather)
        location = findViewById(R.id.tv_location)
        //        notice = findViewById(R.id.tv_notice);
        ivLocation = findViewById(R.id.iv_location)
        date!!.text = fullTime
        tvHour!!.text = hourTime
        tvMin!!.text = minTime
        val hour = hourTime
        val minute = minTime
        Log.e("letianpai", "getHourTime: $hour")
        Log.e("letianpai", "getMinTime: $minute")
    }


    private val fullTime: String
        get() =//        if (RobotDifferenceUtil.isChinese()){
//            return convertTimeFormat("yyyy年MM月dd日   E");
//        }else{
            convertTimeFormat("yyyy/MM/dd   E")

    //        }

    private val clockTime: String
        get() = convertTimeFormat("HH:mm")

    private fun get12HourTime(): String {
        return convertTimeFormat("hh")
    }

    private val hourTime: String
        get() = if (mContext?.let { LetianpaiFunctionUtil.is24HourFormat(it) } == true) {
            get24HourTime()
        } else {
            get12HourTime()
        }

    private fun get24HourTime(): String {
        return convertTimeFormat("HH")
    }

    private val minTime: String
        get() = convertTimeFormat("mm")


    private fun convertTimeFormat(strFormat: String): String {
        val date = Date(System.currentTimeMillis())
        val format = if (isRobotInChinese) {
            SimpleDateFormat(strFormat, Locale.SIMPLIFIED_CHINESE)
        } else {
            SimpleDateFormat(strFormat, (Locale.ENGLISH))
        }
        return format.format(date)
    }

    //
    //    private String convertTimeFormat(String strFormat) {
    //        Date date = new Date(System.currentTimeMillis());
    ////        SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
    //        SimpleDateFormat format = new SimpleDateFormat(strFormat, (Locale.ENGLISH));
    //        return format.format(date);
    //    }
    private fun addListeners() {
        GeneralInfoCallback.instance.setGeneralInfoUpdateListener { generalInfo ->
            updateViewData(generalInfo)
            update()
        }
    }

    private fun update() {
        val message = Message()
        message.what = UPDATE_STATUS
        mHandler!!.sendMessage(message)
    }

    private fun updateTime() {
        val message = Message()
        message.what = UPDATE_TIME
        mHandler!!.sendMessage(message)
        addTimeFormatChangeListeners()
    }

    private fun addTimeFormatChangeListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mContext!!.contentResolver.registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                object : ContentObserver(Handler()) {
                    override fun onChange(selfChange: Boolean) {
                        super.onChange(selfChange)
                        if (Settings.System.getString(
                                mContext!!.contentResolver,
                                Settings.System.TIME_12_24
                            ) == "24"
                        ) {
                            // 时间格式为24小时制
                            tvHour?.text = hourTime
                        } else {
                            // 时间格式为12小时制
                            tvHour?.text = hourTime
                        }
                    }
                }
            )
        }
    }

    private var mHandler: UpdateViewHandler? = null

    private inner class UpdateViewHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_STATUS -> updateViews(mGeneralInfo)
                UPDATE_TIME -> {
                    tvHour?.text = hourTime
                    tvMin?.text = minTime
                    date?.text = fullTime
                }
            }
        }
    }


    companion object {
        //    private static String DU = "℃";
        private const val DU = "°"
        private const val UPDATE_STATUS = 110
        private const val UPDATE_TIME = 111
    }
}
