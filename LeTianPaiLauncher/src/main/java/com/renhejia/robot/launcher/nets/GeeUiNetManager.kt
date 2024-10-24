package com.renhejia.robot.launcher.nets

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.renhejia.robot.guidelib.utils.SystemUtil.hardCode
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.isNetworkAvailable
import com.renhejia.robot.launcher.encryption.EncryptionUtils.Companion.getHardCodeSign
import com.renhejia.robot.launcher.encryption.EncryptionUtils.Companion.getRobotSign
import com.renhejia.robot.launcher.encryption.EncryptionUtils.Companion.ts
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder

/**
 *
 */
object GeeUiNetManager {
    /**
     * 获取通用信息列表
     *
     * @param context
     * @param callback
     */
    fun getDeviceBindInfo(context: Context, isChinese: Boolean, callback: Callback) {
        get(context, isChinese, GeeUINetworkConsts.BIND_INFO, callback)
    }

    /**
     * 获取日历列表
     *
     * @param context
     * @param callback
     */
    fun getCalendarList(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.CALENDAR_LIST, callback)
    }

    /**
     * 获取倒计时列表
     *
     * @param context
     * @param callback
     */
    fun getCountDownList(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.COUNTDOWN_LIST, callback)
    }

    /**
     * 获取粉丝信息
     *
     * @param context
     * @param callback
     */
    fun getFansInfoList(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.FANS_INFO_LIST, callback)
    }

    /**
     * 获取通用信息列表
     *
     * @param context
     * @param callback
     */
    fun getGeneralInfoList(context: Context, isChinese: Boolean, callback: Callback) {
        get(context, isChinese, GeeUINetworkConsts.GENERAL_INFO, callback)
    }

    /**
     * 获取天气信息
     *
     * @param context
     * @param callback
     */
    fun getWeatherInfo(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.WEATHER_INFO, callback)
    }

    /**
     * 获取闹钟列表
     *
     * @param context
     * @param callback
     */
    fun getClockList(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.CLOCK_LIST, callback)
    }

    /**
     * 获取唤醒词列表
     *
     * @param context
     * @param callback
     */
    fun getTipsList(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.GET_COMMON_CONFIG, callback)
    }

    /**
     * 获取机器人全部配置
     *
     * @param context
     * @param callback
     */
    fun getAllConfig(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get1(context, GeeUINetworkConsts.GET_ALL_CONFIG, callback)
    }

    /**
     * 获取股票信息
     *
     * @param context
     * @param callback
     */
    fun getDeviceInfo(context: Context?, callback: Callback) {
        val ts = ts
        val auth = getHardCodeSign(ts)
        GeeUINetworkUtil.get11(
            context,
            auth,
            ts,
            GeeUINetworkConsts.GET_SN_BY_MAC,
            callback
        )
    }

    /**
     * 获取设置渠道的LOGO
     *
     * @param context
     * @param callback
     */
    fun getDeviceChannelLogo(context: Context, isChinese: Boolean, callback: Callback) {
        get(context, isChinese, GeeUINetworkConsts.GET_DEVICE_CHANNELLOGO, callback)
    }

    // /**
    // * 更新机器人状态
    // * @param context
    // * @param callback
    // */
    // public static void uploadStatus(Context context, Callback callback) {
    // GeeUINetworkUtil.uploadStatus(context,callback);
    // }
    /**
     * @param context
     * @param uri
     * @param callback
     */
    fun get(context: Context, isChinese: Boolean, uri: String, callback: Callback) {
        if (!isNetworkAvailable(context)) {
            Log.e("apprequest ", "网络未连接")
            return
        }
        val ts = ts
        var sn: String? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            sn = Build.getSerial()
        }

        val hardCode = hardCode
        val auth = getRobotSign(sn!!, hardCode!!, ts)

        get(context, isChinese, auth, sn, ts, uri, callback)
    }

    private const val AUTHORIZATION = "Authorization"
    private const val COUNTRY = "country"
    private const val CN = "cn"
    private const val GLOBAL = "global"

    fun get(
        context: Context?, isChinese: Boolean, auth: String, sn: String?, ts: String?, uri: String,
        callback: Callback
    ) {
        val okHttpClient = OkHttpClient()

        val httpBuilder: HttpUrl.Builder = if (isChinese) {
            "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder()
        } else {
            "https://yourservice.com$uri".toHttpUrlOrNull()?.newBuilder() ?: HttpUrl.Builder()
        }

        httpBuilder.addQueryParameter("sn", sn)
        httpBuilder.addQueryParameter("ts", ts)
        // httpBuilder.addQueryParameter("sn", "2001013760426");
        

        Log.e("apprequest", "" + httpBuilder.toString())
        var country = CN
        if (!isChinese) {
            country = GLOBAL
        }
        val request: Request = Builder()
            .url(httpBuilder.build())
            .addHeader(AUTHORIZATION, auth)
            .addHeader(COUNTRY, country)
            .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(callback)
    }

    /**
     * @param context
     * @param callback
     */
    fun getTimeStamp(context: Context?, callback: Callback) {
        GeeUINetworkUtil.get(context, GeeUINetworkConsts.GET_SERVER_TIME_STAMP, callback)
    }
}
