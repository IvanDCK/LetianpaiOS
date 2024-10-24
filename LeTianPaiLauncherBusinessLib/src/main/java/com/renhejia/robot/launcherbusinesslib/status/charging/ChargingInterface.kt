package com.renhejia.robot.launcherbusinesslib.status.charging

interface ChargingInterface {
    fun registerObserver(observer: ChargingObserver?)

    fun unregisterObserver(Observer: ChargingObserver?)

    fun notifyObservers()
}
