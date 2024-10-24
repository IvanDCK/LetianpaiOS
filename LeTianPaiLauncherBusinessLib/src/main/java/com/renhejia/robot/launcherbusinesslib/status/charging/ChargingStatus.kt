package com.renhejia.robot.launcherbusinesslib.status.charging

import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback
import com.renhejia.robot.launcherbaselib.battery.ChargingUpdateCallback.ChargingUpdateListener

/**
 * 充电状态
 *
 * @author liujunbin
 */
class ChargingStatus : ChargingInterface {
    private val observerList: ArrayList<ChargingObserver?> = ArrayList()
    var chargingStatus: Boolean = false
        private set
    var batteryPercent: Int = 0
        private set


    init {
        addListener()
    }

    private fun addListener() {
        ChargingUpdateCallback.instance.registerChargingStatusUpdateListener(object :
            ChargingUpdateListener {
            override fun onChargingUpdateReceived(changingStatus: Boolean, percent: Int) {
            }

            override fun onChargingUpdateReceived(
                changingStatus: Boolean,
                percent: Int,
                chargePlug: Int
            ) {
                setChargingStatus(changingStatus, percent, chargePlug)
            }
        })
    }

    override fun registerObserver(observer: ChargingObserver?) {
        observerList.add(observer)
    }

    override fun unregisterObserver(observer: ChargingObserver?) {
        if (observerList.size > 0) {
            observerList.remove(observer)
        }
    }

    override fun notifyObservers() {
        for (i in observerList.indices) {
            observerList[i]!!.updateChargingStatus(chargingStatus, batteryPercent)
        }
    }


    fun chargingStatusChanged() {
        notifyObservers()
    }


    fun setChargingStatus(chargingStatus: Boolean, percent: Int, chargePlug: Int) {
        if ((chargingStatus != this.chargingStatus) || (percent != batteryPercent)) {
            this.chargingStatus = chargingStatus
            this.batteryPercent = percent
            chargingStatusChanged()
        }
        // TODO to be removed 后续考虑是否要移除
    }

    companion object {
        var instance: ChargingStatus? = null
            get() {
                if (field == null) {
                    field = ChargingStatus()
                }
                return field
            }
            private set
    }
}
