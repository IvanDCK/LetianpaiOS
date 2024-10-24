package com.renhejia.robot.guidelib.ble.ancs

import android.util.Log
import com.renhejia.robot.guidelib.ble.ancs.ANCSService.Companion.TAG
import com.renhejia.robot.guidelib.ble.util.BlePermissions.checkBluetoothPermissions
import com.rhj.audio.BuildConfig

class BondDeviceReceiver(private val mHandler: android.os.Handler) :
    android.content.BroadcastReceiver() {
    override fun onReceive(context: android.content.Context, intent: android.content.Intent) {
        if (BuildConfig.DEBUG) {
           Log.e("<<<<<<<", "action:" + intent.action)
        }
        if (android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED == intent.action) {
            val btDevice =
                intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)

            if (checkBluetoothPermissions(context)) {
                try {

                    if (btDevice!!.bondState == android.bluetooth.BluetoothDevice.BOND_BONDED) {
                        android.util.Log.e("<<<<<<<", "已经绑定 state=" + btDevice.bondState)
                        val msg = mHandler.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_BONDED
                        mHandler.sendMessage(msg)
                    } else if (btDevice.bondState == android.bluetooth.BluetoothDevice.BOND_BONDING) {
                        android.util.Log.e("<<<<<<<", "正在绑定 state=" + btDevice.bondState)
                        val msg = mHandler.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_BONDING
                        mHandler.sendMessage(msg)
                    } else {
                        android.util.Log.e("<<<<<<<", "解除绑定 state=" + btDevice.bondState)
                        val msg = mHandler.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_BONDNONE
                        mHandler.sendMessage(msg)
                    }
                    if (android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED == intent.action) {
                        android.util.Log.e("<<<<<<<", "断开连接")
                        val msg = mHandler.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_DISCONNECT
                        mHandler.sendMessage(msg)
                    } else if (android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED == intent.action) {
                        android.util.Log.e("<<<<<<<", "已连接")
                        val msg = mHandler.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_CONNECT
                        mHandler.sendMessage(msg)
                    } else if (android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED == intent.action) {
                        val msg = mHandler.obtainMessage()
                        if (intent.getIntExtra(
                                android.bluetooth.BluetoothAdapter.EXTRA_STATE,
                                -1
                            ) == android.bluetooth.BluetoothAdapter.STATE_ON
                        ) {
                            android.util.Log.e("<<<<<<<", "蓝牙打开")
                            msg.what = GlobalDefine.BLUETOOTH_ON
                        }
                        if (intent.getIntExtra(
                                android.bluetooth.BluetoothAdapter.EXTRA_STATE,
                                -1
                            ) == android.bluetooth.BluetoothAdapter.STATE_OFF
                        ) {
                            android.util.Log.e("<<<<<<<", "蓝牙关闭")
                            msg.what = GlobalDefine.BLUETOOTH_OFF
                        }
                        mHandler.sendMessage(msg)
                    } else if (android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST == intent.action) {
                        val device =
                            intent.getParcelableExtra<android.bluetooth.BluetoothDevice>(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                        val pairingVariant = intent.getIntExtra(
                            android.bluetooth.BluetoothDevice.EXTRA_PAIRING_VARIANT,
                            android.bluetooth.BluetoothDevice.ERROR
                        )
                        // 处理配对请求
                        if (pairingVariant == android.bluetooth.BluetoothDevice.PAIRING_VARIANT_PIN) {
                            android.util.Log.e("<<<<<<<", "开始pin")
                            // 配对需要PIN码
                            // 在此处显示PIN码给用户输入，例如弹出对话框
                            // String pin = "078616"; // 这里是示例PIN码，应根据实际情况生成
                            // 在这里自动输入PIN码，示例中假设PIN码为6位数字
                            // device.setPin(pin.getBytes());
                            abortBroadcast() // 取消广播，确保不会再弹出系统默认的配对请求对话框
                        } else if (pairingVariant == android.bluetooth.BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION) {
                            android.util.Log.e("<<<<<<<", "开始passkey")
                            // 配对需要用户确认Passkey
                            mHandler.postDelayed({
                                device!!.setPairingConfirmation(
                                    true
                                )
                            }, 3000)

                            // 在这里进行自定义处理，例如弹出对话框提示用户确认配对
                            abortBroadcast() // 取消广播，确保不会再弹出系统默认的配对请求对话框
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                }
            }
        }
    }
}
