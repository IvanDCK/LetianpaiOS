/*
 * Copyright(c) 2020 Bob Shen <ayst.shen@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rhj.audio.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by ayst.shen@foxmail.com on 2016/4/6.
 */
object AppUtils {
    private const val TAG = "AppUtils"

    // version
    private var sVersionName = ""
    private var sVersionCode = -1

    // MAC
    private var sEth0Mac = ""
    private var sWifiMac = ""
    private var sMac = ""
    private var sMacNoColon = ""
    private var sBtMac = ""

    // Screen
    private var sScreenWidth = -1
    private var sScreenHeight = -1

    // Storage
    private var sRootDir = ""

    // Device id
    private var sDeviceId = ""

    // IMEI
    private var sImei = ""

    // Audio Manager
    private var sAudioManager: AudioManager? = null

    // su 别名
    private var sSuAlias = ""

    /**
     * Get version name
     *
     * @param context Context
     * @return version name
     */
    fun getVersionName(context: Context): String {
        if (TextUtils.isEmpty(sVersionName)) {
            try {
                val info = context.packageManager.getPackageInfo(
                    context.packageName, 0
                )
                sVersionName = info.versionName
                sVersionCode = info.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return sVersionName
    }

    /**
     * Get version code
     *
     * @param context Context
     * @return version code
     */
    fun getVersionCode(context: Context): Int {
        if (-1 == sVersionCode) {
            try {
                val info = context.packageManager.getPackageInfo(
                    context.packageName, 0
                )
                sVersionName = info.versionName
                sVersionCode = info.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return sVersionCode
    }

    @get:SuppressLint("HardwareIds")
    val serialNo: String
        /**
         * Get serial number
         *
         * @return serial number
         */
        get() {
            var sn = Build.SERIAL
            if (TextUtils.isEmpty(sn)) {
                sn = getProperty("ro.serialno", "")
                if (TextUtils.isEmpty(sn)) {
                    sn = getProperty("ro.boot.serialno", "")
                    if (TextUtils.isEmpty(sn)) {
                        sn = cPUSerial
                    }
                }
            }

            return sn
        }

    val cPUSerial: String
        /**
         * Get cpu serial
         *
         * @return success: cpu serial, failed: "0000000000000000"
         */
        get() {
            var cpuAddress = "0000000000000000"

            try {
                val process =
                    Runtime.getRuntime().exec("cat /proc/cpuinfo")
                val `is` =
                    InputStreamReader(process.inputStream)
                val input = LineNumberReader(`is`)

                var str: String
                while ((input.readLine().also { str = it }) != null) {
                    if (!TextUtils.isEmpty(str)) {
                        if (str.contains("Serial")) {
                            val cpuStr = str.substring(str.indexOf(":") + 1)
                            cpuAddress = cpuStr.trim { it <= ' ' }
                            break
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "getCPUSerial, " + e.message)
            }

            return cpuAddress
        }

    /**
     * Get the IMEI
     *
     * @param context Context
     * @return IMEI
     */
    @SuppressLint("MissingPermission")
    fun getIMEI(context: Context): String {
        if (TextUtils.isEmpty(sImei)) {
            try {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    sImei = tm.deviceId
                } else {
                    val method = tm.javaClass.getMethod("getImei")
                    sImei = method.invoke(tm) as String
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sImei
    }

    val deviceId: String
        /**
         * Get device id
         *
         * @return device id
         */
        get() {
            if (TextUtils.isEmpty(sDeviceId)) {
                sDeviceId = serialNo
            }

            return sDeviceId
        }

    val country: String
        /**
         * Get current country
         *
         * @return country
         */
        get() = Locale.getDefault().country

    val language: String
        /**
         * Get current language
         *
         * @return language
         */
        get() = Locale.getDefault().language

    /**
     * Is the network connected
     *
     * @param context Context
     * @return true: connected, false: disconnected
     */
    fun isConnNetWork(context: Context): Boolean {
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        (Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val networkInfo = conManager!!.activeNetworkInfo
        return ((networkInfo != null) && networkInfo.isConnected)
    }


    /**
     * Is the WiFi connected
     *
     * @param context Context
     * @return true: connected, false: disconnected
     */
    fun isWifiConnected(context: Context): Boolean {
        val conManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        (Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val wifiNetworkInfo = conManager!!.getNetworkInfo(
            ConnectivityManager.TYPE_WIFI
        )
        return ((wifiNetworkInfo != null) && wifiNetworkInfo.isConnected)
    }

    /**
     * Get ethernet MAC
     *
     * @param context Context
     * @return Mac
     */
    fun getEth0Mac(context: Context?): String {
        if (TextUtils.isEmpty(sEth0Mac)) {
            try {
                var numRead = 0
                val buf = CharArray(1024)
                val strBuf = StringBuffer(1000)
                val reader = BufferedReader(
                    FileReader(
                        "/sys/class/net/eth0/address"
                    )
                )
                while ((reader.read(buf).also { numRead = it }) != -1) {
                    val readData = String(buf, 0, numRead)
                    strBuf.append(readData)
                }
                sEth0Mac = strBuf.toString().replace("\r|\n".toRegex(), "")
                reader.close()
            } catch (ex: IOException) {
                Log.w(TAG, "eth0 mac not exist")
                sEth0Mac = "unknown"
            }
        }
        return sEth0Mac
    }

    fun getWifiMac2(context: Context?): String {
        if (TextUtils.isEmpty(sEth0Mac)) {
            try {
                var numRead = 0
                val buf = CharArray(1024)
                val strBuf = StringBuffer(1000)
                val reader = BufferedReader(
                    FileReader(
                        "sys/class/net/wlan0/address"
                    )
                )
                while ((reader.read(buf).also { numRead = it }) != -1) {
                    val readData = String(buf, 0, numRead)
                    strBuf.append(readData)
                }
                sEth0Mac = strBuf.toString().replace("\r|\n".toRegex(), "")
                reader.close()
            } catch (ex: IOException) {
                Log.w(TAG, "eth0 mac not exist")
                sEth0Mac = "unknown"
            }
        }
        return sEth0Mac
    }

    fun getMacReadSystemFile(context: Context?): String {
        val mac = getWifiMac2(context)
        return mac.replace(":", "")
    }

    /**
     * Get WiFi MAC
     *
     * @param context Context
     * @return Mac
     */
    @SuppressLint("HardwareIds", "MissingPermission")
    fun getWifiMac(context: Context): String {
        if (TextUtils.isEmpty(sWifiMac)) {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            sWifiMac = wifiInfo.macAddress
        }
        return sWifiMac
    }

    /**
     * Get MAC, get the Ethernet MAC first,
     * then get the WiFi MAC if it is empty.
     *
     * @param context Context
     * @return Mac
     */
    fun getMac(context: Context): String {
        if (TextUtils.isEmpty(sMac)) {
            sMac = getEth0Mac(context)
            if (TextUtils.isEmpty(sMac)) {
                sMac = getWifiMac(context)
            }
        }
        return sMac
    }

    /**
     * Get the MAC with the colon removed
     *
     * @param context Context
     * @return Mac
     */
    fun getMacNoColon(context: Context): String {
        if (TextUtils.isEmpty(sMacNoColon)) {
            val mac = getMac(context)
            if (!TextUtils.isEmpty(mac)) {
                sMacNoColon = mac.replace(":", "")
            }
        }
        return sMacNoColon
    }

    /**
     * Get the Bluetooth MAC android 6.0
     *
     * @param context Context
     * @return Mac
     */
    @SuppressLint("MissingPermission")
    fun getBtMac(context: Context?): String {
        if (TextUtils.isEmpty(sBtMac)) {
            val ba = BluetoothAdapter.getDefaultAdapter()
            if (ba != null) {
                sBtMac = ba.address
            }
        }
        return sBtMac
    }

    /**
     * Get screen width
     *
     * @param context Activity
     * @return screen width
     */
    fun getScreenWidth(context: Activity): Int {
        if (-1 == sScreenWidth) {
            sScreenWidth = context.windowManager.defaultDisplay.width
        }
        return sScreenWidth
    }

    /**
     * Get screen height
     *
     * @param context Activity
     * @return screen height
     */
    fun getScreenHeight(context: Activity): Int {
        if (-1 == sScreenHeight) {
            sScreenHeight = context.windowManager.defaultDisplay.height
        }
        return sScreenHeight
    }

    /**
     * Get property
     *
     * @param key          property key
     * @param defaultValue default value
     * @return property value
     */
    @SuppressLint("PrivateApi")
    fun getProperty(key: String?, defaultValue: String): String {
        var value = defaultValue
        try {
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("get", String::class.java, String::class.java)
            value = (get.invoke(c, key, defaultValue)) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return value
    }

    /**
     * Set property
     *
     * @param key   property key
     * @param value property value
     */
    @SuppressLint("PrivateApi")
    fun setProperty(key: String?, value: String?) {
        try {
            val c = Class.forName("android.os.SystemProperties")
            val set = c.getMethod("set", String::class.java, String::class.java)
            set.invoke(c, key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * /storage/emulated/0/"packagename"
     *
     * @param context Context
     * @return path
     */
    fun getExternalRootDir(context: Context?): String {
        if (sRootDir.isEmpty()) {
            var sdcardDir: File? = null
            try {
                if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                    sdcardDir = Environment.getExternalStorageDirectory()
                    Log.i(
                        TAG, ("Environment.MEDIA_MOUNTED :" + sdcardDir.absolutePath
                                + " R:" + sdcardDir.canRead() + " W:" + sdcardDir.canWrite())
                    )

                    if (sdcardDir.canWrite()) {
                        val dir = (sdcardDir.absolutePath
                                + File.separator + "factorytest")
                        val file = File(dir)
                        if (!file.exists()) {
                            Log.i(TAG, "getExternalRootDir, dir not exist and make dir")
                            file.mkdirs()
                        }
                        sRootDir = dir
                        return sRootDir
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sRootDir
    }

    /**
     * /storage/emulated/0/"packagename"/"dirName"
     *
     * @param context Context
     * @param dirName relative path
     * @return full path
     */
    fun getExternalDir(context: Context?, dirName: String): String {
        val dir = getExternalRootDir(context) + File.separator + dirName
        val file = File(dir)
        if (!file.exists()) {
            Log.i(TAG, "getDir, dir not exist and make dir")
            file.mkdirs()
        }
        return dir
    }

    /**
     * /storage/emulated/0/Android/data/"packagename"/cache/"dirName"
     *
     * @param context Context
     * @param dirName relative path
     * @return full path
     */
    fun getExternalCacheDir(context: Context, dirName: String): String {
        var dir = ""
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            dir = (context.externalCacheDir?.absolutePath ?: "") + File.separator + dirName
            val file = File(dir)
            if (!file.exists()) {
                Log.i(TAG, "getExternalCacheDir, dir not exist and make dir")
                file.mkdirs()
            }
        }
        return dir
    }

    /**
     * /data/user/0/"packagename"/cache/"dirName"
     *
     * @param context Context
     * @param dirName relative path
     * @return full path
     */
    fun getCacheDir(context: Context, dirName: String): String {
        val dir = context.cacheDir.absolutePath + File.separator + dirName
        val file = File(dir)
        if (!file.exists()) {
            Log.i(TAG, "getCacheDir, dir not exist and make dir")
            file.mkdirs()
        }
        return dir
    }

    /**
     * Gets all external storage paths
     *
     * @param context context
     * @return storage paths
     */
    fun getStorageList(context: Context): List<String> {
        val paths = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getStorageVolumeList(context)
        } else {
            mountPathList
        }

        if (paths.isEmpty() && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            paths.add(
                Environment.getExternalStorageDirectory()
                    .absolutePath
            )
        }
        return paths
    }

    private val mountPathList: MutableList<String>
        /**
         * Gets all external storage paths, for lower than Android N
         *
         * @return storage paths
         */
        get() {
            val paths: MutableList<String> =
                ArrayList()

            try {
                val p = Runtime.getRuntime().exec("cat /proc/mounts")
                val inputStream =
                    BufferedInputStream(p.inputStream)
                val bufferedReader =
                    BufferedReader(InputStreamReader(inputStream))

                var line: String
                while ((bufferedReader.readLine().also { line = it }) != null) {
                    Log.i(TAG, "getMountPathList, $line")

                    // /data/media /storage/emulated/0 sdcardfs rw,nosuid,nodev,relatime,uid=1023,gid=1023 0 0
                    val temp = TextUtils.split(line, " ")
                    val result = temp[1]
                    val file = File(result)
                    if (file.isDirectory && file.canRead() && file.canWrite()) {
                        Log.d(
                            TAG,
                            "getMountPathList, add --> " + file.absolutePath
                        )
                        paths.add(result)
                    }

                    if (p.waitFor() != 0 && p.exitValue() == 1) {
                        Log.e(TAG, "getMountPathList, cmd execute failed!")
                    }
                }
                bufferedReader.close()
                inputStream.close()
            } catch (e: Exception) {
                Log.e(TAG, "getMountPathList, failed, $e")
            }

            return paths
        }

    /**
     * Gets all external storage paths, for higher than Android N
     *
     * @param context context
     * @return storage paths
     */
    private fun getStorageVolumeList(context: Context): MutableList<String> {
        val paths: MutableList<String> = ArrayList()
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        var volumes: List<StorageVolume>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            volumes = storageManager.storageVolumes
        }

        try {
            val storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")

            for (storageVolume in volumes!!) {
                val storagePath = getPath.invoke(storageVolume) as String
                val isRemovableResult = isRemovable.invoke(storageVolume) as Boolean
                var description: String? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    description = storageVolume.getDescription(context)
                }
                paths.add(storagePath)

                Log.d(
                    TAG, ("getStorageVolumeList, storagePath=" + storagePath
                            + ", isRemovableResult=" + isRemovableResult + ", description=" + description)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "getStorageVolumeList, failed, $e")
        }

        return paths
    }

    /**
     * Reboot
     *
     * @param context Context
     */
    fun reboot(context: Context) {
        val intent = Intent(Intent.ACTION_REBOOT)
        intent.putExtra("nowait", 1)
        intent.putExtra("interval", 1)
        intent.putExtra("window", 0)
        context.sendBroadcast(intent)
    }

    /**
     * Shutdown
     *
     * @param context Context
     */
    fun shutdown(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN")
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * Start the third-party application
     *
     * @param context     Context
     * @param packageName PackageName
     */
    fun startApp(context: Context, packageName: String) {
        val intent = context.packageManager
            .getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            Log.e(TAG, "startApp, Package does not exist.")
        }
    }

    /**
     * Mute
     *
     * @param context Context
     */
    fun mute(context: Context) {
        if (sAudioManager == null) {
            sAudioManager = context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager
        }
        sAudioManager!!.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_MUTE, 0
        )
    }

    /**
     * Unmute
     *
     * @param context Context
     */
    fun unmute(context: Context) {
        if (sAudioManager == null) {
            sAudioManager = context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager
        }
        sAudioManager!!.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_UNMUTE, 0
        )
    }

    val freeStorageSize: Long
        /**
         * 获取剩余存储空间大小
         *
         * @return 剩余存储空间大小（单位：Kb）
         */
        get() {
            val statFs =
                StatFs(Environment.getExternalStorageDirectory().path)
            val blockSize = statFs.blockSize.toLong()
            val availableCount = statFs.availableBlocks.toLong()
            return availableCount * blockSize / 1024
        }

    /**
     * 将时间转换为时间戳
     *
     * @param dateStr 格式化日期
     * @param format  例如："yyyy-MM-dd HH:mm:ss"
     * @return 时间戳
     * @throws ParseException
     */
    @SuppressLint("SimpleDateFormat")
    @Throws(ParseException::class)
    fun dateToStamp(dateStr: String?, format: String): Long {
        val simpleDateFormat = SimpleDateFormat(format)
        val date = simpleDateFormat.parse(dateStr)
        return date.time
    }

    /**
     * 将时间戳转换为时间
     *
     * @param timestamp 时间戳
     * @param format    例如："yyyy-MM-dd HH:mm:ss"
     * @return 格式化日期
     */
    @SuppressLint("SimpleDateFormat")
    fun stampToDate(timestamp: Long, format: String): String {
        val simpleDateFormat = SimpleDateFormat(format)
        val date = Date(timestamp)
        return simpleDateFormat.format(date)
    }

    val suAlias: String
        /**
         * 获取su命令别名（个别项目会重命名su）
         *
         * @return su别名
         */
        get() {
            if (TextUtils.isEmpty(sSuAlias)) {
                sSuAlias = getProperty("ro.su_alias", "su")
            }
            return sSuAlias
        }
}
