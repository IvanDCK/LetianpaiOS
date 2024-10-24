package com.renhejia.robot.launcher.displaymode

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.renhejia.robot.launcher.displaymode.calendar.CalendarNoticeView
import com.renhejia.robot.launcher.displaymode.callback.DisplayInfoUpdateCallback
import com.renhejia.robot.launcher.displaymode.display.DisplayMode
import com.renhejia.robot.launcher.displaymode.eventcountdown.EventCountdownView
import com.renhejia.robot.launcher.displaymode.fans.FansView
import com.renhejia.robot.launcher.displaymode.time.TimeView
import java.lang.ref.WeakReference

/**
 * @author liujunbin
 */
class DisplayModeView : ViewPager {
    private var mContext: Context? = null
    private var adapter: DisplayViewpagerAdapter? = null
    private val displayViews: MutableList<View> = ArrayList()
    private val fansView: FansView? = null
    private var timeView: TimeView? = null
    private val noticeView: CalendarNoticeView? = null
    private val eventCountdownView: EventCountdownView? = null
    private var currentIndex = 1
    private val showPosition = 0
    private var displayModeView: DisplayModeView? = null
    private val realCount = 5
    private var mHandler: UpdateViewHandler? = null
    private var displayModes: DisplayMode? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    val viewSize: Int
        get() = displayViews.size

    fun getView(i: Int): View? {
        return if (i < displayViews.size) {
            displayViews[i]
        } else {
            null
        }
    }

    private fun init(context: Context) {
        displayModeView = this@DisplayModeView
        this.mContext = context
        this.setOverScrollMode(OVER_SCROLL_NEVER)
        mHandler = UpdateViewHandler(context)
        requestData()
        initView()
        fillData()
        addListeners()
        //addPageChangeListener();
    }

    private fun addListeners() {
        DisplayInfoUpdateCallback.Companion.instance
            .seDisplayInfoUpdateListener(object :
                DisplayInfoUpdateCallback.DisplayInfoUpdateListener {
                override fun updateDisplayList(displayMode: DisplayMode?) {
                    updateViewData(displayMode)
                    uploadStatus()
                }
            })
    }

    private fun updateViewData(displayMode: DisplayMode?) {
        this.displayModes = displayMode
    }


    private fun updateViews(displayMode: DisplayMode?) {
        if (displayMode == null) {
            return
        }
        displayViews.clear()

        if (displayMode.general_display_switch == 1) {
            timeView?.let { displayViews.add(it) }
        }
//        if (displayMode.getCalendar_display_switch() == 1){
//            displayViews.add(noticeView);
//        }
        if (displayMode.countdown_display_switch == 1) {
            eventCountdownView?.let { displayViews.add(it) }
        }
        if (displayMode.fans_display_switch == 1) {
            fansView?.let { displayViews.add(it) }
        }

        fillData()
        adapter?.notifyDataSetChanged()
    }


    private fun addPageChangeListener() {
        displayModeView?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                currentIndex = position
                val showPosition = if (position == 0) {
                    realCount
                } else if (position == realCount + 1) {
                    1
                } else {
                    position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager.SCROLL_STATE_IDLE -> {
                        Log.e("letianpai_1111", "currentIndex: $currentIndex")
                        if (currentIndex == 0) {
                            displayModeView!!.setCurrentItem(displayViews.size - 2, false)
                        } else if (currentIndex == displayViews.size - 1) {
                            displayModeView!!.setCurrentItem(1, false)
                        }
                    }

                    SCROLL_STATE_DRAGGING -> if (currentIndex == realCount + 1) {
                        displayModeView!!.setCurrentItem(1, false)
                    } else if (currentIndex == 0) {
                        displayModeView!!.setCurrentItem(displayViews.size - 1, false)
                    }

                    SCROLL_STATE_SETTLING -> {}
                    else -> {}
                }
            }
        })
    }

    private fun requestData() {
        // 请求数据
    }

    private fun fillData() {
//        currentIndex = 1;
//        displayViews.add(0,displayViews.get(displayViews.size()-1));
//        displayViews.add(displayViews.get(1));
//        //

        adapter = DisplayViewpagerAdapter(displayViews)
        displayModeView?.setAdapter(adapter)
        displayModeView?.refreshDrawableState()

        //        displayModeView.setCurrentItem(1);
    }

    private fun initView() {
//        displayViews.add(new )

//        fansView = new FansView(mContext);

        timeView = mContext?.let { TimeView(it) }

        //        noticeView = new CalendarNoticeView(mContext);
//        weatherView = new WeatherView(mContext);
////        weatherView.setDisplayModeView(DisplayModeView.this);
//        eventCountdownView = new EventCountdownView(mContext);

//        displayViews.add(fansView);
//        displayViews.add(timeView);
//        displayViews.add(weatherView);
////        displayViews.add(noticeView);
//        displayViews.add(eventCountdownView);
//        displayViews.add(fansView);
        timeView?.let { displayViews.add(it) }

        //        displayViews.add(0,displayViews.get(displayViews.size()-1));
//        displayViews.add(displayViews.get(1));
    }

    private fun uploadStatus() {
        val message = Message()
        message.what = UPDATE_STATUS
        mHandler!!.sendMessage(message)
    }

    private inner class UpdateViewHandler(context: Context) : Handler() {
        private val context =
            WeakReference(context)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                UPDATE_STATUS -> {}
            }
        }
    }


    companion object {
        private const val UPDATE_STATUS = 110
    }
}
