package com.renhejia.robot.guidelib.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Message
import android.os.ParcelUuid
import android.util.Log
import android.widget.TextView
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.guidelib.ble.callback.BleConnectStatusCallback
import com.renhejia.robot.guidelib.ble.util.BlePermissions.checkBluetoothPermissions
import com.renhejia.robot.guidelib.ble.util.CRC8
import com.renhejia.robot.guidelib.ble.util.IntByteStringHexUtil
import com.renhejia.robot.guidelib.ble.util.PermissionRequestListener
import com.renhejia.robot.guidelib.wifi.WIFIStateReceiver
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import java.util.UUID

class BleServer(var mContext: Context, private val permissionRequestListener: PermissionRequestListener) {
    private fun setWifiChangedListener() {
        BleConnectStatusCallback.instance
            .registerBleConnectStatusListener { connectStatus ->
                if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECT_TO_CLIENT) {
                    logd(
                        "BleServer",
                        "onBleConnectStatusChanged: BLE_STATUS_CONNECT_TO_CLIENT"
                    )
                    // enterWifiConnectMode();
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_SUCCESS) {
                    logd(
                        "BleServer",
                        "onBleConnectStatusChanged: BLE_STATUS_CONNECTED_NET_SUCCESS"
                    )
                    wifiConnectedSuccess()
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_CONNECTED_NET_FAILED) {
                    logd(
                        "BleServer",
                        "onBleConnectStatusChanged: BLE_STATUS_CONNECTED_NET_FAILED"
                    )
                    wifiConnectedFailed()
                } else if (connectStatus == BleConnectStatusCallback.Companion.BLE_STATUS_DISCONNECT_FROM_CLIENT) {
                    logd(
                        "BleServer",
                        "onBleConnectStatusChanged: BLE_STATUS_DISCONNECT_FROM_CLIENT"
                    )
                }
            }
    }

    private val mTips: TextView? = null
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null // BLE广播
    private var mBluetoothGattServer: BluetoothGattServer? = null // BLE服务端

    private var deviceNotify: BluetoothDevice? = null
    private var deviceNotifyRead: BluetoothDevice? = null // 给付坤
    private var descriptorNotify: BluetoothGattDescriptor? = null
    private var characteristicRead: BluetoothGattCharacteristic? = null

    // BLE广播Callback
    private val mAdvertiseCallback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            logi(TAG, "BLE广播开启成功")
        }

        override fun onStartFailure(errorCode: Int) {
            logi(
                TAG,
                "BLE广播开启失败,错误码:$errorCode"
            )
        }
    }

    fun enableBluetooth(context: Context) {
        logi(TAG, "enableBluetooth ====== 0:")
        // 检查蓝牙开关
        val adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null) {
            logi(TAG, "enableBluetooth ====== 1:")
            return
        } else {
            logi(TAG, "enableBluetooth ====== 2:")
            if (!adapter.isEnabled) {
                logi(TAG, "enableBluetooth ====== 3:")
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        // 直接开启蓝牙
                        adapter.enable()
                        // 跳转到设置界面
                        // startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                        // 112);
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }

            }
        }

        // 检查是否支持BLE蓝牙
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            logi(TAG, "enableBluetooth ====== 4:")
            return
        }

    }

    private val mBluetoothGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(
                device: BluetoothDevice,
                status: Int,
                newState: Int
            ) {
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG, String.format(
                                "onConnectionStateChange:%s,%s,%s,%s", device.name,
                                device.address, status, newState
                            )
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }

                logi(
                    TAG, String.format(
                        if (status == 0) (if (newState == 2) "与[%s]连接成功" else "与[%s]连接断开") else ("与[%s]连接出错,错误码:$status"),
                        device
                    )
                )

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothGatt.STATE_CONNECTED) { // 连接成功
                        BleConnectStatusCallback.instance
                            .setBleConnectStatus(BleConnectStatusCallback.Companion.BLE_STATUS_CONNECT_TO_CLIENT)
                        deviceNotifyRead = device
                    } else if (newState == BluetoothGatt.STATE_DISCONNECTED) { // 断开连接
                        BleConnectStatusCallback.instance
                            .setBleConnectStatus(BleConnectStatusCallback.Companion.BLE_STATUS_DISCONNECT_FROM_CLIENT)
                    }
                } else {
                    logi(TAG, "connect fail")
                }
            }

            override fun onServiceAdded(status: Int, service: BluetoothGattService) {
                logi(TAG, String.format("onServiceAdded:%s,%s", status, service.uuid))
                logi(
                    TAG,
                    String.format(
                        if (status == 0) "添加服务[%s]成功" else "添加服务[%s]失败,错误码:$status",
                        service.uuid
                    )
                )
            }

            override fun onCharacteristicReadRequest(
                device: BluetoothDevice, requestId: Int, offset: Int,
                characteristic: BluetoothGattCharacteristic
            ) {
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG, String.format(
                                "onCharacteristicReadRequest:%s,%s,%s,%s,%s", device.name,
                                device.address, requestId, offset, characteristic.uuid
                            )
                        )
                        val response = "CHAR_" + (Math.random() * 100).toInt() // 模拟数据
                        mBluetoothGattServer!!.sendResponse(
                            device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                            response.toByteArray()
                        ) // 响应客户端
                        logi(
                            TAG,
                            """
                    客户端读取Characteristic[${characteristic.uuid}]:
                    $response
                    """.trimIndent()
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }

            }

            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                requestBytes: ByteArray
            ) {
                // 获取客户端发过来的数据
                val buffer = ByteBuffer.wrap(requestBytes)
                // 将ByteBuffer的内容转换为字符串
                val requestStr = StandardCharsets.UTF_16LE.decode(buffer).toString()

                // String requestStr = new String(requestBytes);
                // LogUtils.logi(TAG,
                // String.format("onCharacteristicWriteRequest:%s,%s,%s,%s,%s,%s,%s,%s",
                // device.getName(), device.getAddress(), requestId, characteristic.getUuid(),
                // preparedWrite, responseNeeded, offset, requestStr));
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        mBluetoothGattServer!!.sendResponse(
                            device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            offset,
                            requestBytes
                        ) // 响应客户端
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
                logi(
                    TAG,
                    "客户端写入Characteristic$requestStr"
                )

                sendMessageDelay(HANDLE_SET_NET_INFO, 10, requestStr)
                // receiveAppCmd(requestBytes);
            }

            override fun onDescriptorReadRequest(
                device: BluetoothDevice, requestId: Int, offset: Int,
                descriptor: BluetoothGattDescriptor
            ) {

                logi(TAG, "onDescriptorReadRequest =========== 1" + descriptor.uuid + "]:\n")

                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG, String.format(
                                "onDescriptorReadRequest:%s,%s,%s,%s,%s", device.name,
                                device.address, requestId, offset, descriptor.uuid
                            )
                        )
                        val response = "DESC_" + (Math.random() * 100).toInt() // 模拟数据
                        mBluetoothGattServer!!.sendResponse(
                            device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                            response.toByteArray()
                        ) // 响应客户端

                        logi(
                            TAG,
                            """
                    客户端读取Descriptor[${descriptor.uuid}]:
                    $response
                    """.trimIndent()
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
              
            }

            override fun onDescriptorWriteRequest(
                device: BluetoothDevice,
                requestId: Int,
                descriptor: BluetoothGattDescriptor,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray
            ) {
                logi(TAG, "onDescriptorReadRequest =========== 2" + descriptor.uuid + "]:\n")
                // 获取客户端发过来的数据
                val valueStr = value.contentToString()
                
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG,
                            String.format(
                                "onDescriptorWriteRequest:%s,%s,%s,%s,%s,%s,%s,%s",
                                device.name,
                                device.address,
                                requestId,
                                descriptor.uuid,
                                preparedWrite,
                                responseNeeded,
                                offset,
                                valueStr
                            )
                        )

                        mBluetoothGattServer!!.sendResponse(
                            device,
                            requestId,
                            BluetoothGatt.GATT_SUCCESS,
                            offset,
                            value
                        ) // 响应客户端
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
                logi(
                    TAG,
                    """
                    客户端写入Descriptor[${descriptor.uuid}]:
                    $valueStr
                    """.trimIndent()
                )

                // 简单模拟通知客户端Characteristic变化
                if (BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE.contentToString() == valueStr) { // 是否开启通知

                    deviceNotify = device
                    descriptorNotify = descriptor
                    if (descriptorNotify == null) {
                        logi(
                            "write_command",
                            "descriptorNotify is null:  ============ 1 ============="
                        )
                    } else {
                        logi(
                            "write_command",
                            "descriptorNotify is not null: ============ 2 ============="
                        )
                    }

                    if (deviceNotify == null) {
                        logi("write_command", "deviceNotify is null:  ============ 1 =============")
                    } else {
                        logi(
                            "write_command",
                            "deviceNotify is not null: ============ 2 ============="
                        )
                    }
                }
            }

            override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {

                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG, String.format(
                                "onExecuteWrite:%s,%s,%s,%s", device.name, device.address,
                                requestId, execute
                            )
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
                
            }

            override fun onNotificationSent(device: BluetoothDevice, status: Int) {
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(
                            TAG,
                            String.format(
                                "onNotificationSent:%s,%s,%s",
                                device.name,
                                device.address,
                                status
                            )
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
              
            }

            override fun onMtuChanged(device: BluetoothDevice, mtu: Int) {
                if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                    try {
                        logi(TAG, String.format("onMtuChanged:%s,%s,%s", device.name, device.address, mtu))

                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }

            }
        }

    fun initBleBlueTooth(context: Context) {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            if (checkBluetoothPermissions(mContext, permissionRequestListener))  {
                try {
                    bluetoothAdapter.enable()
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                }
            }
           
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        // BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // ============启动BLE蓝牙广播(广告)
        // =================================================================================
        // 广播设置(必须)
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY) // 广播模式: 低功耗,平衡,低延迟
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH) // 发射功率级别: 极低,低,中,高
            .setTimeout(0).setConnectable(true) // 能否连接,广播分为可连接广播和不可连接广播
            .build()

        // 广播数据(必须，广播启动就会发送)
        val advertiseData = AdvertiseData.Builder().setIncludeDeviceName(true) // 包含蓝牙名称
            // .setIncludeDeviceName(false) //包含蓝牙名称
            .setIncludeTxPowerLevel(true) // 包含发射功率级别
            .addManufacturerData(1, byteArrayOf(68, 86)) // 设备厂商数据，自定义
            .build()

        // 扫描响应数据(可选，当客户端扫描时才发送)
        val scanResponse =
            AdvertiseData.Builder().addManufacturerData(2, byteArrayOf(66, 66)) // 设备厂商数据，自定义
                .addServiceUuid(ParcelUuid(UUID_SERVICE)) // 服务UUID
                .build()

        mBluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

        // setting关闭蓝牙之后，这里会空指针
        if (mBluetoothLeAdvertiser != null) {
            if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
                try {
                    mBluetoothLeAdvertiser!!.startAdvertising(
                        settings,
                        advertiseData,
                        scanResponse,
                        mAdvertiseCallback
                    )                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                }
            }
           
        }
        // 注意：必须要开启可连接的BLE广播，其它设备才能发现并连接BLE服务端!
        // =============启动BLE蓝牙服务端=====================================================================================
        val service = BluetoothGattService(
            UUID_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // //添加可读+通知characteristic
        // BluetoothGattCharacteristic characteristicRead = new
        // BluetoothGattCharacteristic(UUID_CHAR_READ_NOTIFY,BluetoothGattCharacteristic.PROPERTY_READ
        // | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
        // BluetoothGattCharacteristic.PERMISSION_READ);
        // characteristicRead.addDescriptor(new
        // BluetoothGattDescriptor(UUID_DESC_NOTITY,
        // BluetoothGattCharacteristic.PERMISSION_WRITE));
        // service.addCharacteristic(characteristicRead);

        // 添加可读+通知characteristic
        characteristicRead = BluetoothGattCharacteristic(
            UUID_CHAR_READ_NOTIFY,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        characteristicRead!!.addDescriptor(
            BluetoothGattDescriptor(UUID_DESC_NOTITY, BluetoothGattCharacteristic.PERMISSION_WRITE)
        )
        characteristicRead!!
            .addDescriptor(
                BluetoothGattDescriptor(
                    CCCD_ID,
                    BluetoothGattCharacteristic.PERMISSION_READ
                )
            )
        service.addCharacteristic(characteristicRead)

        // 添加可写characteristic
        val characteristicWrite = BluetoothGattCharacteristic(
            UUID_CHAR_WRITE,
            BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(characteristicWrite)
        if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
            try {
                if (bluetoothManager != null) {

                    mBluetoothGattServer =
                        bluetoothManager.openGattServer(context, mBluetoothGattServerCallback)
                }

                mBluetoothGattServer!!.addService(service)
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
        
    }

    fun unInit() {
        if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
            try {
                if (mBluetoothLeAdvertiser != null) {

                    mBluetoothLeAdvertiser!!.stopAdvertising(mAdvertiseCallback)
                }

                if (mBluetoothGattServer != null) {
                    mBluetoothGattServer!!.close()
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
    }

    /***************************************************
     * ble指令解析
     */
    var isFullMessage: Boolean = true
    var totalMessageLength: Int = 0
    var curMessageLength: Int = 0
    var strFullMessage: String = ""
    var isEncrypted: Boolean = false

    private fun receiveAppCmd(requestBytes: ByteArray) {
        val appCmd = IntByteStringHexUtil.byteArrayToHexStr(requestBytes)
        val a = IntByteStringHexUtil.hex2Str(appCmd!!)

        logi(
            TAG,
            "receiveAppCmd 客户端写入Characteristic HexStr ====== 0 :\n$appCmd"
        )
        logi(
            TAG,
            "receiveAppCmd 客户端写入Characteristic HexStr ====== 1 :\n$a"
        )

        if (isFullMessage) {
            val version_number = appCmd.substring(0, 4)
            val message_id = appCmd.substring(4, 6)
            val function = appCmd.substring(6, 8)
            val sub_function = appCmd.substring(8, 10)

            val data_length = appCmd.substring(10, 14)
            totalMessageLength = 14 + data_length.toInt(16) * 2 + 2 // 头+data+crc

            if (appCmd.length < totalMessageLength) {
                logi(TAG, "receiveAppCmd isFullMessage=false")

                isFullMessage = false
                strFullMessage = appCmd
                curMessageLength = appCmd.length
            } else {
                isFullMessage = true
                strFullMessage = appCmd
            }
        } else {
            curMessageLength += appCmd.length

            strFullMessage += appCmd

            if (curMessageLength >= totalMessageLength) {
                isFullMessage = true
            }
        }

        if (isFullMessage) {
            logi(
                TAG,
                "strFullMessage =$strFullMessage"
            )
            logi(
                TAG,
                "客户端写入长指令(大于20字节) strFullMessage:\n$strFullMessage"
            )

            // crc校验
            val crc = strFullMessage.substring(strFullMessage.length - 2)
            val message = strFullMessage.substring(0, strFullMessage.length - 2)
            val verifyCrc = CRC8.calcCrc8Str(IntByteStringHexUtil.hexStrToByteArray(message)!!)

            val ret = crc.lowercase(Locale.getDefault()) == verifyCrc.lowercase(Locale.getDefault())

            logi(
                TAG,
                "verifyCrc ret=$ret"
            )

            if (ret) {
                parseAppCmd(strFullMessage)
            }
        }
    }

    private fun parseAppCmd(appCmd: String) {
        logi(
            TAG,
            "parseAppCmd appCmd=$appCmd"
        )

        if (appCmd.length < 16) {
            return
        }

        val version_number = appCmd.substring(0, 4)
        val message_id = appCmd.substring(4, 6)
        val function = appCmd.substring(6, 8)
        val sub_function = appCmd.substring(8, 10)
        logi(
            TAG,
            "parseAppCmd function=$function,sub_function=$sub_function"
        )

        if (function == "01" && sub_function == "01") {
        }
    }

    private fun responseBleReceiveInfo(requestStr: String) {
        logd(
            "BleServer",
            "responseBleReceiveInfo: $requestStr"
        )

        if (requestStr.contains(NET_SSID) && requestStr.contains(NET_PASSWORD) && requestStr.contains(
                NET_SPLIT
            )
        ) {
            // //需要配网了，因为是单例所以需要将原来的ssid清空
            // WIFIConnectionManager.getInstance(mContext).setCurrentSsid("");
            // registerWIFIStateReceiver();
            // WIFIConnectionManager.getInstance(mContext).setSetIncorrectPassword(false);
            // BleConnectStatusCallback.getInstance().setBleConnectStatus(BleConnectStatusCallback.BLE_STATUS_CONNECTING_NET);
            // LogUtils.logi(TAG, "responseNetInfo= === 3 ");
            // String[] netInfo = requestStr.split(NET_SPLIT);
            // if (netInfo != null && netInfo.length > 1) {
            // String wifiName = netInfo[0].replace(NET_SSID, "");
            // String wifiPassword = netInfo[1].replace(NET_PASSWORD, "");
            // LogUtils.logi(TAG, "responseNetInfo= === 5_1 " + wifiName);
            // GuideWifiConnectCallback.getInstance().setWifiInfo(wifiName, wifiPassword);
            // WIFIAutoConnectionService.start(mContext, wifiName, wifiPassword);
            // }
            // } else if (requestStr.equals(REQUEST_MAC_ADDRESS)) {
            // LogUtils.logi(TAG, "responseNetInfo= === 5 ");
            // String macInfo = MAC_START + SystemUtil.getWlanMacAddress() + MAC_END;
            //// writeCmdForFukun(macInfo);
            //
            // String mac = SystemUtil.getWlanMacAddress();
            // String ts = (System.currentTimeMillis() / 1000) + "";
            // String sn = SystemUtil.getLtpSn();
            // String deviceSign = getDeviceSign(mac, ts);
            // String json = "{" + "\"cmd\":\"requestMacAddress\"," + "\"mac\":\"" + mac +
            // "\"," + "\"ts\":" + ts + "," + "\"device_sign\":\"" + deviceSign + "\"," +
            // "\"sn\":\"" + sn + "\"" + "}";
            // writeKeyValueMini(json);
            // } else if (requestStr.equals(REQUEST_SYSTEM_VERSION)) {
            // LogUtils.logi(TAG, "responseNetInfo= === 5 ");
            // //TODO 此处为假的版本号，稍后建斌会提供真的版本号获取方式
            // String version = "1.0.0.1";
            // writeCmdForFukun(version);
        } else {
            fenfaBleData(requestStr)
        }
    }

    private fun fenfaBleData(bleReceiveData: String) {
        if (bleDataListener != null) {
            bleDataListener!!.onReceiveBleData(bleReceiveData)
        }
    }

    var partSecretKey: String = "your partSecretKey"

    fun getDeviceSign(inputValue: String, timeStr: String): String? {
        val deviceSecretKey = md5(inputValue + timeStr + partSecretKey)
        // deviceSecretKey: c6478a35ec15a395ac65ea295390846a
        val mac_sign = sha256(inputValue + timeStr + deviceSecretKey)
        return mac_sign
        // deviceSign: cc5dc034069905a983e7f08be16e082d82dc23fa76b5aa1090af4e9e806ff9b6
    }

    fun writeData(response: String) {
        if (deviceNotifyRead == null) {
            logi(
                "auto_connect",
                "deviceNotifyRead is null: $response"
            )
            return
        }
        logd(
            "BleServer",
            "writeData: 发送给他人数据:$response"
        )
        characteristicRead!!.setValue(response)
        if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
            try {
                mBluetoothGattServer!!.notifyCharacteristicChanged(
                    deviceNotifyRead,
                    characteristicRead,
                    false
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
    }

    // NET_SSID + "Wi-Fi名"+ NET_SPLIT + NET_PASSWORD+ "password"
    private fun writeCmdForFukun(response: String) {
        if (deviceNotifyRead == null) {
            logi(
                "auto_connect",
                "deviceNotifyRead is null: $response"
            )
            return
        }

        characteristicRead!!.setValue(response)
        if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
            try {
                mBluetoothGattServer!!.notifyCharacteristicChanged(
                    deviceNotifyRead,
                    characteristicRead,
                    false
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }

        logi("auto_connect", "writeCmd_fk: $response")
    }

    private fun writeKeyValueMini(response: String) {
        if (deviceNotifyRead == null) {
            logi(
                "auto_connect",
                "deviceNotifyRead is null: $response"
            )
            return
        }



        characteristicRead!!.setValue(response)
        if (checkBluetoothPermissions(mContext, permissionRequestListener)) {
            try {
                mBluetoothGattServer!!.notifyCharacteristicChanged(
                    deviceNotifyRead,
                    characteristicRead,
                    false
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }

        logd(
            "BleServer",
            "writeKeyValueMini: end: $response"
        )
    }

    fun wifiConnectedSuccess() {
        writeCmdForFukun(NETWORK_CONNECT_SUCCESS)
        Log.i(TAG, "BleServer wifiConnectedSuccess: ")
        // LTPGuideConfigManager.getInstance(mContext).setActivated(true);
        // LTPGuideConfigManager.getInstance(mContext).commit();
        // SystemUtil.setRobotActivated();
        // 关闭蓝牙
        // mHandler.postDelayed(() -> unInit(), 3 * 1000);
        unregisterWIFIStateReceiver()
    }

    fun wifiConnectedFailed() {
        logi("auto_connect", "================wifiConnectedFailed================")
        // writeCmd(NETWORK_CONNECT_FAILED);
        writeCmdForFukun(NETWORK_CONNECT_FAILED)
    }

    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg != null) {
                when (msg.what) {
                    HANDLE_SET_NET_INFO -> responseBleReceiveInfo(msg.obj as String)
                }
            }
        }
    }

    private fun sendMessageDelay(handleid: Int, delay: Long, netInfo: String) {
        if (mHandler != null) {
            mHandler.removeMessages(handleid)
            val message = Message()
            message.what = handleid
            message.obj = netInfo
            mHandler.sendMessageDelayed(message, delay)
        }
    }

    private var mWIFIStateReceiver: WIFIStateReceiver? = null

    fun registerWIFIStateReceiver() {
        if (mWIFIStateReceiver == null) {
            mWIFIStateReceiver = WIFIStateReceiver(mContext)
            val filter = IntentFilter()
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
            mContext.registerReceiver(mWIFIStateReceiver, filter)
        }
    }

    fun unregisterWIFIStateReceiver() {
        if (mWIFIStateReceiver != null) {
            mContext.unregisterReceiver(mWIFIStateReceiver)
            mWIFIStateReceiver = null
        }
    }

    private var bleDataListener: BleDataListener? = null

    init {
        setWifiChangedListener()
        enableBluetooth(mContext)
        // initBleBlueTooth(context);
    }

    fun setBleDataListener(bleDataListener: BleDataListener?) {
        this.bleDataListener = bleDataListener
    }

    fun interface BleDataListener {
        fun onReceiveBleData(bleData: String?)
    }

    companion object {
        private var instance: BleServer? = null
        private const val NET_SSID = "ssid="
        private const val NET_PASSWORD = "password="
        private const val NET_SPLIT = "_;;_"
        private const val NETWORK_CONNECT_ENTERING = "network_connect_prepare"
        private const val NETWORK_CONNECTING = "network_connecting"
        private const val NETWORK_CONNECT_SUCCESS = "network_connect_success"
        private const val NETWORK_CONNECT_FAILED = "network_connect_failed"
        private const val NETWORK_CONNECT_TEST = "network_connect_test"
        private const val REQUEST_MAC_ADDRESS = "requestMacAddress"
        private const val REQUEST_SYSTEM_VERSION = "requestSystemVersion"
        private const val MAC_START = "//;;__"
        private const val MAC_END = "__;;//"

        // MAC_START+ macAddress +MAC_END
        // macAddress: 88:12:AC:54:4A:A2
        // NET_SSID + "Wi-Fi名"+ NET_SPLIT + NET_PASSWORD+ "password"
        // "ssid=LETIANPAI-5G_;;_password=Renhejia0801"
        fun getInstance(context: Context, permissionRequestListener: PermissionRequestListener): BleServer {
            synchronized(BleServer::class.java) {
                if (instance == null) {
                    instance = BleServer(context, permissionRequestListener)
                }
                return instance!!
            }
        }

        const val TAG: String = "ble_server"
        val UUID_SERVICE: UUID = UUID.fromString("10000000-0000-0000-0000-000000000000") // 自定义UUID
        val UUID_CHAR_READ_NOTIFY: UUID = UUID.fromString("11000000-0000-0000-0000-000000000000")
        val UUID_DESC_NOTITY: UUID = UUID.fromString("11100000-0000-0000-0000-000000000000")
        val UUID_CHAR_WRITE: UUID = UUID.fromString("12000000-0000-0000-0000-000000000000")

        // 固定的值，flutter连网需要
        private val CCCD_ID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        private const val HANDLE_SET_NET_INFO = 99

        fun md5(string: String): String {
            try {
                val md = MessageDigest.getInstance("MD5")
                md.update(string.toByteArray())
                var tem: Int
                val buffer = StringBuffer()
                for (it in md.digest()) {
                    tem = it.toInt()
                    if (tem < 0) {
                        tem += 256
                    }
                    if (tem < 16) {
                        buffer.append("0")
                    }
                    buffer.append(Integer.toHexString(tem))
                }
                return buffer.toString()
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }

        fun sha256(input: String): String? {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val encodedhash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
                return bytesToHex(encodedhash)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
                return null
            }
        }

        private fun bytesToHex(hash: ByteArray): String {
            val hexString = StringBuilder(2 * hash.size)
            for (b in hash) {
                val hex = Integer.toHexString(0xff and b.toInt())
                if (hex.length == 1) {
                    hexString.append('0')
                }
                hexString.append(hex)
            }
            return hexString.toString()
        }
    }
}
