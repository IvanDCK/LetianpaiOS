package com.renhejia.robot.launcher.encryption

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.renhejia.robot.guidelib.utils.SystemUtil.wlanMacAddress
import java.util.Locale

/**
 * @author liujunbin
 */
class EncryptionUtils private constructor(private val mContext: Context) {
    private var mGson: Gson? = null

    init {
        init()
    }

    private fun init() {
        mGson = Gson()
    }

    private val hardcode: Unit
        get() {
        }

    companion object {
        private const val partSecretKey = "your partSecretKey"

        fun getInstance(context: Context): EncryptionUtils {
            return EncryptionUtils(context.applicationContext)
        }

        /**
         * 获取签名的方式
         *
         * @param inputValue
         * @param ts
         * @return
         */
        private fun getDeviceSign(inputValue: String, ts: String): String {
            val deviceSecretKey = MD5.encode(inputValue + ts + partSecretKey)
            Log.e("letianpai_encode", "deviceSecretKey: $deviceSecretKey")
            val macSign = Sha256Utils.getSha256Str(inputValue + ts + deviceSecretKey)
            Log.e("letianpai_encode", "macSign: $macSign")
            return macSign
        }

        val robotSign: String
            get() {
                val mac = robotMac
                Log.e("letianpai_encode", "mac: $mac")
                val ts = ts
                Log.e("letianpai_encode", "ts: $ts")
                var robotSign = getDeviceSign(mac, ts)
                Log.e("letianpai_encode", "robotSign: $robotSign")
                robotSign = "Bearer $robotSign"
                Log.e("letianpai_encode", "robotSign1: $robotSign")
                return robotSign
            }

        @JvmStatic
        fun getHardCodeSign(ts: String): String {
            val mac = robotMac
            Log.e("letianpai_encode", "mac: $mac")
            Log.e("letianpai_encode", "ts: $ts")
            var robotSign = getDeviceSign(mac, ts)
            Log.e("letianpai_encode", "robotSign: $robotSign")
            robotSign = "Bearer $robotSign"
            Log.e("letianpai_encode", "robotSign1: $robotSign")
            return robotSign
        }

        /**
         * 获取机器人签名
         *
         * @param sn
         * @param hardcode
         * @param ts
         * @return
         */
        @JvmStatic
        fun getRobotSign(sn: String, hardcode: String, ts: String): String {
            Log.e("letianpai_encode", "ts: $ts")
            var robotSign = getDeviceSign(sn + hardcode, ts)
            Log.e("letianpai_encode", "getRobotSign:======= $robotSign")
            robotSign = "Bearer $robotSign"
            Log.e("letianpai_encode", "getRobotSign1: ========== $robotSign")
            return robotSign
        }

        fun getDeviceSign(sn: String, hardcode: String, ts: String): String {
            Log.e("letianpai_encode", "ts: $ts")
            val robotSign = getDeviceSign(sn + hardcode, ts)
            Log.e("letianpai_encode", "robotSign: $robotSign")
            return robotSign
        }

        @JvmStatic
        val robotMac: String
            get() = (wlanMacAddress)!!.lowercase(Locale.getDefault())

        @JvmStatic
        val ts: String
            get() = (System.currentTimeMillis() / 1000).toString() + ""
    }
}
