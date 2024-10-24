package com.renhejia.robot.launcher.net

import android.content.Context
import com.google.gson.Gson

class LTPNetworkManager private constructor(context: Context) {
    private var mContext: Context? = null
    private var gson: Gson? = null

    init {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
    }

    companion object {
        fun getInstance(context: Context): LTPNetworkManager {
            return  LTPNetworkManager(context.applicationContext)
        }
    }
}
