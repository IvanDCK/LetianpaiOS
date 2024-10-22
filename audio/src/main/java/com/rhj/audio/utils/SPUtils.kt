package com.rhj.audio.utils

import android.content.Context
import android.content.SharedPreferences

class SPUtils private constructor(context: Context) {
    private var sharedPreferences: SharedPreferences? = null
    val editor: SharedPreferences.Editor
        get() = sharedPreferences!!.edit()

    //sp存数据
    fun putString(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    //sp取数据
    fun getString(key: String?): String? {
        return sharedPreferences!!.getString(key, "")
        //"暂无保存的数据"为该key为空时的默认返回值
    }

    fun getInt(key: String?): Int {
        return sharedPreferences!!.getInt(key, 0)
    }

    fun getLong(key: String?): Long {
        return sharedPreferences!!.getLong(key, 0)
    }

    fun getBoolean(key: String?): Boolean {
        return sharedPreferences!!.getBoolean(key, false)
    }

    init {
        sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val SP_NAME = "audiosetting"
        private var spUtils: SPUtils? = null

        @JvmStatic
        fun getInstance(context: Context): SPUtils? {
            if (spUtils == null) {
                return newInstance(context)
            }
            return spUtils
        }

        @Synchronized
        private fun newInstance(context: Context): SPUtils {
            if (spUtils == null) {
                spUtils = SPUtils(context)
            }
            return spUtils!!
        }
    }
}

