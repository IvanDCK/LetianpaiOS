package com.rhj.audio.utils

import android.util.Log
//import com.rhj.audio.BuildConfig;
import com.rhj.audio.BuildConfig



object LogUtils {
    @JvmStatic
    fun logi(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message?: "")
        }
    }

    fun logw(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message?: "")
        }
    }

    @JvmStatic
    fun logd(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message?: "")
        }
    }

    fun loge(tag: String?, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message?: "")
        }
    }

    private const val showLength = 1000

    fun showLargeLog(tag: String?, logContent: String) {
        if (logContent.length > showLength) {
            val show = logContent.substring(0, showLength)
            logd(tag, show)
            /*剩余的字符串如果大于规定显示的长度，截取剩余字符串进行递归，否则打印结果*/
            if ((logContent.length - showLength) > showLength) {
                val partLog = logContent.substring(showLength, logContent.length)
                showLargeLog(tag, partLog)
            } else {
                val printLog = logContent.substring(showLength, logContent.length)
                logd(tag, printLog)
            }
        } else {
            logd(tag, logContent)
        }
    }
}
