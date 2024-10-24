package com.renhejia.robot.launcher.countdown

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.renhejia.robot.gesturefactory.util.GestureConsts
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcher.dispatch.gesture.GestureCallback
import java.lang.ref.WeakReference

/**
 * @author liujunbin
 */
class CountdownView : RelativeLayout {
    private var mContext: Context? = null
    private var countDownTime: TextView? = null
    private var countdownFinish: TextView? = null
    private var clock: ImageView? = null
    private var title: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var clockCountDownTimer: CountDownTimer? = null
    private val UPDATE_COUNT_DOWN = 1
    private val FINISH_COUNT_DOWN = 2
    private var updateCountDownHandler: UpdateCountDownHandler? = null
    private var mHour: Long = 0
    private var mMinute: Long = 0
    private var mSecond: Long = 0

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
        updateCountDownHandler = UpdateCountDownHandler(context)
        inflate(context, R.layout.robot_countdown_view, this)
        countDownTime = findViewById(R.id.time_count_down)
        countdownFinish = findViewById(R.id.time_count_down_finish)
        title = findViewById(R.id.tv_countdown_title)
        clock = findViewById(R.id.clock)
        //        setCountDownTime("00:05:00");
    }

    private fun startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer!!.start()
        }
    }

    fun setCountDownTime(time: String) {
        title!!.visibility = VISIBLE
        countDownTime!!.visibility = VISIBLE
        clock!!.visibility = GONE
        countdownFinish!!.visibility = GONE

        val countTime = convertTime(time)
        initCountDownTimer(countTime, 1000)
        countDownTime!!.text = time
        startCountdownTimer()
    }

    private fun convertTime(time: String): Long {
        Log.e("letianpai_1356789", "time: $time")
        val times = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (times == null && times.size < 3) {
            return 0
        } else {
            val hour = if (times[0] == "00") {
                0
            } else {
                times[0].toLong() * 1000 * 60 * 60
            }
            mHour = hour

            val minute = if (times[1] == "00") {
                0
            } else {
                times[1].toLong() * 1000 * 60
            }
            mMinute = minute

            val second = if (times[2] == "00") {
                0
            } else {
                times[2].toLong() * 1000
            }
            mSecond = second
            Log.e("letianpai_1356789", "hour + minute + second: $hour$minute$second")
            return hour + minute + second
        }
    }

    fun initCountDownTimer(millisInFuture: Long, countDownInterval: Long) {
        countDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("letianpai_1356789", "millisUntilFinished: $millisUntilFinished")
                updateCountDown(millisUntilFinished)
            }

            override fun onFinish() {
                finishCountDown()
            }
        }

        clockCountDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                GestureCallback.instance.setGesture(GestureConsts.GESTURE_STAND_RESET)
            }
        }
    }

    private fun updateCountDown(millisUntilFinished: Long) {
        val message = Message()
        message.what = UPDATE_COUNT_DOWN
        message.obj = millisUntilFinished
        updateCountDownHandler!!.sendMessage(message)
    }

    private fun finishCountDown() {
        val message = Message()
        message.what = FINISH_COUNT_DOWN
        updateCountDownHandler!!.sendMessage(message)
    }

    private fun updateCountDownView(message: Message) {
        if (message.obj == null) {
            return
        }
        val time = message.obj as Long
        Log.e("letianpai_1356789", "time: $time")
        val hour = time / 1000 / 60 / 60
        val minute = time / 1000 / 60
        val second = time / 1000 % 60

        Log.e("letianpai_1356789", "hour: $hour")
        Log.e("letianpai_1356789", "minute: $minute")
        Log.e("letianpai_1356789", "second: $second")
        countDownTime!!.text =
            getCorrectString(hour) + ":" + getCorrectString(minute) + ":" + getCorrectString(second)
    }

    private fun getCorrectString(number: Long): String {
        return if (number < 10) {
            "0$number"
        } else {
            "" + number
        }
    }

    private fun finishCountDownView(message: Message) {
        Log.e("letianpai", "time_finished")
        GestureCallback.instance.setGesture(GestureConsts.GESTURE_COUNT_DOWN)
        title!!.visibility = GONE
        countDownTime!!.visibility = GONE
        clock!!.visibility = VISIBLE
        countdownFinish!!.visibility = VISIBLE
        setFinishText(mHour, mMinute, mSecond)
        clockCountDownTimer!!.start()
    }

    private inner class UpdateCountDownHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_COUNT_DOWN -> updateCountDownView(msg)
                FINISH_COUNT_DOWN -> finishCountDownView(msg)
            }
        }
    }

    fun setFinishText(hour: Long, minute: Long, second: Long) {
        var hourStr: String? = null
        var minuteStr: String? = null
        var secondStr: String? = null
        hourStr = if (hour == 0L) {
            ""
        } else {
            hour.toString() + mContext!!.getString(R.string.hour)
        }
        minuteStr = if (minute == 0L) {
            ""
        } else {
            minute.toString() + mContext!!.getString(R.string.minute)
        }
        secondStr = if (second == 0L) {
            ""
        } else {
            second.toString() + mContext!!.getString(R.string.second)
        }
        //        String result = String.format(mContext.getString(R.string.countdown_finish), hourStr, minuteStr,secondStr);
        val result = mContext!!.getString(R.string.countdown_finish1)
        countdownFinish!!.text = result
    }
}
