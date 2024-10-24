package com.renhejia.robot.guidelib.ble.ancs


/**
 * a notice from iPhone ANCS<br></br>
 */
class IOSNotification {
    /**
     * the unique identifier (UID) for the iOS notification
     */
    var uid: Int = 0

    /**
     * title for the iOS notification
     */
    var title: String? = null

    /**
     * subtitle for the iOS notification
     */
    var subtitle: String? = null

    /**
     * message(content) for the iOS notification
     */
    var message: String? = null

    /**
     * size (how many byte) of message
     */
    var messageSize: String? = null

    /**
     * the time for the iOS notification
     */
    var date: String? = null
    var bundleId: String? = null

    constructor()
    constructor(t: String?, s: String?, m: String?, ms: String?, d: String?, b: String?) {
        title = t
        subtitle = s
        message = m
        messageSize = ms
        date = d
        bundleId = b
    }

    val isAllInit: Boolean
        get() = title != null && subtitle != null && message != null && messageSize != null && date != null && bundleId != null
}
