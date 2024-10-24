package com.renhejia.robot.launcher.main.activity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.renhejia.robot.guidelib.ble.BleConnectionManager
import com.renhejia.robot.guidelib.ble.BleService
import com.renhejia.robot.guidelib.ble.callback.GuideFunctionCallback.GuideFunctionListener
import com.renhejia.robot.guidelib.ble.util.PermissionRequestListener
import com.renhejia.robot.guidelib.manager.LTPGuideConfigManager
import com.renhejia.robot.guidelib.utils.SystemUtil.setAppLanguage
import com.renhejia.robot.launcher.system.SystemFunctionUtil
import com.renhejia.robot.launcherbusinesslib.ui.activity.LauncherBaseActivity

class LauncherActivity : LauncherBaseActivity(), PermissionRequestListener {
    private var closeGuidedListener: GuideFunctionListener? = null

    private val REQUEST_CODE_BLUETOOTH = 111
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private lateinit var bleConnectionManager: BleConnectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // getOTAStatus();
        setAppLanguage(this@LauncherActivity)

        val decorView = window.decorView
        val uiOptions =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions


        // Check if the app has the necessary permissions to use Bluetooth
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissions.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }) {
                bleConnectionManager = BleConnectionManager(this)
                bleConnectionManager.setPermissionRequestListener(this)
                bleConnectionManager.bindService()

                val intent = Intent(this, BleService::class.java)
                startService(intent)

            } else {
                requestPermissions(permissions, REQUEST_CODE_BLUETOOTH)
            }

        } else {
        }
        if (LTPGuideConfigManager.getInstance(this@LauncherActivity)!!.isActivated) {
            skipToMainView()
        } else {
//            setContentView(R.layout.activity_launcher_guide);
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            inits()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startService(Intent(this, BleService::class.java))
            } else {
                // Handle case where permissions were not granted
                Log.e("LauncherActivity", "Permissions not granted")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        if (Settings.System.canWrite(this)) {
            setTimeFormat()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

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
        //openWifiConnectView()
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
        bleConnectionManager.unbindService()
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

    override fun requestBluetoothPermissions(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

}
