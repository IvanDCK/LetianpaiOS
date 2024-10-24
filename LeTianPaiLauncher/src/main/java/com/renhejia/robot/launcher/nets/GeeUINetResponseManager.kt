package com.renhejia.robot.launcher.nets

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.letianpai.robot.notice.general.parser.GeneralInfo
import com.renhejia.robot.commandlib.consts.RobotRemoteConsts
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.commandlib.parser.displaymodes.calendar.CalenderInfo
import com.renhejia.robot.commandlib.parser.displaymodes.countdown.CountDownListInfo
import com.renhejia.robot.commandlib.parser.displaymodes.fans.FansInfo
import com.renhejia.robot.commandlib.parser.displaymodes.logo.LogoInfo
import com.renhejia.robot.commandlib.parser.displaymodes.weather.WeatherInfo
import com.renhejia.robot.guidelib.utils.SystemUtil.isRobotInChinese
import com.renhejia.robot.guidelib.wifi.WIFIConnectionManager.Companion.isNetworkAvailable
import com.renhejia.robot.launcher.displaymode.callback.CalendarNoticeInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.callback.CountDownInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.callback.DeviceChannelLogoCallBack
import com.renhejia.robot.launcher.displaymode.callback.DisplayInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.callback.FansInfoCallback
import com.renhejia.robot.launcher.displaymode.callback.GeneralInfoCallback
import com.renhejia.robot.launcher.displaymode.callback.WeatherInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.display.DisplayMode
import com.renhejia.robot.launcherbaselib.storage.manager.LauncherConfigManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * @author liujunbin
 */
class GeeUINetResponseManager private constructor(context: Context) {
    private var mContext: Context? = null
    private var gson: Gson? = null
    var fansInfo: FansInfo? = null
        get() {
            if (field == null) {
                fansInfos
            }
            return field
        }
    var weatherInfo: WeatherInfo? = null
        get() {
            if (field == null) {
                weather
            }

            return field
        }
    var generalInfo: GeneralInfo? = null
        get() {
            if (field == null) {
//            if (LocaleUtils.isChinese()){
                if (isRobotInChinese) {
                    getGeneralInfoList(true)
                } else {
                    getGeneralInfoList(false)
                }
            }
            return field
        }
    var countDownListInfo: CountDownListInfo? = null
        get() {
            if (field == null) {
                countDownList
            }
            return field
        }
    var calenderInfo: CalenderInfo? = null
        get() {
            if (field == null) {
                calendarList
            }
            return field
        }

    var logoInfo: LogoInfo? = null
        get() {
            if (isRobotInChinese) {
                getDeviceChannelLogo(true)
            } else {
                getDeviceChannelLogo(false)
            }

            return field
        }

    init {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        gson = Gson()
    }

    val displayInfo: Unit
        get() {
            if (isNetworkAvailable(mContext!!)) {
                updateGeneralInfo()
                //            updateWeather();
//            updateCalendarList();
//            updateCountDownList();
//            updateFansInfo();
            }
        }

    fun dispatchTask(cmd: String?, data: Any?) {
        Log.e("letianpai123456789", "commandData: ======= 2 ")
        if (cmd == null) {
            return
        }
        Log.e("letianpai123456789", "commandData: ======= 3 ")
        if (cmd == RobotRemoteConsts.COMMAND_TYPE_UPDATE_GENERAL_CONFIG) {
            updateGeneralInfo()
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_UPDATE_WEATHER_CONFIG) {
            updateWeather()
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_UPDATE_CALENDAR_CONFIG) {
            updateCalendarList()
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_UPDATE_COUNT_DOWN_CONFIG) {
            updateCountDownList()
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_UPDATE_FANS_CONFIG) {
            updateFansInfo()
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_APP_DISPLAY_SWITCH_CONFIG) {
            updateDisplayViews(data)
        } else if (cmd == RobotRemoteConsts.COMMAND_TYPE_CHANGE_SHOW_MODULE) {
            Log.e("letianpai", "changeShowModule === ======1")
            //responseChangeApp(data);
        }
    }

    private fun updateDisplayViews(data: Any?) {
        val displayMode = gson!!.fromJson(
            data as String?,
            DisplayMode::class.java
        )
        if (displayMode != null) {
            DisplayInfoUpdateCallback.instance.updateDisplayViewInfo(displayMode)
        }
    }

    /**
     *
     */
    fun updateGeneralInfo() {
        Thread {
            if (isRobotInChinese) {
                getGeneralInfoList(true)
            } else {
                getGeneralInfoList(false)
            }
        }.start()
    }

    /**
     *
     */
    fun updateWeather() {
        Thread { weatherInfo }.start()
    }

    /**
     *
     */
    private fun updateCalendarList() {
        Thread { calendarList }.start()
    }


    /**
     *
     */
    private fun updateFansInfo() {
        Thread { fansInfos }.start()
    }

    /**
     *
     */
    private fun updateCountDownList() {
        Thread { countDownList }.start()
    }


    private val weather: Unit
        get() {
            GeeUiNetManager.getWeatherInfo(mContext, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("letianpai123456789", "commandData: ======= 6 ")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        var weatherInfo: WeatherInfo? = null
                        var info = ""
                        if (response.body != null) {
                            info = response.body!!.string()
                        }
                        if (info != null) {
                            weatherInfo = gson!!.fromJson(info, WeatherInfo::class.java)
                            if (weatherInfo != null) {
                                logi(
                                    "letianpai_1234567",
                                    "weatherInfo: $weatherInfo"
                                )
                                WeatherInfoUpdateCallback.instance.updateWeather(weatherInfo)
                                LauncherConfigManager.getInstance(mContext!!)!!.robotWeather =
                                    weatherInfo.toString()
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            }
                        }
                    }
                }
            })
        }

    private val calendarList: Unit
        get() {
            GeeUiNetManager.getCalendarList(mContext, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        var calenderInfo: CalenderInfo? = null
                        var info = ""

                        if (response.body != null) {
                            info = response.body!!.string()
                            logi(
                                "letianpai",
                                "response: $response"
                            )
                        }
                        if (info != null) {
                            calenderInfo = gson!!.fromJson(
                                info,
                                CalenderInfo::class.java
                            )
                            if (calenderInfo != null) {
                                logi(
                                    "letianpai_1234567",
                                    "Calendar: $calenderInfo"
                                )
                                CalendarNoticeInfoUpdateCallback.instance.updateNotice(calenderInfo)
                                LauncherConfigManager.getInstance(mContext!!)!!.robotCalendar =
                                    calenderInfo.toString()
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            }
                        }
                    }
                }
            })
        }

    private val clockList: Unit
        get() {
            GeeUiNetManager.getClockList(mContext, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        Log.e("letianpai_123456", "response: $response")
                        Log.e(
                            "letianpai_123456",
                            "response11: " + response.body.toString()
                        )
                        val info = response.body!!.string()

                        if (info != null) {
                            Log.e(
                                "letianpai_1234567",
                                "getClockList: $info"
                            )
                            //                        fansInfo = new Gson().fromJson(info, FansInfo.class);
//                        Log.e("letianpai_1234", "FansInfo.getMsg(): " + fansInfo.getMsg());
//                        Log.e("letianpai_1234", "FansInfo.getCode(): " + fansInfo.getCode());
//                        Log.e("letianpai_1234", " fansInfo.getData()[0].getFans_count(): " + fansInfo.getData()[0].getFans_count());
//                        Log.e("letianpai_1234", " fansInfo.getData()[0].getAvatar(): " + fansInfo.getData()[0].getAvatar());
//                        Log.e("letianpai_1234", " fansInfo.getData()[0].getNick_name(): " + fansInfo.getData()[0].getNick_name());
                        } else {
                            Log.e("letianpai_123456", "info is null ")
                        }
                    }
                }
            })
        }

    private val fansInfos: Unit
        get() {
            GeeUiNetManager.getFansInfoList(mContext, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        var fansInfo: FansInfo? = null
                        val info = response.body!!.string()

                        if (info != null) {
                            fansInfo = gson!!.fromJson(info, FansInfo::class.java)
                            logi(
                                "letianpai_1234567",
                                "fansInfo: $fansInfo"
                            )
                            if (fansInfo != null) {
                                FansInfoCallback.instance.setFansInfo(fansInfo)
                                LauncherConfigManager.getInstance(mContext!!)!!.robotFansInfo =
                                    fansInfo.toString()
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            }
                        }
                    }
                }
            })
        }

    private fun getGeneralInfoList(isChinese: Boolean) {
        GeeUiNetManager.getGeneralInfoList(mContext!!, isChinese, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body != null) {
                    Log.e("letianpai_1234", "response: $response")
                    Log.e("letianpai_1234", "response11: " + response.body.toString())
                    var generalInfo2: GeneralInfo?
                    val info = response.body!!.string()

                    if (info != null) {
                        try {
                            generalInfo2 = Gson().fromJson(
                                info,
                                GeneralInfo::class.java
                            )
                            if (generalInfo2 != null) {
                                logi(
                                    "letianpai_1234567",
                                    "generalInfo: $generalInfo2"
                                )
                                GeneralInfoCallback.instance.setGeneralInfo(generalInfo2)
                                generalInfo = generalInfo2
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            } else {
                                Log.e("letianpai_1234", "generalInfo is null: ")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }


    private fun getDeviceChannelLogo(isChinese: Boolean) {
        GeeUiNetManager.getDeviceChannelLogo(mContext!!, isChinese, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.body != null) {
                    var generalInfo: LogoInfo? = null
                    val info = response.body!!.string()

                    if (info != null) {
                        try {
                            generalInfo = Gson().fromJson(info, LogoInfo::class.java)
                            if (generalInfo != null) {
                                logi(
                                    "letianpai_1234567",
                                    "getDeviceChannelLogo: $generalInfo"
                                )
                                DeviceChannelLogoCallBack.instance.setDeviceChannelLogo(generalInfo)
                                logoInfo = generalInfo
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            } else {
                                Log.e("letianpai_1234", "getDeviceChannelLogo is null: ")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    private val countDownList: Unit
        get() {
            GeeUiNetManager.getCountDownList(mContext, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        logi(
                            "letianpai_1234",
                            "getCountDownList_response: $response"
                        )
                        logi(
                            "letianpai_1234",
                            "getCountDownList_response11: " + response.body.toString()
                        )

                        var countDownListInfo: CountDownListInfo? = null
                        var info = ""

                        if (response.body != null) {
                            info = response.body!!.string()
                            logi(
                                "letianpai",
                                "response: info$info"
                            )
                        }
                        if (info != null) {
                            countDownListInfo = gson!!.fromJson(
                                info,
                                CountDownListInfo::class.java
                            )
                            if (countDownListInfo != null) {
                                logi(
                                    "letianpai_1234567",
                                    "countDownListInfo: $countDownListInfo"
                                )
                                CountDownInfoUpdateCallback.instance.updateCountDown(
                                    countDownListInfo
                                )
                                LauncherConfigManager.getInstance(mContext!!)!!.robotCountDown =
                                    countDownListInfo.toString()
                                LauncherConfigManager.getInstance(mContext!!)!!.commit()
                            }
                        }
                    }
                }
            })
        }


    companion object {
        private var instance: GeeUINetResponseManager? = null
        fun getInstance(context: Context): GeeUINetResponseManager {
            synchronized(GeeUINetResponseManager::class.java) {
                if (instance == null) {
                    instance = GeeUINetResponseManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}
