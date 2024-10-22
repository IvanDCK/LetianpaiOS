package com.renhejia.robot.display.manager

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class WatchSharedPreference(context: Context?, fileName: String?, action: String?) {
    private var mContext: Context? = null
    private var mFileName: String? = null

    /**
     * 使用的系统SharedPreferences对象
     */
    private var mEditor: SharedPreferences.Editor? = null
    private var mSharedPref: SharedPreferences? = null

    /**
     * sharedpreference对于的资源id,默认-1
     */
    private var mMode: Int = Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS

    /**
     * 内存数据的map
     */
    private var mMap: MutableMap<String, Any?>? = null

    /**
     * 表示内存数据是否发生过改变，避免不必要的写文件操作
     */
    private var mHasChanged: Boolean = false

    private var mHandler: Handler? = null

    init {
        mContext = context

        mMode = Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS

        this.mFileName = fileName
        reloadSharedPref(false)

        mHandler = MyHandler(this)
    }

    private class MyHandler(watchSharedPreference: WatchSharedPreference) : Handler() {
        private val weakReference: WeakReference<WatchSharedPreference> = WeakReference(watchSharedPreference)

        override fun handleMessage(msg: Message) {
            val watchSharedPreference = weakReference.get()
            if (watchSharedPreference != null) {
                when (msg.what) {
                    HANDLE_SETTING_CHANGED -> watchSharedPreference.sendSettingChangeBroadcast()
                }
            }
        }
    }


    /**
     * 文件操作，重新加载配置文件
     *
     * @param syncIPCFlag
     * true的时候会通知所有的进程全部重新加载，否则只加载调用进程
     */
    fun reloadSharedPref(syncIPCFlag: Boolean) {
        mSharedPref = mSharedPref ?: mContext?.getSharedPreferences(mFileName, mMode)
        mEditor = mSharedPref?.edit()
        mHasChanged = false

        reloadMap()

        if (syncIPCFlag) {
            //sendIPCSyncBroadcast();
            sendSettingChangeBroadcast()
        }
    }

    private fun sendSettingChangeBroadcast() {
        val intent: Intent = Intent(ACTION_INTENT_CONFIG_CHANGE)
        mContext!!.sendBroadcast(intent)
    }

    private fun sendMessageDelay(handleid: Int, delay: Long) {
        if (mHandler != null) {
            mHandler!!.removeMessages(handleid)
            mHandler!!.sendEmptyMessageDelayed(handleid, delay)
        }
    }


    fun reloadMap() {
        if (mMap != null) {
            mMap!!.clear()
        }
        mMap = mSharedPref!!.getAll() as MutableMap<String, Any?>?
    }

    val map: Map<String, Any?>?
        get() {
            return this.mMap
        }

    /**
     * 内存操作，释放对象占用的资源，取消广播，清空内存数据
     */
    fun terminate() {
        try {
            // mContext.unregisterReceiver(mConfigChangeReceiver);
            // if (mMap != null) {
            // mMap.clear();
            // mMap = null;
            // }
        } catch (e: Exception) {
        }
    }

    /**
     * 判断Map中是否包含指定key
     *
     *
     * @param key
     * @return boolean
     */
    fun contains(key: String): Boolean {
        return mMap!!.containsKey(key)
    }

    /**
     * 文件操作，提交数据到文件，此函数进行磁盘io写文件, 成功后会通知使用该文件数据的数据重新加载数据
     *
     * @return boolean true写文件成功；false写文件失败
     */
    fun commit(): Boolean {
        if (!mHasChanged) {
            return false
        }
        if (mEditor != null) {
            if (mEditor!!.commit()) {
                mHasChanged = false
                sendMessageDelay(HANDLE_SETTING_CHANGED, DELAY_SEND_BROADCAST)
                //sendSettingChangeBroadcast();
                return true
            }
        }
        return false
    }

    /**
     * 内存操作，移除包含特定key的数据
     *
     * @param key
     * void
     */
    fun remove(key: String) {
        mEditor = mEditor!!.remove(key)
        mMap!!.remove(key)
        mHasChanged = true
    }

    /**
     * 内存操作，清空数据
     *
     * @return boolean true成功；false失败
     */
    fun clear(): Boolean {
        if (mEditor != null) {
            mEditor!!.clear()
            mMap!!.clear()
            mHasChanged = true
            return true
        }
        return false
    }

    /**
     * 私有公用方法，添加数据，value是object
     *
     * @param key
     * @param defValue
     * @return boolean true成功，false失败
     */
    private fun setValue(key: String, defValue: Any): Boolean {
        val preValue: Any? = mMap!!.put(key, defValue)
        if (preValue == null || preValue != defValue) {
            mHasChanged = true
            return true
        }
        return false
    }

    /**
     * 内存操作，添加数据，value是boolean
     *
     * @param key
     * @param defValue
     */
    fun putBoolean(key: String, defValue: Boolean) {
        if (setValue(key, defValue)) {
            mEditor = mEditor!!.putBoolean(key, defValue)
        }
    }

    /**
     * 内存操作，添加数据，value是int
     *
     * @param key
     * @param defValue
     * void
     */
    fun putInt(key: String, defValue: Int) {
        if (setValue(key, defValue)) {
            mEditor = mEditor!!.putInt(key, defValue)
        }
    }

    /**
     * 内存操作，添加数据，value是long
     *
     * @param key
     * @param defValue
     * void
     */
    fun putLong(key: String, defValue: Long) {
        if (setValue(key, defValue)) {
            mEditor = mEditor!!.putLong(key, defValue)
        }
    }

    /**
     * 内存操作，添加数据，value是float
     *
     * @param key
     * @param defValue
     * void
     */
    fun putFloat(key: String, defValue: Float) {
        if (setValue(key, defValue)) {
            mEditor = mEditor!!.putFloat(key, defValue)
        }
    }

    /**
     * 内存操作，添加数据，value是String
     *
     * @param key
     * @param defValue
     * void
     */
    fun putString(key: String, defValue: String) {
        if (setValue(key, defValue)) {
            mEditor = mEditor!!.putString(key, defValue)
        }
    }

    /**
     * 内存操作，获取boolean类型的数据
     *
     * @param key
     * @param defValue
     * 默认值
     * @return boolean
     */
    fun getBoolean(key: String, defValue: Boolean): Boolean {
        val v: Boolean? = mMap!!.get(key) as Boolean?
        return if (v != null) v else defValue
    }

    /**
     * 内存操作，获取float类型的数据
     *
     * @param key
     * @param defValue
     * 默认值
     * @return float
     */
    fun getFloat(key: String, defValue: Float): Float {
        val v: Float? = mMap!!.get(key) as Float?
        return if (v != null) v else defValue
    }

    /**
     * 内存操作，获取int类型的数据
     *
     * @param key
     * @param defValue
     * 默认值
     * @return int
     */
    fun getInt(key: String, defValue: Int): Int {
        val v: Int? = mMap!!.get(key) as Int?
        return if (v != null) v else defValue
    }

    /**
     * 内存操作，获取long类型的数据
     *
     * @param key
     * @param defValue
     * 默认值
     * @return long
     */
    fun getLong(key: String, defValue: Long): Long {
        val v: Long? = mMap!!.get(key) as Long?
        return if (v != null) v else defValue
    }

    /**
     * 内存操作，获取string类型的数据
     *
     * @param key
     * @param defValue
     * 默认值
     * @return String
     */
    fun getString(key: String, defValue: String?): String {
        val v: String? = mMap!!.get(key) as String?
        return if (v != null) v else defValue!!
    }

    companion object {
        const val UPDATE_TYPE_NONE: Int = 0

        /**
         * 广播相关
         */
        const val ACTION_INTENT_CONFIG_CHANGE: String = "com.letianpai.robot.SETTING_CHANGE"

        //	private static final String BROADCAST_PID = "broadcastPid";
        //	private static final String UPDATE_TYPE = "updateType";
        const val SHARE_PREFERENCE_NAME: String = "WatchConfig"

        private const val HANDLE_SETTING_CHANGED: Int = 10
        private const val DELAY_SEND_BROADCAST: Long = 200
    }
}
