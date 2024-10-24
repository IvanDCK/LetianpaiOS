package com.renhejia.robot.launcher.main.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import com.renhejia.robot.guidelib.ble.BleService
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback.GuideFunctionListener
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager
import com.renhejia.robot.guidelib.utils.SystemUtil.setAppLanguage
import com.renhejia.robot.launcher.system.SystemFunctionUtil
import com.renhejia.robot.launcherbusinesslib.ui.activity.LauncherBaseActivity

class LauncherActivity : LauncherBaseActivity() {
    private var closeGuidedListener: GuideFunctionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getOTAStatus();
        setAppLanguage(this@LauncherActivity)

        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        startService(Intent(this, BleService::class.java))
        if (LTPGuideConfigManager.getInstance(this@LauncherActivity)!!.isActivated) {
            skipToMainView()
        } else {
//            setContentView(R.layout.activity_launcher_guide);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            inits()
        }
    }

    override fun onResume() {
        super.onResume()
        setTimeFormat()
    }

    private fun setTimeFormat() {
        val timeFormat = Settings.System.getString(contentResolver, Settings.System.TIME_12_24)
        if (timeFormat == null) {
            SystemFunctionUtil.set24HourFormat(this@LauncherActivity)
        }
    }


    private fun skipToMainView() {
        val intent = Intent(this@LauncherActivity, LeTianPaiMainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun inits() {
        SystemFunctionUtil.wakeUp(this@LauncherActivity)
        initListeners()
        registerCloseGuidedListener()
        //
        openWifiConnectView()
    }

    private fun openWifiConnectView() {
        val packageName = "com.letianpai.robot.wificonnet"
        val activityName = "com.letianpai.robot.wificonnet.MainActivity"
        val intent = Intent()
        intent.setComponent(ComponentName(packageName, activityName))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }


    private fun initListeners() {
        closeGuidedListener = object : GuideFunctionListener {
            override fun onCloseGuideReceived() {
                skipToMainView()
            }
        }
    }

    private fun registerCloseGuidedListener() {
//        GuideFunctionCallback.getInstance().registerGuideFunctionListener(closeGuidedListener);
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterCloseGuidedListener()
    }

    private fun unregisterCloseGuidedListener() {
//        if (closeGuidedListener != null) {
//            GuideFunctionCallback.getInstance().unregisterCloseGuidedListener(closeGuidedListener);
//        }
    }

    companion object {
        private const val OPEN_FROM = "from"
        private const val OPEN_FROM_START = "from_open_robot"
    }
}
