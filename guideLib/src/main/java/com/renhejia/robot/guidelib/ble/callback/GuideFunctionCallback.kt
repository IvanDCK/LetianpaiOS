package com.renhejia.robot.guidelib.ble.callback

/**
 * @author liujunbin
 */
class GuideFunctionCallback private constructor() {
    private object CloseGuideCallbackHolder {
        val instance: GuideFunctionCallback = GuideFunctionCallback()
    }

    private val guideFunctionListener: MutableList<GuideFunctionListener?> = ArrayList()
    private val robotShutDownListener: MutableList<RobotShutDownListener?> = ArrayList()

    interface GuideFunctionListener {
        fun onCloseGuideReceived()
    }

    interface RobotShutDownListener {
        fun onShutDown()
        fun onShutDownSteeringEngine()
    }

    fun registerGuideFunctionListener(listener: GuideFunctionListener?) {
        if (listener != null) {
            guideFunctionListener.add(listener)
        }
    }

    fun unregisterCloseGuidedListener(listener: GuideFunctionListener?) {
        if (listener != null) {
            guideFunctionListener.remove(listener)
        }
    }

    fun registerRobotShutDownListener(listener: RobotShutDownListener?) {
        if (listener != null) {
            robotShutDownListener.add(listener)
        }
    }

    fun unregisterRobotShutDownListener(listener: RobotShutDownListener?) {
        if (listener != null) {
            robotShutDownListener.remove(listener)
        }
    }

    fun closeGuide() {
        for (i in guideFunctionListener.indices) {
            if (guideFunctionListener[i] != null) {
                guideFunctionListener[i]!!.onCloseGuideReceived()
            }
        }
    }

    fun onShutDown() {
        for (i in robotShutDownListener.indices) {
            if (robotShutDownListener[i] != null) {
                robotShutDownListener[i]!!.onShutDown()
            }
        }
    }

    fun shutDownSteeringEngine() {
        for (i in robotShutDownListener.indices) {
            if (robotShutDownListener[i] != null) {
                robotShutDownListener[i]!!.onShutDownSteeringEngine()
            }
        }
    }


    companion object {
        @JvmStatic
        val instance: GuideFunctionCallback
            get() = CloseGuideCallbackHolder.instance
    }
}
