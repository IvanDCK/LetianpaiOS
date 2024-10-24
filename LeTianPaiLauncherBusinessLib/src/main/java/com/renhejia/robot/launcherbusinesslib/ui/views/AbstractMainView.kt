package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup

abstract class AbstractMainView : ViewGroup {
    @JvmField
    protected var mContext: Context? = null
    var FLIT_ANIMATION_TIME: Int = 60

    private var mGestureView: MLGestureView? = null

    private var mRightView: View? = null
    private var mBottomView: View? = null
    private var mTopView: View? = null //系统信息页
    private var mLeftView: View? = null
    protected var mDesktopView: DesktopView? = null
    private var currentView: View? = null

    private val hadUpdateAppList = false
    private val PREFERENCE_KEY_IN_HOME_SCREEN = "watch_status_in_home_screen"

    private var isLeftViewDisable = false
    private var isRightViewDisable = false
    private var isTopViewDisable = false
    private var isBottomViewDisable = false

    //    private RelativeLayout mLeftView;
    private var mScrollView: View? = null
    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    protected fun init(context: Context?) {
        this.mContext = context
        initView()
        addListeners()
        addHomeListener()
    }

    private fun addHomeListener() {
//        HomeKeyCallback.getInstance().registerHomeKeyListener(new HomeKeyCallback.HomeKeyPressedListener() {
//            @Override
//            public void onHomeKeyPressed(boolean isDisableMode) {
//                if (mHeight != 0 && mWidth != 0) {
//                    if (isDisableMode) {
//                        resetViews();
//                    } else {
//                        if ((FunctionUtils.isLauncherRunningForeground(mContext))) {
//                            resetViews();
//                        }
//                    }
//                }
//            }
//        });
    }

    /**
     * 初始化页面
     */
    protected fun resetViews() {
        mTopView!!.y = -mHeight.toFloat()
        mRightView!!.x = mWidth.toFloat()
        mLeftView!!.x = -mWidth.toFloat()
        mBottomView!!.y = mHeight.toFloat()

        Handler().postDelayed({ setWatchDesktopStatus(true) }, 500)


        //                    mTopView.animate().setDuration(FLIT_ANIMATION_TIME);
//                    mTopView.animate().translationY(-mHeight);
//
//                    mRightView.animate().setDuration(FLIT_ANIMATION_TIME);
//                    mRightView.animate().translationX(mWidth);
//
//                    mLeftView.animate().setDuration(FLIT_ANIMATION_TIME);
//                    mLeftView.animate().translationX(-mWidth);
//
//                    mBottomView.animate().setDuration(FLIT_ANIMATION_TIME);
//                    mBottomView.animate().translationY(mHeight);
    }

    /**
     * 初始所有页面
     */
    protected fun resetAllViews() {
        mGestureView = MLGestureView(mContext)
        mGestureView!!.setDelegate(this@AbstractMainView)

        fillWatchViews()
        addViews()
        resetViews()
    }

    private fun addViews() {
        addView(mGestureView)
        addView(mDesktopView)
        addView(mRightView)
        addView(mLeftView)
        addView(mTopView)
        addView(mBottomView)
    }

    private fun addListeners() {
//        WatchSpineClockViewUpdateCallback.getInstance().setWatchSpineClockViewUpdateListener(new WatchSpineClockViewUpdateCallback.WatchSpineClockViewUpdateListener() {
//            @Override
//            public void onWatchSpineClockViewUpdate(boolean isSpine, String skinPath) {
//                if (skinPath != null) {
//                    responseChangedSpineSkin(skinPath);
//                }
//            }
//        });
//
//        UnreadMessageClickCallback.getInstance().setUnreadMessageIconClickListener(new UnreadMessageClickCallback.UnreadMessageClickListener() {
//            @Override
//            public void onMessageIconClicked() {
//                responseUnreadMessageIconOnClicked();
//                showMessageView();
//            }
//        });
//
//
//        WatchViewUpdateCallback.getInstance().setWatchViewUpdateListener(new WatchViewUpdateCallback.WatchViewUpdateListener() {
//            @Override
//            public void onWatchViewUpdate() {
//                removeAllViews();
//                resetAllViews();
//            }
//        });
    }

    /**
     * 响应未读消息图标点击事件
     */
    protected fun responseUnreadMessageIconOnClicked() {
    }

    /**
     * 响应换肤
     *
     * @param skinPath
     */
    protected abstract fun responseChangedSpineSkin(skinPath: String?)

    protected fun initView() {
        mGestureView = MLGestureView(mContext)
        mGestureView!!.setDelegate(this@AbstractMainView)

        fillWatchViews()
        addViews()
    }

    /**
     * 填充5个页面
     */
    protected abstract fun fillWatchViews()

    protected fun setDesktopView(desktopView: DesktopView?) {
        this.mDesktopView = desktopView
    }

    protected fun setLeftView(leftView: View?) {
        this.mLeftView = leftView
    }

    protected fun setRightView(rightView: View?) {
        this.mRightView = rightView
    }

    protected fun setTopView(topView: View?) {
        this.mTopView = topView
    }

    protected fun setBottomView(bottomView: View?) {
        this.mBottomView = bottomView
    }

    protected fun setLeftViewDisable(leftViewStatus: Boolean) {
        this.isLeftViewDisable = leftViewStatus
    }

    protected fun setRightViewDisable(rightViewStatus: Boolean) {
        this.isRightViewDisable = rightViewStatus
    }

    protected fun setTopViewDisable(topViewStatus: Boolean) {
        this.isTopViewDisable = topViewStatus
    }

    protected fun setBottomViewDisable(bottomViewStatus: Boolean) {
        this.isBottomViewDisable = bottomViewStatus
    }

    protected val isAnimationRunning: Boolean
        get() = mLeftView!!.hasTransientState() || mTopView!!.hasTransientState()
                || mBottomView!!.hasTransientState() || mRightView!!.hasTransientState()
                || (mRightView != null && mRightView!!.hasTransientState())

    fun onUnknown(pt: Point?) {
        if (isAnimationRunning) {
            return
        }

        //        if (mTopViewRight == null) {
//            if (mRightView.getX() != 0 && mLeftView.getX() != 0 &&
//                    mTopView.getY() != 0 && mBottomView.getY() != 0) {
////                mTopView.getY() != 0 && mBottomView.getY() != 0) {
//                //Toast.makeText(getContext(), "onUnknown x=" + pt.x + ", y=" + pt.y, Toast.LENGTH_SHORT).show();
//
//            }
//        } else {
//            if (mRightView.getX() != 0 && mLeftView.getX() != 0 &&
//                    mTopViewRight.getY() != 0 && mTopView.getY() != 0 && mBottomView.getY() != 0) {
////                mTopView.getY() != 0 && mBottomView.getY() != 0) {
//                //Toast.makeText(getContext(), "onUnknown x=" + pt.x + ", y=" + pt.y, Toast.LENGTH_SHORT).show();
//
//            }
//        }
    }

    fun onClick(pt: Point?) {
        if (isAnimationRunning) {
            return
        }

        if (mRightView!!.x != 0f && mLeftView!!.x != 0f && mTopView!!.y != 0f && mBottomView!!.y != 0f) {
            responseOnClick(pt)
        }
    }

    /**
     * 响应点击
     *
     * @param pt
     */
    protected abstract fun responseOnClick(pt: Point?)

    //    public void onDoubleClick(Point pt) {
    //        if (mLeftView.hasTransientState() || mTopView.hasTransientState()
    //                || mBottomView.hasTransientState() || mRightView.hasTransientState()) {
    //            return;
    //        }
    //
    //        if (mRightView.getX() != 0 && mLeftView.getX() != 0 &&
    //                mTopView.getY() != 0 && mBottomView.getY() != 0) {
    //            ////mClockView.onDoubleClick(pt);
    //        }
    //    }
    //
    //
    //    public void onLongPress(Point pt) {
    //
    //        if (isAnimationRunning()) {
    //            return;
    //        }
    //
    //        if (mRightView.getX() != 0 && mLeftView.getX() != 0 &&
    //                mTopView.getY() != 0 && mBottomView.getY() != 0) {
    //            responseLongPress();
    //        }
    //    }
    //
    fun onDoubleClick(pt: Point?) {
        if (mLeftView!!.hasTransientState() || mTopView!!.hasTransientState()
            || mBottomView!!.hasTransientState() || mRightView!!.hasTransientState()
        ) {
            return
        }

        if (mRightView!!.x != 0f && mLeftView!!.x != 0f && mTopView!!.y != 0f && mBottomView!!.y != 0f) {
            ////mClockView.onDoubleClick(pt);
        }
    }


    fun onLongPress(pt: Point?) {
        if (isAnimationRunning) {
            return
        }

        if (mRightView!!.x != 0f && mLeftView!!.x != 0f && mTopView!!.y != 0f && mBottomView!!.y != 0f) {
            responseLongPress()
        }
    }

    private fun responseLongPress() {
//        DevUtil.vibrate(mContext,300);
        responseLongPressOnWatchView()
    }

    /**
     * 响应长按
     */
    protected abstract fun responseLongPressOnWatchView()

    fun onFlit(direct: Int) {
        if (isAnimationRunning) {
            return
        }

        //checkListStatus();
        if (direct == MLGestureView.Companion.RIGHT_TO_LEFT) {
            if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
                if (mLeftView!!.x == 0f) {
                    mLeftView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mLeftView!!.animate().translationX(-mWidth.toFloat())

                    //TODO
                } else if (mRightView!!.x == mWidth.toFloat()) {
                    if (isRightViewDisable) {
                        return
                    }
                    mRightView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mRightView!!.animate().translationX(0f)
                    setWatchDesktopStatus(false)
                }
            }
        }

        if (direct == MLGestureView.Companion.LEFT_TO_RIGHT) {
            if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
                if (mRightView!!.x == 0f) {
                    mRightView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mRightView!!.animate().translationX(mWidth.toFloat())
                } else if (mLeftView!!.x == -mWidth.toFloat()) {
                    if (isLeftViewDisable) {
                        return
                    }

                    mLeftView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mLeftView!!.animate().translationX(0f)
                }
            }
        }

        if (direct == MLGestureView.Companion.UP_TO_DOWN) {
            if (mLeftView!!.x != 0f && mRightView!!.x != 0f) {
                if (mBottomView!!.y == 0f) {
                    mBottomView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mBottomView!!.animate().translationY(mHeight.toFloat())
                    updateMessageIconStatus()
                } else if (mTopView!!.y == -mHeight.toFloat()) {
                    if (isTopViewDisable) {
                        return
                    }
                    Log.e("view_test", "onFlit")
                    mTopView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mTopView!!.animate().translationY(0f)
                    setWatchDesktopStatus(false)
                    updateData()
                }
            }
        }

        if (direct == MLGestureView.Companion.DOWN_TO_UP) {
            if (mLeftView!!.x != 0f && mRightView!!.x != 0f) {
                if (mTopView!!.y == 0f) {
                    mTopView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                    mTopView!!.animate().translationY(-mHeight.toFloat())
                    setWatchDesktopStatus(true)
                } else if (mBottomView!!.y == mHeight.toFloat()) {
                    if (isBottomViewDisable) {
                        return
                    }
                    showMessageListView()
                }
            }
        }
    }

    protected fun showMessageListView() {
//        mBottomView.getMessageList();
        Log.d("unread_event", "showMessageListView ===>> mBottomView: $mBottomView")
        mBottomView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
        mBottomView!!.animate().translationY(0f)
        setWatchDesktopStatus(false)
    }

    fun resetPostion() {
        if (mTopView!!.y != 0f && mTopView!!.y != -mHeight.toFloat()) {
            mTopView!!.y = -mHeight.toFloat()
        }

        if (mRightView!!.x != 0f && mRightView!!.x != mWidth.toFloat()) {
            mRightView!!.x = mWidth.toFloat()
        }
        if (mLeftView!!.x != 0f && mLeftView!!.x != -mWidth.toFloat()) {
            mLeftView!!.x = -mWidth.toFloat()
        }

        if (mBottomView!!.y != 0f && mBottomView!!.y != mHeight.toFloat()) {
            mBottomView!!.y = mHeight.toFloat()
        }
    }

    fun onScrollBegin(direct: Int) {
        Log.e("view_test1", "onScrollBegin ==== 2+ direct: $direct")

        if (isAnimationRunning) {
            return
        }

        mScrollView = null

        if (direct == MLGestureView.Companion.RIGHT_TO_LEFT) {
            if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
                if (mLeftView!!.x == 0f) {
                    mScrollView = mLeftView
                    //                } else if (mAppListView.getX() == mWidth) {
//                    mScrollView = mAppListView;
                } else if (mRightView!!.x == mWidth.toFloat()) {
                    if (isRightViewDisable) {
                        return
                    }
                    mScrollView = mRightView
                }
            }
        }

        if (direct == MLGestureView.Companion.LEFT_TO_RIGHT) {
            if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
                if (mRightView!!.x == 0f) {
                    mScrollView = mRightView
                } else if (mLeftView!!.x == -mWidth.toFloat()) {
                    if (isLeftViewDisable) {
                        return
                    }
                    mScrollView = mLeftView
                }
            }
        }

        if (direct == MLGestureView.Companion.UP_TO_DOWN) {
            if (mLeftView!!.x != 0f && mRightView!!.x != 0f) {
                if (mBottomView!!.y == 0f) {
                    mScrollView = mBottomView
                } else if (mTopView!!.y == -mHeight.toFloat()) {
                    if (isTopViewDisable) {
                        return
                    }
                    Log.e("view_test", "onScrollBegin")
                    mScrollView = mTopView
                    updateData()
                }
            }
        }

        if (direct == MLGestureView.Companion.DOWN_TO_UP) {
            if (mLeftView!!.x != 0f && mRightView!!.x != 0f) {
                if (mTopView!!.y == 0f) {
                    mScrollView = mTopView
                } else if (mBottomView!!.y == mHeight.toFloat()) {
                    if (isBottomViewDisable) {
                        return
                    }
                    mScrollView = mBottomView
                }
            }
        }
    }

    fun onScrolling(direct: Int, pt: Point) {
        if (mScrollView == null || isAnimationRunning) {
            return
        }

        if (direct == MLGestureView.Companion.RIGHT_TO_LEFT) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = pt.x.toFloat()
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = (pt.x - mWidth).toFloat()
            }
        }

        if (direct == MLGestureView.Companion.LEFT_TO_RIGHT) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = pt.x.toFloat()
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = (pt.x - mWidth).toFloat()
            }
        }

        if (direct == MLGestureView.Companion.UP_TO_DOWN) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = pt.y.toFloat()
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = (pt.y - mHeight).toFloat()
                Log.e("view_test", "onScrolling")
            }
        }


        if (direct == MLGestureView.Companion.DOWN_TO_UP) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = pt.y.toFloat()
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = (pt.y - mHeight).toFloat()
            }
        }
    }

    fun onScrollFinish(direct: Int) {
        if (mScrollView == null || isAnimationRunning) {
            return
        }

        if (direct == MLGestureView.Companion.RIGHT_TO_LEFT) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = 0f
                currentView = mRightView
                setWatchDesktopStatus(false)
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = -mWidth.toFloat()
                currentView = null
                setWatchDesktopStatus(true)
            }
            //printView(currentView);
        }

        if (direct == MLGestureView.Companion.LEFT_TO_RIGHT) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = mWidth.toFloat()
                currentView = null
                setWatchDesktopStatus(true)
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = 0f
                currentView = mLeftView
                setWatchDesktopStatus(false)
            }
            //printView(currentView);
        }

        if (direct == MLGestureView.Companion.UP_TO_DOWN) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = mHeight.toFloat()
                currentView = null
                setWatchDesktopStatus(true)
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = 0f
                Log.e("view_test", "onScrollFinish")
                currentView = mTopView
                setWatchDesktopStatus(false)
            }
            //printView(currentView);
        }

        if (direct == MLGestureView.Companion.DOWN_TO_UP) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = 0f
                currentView = mBottomView
                setWatchDesktopStatus(false)
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = -mHeight.toFloat()
                currentView = null
                setWatchDesktopStatus(true)
            }

            //printView(currentView);
        }
        mScrollView = null
    }

    fun onScrollCancel(direct: Int) {
        if (mScrollView == null || isAnimationRunning) {
            return
        }

        if (direct == MLGestureView.Companion.RIGHT_TO_LEFT) {
//            if (mScrollView == mAppListView) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = mWidth.toFloat()
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = 0f
            }
        }

        if (direct == MLGestureView.Companion.LEFT_TO_RIGHT) {
//            if (mScrollView == mAppListView) {
            if (mScrollView === mRightView) {
                mScrollView!!.x = 0f
            }

            if (mScrollView === mLeftView) {
                mScrollView!!.x = -mWidth.toFloat()
            }
        }

        if (direct == MLGestureView.Companion.UP_TO_DOWN) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = 0f
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = -mHeight.toFloat()
                Log.e("view_test", "onScrollCancel")
            }
        }


        if (direct == MLGestureView.Companion.DOWN_TO_UP) {
            if (mScrollView === mBottomView) {
                mScrollView!!.y = mHeight.toFloat()
            }

            if (mScrollView === mTopView) {
                mScrollView!!.y = 0f
            }
        }
        mScrollView = null
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h

        mTopView!!.y = -h.toFloat()
        mRightView!!.x = w.toFloat()
        mLeftView!!.x = -w.toFloat()
        mBottomView!!.y = h.toFloat()
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
            childView.layout(l, t, r, b)
        }
    }

    fun onFlitAppList() {
        if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
            if (mRightView!!.x == 0f) {
                mRightView!!.animate().setDuration(FLIT_ANIMATION_TIME.toLong())
                mRightView!!.animate().translationX(mWidth.toFloat())
                currentView = null
                //printView(currentView);
            }
        }
    }

    /**
     * 通过外部滑动收起VoiceServiceView
     */
    fun onFlitVoiceService() {
        if (mTopView!!.y != 0f && mBottomView!!.y != 0f) {
            if (mLeftView!!.x == 0f) {
                mLeftView!!.x = -mWidth.toFloat()

                //mLeftView.animate().setDuration(FLIT_ANIMATION_TIME);
                //mLeftView.animate().translationX(-mWidth);
                Settings.Global.putInt(mContext!!.contentResolver, "watch_status_in_home_screen", 1)
            }
        }
    }


    val isCurrentWatchView: Boolean
        /**
         * 当前Launcher 的View 是否为表盘
         *
         * @return
         */
        get() = if (currentView == null) {
            true
        } else {
            false
        }

    fun unregisterActivationReceiver() {
        if (mDesktopView != null) {
            mDesktopView!!.unregisterActivationReceiver()
        }
    }

    fun playUnReadMessageAnimator() {
        //TODO ：增加隐藏spine表盘逻辑
//        mDesktopView.playUnReadMessageAnimator();
    }

    fun stopUnReadMessageAnimator() {
//        mDesktopView.stopUnReadMessageAnimator();
    }

    /**
     * 机器人禁用的时候，显示这个
     */
    protected fun showClassLimitDialog() {
    }

    protected fun setWatchDesktopStatus(status: Boolean) {
    }


    //    }
    /**
     * 更新数据
     */
    protected abstract fun updateData()


    private fun updateMessageIconStatus() {
    }
}
