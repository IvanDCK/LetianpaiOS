package com.renhejia.robot.launcherbaselib.timer

/**
 * Created by liujunbin
 */
class TimerKeeperCallback private constructor() {
    private val mTimerKeeperUpdateListener: MutableList<TimerKeeperUpdateListener?> =
        ArrayList()

    private object TimerKeeperUpdateCallBackHolder {
        val instance: TimerKeeperCallback = TimerKeeperCallback()
    }

    interface TimerKeeperUpdateListener {
        fun onTimerKeeperUpdateReceived(hour: Int, minute: Int)
    }

    fun registerTimerKeeperUpdateListener(listener: TimerKeeperUpdateListener?) {
        if (mTimerKeeperUpdateListener != null) {
            mTimerKeeperUpdateListener.add(listener)
        }
    }

    fun unregisterTimerKeeperUpdateListener(listener: TimerKeeperUpdateListener?) {
        if (mTimerKeeperUpdateListener != null) {
            mTimerKeeperUpdateListener.remove(listener)
        }
    }


    fun setTimerKeeper(hour: Int, minute: Int) {
        for (i in mTimerKeeperUpdateListener.indices) {
            if (mTimerKeeperUpdateListener.get(i) != null) {
                mTimerKeeperUpdateListener.get(i)!!.onTimerKeeperUpdateReceived(hour, minute)
            }
        }
    }

    companion object {
        @JvmStatic
        val instance: TimerKeeperCallback
            get() {
                return TimerKeeperUpdateCallBackHolder.instance
            }
    }
}
