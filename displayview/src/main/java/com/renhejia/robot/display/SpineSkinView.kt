package com.renhejia.robot.display

import android.content.Context
import android.database.ContentObserver
import android.graphics.Point
import android.os.Handler
import android.provider.Settings
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.Toast
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.display.SpineSkinView
import com.renhejia.robot.display.callback.RobotPlatformInfoUpdateCallback.Companion.instance
import com.renhejia.robot.display.callback.RobotPlatformInfoUpdateCallback.SpinePlatformListener
import com.renhejia.robot.display.manager.LauncherRobotSkinInfoManager.Companion.getInstance
import com.renhejia.robot.display.utils.SpineSkinUtils

class SpineSkinView : ViewGroup {
    private var mResPool: SpineSkinResPool? = null

    private var mVideoView: SpineVideoView? = null
    private var mClockView: RobotClockView? = null

    //private SpineLottieView mLottieView;
    private val mAnimType = ANIM_MP4
    private var m12HourFormat = false
    private var mContext: Context
    private var infoItem: RobotSkinInfoItem? = null
    private var skinPathName: String? = null
    private val isRegisterMode = false


    constructor(context: Context) : super(context) {
        this.mContext = context
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context

        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    //ToDo 临时需求开发 对架构不熟悉，需要对公用代码进行重用重构
    var mSettingsObserver: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            //            LogUtil.d(TAG, "is24HourFormat onChanged:" + Settings.System.getString(getContext().getContentResolver(), Settings.System.TIME_12_24));
            set12HourFormat(!DateFormat.is24HourFormat(context))
            postInvalidate()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //        LogUtil.d(TAG, "registerContentObserver.....");
        context.contentResolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.TIME_12_24),
            true,
            mSettingsObserver
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //        LogUtil.d(TAG, "unregisterContentObserver.....");
        context.contentResolver.unregisterContentObserver(mSettingsObserver)
    }

    fun set12HourFormat(is12HourFormat: Boolean) {
        m12HourFormat = is12HourFormat
        if (mClockView != null) {
            mClockView!!.hourFormat = (if (m12HourFormat) RobotClockView.Companion.SPINE_CLOCK_HOURS_12 else RobotClockView.Companion.SPINE_CLOCK_HOURS_24)
        }
    }

    fun is12HourFormat(): Boolean {
        return m12HourFormat
    }

    fun initView() {
        infoItem = getInstance(this.context).spineSkinInfoItem
        mResPool = SpineSkinResPool(this.context)
        mClockView = RobotClockView(this.context)
        mVideoView = SpineVideoView(this.context)

        //
////        mLottieView = new SpineLottieView(this.getContext());
//        setWatchClockStatus(infoItem);
//        if (!LauncherDifferenceUtil.isLowMemoryDevice() && mVideoView != null) {
//            this.addView(mVideoView);
//        }
        this.addView(mClockView)
        //[niu][20191211]Format 12/24 hour time base on system setting
        m12HourFormat = !DateFormat.is24HourFormat(context)
        //--end [niu][20191211]....
        set12HourFormat(m12HourFormat)
        initPlatformListener()
    }


    private fun unregisterPlatformListener() {
    }

    private fun registerPlatformListener() {
    }

    private fun setWatchClockStatus(infoItem: RobotSkinInfoItem) {
        val platState = RobotPlatformState()

        //        platState.airQuality = infoItem.getWeatherInfoItem().getAirQuality();
//        platState.currentTemp = infoItem.getWeatherInfoItem().getCurrentTemp();
//        platState.weatherState = infoItem.getWeatherInfoItem().getWeatherState();
        platState.batteryLevel = infoItem.getBatteryLevel()
        platState.batteryCharging = infoItem.getChargingStates()
        platState.bluetoothEnabled = infoItem.getBluetoothStatus()
        platState.mediaVolume = infoItem.getVolume()
        platState.stepNumber = infoItem.getStepCount()
        platState.wifiEnabled = infoItem.getWifiStatus()
        mClockView!!.updateAll(platState)
    }


    fun loadSkin(pathName: String) {
        skinPathName = pathName

        if (!mResPool!!.isValidSpineSkin(pathName)) {
            skinPathName = SpineSkinUtils.getDefaultSkin(mContext)
        }
        logi("Clock_Skin", "skinPathName: $skinPathName")

        mResPool!!.reset()
        val skin = mResPool!!.createSkin(skinPathName)

        if (skin != null) {
            mClockView!!.setSkin(skin)
            infoItem = getInstance(this.context).spineSkinInfoItem
            setWatchClockStatus(infoItem!!)
            mClockView!!.invalidate()

            //            if (LauncherDifferenceUtil.isLowMemoryDevice()) {
//                set12HourFormat(m12HourFormat);
//            } else {
//                mVideoView.setAssetsPath(skinPathName, skin.getVideoTotal());
//                set12HourFormat(m12HourFormat);
//                if (skin.getVideoTotal() != 0) {
//                    mVideoView.playRand();
//                }
//            }
        }
    }

    fun setRefreshStatus(isRefresh: Boolean) {
        mClockView!!.setRefreshNow(isRefresh)
    }


    //    public void loadCustomSkin(String pathName) {
    //        skinPathName = pathName;
    //
    //        if (!mResPool.isValidCustomSkin(pathName)) {
    //            skinPathName = SpineSkinUtils.getDefaultSkin(mContext);
    //        }
    //        LogUtils.logi("Clock_Skin", "skinPathName: " + skinPathName);
    //
    //        mResPool.reset();
    //        SpineClockSkin skin = mResPool.createSkin(skinPathName);
    //
    //        if (skin != null) {
    //            mClockView.setSkin(skin);
    //            infoItem = LauncherSpineSkinInfoManager.getInstance(this.getContext()).getSpineSkinInfoItem();
    //            setWatchClockStatus(infoItem);
    //            mClockView.invalidate();
    //            if (LauncherDifferenceUtil.isLowMemoryDevice()) {
    //                set12HourFormat(m12HourFormat);
    //            } else {
    //                mVideoView.setAssetsPath(skinPathName, skin.getVideoTotal());
    //                set12HourFormat(m12HourFormat);
    //                if (skin.getVideoTotal() != 0) {
    //                    mVideoView.playRand();
    //                }
    //            }
    //
    //        }
    //    }
    fun playAnim(pt: Point?) {
//        if (LauncherDifferenceUtil.isLowMemoryDevice()) {
//            return;
//        }

        if (mVideoView!!.parent != null) {
            if (mVideoView!!.isPlayVideo()) {
                mVideoView!!.stopVideo()
            }
            mVideoView!!.playRand()
        }
    }

    fun onClick(pt: Point?) {
//        if (LauncherDifferenceUtil.isLowMemoryDevice()) {
//            return;
//        }

        if (mVideoView!!.parent != null) {
            if (mVideoView!!.isPlayVideo()) {
                mVideoView!!.stopVideo()
            }
            mVideoView!!.playNext()
        }
    }

    fun onDoubleClick(pt: Point) {
        Toast.makeText(context, "onDoubleClick x=" + pt.x + ", y=" + pt.y, Toast.LENGTH_SHORT)
            .show()
    }

    fun onLongPress(pt: Point?) {
//        this.removeAllViews();
//
//        switch (mAnimType) {
//            case ANIM_GIF:
//                mAnimType = ANIM_MP4;
//                this.addView(mVideoView);
//                this.addView(mClockView);
//                Toast.makeText(getContext(), "switch to mp4", Toast.LENGTH_SHORT).show();
//                break;
//            case ANIM_MP4:
//                mAnimType = ANIM_LOTTIE;
//                this.addView(mLottieView);
//                this.addView(mClockView);
//                Toast.makeText(getContext(), "switch to lottie", Toast.LENGTH_SHORT).show();
//                break;
//            case ANIM_LOTTIE:
//                mAnimType = ANIM_GIF;
//                this.addView(mClockView);
//                Toast.makeText(getContext(), "switch to gif", Toast.LENGTH_SHORT).show();
//                break;
//        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidth, measureHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.layout(0, 0, r, b)
        }
    }

    /**
     * 返回对应控件ID
     *
     * @param x
     * @param y
     * @return
     */
    fun getCtrlId(x: Int, y: Int): Int {
        //TODO : 增加相关逻辑
        return 0
    }

    fun updateClockView() {
        if (mClockView != null) {
            mClockView!!.invalidate()
        }
    }


    /**
     * @param x
     * @param y
     * @return
     */
    fun onClick(x: Int, y: Int): Int {
//        int ctrlId = mClockView.getCtrlId(x, y);
//
//        if (ctrlId == SpineClockSkin.CTRL_NONE_ID) {
//            //mSpineSkinView.playNextAnim();
//        }

        return RobotClockSkin.Companion.CTRL_NONE_ID
    }


    private fun initPlatformListener() {
        instance.setSpinePlatformListener(object : SpinePlatformListener {
            override fun updateBluetoothEnabled(isBluetoothEnabled: Boolean) {
                mClockView!!.updateBluetoothEnabled(isBluetoothEnabled)
            }

            override fun updateBatteryLevel(batteryLevel: Int) {
                mClockView!!.updateBatteryLevel(batteryLevel)
            }

            override fun updateBatteryCharging(isBatteryCharging: Boolean) {
                mClockView!!.updateBatteryCharging(isBatteryCharging)
            }

            override fun updateWifiEnabled(isWifiEnabled: Boolean) {
                mClockView!!.updateWifiEnabled(isWifiEnabled)
            }

            override fun updateMediaVolume(batteryLevel: Int) {
                mClockView!!.updateMediaVolume(batteryLevel)
            }

            override fun updateStepNumber(stepNumber: Int) {
                mClockView!!.updateStepNumber(stepNumber)
            }

            override fun updateWeather(weatherState: Int, currentTemp: Int, airQuality: Int) {
                mClockView!!.updateWeather(weatherState, currentTemp, airQuality)
            }
        })
    }

    companion object {
        private val TAG: String = SpineSkinView::class.java.simpleName

        const val ANIM_GIF: Int = 0
        const val ANIM_MP4: Int = 1
        const val ANIM_LOTTIE: Int = 2
    }
}
