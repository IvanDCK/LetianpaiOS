package com.renhejia.robot.guidelib.ble.util

interface PermissionRequestListener {
    fun requestBluetoothPermissions(permissions: Array<String>, requestCode: Int)
}