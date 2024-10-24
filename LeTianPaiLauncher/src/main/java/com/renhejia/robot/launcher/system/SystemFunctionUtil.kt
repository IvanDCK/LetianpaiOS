package com.renhejia.robot.launcher.system

import android.app.AlarmManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.provider.Settings
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.launcher.R
import java.io.IOException
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import kotlin.math.min

/**
 * @author liujunbin
 */
class SystemFunctionUtil {
    var wakeLock: WakeLock? = null

    companion object {
        /**
         * 关机
         *
         * @param context
         */
        fun shutdownRobot(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val clazz: Class<*> = pm.javaClass
            try {
                val shutdown = clazz.getMethod(
                    "shutdown",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                shutdown.invoke(pm, false, "shutdown", false)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /**
         * 重启
         *
         * @param context
         */
        fun reboot(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            Log.i("responseReboot", "responseReboot ===== 3 ==== ")
            if (pm != null) {
                Log.i("responseReboot", "responseReboot ===== 4 ==== ")
                pm.reboot(null)
            }
        }


        /**
         * 清理用户数据
         *
         * @param packageName
         * @return
         */
        fun clearAppUserData(packageName: String): Process? {
            val p = execRuntimeProcess("pm clear $packageName")
            if (p == null) {
                logi(
                    "Letianpai", ("Clear app data packageName:" + packageName
                            + ", FAILED !")
                )
            } else {
                logi(
                    "Letianpai", ("Clear app data packageName:" + packageName
                            + ", SUCCESS !")
                )
            }
            return p
        }

        /**
         * @param commond
         * @return
         */
        fun execRuntimeProcess(commond: String): Process? {
            var p: Process? = null
            try {
                p = Runtime.getRuntime().exec(commond)
                logi(
                    "Letianpai",
                    "exec Runtime commond:$commond, Process:$p"
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return p
        }

        /**
         * 重启
         *
         * @param context
         */
        fun screenOff(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val clazz: Class<*> = pm.javaClass
            try {
                val shutdown = clazz.getMethod(
                    "reboot",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                shutdown.invoke(pm, false, "reboot", false)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        /**
         * 重启
         *
         * @param context
         */
        fun screenOn(context: Context) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val clazz: Class<*> = pm.javaClass
            try {
                val shutdown = clazz.getMethod(
                    "reboot",
                    Boolean::class.javaPrimitiveType,
                    String::class.java,
                    Boolean::class.javaPrimitiveType
                )
                shutdown.invoke(pm, false, "reboot", false)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        //    /**
        //     * 重启
        //     * @param context
        //     */
        //    public static void goToSleep(Context context) {
        //        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        //        Class clazz = pm.getClass();
        //        try {
        //            Method shutdown = clazz.getMethod("goToSleep", long.class, String.class);
        //            long current = System.currentTimeMillis()+ 5000;
        //            shutdown.invoke(pm, current, "goToSleep");
        //
        //        } catch (Exception ex) {
        //            ex.printStackTrace();
        //        }
        //
        //
        //    }
        /**
         * 关闭屏幕 ，其实是使系统休眠
         */
        fun goToSleep(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            try {
                powerManager.javaClass.getMethod(
                    "goToSleep", *arrayOf<Class<*>?>(
                        Long::class.javaPrimitiveType
                    )
                ).invoke(powerManager, SystemClock.uptimeMillis())
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }

        /**
         * 关闭屏幕 ，但是系统不休眠
         */
        fun goToSleep1(context: Context) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock =
                powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyApp::MyWakelockTag")
            wakeLock.acquire()
        }
        private var wakeLock: PowerManager.WakeLock? = null

        /**
         * 唤醒屏幕
         *
         * @param context
         */
        fun wakeUp(context: Context) {
            //val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            try {
                /*
                powerManager.javaClass.getMethod(
                    "wakeUp", *arrayOf<Class<*>?>(
                        Long::class.javaPrimitiveType
                    )
                ).invoke(powerManager, SystemClock.uptimeMillis())
                 */
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerManager.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "LauncherActivity::WakelockTag"
                )

                wakeLock!!.acquire(10 * 60 * 1000L /*10 minutes*/)

                wakeLock!!.release()

            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }

        /**
         * 设置背光亮度
         *
         * @param context
         * @param brightness The brightness value from 0 to 255.
         */
        fun setBacklightBrightness(context: Context, brightness: Int) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            try {
//            powerManager.getClass().getMethod("setBacklightBrightness", new Class[]{int.class}).invoke(powerManager, brightness);
                powerManager.javaClass.getMethod(
                    "setBacklightBrightness", *arrayOf<Class<*>>(
                        Int::class.java
                    )
                ).invoke(powerManager, brightness)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            }
        }

        /**
         * 设置东八区时区
         *
         * @param context
         */
        fun setTimeZone(context: Context) {
//        ((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).setTimeZone("Asia/Tokyo");
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setTimeZone("Asia/Shanghai")
        }

        /**
         * 设置东八区时区
         *
         * @param context
         */
        fun setTimeZone(context: Context, timeZone: String?) {
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setTimeZone(timeZone)
        }

        /**
         * 设置时区
         *
         * @param context
         */
        fun set24HourFormat(context: Context) {
            Settings.System.putString(
                context.contentResolver,
                Settings.System.TIME_12_24, "24"
            )
        }

        // TODO 音量调节
        // TODO 屏幕关闭
        // TODO 点亮屏幕
        //
        /**
         * @param version1
         * @param version2
         * @return
         * @a
         */
        fun compareVersion(version1: String, version2: String): Boolean {
            // 切割点 "."；
            val versionArray1 =
                version1.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val versionArray2 =
                version2.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var idx = 0
            // 取最小长度值
            val minLength =
                min(versionArray1.size.toDouble(), versionArray2.size.toDouble()).toInt()
            var diff = 0
            // 先比较长度 再比较字符
            while (idx < minLength && ((versionArray1[idx].length - versionArray2[idx].length).also {
                    diff = it
                }) == 0 && (versionArray1[idx].compareTo(
                    versionArray2[idx]
                ).also { diff = it }) == 0
            ) {
                ++idx
            }
            // 如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
            diff = if ((diff != 0)) diff else versionArray1.size - versionArray2.size
            return diff > 0
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm == null) {
            } else {
                //如果仅仅是用来判断网络连接
                //则可以使用 cm.getActiveNetworkInfo().isAvailable();
                val info = cm.allNetworkInfo
                if (info != null) {
                    for (i in info.indices) {
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        val btAddressByReflection: String?
            /**
             * 获取蓝牙地址
             *
             * @return
             */
            get() {
                val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                var field: Field? = null
                try {
                    field = BluetoothAdapter::class.java.getDeclaredField("mService")
                    field.isAccessible = true
                    val bluetoothManagerService = field[bluetoothAdapter] ?: return null
                    val method =
                        bluetoothManagerService.javaClass.getMethod("getAddress")
                    if (method != null) {
                        val obj = method.invoke(bluetoothManagerService)
                        if (obj != null) {
                            return obj.toString()
                        }
                    }
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
                return null
            }

        /**
         * 获取当前wifi名字
         *
         * @param context
         * @return
         */
        fun getConnectWifiSsid(context: Context): String {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            return wifiInfo.ssid
        }

        // 5分19帧   7秒19帧
        //bitmap = rsBlur(mContext,bitmap,10,1);
        /**
         * 高斯模糊
         *
         * @param context
         * @param source
         * @param radius
         * @param scale
         * @return
         */
        private fun rsBlur(
            context: Context,
            source: Bitmap?,
            radius: Float,
            scale: Float
        ): Bitmap? {
            if (source == null) {
                return null
            }
            val scaleWidth = (source.width * scale).toInt()
            val scaleHeight = (source.height * scale).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(
                source, scaleWidth,
                scaleHeight, false
            )

            val inputBitmap = scaledBitmap
            Log.i("RenderScriptActivity", "size:" + inputBitmap.width + "," + inputBitmap.height)

            //创建RenderScript
            val renderScript = RenderScript.create(context)

            //创建Allocation
            val input = Allocation.createFromBitmap(
                renderScript,
                inputBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            val output = Allocation.createTyped(renderScript, input.type)

            //创建ScriptIntrinsic
            val intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

            intrinsicBlur.setInput(input)

            intrinsicBlur.setRadius(radius)

            intrinsicBlur.forEach(output)

            output.copyTo(inputBitmap)

            renderScript.destroy()

            return inputBitmap
        }

        private fun drawableToBitamp(drawable: Drawable): Bitmap {
            //声明将要创建的bitmap
            var bitmap: Bitmap? = null
            //获取图片宽度
            val width = drawable.intrinsicWidth
            //获取图片高度
            val height = drawable.intrinsicHeight
            //图片位深，PixelFormat.OPAQUE代表没有透明度，RGB_565就是没有透明度的位深，否则就用ARGB_8888。详细见下面图片编码知识。
            val config =
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            //创建一个空的Bitmap
            bitmap = Bitmap.createBitmap(width, height, config)
            //在bitmap上创建一个画布
            val canvas = Canvas(bitmap)
            //设置画布的范围
            drawable.setBounds(0, 0, width, height)
            //将drawable绘制在canvas上
            drawable.draw(canvas)
            return bitmap
        }

        fun getBitmap(context: Context): Bitmap? {
            //        Bitmap bitmap = drawableToBitamp(context.getDrawable(R.drawable.test_background));

            val bitmap = drawableToBitamp(
                context.getDrawable(R.drawable.time)!!
            )

            return rsBlur(context, bitmap, 10f, 1f)
        }
    }
}
