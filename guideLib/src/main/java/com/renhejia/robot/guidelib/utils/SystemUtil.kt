package com.renhejia.robot.guidelib.utils

import android.content.Context
import android.content.res.Configuration
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.NetworkInterface
import java.net.SocketException
import java.util.Enumeration
import java.util.Locale

/**
 * @author liujunbin
 */
object SystemUtil {
    private var sysPropGet: Method? = null
    private var sysPropGetInt: Method? = null
    private var sysPropSet: Method? = null
    private const val SN: String = "ro.serialno"
    private const val MCU_VERSION: String = "persist.sys.mcu.version"
    const val REGION_LANGUAGE: String = "persist.sys.region.language"

    //    private static final String ROBOT_STATUS = "ro.robot.status";
    private const val ROBOT_STATUS: String = "persist.sys.robot.status"
    private const val ROBOT_STATUS_TRUE: String = "true"
    private const val ROBOT_STATUS_FALSE: String = "false"
    const val HARD_CODE: String = "persist.sys.hardcode"
    const val DEVICE_SIGN: String = "persist.sys.device.sign"

    init {
        try {
            val S: Class<*> = Class.forName("android.os.SystemProperties")
            val M: Array<Method> = S.methods
            for (m: Method in M) {
                val n: String = m.name
                if (n == "get") {
                    sysPropGet = m
                } else if (n == "getInt") {
                    sysPropGetInt = m
                } else if (n == "set") {
                    sysPropSet = m
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun get(name: String?, default_value: String?): String? {
        try {
            return sysPropGet!!.invoke(null, name, default_value) as String
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return default_value
    }

    fun set(name: String?, value: String?) {
        try {
            sysPropSet!!.invoke(null, name, value)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun setRobotActivated() {
        set(ROBOT_STATUS, ROBOT_STATUS_TRUE)
    }

    @JvmStatic
    val robotActivateStatus: Boolean
        get() {
            val status: String? = get(
                ROBOT_STATUS,
                null
            )
            if (status == ROBOT_STATUS_TRUE) {
                return true
            }
            return false
        }
    @JvmStatic
    val isRobotInChinese: Boolean
        get() {
            val pro: String? = get(
                REGION_LANGUAGE,
                "zh"
            )
            if ("zh" == pro) {
                return true
            } else {
                return false
            }
        }
    val isRobotInChineseStatus: Boolean
        get() {
            val pro: String? =
                getRobotInChineseStatus()
            if (TextUtils.isEmpty(pro)) {
                if (isChinese) {
                    return true
                } else {
                    return false
                }
            } else {
                return isRobotInChinese
            }
        }

    private val PRC_LAUNGUAGE: String = "zh"
    @JvmStatic
    val isChinese: Boolean
        get() {
            val language: String = Locale.getDefault().language
            if (language == PRC_LAUNGUAGE) {
                return true
            } else {
                return false
            }
        }

    fun getRobotInChineseStatus(): String? {
        val pro: String? = get(REGION_LANGUAGE, "zh")
        return pro
    }

    @JvmStatic
    val ltpSn: String?
        get() {
//        return get(SN,null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Build.getSerial()
            } else {
                return get(
                    SN,
                    null
                )
            }
        }

    val ltpLastSn: String?
        get() {
            val sn: String? = ltpSn
            if (TextUtils.isEmpty(sn)) {
                return null
            } else {
                return sn!!.substring(sn.length - 4)
            }
        }

    @JvmStatic
    var hardCode: String?
        get() = get(
            HARD_CODE,
            null
        )
        set(hardCode) {
//        set(HARD_CODE,hardCode);
            set(
                HARD_CODE,
                hardCode
            )
        }

    @JvmStatic
    fun hasHardCode(): Boolean {
        if (TextUtils.isEmpty(hardCode)) {
            return false
        }
        return true
    }

    var deviceSign: String?
        get() {
            return get(
                DEVICE_SIGN,
                null
            )
        }
        set(deviceSign) {
            set(
                DEVICE_SIGN,
                deviceSign
            )
        }

    fun hadDeviceSign(): Boolean {
        if (TextUtils.isEmpty(deviceSign)) {
            return false
        }
        return true
    }

    @JvmStatic
    fun setRobotInactive() {
        set(ROBOT_STATUS, ROBOT_STATUS_FALSE)
    }

    @JvmStatic
    val wlanMacAddress: String?
        get() {
            try {
                val networkInterfaces: Enumeration<NetworkInterface> =
                    NetworkInterface.getNetworkInterfaces()
                while (networkInterfaces.hasMoreElements()) {
                    val networkInterface: NetworkInterface =
                        networkInterfaces.nextElement()
                    if (networkInterface.name == "wlan0") {
                        val mac: StringBuilder = StringBuilder()
                        val hardwareAddress: ByteArray =
                            networkInterface.hardwareAddress
                        var hex: String =
                            Integer.toHexString(hardwareAddress.get(0).toInt() and 0xff)
                        if (hex.length == 1) {
                            mac.append('0')
                        }
                        mac.append(hex)
                        for (i in 1 until hardwareAddress.size) {
                            mac.append(':')
                            hex = Integer.toHexString(
                                hardwareAddress.get(i).toInt() and 0xff
                            )
                            if (hex.length == 1) {
                                mac.append('0')
                            }
                            mac.append(hex)
                        }
                        return mac.toString()
                    }
                }
            } catch (ex: SocketException) {
            }
            return null
        }

    val mcu: String?
        get() {
            return get(
                MCU_VERSION,
                null
            )
        }

    fun getIp(context: Context): String? {
        val wifiManager: WifiManager? =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifiManager != null) {
            val wifiInfo: WifiInfo = wifiManager.connectionInfo
            val ip: Int = wifiInfo.ipAddress

            val ipAddress: String = String.format(
                "%d.%d.%d.%d",
                (ip and 0xff),
                (ip shr 8 and 0xff),
                (ip shr 16 and 0xff),
                (ip shr 24 and 0xff)
            )
            return ipAddress
        }

        return null
    }

    const val REGION_LANGUAGE_ZH: String = "zh"
    const val REGION_LANGUAGE_EN: String = "en"
    val language: String?
        get() {
            return get(
                REGION_LANGUAGE,
                REGION_LANGUAGE_ZH
            )
        }

    val isChineseLanguage: Boolean
        get() {
            if (language == REGION_LANGUAGE_ZH) {
                return true
            } else {
                return false
            }
        }


    /**
     * @param context
     * @param language
     */
    fun setDefaultLanguage(context: Context, language: String) {
        if (TextUtils.isEmpty(language)) {
            return
        }

        val locale: Locale = Locale(language)
        Locale.setDefault(locale)

        val configuration: Configuration = context.resources.configuration
        val metrics: DisplayMetrics = context.resources.displayMetrics

        var loc: Locale = Locale.CHINA
        if (language == REGION_LANGUAGE_EN) {
            loc = Locale.ENGLISH
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(loc)
        } else {
            configuration.locale = loc
        }

        context.resources.updateConfiguration(configuration, metrics)
    }

    /**
     * @param context
     */
    @JvmStatic
    fun setAppLanguage(context: Context) {
        if (isInChinese) {
            setDefaultLanguage(context, "zh")
        } else {
            setDefaultLanguage(context, REGION_LANGUAGE_EN)
        }
    }


    val isInChinese: Boolean
        get() {
            val pro: String? = get(
                REGION_LANGUAGE,
                "zh"
            )
            if ("zh" == pro) {
                return true
            } else {
                return false
            }
            //        return false;
        }
}



