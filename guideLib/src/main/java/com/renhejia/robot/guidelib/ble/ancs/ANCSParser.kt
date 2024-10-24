package com.renhejia.robot.guidelib.ble.ancs

import android.util.Log

class ANCSParser {
    private var mCurrentANCSData: ANCSData? = null
    fun getmCurrentANCSData(): ANCSData? {
        return mCurrentANCSData
    }

    fun setmCurrentANCSData(mCurrentANCSData: ANCSData?) {
        this.mCurrentANCSData = mCurrentANCSData
    }

    fun onDSNotification(data: ByteArray) {
        mCurrentANCSData = ANCSData(data)
        Log.i(TAG, "noti.title:" + mCurrentANCSData!!.notification.title)
        Log.i(TAG, "noti.message:" + mCurrentANCSData!!.notification.message)
        Log.i(TAG, "noti.date:" + mCurrentANCSData!!.notification.date)
        Log.i(TAG, "noti.subtitle:" + mCurrentANCSData!!.notification.subtitle)
        Log.i(TAG, "noti.messageSize:" + mCurrentANCSData!!.notification.messageSize)
        Log.i(TAG, "noti.bundleid:" + mCurrentANCSData!!.notification.bundleId)
        Log.i(TAG, "got a notification! data size = " + mCurrentANCSData!!.notifyData.size)
    }

    companion object {
        protected const val TAG: String = "ANCSParser"
    }
}
