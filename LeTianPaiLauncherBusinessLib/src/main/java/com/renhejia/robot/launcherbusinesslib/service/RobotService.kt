package com.renhejia.robot.launcherbusinesslib.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

abstract class RobotService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
