package com.renhejia.robot.guidelib.ble.ancs


import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import com.renhejia.robot.guidelib.ble.util.BlePermissions.checkBluetoothPermissions
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class ANCSService(private val mContext: Context) : Handler.Callback {
    var mANCSParser: ANCSParser = ANCSParser()
    private var serverHandler: Handler? = null
    private var handlerThread: HandlerThread? = null

    private var mBondDeviceReceiver: BondDeviceReceiver? = null

    //bt
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mIphoneDevice: BluetoothDevice? = null
    private val mGattCallback = LocalBluetoothGattCallback()
    private var mConnectedGatt: BluetoothGatt? = null
    private var mANCSService: BluetoothGattService? = null
    private var mNotificationSourceChar: BluetoothGattCharacteristic? = null
    private var mPointControlChar: BluetoothGattCharacteristic? = null
    private var mDataSourceChar: BluetoothGattCharacteristic? = null

    //BLE GATT Server
    private var bluetoothGattServer: BluetoothGattServer? = null
    private val bluetoothGattServerCallback: BluetoothGattServerCallback =
        LocalBluetoothGattServerCallback()

    


    fun onCreate() {
        handlerThread = HandlerThread("ServerHandlerThread")
        handlerThread!!.start()
        mBluetoothManager = mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        mBluetoothAdapter = mBluetoothManager!!.adapter
        
        if (checkBluetoothPermissions(mContext)) {
            try {
                if (!mBluetoothAdapter!!.isEnabled) {
                    mBluetoothAdapter!!.enable()
                } else {
                    initGATTServer()
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }

        serverHandler = Handler(handlerThread!!.looper, this)
        mBondDeviceReceiver = BondDeviceReceiver(serverHandler!!)
        val intentFilter = IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        mContext.registerReceiver(mBondDeviceReceiver, intentFilter)
    }

    fun onDestroy() {
        closeGattServer()
        handlerThread!!.looper.quit()
        handlerThread = null
        //蓝牙线程终止
        serverHandler = null
        mContext.unregisterReceiver(mBondDeviceReceiver)
        Log.e("<<<<<", "onDestroy")
    }

    private fun sendNotifationData(bleReceiveData: String) {
        if (ancsBleDataListener != null) {
            ancsBleDataListener!!.onReceiveBleData(bleReceiveData)
        }
    }

    override fun handleMessage(message: Message): Boolean {
        when (message.what) {
            GlobalDefine.BLUETOOTH_DISCONNECT -> {}
            GlobalDefine.BLUETOOTH_BONDED -> if (mIphoneDevice != null) {
                Log.d(TAG, "connect gatt")
                if (checkBluetoothPermissions(mContext)) {
                    try {
                        mIphoneDevice!!.connectGatt(mContext.applicationContext, false, mGattCallback)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
            }

            GlobalDefine.BLUETOOTH_ON -> initGATTServer()
            GlobalDefine.BLUETOOTH_DISPLAY_INFO -> {
                val data = message.obj as ByteArray
                mANCSParser.onDSNotification(data)
                val bundleId = mANCSParser.getmCurrentANCSData()!!.notification.bundleId
                val title = mANCSParser.getmCurrentANCSData()!!.notification.title
                val subtitle = mANCSParser.getmCurrentANCSData()!!.notification.subtitle
                val msg = mANCSParser.getmCurrentANCSData()!!.notification.message
                var time = mANCSParser.getmCurrentANCSData()!!.notification.date
                if (time != null) {
                    //处理时间格式
                    var inputFormatter: DateTimeFormatter? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")
                        // 将输入日期时间字符串解析为 LocalDateTime 对象
                        val dateTime = LocalDateTime.parse(time, inputFormatter)
                        // 定义输出日期时间的格式
                        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        // 格式化 LocalDateTime 对象为指定格式的字符串
                        time = dateTime.format(outputFormatter)
                    }
                } else {
                    time = ""
                }

                val jsonObject = JSONObject()
                try {
                    jsonObject.put("title", title)
                    jsonObject.put("msg", msg)
                    jsonObject.put("time", time)
                    jsonObject.put("package", bundleId)
                    val jsonStr = jsonObject.toString()
                    //发送数据
                    sendNotifationData("003$jsonStr")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            GlobalDefine.BLUETOOTH_GET_MORE_INFO -> {
                val data2 = message.obj as ByteArray
                // mANCSParser.onNotification(nsData);
                retrieveMoreInfo(data2)
            }

            else -> {}
        }

        return false
    }


    fun createBond(btClass: Class<*>, btDevice: BluetoothDevice?): Boolean {
        var createBondMethod: Method? = null
        var returnValue: Boolean? = null
        try {
            createBondMethod = btClass.getMethod("createBond")
            returnValue = createBondMethod.invoke(btDevice) as Boolean
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return returnValue!!
    }


    private fun closeGattServer() {

        if (checkBluetoothPermissions(mContext)) {
            try {
                if (bluetoothGattServer != null) {
                    bluetoothGattServer!!.clearServices()
                    bluetoothGattServer!!.close()
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
      
    }
    

    fun initGATTServer() {
        if (checkBluetoothPermissions(mContext)) {
            try {
                bluetoothGattServer =
                    mBluetoothManager!!.openGattServer(mContext, bluetoothGattServerCallback)
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
    }

    fun retrieveMoreInfo(nid: ByteArray) {
        val bout = ByteArrayOutputStream()

        bout.write(0.toByte().toInt())

        bout.write(nid[0].toInt())
        bout.write(nid[1].toInt())
        bout.write(nid[2].toInt())
        bout.write(nid[3].toInt())


        bout.write(1)
        bout.write(50)
        bout.write(0)
        // subtitle
        bout.write(2)
        bout.write(100)
        bout.write(0)

        // message
        bout.write(3)
        bout.write(500)
        bout.write(0)

        // message size
        bout.write(4)
        bout.write(10)
        bout.write(0)
        // date
        bout.write(5)
        bout.write(10)
        bout.write(0)

        val getNotificationAttribute = bout.toByteArray()


        //         byte[] getNotificationAttribute = {
//                 (byte) 0x00,
//                 //UID
//                 nid[0], nid[1], nid[2], nid[3],
//
//                 //title
//                 (byte) 0x01, (byte) 0xff, (byte) 0xff,
//                 //subtitle
// //                (byte) 0x02, (byte) 0xff, (byte) 0xff,
//                 //message
//                 (byte) 0x03, (byte) 0xff, (byte) 0xff
//         };


        // Log.i(TAG, "get information detail::" + StringTools.Bytes2HexString(getNotificationAttribute, 0, getNotificationAttribute.length));
        //如果已经绑定，而且此时未断开
        if (mConnectedGatt != null) {
            val service = mConnectedGatt!!.getService(UUID.fromString(GlobalDefine.service_ancs))
            if (service == null) {
                Log.d(TAG, "cant find service")
            } else {
                // Log.d(TAG, "find service");
                val characteristic =
                    service.getCharacteristic(UUID.fromString(GlobalDefine.characteristics_control_point))
                if (characteristic == null) {
                    Log.d(TAG, "cant find chara")
                } else {
                    // Log.d(TAG, "find chara");
                    characteristic.setValue(getNotificationAttribute)
                    if (checkBluetoothPermissions(mContext)) {
                        try {
                            mConnectedGatt!!.writeCharacteristic(characteristic)
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                            Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                        }
                    }
                }
            }
        }
    }

    private fun setNotificationEnabled(characteristic: BluetoothGattCharacteristic) {


        if (checkBluetoothPermissions(mContext)) {
            try {
                mConnectedGatt!!.setCharacteristicNotification(characteristic, true)
                val descriptor =
                    characteristic.getDescriptor(UUID.fromString(GlobalDefine.descriptor_config))
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                    mConnectedGatt!!.writeDescriptor(descriptor)
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
            }
        }
        
       
    }


    private inner class LocalBluetoothGattCallback : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "connected")
                mConnectedGatt = gatt

                if (checkBluetoothPermissions(mContext)) {
                    try {
                        gatt.discoverServices()
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                    }
                }
                
                
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //外设主动断开
                Log.d(TAG, "disconnected")
                initGATTServer()
                mConnectedGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val services = gatt.services
            for (service in services) {
                Log.d(TAG, "---service uuid--" + service.uuid)
            }

            if (status == BluetoothGatt.GATT_SUCCESS) {
                val ancsService = gatt.getService(UUID.fromString(GlobalDefine.service_ancs))
                if (ancsService == null) {
                    Log.d(TAG, "ANCS cannot find")
                } else {
                    Log.d(TAG, "ANCS find")
                    mANCSService = ancsService
                    mDataSourceChar =
                        ancsService.getCharacteristic(UUID.fromString(GlobalDefine.characteristics_data_source))
                    mPointControlChar =
                        ancsService.getCharacteristic(UUID.fromString(GlobalDefine.characteristics_control_point))
                    mNotificationSourceChar =
                        ancsService.getCharacteristic(UUID.fromString(GlobalDefine.characteristics_notification_source))
                    setNotificationEnabled(mDataSourceChar!!)
                }
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            Log.d(TAG, " onDescriptorWrite:: $status")
            // Notification source
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (descriptor.characteristic.uuid == UUID.fromString(GlobalDefine.characteristics_data_source)) {
                    setNotificationEnabled(mNotificationSourceChar!!)
                    Log.d(TAG, "data_source 订阅成功 ")
                }
                if (descriptor.characteristic.uuid == UUID.fromString(GlobalDefine.characteristics_notification_source)) {
                    Log.d(TAG, "notification_source　订阅成功 ")
                }
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.d(TAG, "onCharacteristicWrite")
            if (GlobalDefine.characteristics_control_point == characteristic.uuid.toString()) {
                Log.d(TAG, "control_point  Write successful")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (GlobalDefine.characteristics_notification_source == characteristic.uuid.toString()) {
                // Log.d(TAG, "notification_source Changed");
                val nsData = characteristic.value

                if (nsData[0].toInt() == 0x02) {
                    // Log.i(TAG,"通知被iphone删除");
                } else {
                    val NotificationUID = ByteArray(4)
                    System.arraycopy(nsData, 4, NotificationUID, 0, 4)
                    if (serverHandler != null) {
                        val msg = serverHandler!!.obtainMessage()
                        msg.what = GlobalDefine.BLUETOOTH_GET_MORE_INFO
                        msg.obj = NotificationUID
                        serverHandler!!.sendMessage(msg)
                    }
                }
            }
            if (GlobalDefine.characteristics_data_source == characteristic.uuid.toString()) {
                Log.d(TAG, "characteristics_data_source changed")
                if (serverHandler != null) {
                    val get_data = characteristic.value
                    val msg = serverHandler!!.obtainMessage()
                    msg.what = GlobalDefine.BLUETOOTH_DISPLAY_INFO
                    msg.obj = get_data
                    serverHandler!!.sendMessage(msg)
                }
            }

            if (GlobalDefine.characteristics_control_point == characteristic.uuid.toString()) {
                Log.d(TAG, "characteristics_control_point changed")
                val cpData = characteristic.value
                Log.i(TAG, "控制数据：" + StringTools.Bytes2HexString(cpData, 0, cpData.size))
            }
        }
    }

    private inner class LocalBluetoothGattServerCallback : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (checkBluetoothPermissions(mContext)) {
                try {
                    Log.i(
                        TAG,
                        String.format(
                            "ANCS--onConnectionStateChange:%s,%s,%s,%s",
                            device.name,
                            device.address,
                            status,
                            newState
                        )
                    )
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                }
            }
           

            mIphoneDevice = device
            
            if (checkBluetoothPermissions(mContext)) {
                try {
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        closeGattServer()
                        val MacAddress = mIphoneDevice!!.address
                        Log.i(TAG, "已连接设备MAC：$MacAddress")

                        if (mIphoneDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                            mIphoneDevice!!.connectGatt(mContext.applicationContext, false, mGattCallback)
                        } else {
                            createBond(device.javaClass, device)
                        }
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    Log.e(TAG, "Bluetooth enable failed due to missing permissions.")
                }
            }
            
          
        }
    }

    private var ancsBleDataListener: ANCSBleDataListener? = null

    init {
        onCreate()
    }

    fun setBleDataListener(bleDataListener: ANCSBleDataListener?) {
        this.ancsBleDataListener = bleDataListener
    }

    fun interface ANCSBleDataListener {
        fun onReceiveBleData(bleData: String?)
    }

    companion object {
        const val TAG: String = "ANCSService"
    }
}
