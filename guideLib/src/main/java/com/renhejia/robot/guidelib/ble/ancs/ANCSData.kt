package com.renhejia.robot.guidelib.ble.ancs

import android.util.Log

class ANCSData internal constructor(// 8 bytes
    val notifyData: ByteArray
) {
    var timeExpired: Long
    var curStep: Int = 0

    var notification: IOSNotification

    init {
        curStep = 0
        timeExpired = System.currentTimeMillis()
        notification = IOSNotification()
        initNotification()
    }


    val uID: Int
        get() = (0xff and (notifyData[7].toInt() shl 24)) or (0xff and (notifyData[6]
            .toInt() shl 16)) or (0xff and (notifyData[5]
            .toInt() shl 8)) or (0xff and notifyData[4].toInt())

    private fun initNotification() {
        logD(notifyData)
        if (notifyData.size < 5) {
            return
        }
        // check if finished ?
        val cmdId = notifyData[0].toInt() // should be 0 //0 commandID
        // if (cmdId != 0) {
        //     Log.i(TAG, "bad cmdId: " + cmdId);
        //     return;
        // }
        val uid =
            ((0xff and notifyData[4].toInt()) shl 24) or ((0xff and notifyData[3].toInt()) shl 16) or ((0xff and notifyData[2].toInt()) shl 8) or ((0xff and notifyData[1].toInt()))

        // if (uid != mCurrentANCSData.getUID()) {
        //
        //     Log.i(TAG, "bad uid: " + uid + "->" + mCurrentANCSData.getUID());
        //     return;
        // }

        // read attributes
        notification.uid = uid
        var curIdx = 5 // hard code
        while (!notification.isAllInit) {
            if (notifyData.size < curIdx + 3) {
                return
            }
            // attributes head
            val attrId = notifyData[curIdx].toInt()
            val attrLen =
                ((notifyData[curIdx + 1]).toInt() and 0xFF) or (0xFF and (notifyData[curIdx + 2].toInt() shl 8))
            curIdx += 3
            if (notifyData.size < curIdx + attrLen) {
                return
            }
            val `val` = String(notifyData, curIdx, attrLen) // utf-8 encode
            if (attrId == GlobalDefine.NotificationAttributeIDTitle) {
                notification.title = `val`
            } else if (attrId == GlobalDefine.NotificationAttributeIDMessage) {
                notification.message = `val`
            } else if (attrId == GlobalDefine.NotificationAttributeIDDate) {
                notification.date = `val`
            } else if (attrId == GlobalDefine.NotificationAttributeIDSubtitle) {
                notification.subtitle = `val`
            } else if (attrId == GlobalDefine.NotificationAttributeIDMessageSize) {
                notification.messageSize = `val`
            } else if (attrId == GlobalDefine.NotificationAttributeIDBundleId) {
                notification.bundleId = `val`
            }
            curIdx += attrLen
        }
    }

    fun logD(d: ByteArray) {
        val sb = StringBuffer()
        val len = d.size
        for (i in 0 until len) {
            sb.append(d[i].toString() + ", ")
        }
        Log.i(TAG, "log Data size[$len] : $sb")
    }

    companion object {
        protected const val TAG: String = "ANCSData"
    }
}