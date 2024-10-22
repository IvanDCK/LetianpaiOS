package com.rhj.audio

import android.text.TextUtils
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * @author liujunbin
 */
object SystemUtil {
    private var sysPropGet: Method? = null
    private var sysPropGetInt: Method? = null
    private var sysPropSet: Method? = null
    private const val SN = "ro.serialno"

    init {
        try {
            val S = Class.forName("android.os.SystemProperties")
            val M = S.methods
            for (m in M) {
                val n = m.name
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

    val ltpSn: String?
        get() = get(SN, null)
    val ltpLastSn: String?
        get() {
            val sn = ltpSn
            return if (TextUtils.isEmpty(sn)) {
                null
            } else {
                sn!!.substring(sn.length - 4)
            }
        }
}
