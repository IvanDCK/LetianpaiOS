package com.renhejia.robot.launcher.statusbar

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtil {
    fun getWeek(time: Long): String {
        val cd = Calendar.getInstance()
        cd.time = Date(time)

        val year = cd[Calendar.YEAR] //获取年份
        val month = cd[Calendar.MONTH] //获取月份
        val day = cd[Calendar.DAY_OF_MONTH] //获取日期
        val week = cd[Calendar.DAY_OF_WEEK] //获取星期
        val weekString = when (week) {
            Calendar.SUNDAY -> "周日"
            Calendar.MONDAY -> "周一"
            Calendar.TUESDAY -> "周二"
            Calendar.WEDNESDAY -> "周三"
            Calendar.THURSDAY -> "周四"
            Calendar.FRIDAY -> "周五"
            else -> "周六"
        }

        return weekString
    }

    /**
     *
     * @param time  1541569323155
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-11-07 13:42:03
     */
    fun getDate2String(time: Long, pattern: String?): String {
        val date = Date(time)
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(date)
    }

    val correctTime: String
        get() {
            val pattern1 = "yyyy/MM/dd"
            val pattern2 = "HH:mm"
            val currentTime = System.currentTimeMillis()
            val a =
                getDate2String(currentTime, pattern1)
            val b =
                getDate2String(currentTime, pattern2)
            val c =
                getWeek(currentTime)
            return "$a $c $b"
        }
}
