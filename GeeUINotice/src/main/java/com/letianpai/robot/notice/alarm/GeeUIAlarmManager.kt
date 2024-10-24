package com.letianpai.robot.notice.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.letianpai.robot.notice.alarm.receiver.AlarmReceiver
import java.util.Calendar

class GeeUIAlarmManager private constructor(context: Context) {
    private var alarmManager: AlarmManager? = null

    private var mContext: Context? = null

    //    private Gson gson;
    init {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        alarmManager = mContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //        gson = new Gson();
    }

    // 删除闹钟
    fun createAlarm(lateTime: Int) {
        val calendar_now = Calendar.getInstance()
        calendar_now.timeInMillis = System.currentTimeMillis()
        calendar_now[Calendar.HOUR_OF_DAY] = calendar_now[Calendar.HOUR_OF_DAY]
        calendar_now[Calendar.MINUTE] =
            calendar_now[Calendar.MINUTE] + lateTime
        calendar_now[Calendar.SECOND] = 0
        calendar_now[Calendar.MILLISECOND] = 0

        val intent = Intent(mContext, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(
            mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager = mContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //正常设置
        alarmManager!![AlarmManager.RTC_WAKEUP, calendar_now.timeInMillis] = sender

        //设置循环
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar_now.getTimeInMillis(), 60* 60* 1000 *24,sender);
    }

    // 创建闹钟
    fun createAlarm(hour: Int, minute: Int) {
        val calendar_now = Calendar.getInstance()
        calendar_now.timeInMillis = System.currentTimeMillis()
        calendar_now[Calendar.HOUR_OF_DAY] = hour
        calendar_now[Calendar.MINUTE] = minute
        calendar_now[Calendar.SECOND] = 0
        calendar_now[Calendar.MILLISECOND] = 0

        val intent = Intent(mContext, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(
            mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        //        AlarmManager am;
        alarmManager = mContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //正常设置
        alarmManager!![AlarmManager.RTC_WAKEUP, calendar_now.timeInMillis] = sender
        //设置循环
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar_now.getTimeInMillis(), 60* 60* 1000 *24,sender);
    }


    // 删除闹钟
    fun createRepeatingAlarm(lateTime: Int, intervalMillis: Long) {
        val calendar_now = Calendar.getInstance()
        calendar_now.timeInMillis = System.currentTimeMillis()
        calendar_now[Calendar.HOUR_OF_DAY] = calendar_now[Calendar.HOUR_OF_DAY]
        calendar_now[Calendar.MINUTE] =
            calendar_now[Calendar.MINUTE] + lateTime
        calendar_now[Calendar.SECOND] = 0
        calendar_now[Calendar.MILLISECOND] = 0

        val intent = Intent(mContext, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(
            mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val am =
            mContext!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //正常设置
//        am.set(AlarmManager.RTC_WAKEUP, calendar_now.getTimeInMillis(), sender);
        //设置循环
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar_now.timeInMillis, intervalMillis, sender)
    }

    // 删除闹钟
    fun createAlarm(context: Context, hour: Int, minute: Int) {
        val pendingIntent: PendingIntent

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 创建一个 Intent 对象，用于指定闹钟触发时需要执行的操作
        val intent = Intent(mContext, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0)

        // 获取当前时间和日期
        val calendar = Calendar.getInstance()
        val currentHour = calendar[Calendar.HOUR_OF_DAY]
        val currentMinute = calendar[Calendar.MINUTE]

        // 计算闹钟触发时间
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        if (currentHour > hour || (currentHour == hour && currentMinute >= 0)) {
            // 如果当前时间已经过了 2:00，就将闹钟设置为第二天的 2:00
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // 设置闹钟
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )
    }


    // 删除闹钟
    private fun deleteAlarm(position: Int) {
//         把闹钟从闹钟列表移除
//         移除闹钟
//        alarmManager.cancel(PendingIntent.getBroadcast(mContext,ad.getId(),new Intent(getContext(), AlarmReceiver.class),0));
    }


    companion object {
        private var instance: GeeUIAlarmManager? = null
        fun getInstance(context: Context): GeeUIAlarmManager {
            synchronized(GeeUIAlarmManager::class.java) {
                if (instance == null) {
                    instance = GeeUIAlarmManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}