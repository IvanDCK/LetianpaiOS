package com.renhejia.robot.launcher.app

import android.content.Intent
import com.renhejia.robot.launcher.statusbar.service.RobotService
import com.renhejia.robot.launcherbaselib.init.LauncherInitManager
import com.renhejia.robot.launcherbusinesslib.app.LeTianPaiLauncherBusinessApp

/**
 * @author liu junbin
 */
class LeTianPaiLauncherApp : LeTianPaiLauncherBusinessApp() {
    private var serviceIntent: Intent? = null

    override fun onCreate() {
        super.onCreate()
        ltpApp = this
        serviceIntent = Intent(this, RobotService::class.java)
        startService(serviceIntent)
        LauncherInitManager.getInstance(this)
    }

    companion object {
        private var ltpApp: LeTianPaiLauncherApp? = null
    }
}
