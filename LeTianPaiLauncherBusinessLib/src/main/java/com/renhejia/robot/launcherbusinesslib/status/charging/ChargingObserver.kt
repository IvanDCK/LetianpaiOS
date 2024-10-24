package com.renhejia.robot.launcherbusinesslib.status.charging

interface ChargingObserver {
    fun updateChargingStatus(chargingStatus: Boolean, percent: Int)
}
