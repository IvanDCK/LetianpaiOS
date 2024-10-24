package com.renhejia.robot.launcher.system

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.renhejia.robot.launcher.LTPConfigConsts

/**
 * @author liujunbin
 */
object LetianpaiFunctionUtil {
    /**
     * 打开工厂app
     * @param context
     */
    fun openFactoryApp(context: Context) {
        val intent = Intent()
        intent.setComponent(
            ComponentName(
                "com.letianpai.ltp_factory_test2",
                "com.letianpai.ltp_factory_test2.LauncherActivity"
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }


    const val LAUNCHER_CLASS_NAME: String =
        "com.renhejia.robot.launcher.main.activity.LeTianPaiMainActivity"

    /**
     * @param context
     * @return
     */
    fun isLauncherOnTheTop(context: Context): Boolean {
        val activityName = getTopActivityName(context)
        Log.e("letianpai_", "")
        return if (activityName != null && activityName == LAUNCHER_CLASS_NAME) {
            true
        } else {
            false
        }
    }

    /**
     * 获取顶部 Activity
     *
     * @param context
     * @return
     */
    fun getTopActivityName(context: Context): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningTasks = am.getRunningTasks(1)
        if (runningTasks != null && runningTasks.size > 0) {
            val taskInfo = runningTasks[0]
            val componentName = taskInfo.topActivity
            if (componentName != null && componentName.className != null) {
                return componentName.className
            }
        }
        return null
    }

    /**
     * 开启闹钟
     * @param context
     */
    fun openAlarm(context: Context, hour: Int, minute: Int, type: String?) {
        val intent = Intent()
        intent.putExtra(LTPConfigConsts.TIME_HOUR, hour)
        intent.putExtra(LTPConfigConsts.TIME_MINUTE, minute)
        intent.putExtra(LTPConfigConsts.ALARM_TYPE, type)
        intent.setComponent(
            ComponentName(
                "com.letianpai.robot.alarm",
                "com.letianpai.robot.alarm.MainActivity"
            )
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }


    fun is24HourFormat(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val timeFormat =
                Settings.System.getString(context.contentResolver, Settings.System.TIME_12_24)

            if (timeFormat == null) {
                Log.e("letianpai_time", "timeFormat is null")
            }
            return if (timeFormat != null && timeFormat == "24") {
                true
                //                android.provider.Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24, "12");
                //                DateFormat.is24HourFormat(context); // 更新时间格式
            } else {
                false
                //                android.provide
                //                r.Settings.System.putString(context.getContentResolver(), android.provider.Settings.System.TIME_12_24, "24");
                //                DateFormat.is24HourFormat(context); // 更新时间格式
            }
        } else {
            return true
        }
    }

    val isFactoryRom: Boolean
        /**
         *
         * @return
         */
        get() {
            val displayVersion = Build.DISPLAY
            Log.e("letianpai_test", "displayVersion: $displayVersion")
            return if (displayVersion.endsWith("f")) {
                true
            } else {
                false
            }
        }
}
