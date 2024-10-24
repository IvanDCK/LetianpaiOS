package com.renhejia.robot.launcher.displaymode.callback

import com.renhejia.robot.commandlib.parser.displaymodes.calendar.CalenderInfo


/**
 * Created by liujunbin
 */
class CalendarNoticeInfoUpdateCallback private constructor() {
    private var mRobotInfoListener: RobotInfoListener? = null

    private object RobotInfoUpdateCallbackHolder {
        val instance: CalendarNoticeInfoUpdateCallback = CalendarNoticeInfoUpdateCallback()
    }

    fun interface RobotInfoListener {
        fun updateNotice(calenderInfo: CalenderInfo)
    }

    fun setCalendarInfoListener(listener: RobotInfoListener?) {
        this.mRobotInfoListener = listener
    }


    fun updateNotice(calenderInfo: CalenderInfo) {
        if (mRobotInfoListener != null) {
            mRobotInfoListener!!.updateNotice(calenderInfo)
        }
    }


    companion object {
        @JvmStatic
        val instance: CalendarNoticeInfoUpdateCallback
            get() = RobotInfoUpdateCallbackHolder.instance
    }
}
