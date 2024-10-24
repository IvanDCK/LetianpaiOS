package com.renhejia.robot.guidelib.ble

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.renhejia.robot.guidelib.ble.util.PermissionRequestListener

class BleConnectionManager(private val context: Context) : ServiceConnection {

    private var permissionRequestListener: PermissionRequestListener? = null
    private var bleService: BleService? = null

    fun bindService() {
        val intent = Intent(context, BleService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        context.unbindService(this)
    }

    fun setPermissionRequestListener(listener: PermissionRequestListener) {
        permissionRequestListener = listener
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as BleService.BleServiceBinder
        bleService = binder.getService()
        permissionRequestListener?.let { bleService?.setPermissionRequestListener(it) }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        bleService = null
    }
}