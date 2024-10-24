package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.RelativeLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

abstract class BaseView : RelativeLayout {
    @JvmField
    protected var mContext: Context? = null

    private val FLIT_ANIMATION_TIME = 60

    private var mShutDirect = UP_TO_DOWN

    private var mWidth = 0
    private var mHeight = 0
    private var mLongPressMaxDist = 0
    private var mClickMaxDist = 0
    private var mFitMinDist = 0
    private var mDoubleClickMaxDist = 0
    private var mScrollMinDist = 0

    private val mDownPoint = Point(-1, -1)
    private var mLastClickTime: Long = -1
    private val mLastDownPoint = Point(-1, -1)
    private var mIsLongPress = false

    private var mIsScrollStartPoint = false
    private var mIsScrolling = false
    private var mScrollDirect = -1

    private val mRect1 = Rect(0, 0, 0, 0)
    private val mRect2 = Rect(0, 0, 0, 0)
    private val mRect3 = Rect(0, 0, 0, 0)

    private val mTouchPoints = ArrayList<Point>()

    constructor(context: Context?) : super(context) {
        init(context)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
        initView()
    }

    protected open fun initView() {
    }

    protected fun init(context: Context?) {
        this.mContext = context
    }

    private fun getLastFivePointXDist(points: ArrayList<Point>): Int {
        var nDist = 0
        if (points.size > SCROLL_LAST_POINT_NUM) {
            for (i in points.size - SCROLL_LAST_POINT_NUM until points.size) {
                nDist += points[i].x - points[i - 1].x
            }
        } else {
            nDist = points[points.size - 1].x - points[0].x
        }

        return nDist
    }

    private fun getLastFivePointYDist(points: ArrayList<Point>): Int {
        var nDist = 0
        if (points.size > SCROLL_LAST_POINT_NUM) {
            for (i in points.size - SCROLL_LAST_POINT_NUM until points.size) {
                nDist += points[i].y - points[i - 1].y
            }
        } else {
            nDist = points[points.size - 1].y - points[0].y
        }

        return nDist
    }

    private fun getMaxDist(points: ArrayList<Point>): Int {
        var nMaxDist = 0
        if (points.size > 1) {
            val begin = points[0]
            for (i in 1 until points.size) {
                nMaxDist = max(
                    nMaxDist.toDouble(),
                    max(
                        abs((begin.x - points[i].x).toDouble()),
                        abs((begin.y - points[i].y).toDouble())
                    )
                ).toInt()
            }
        }

        return nMaxDist
    }

    fun setShutDirect(shutDirect: Int) {
        mShutDirect = shutDirect
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        var interceptEvent = false

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mIsLongPress = false
                mIsScrolling = false
                mDownPoint[x] = y
                mIsScrollStartPoint = (mRect1.contains(x, y) && !mRect2.contains(x, y))
                mTouchPoints.clear()

                mTouchPoints.add(Point(x, y))
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                run {
                    mTouchPoints.add(Point(x, y))
                    val lenX = abs((x - mDownPoint.x).toDouble()).toInt()
                    val lenY = abs((y - mDownPoint.y).toDouble()).toInt()
                    val maxLen = max(lenX.toDouble(), lenY.toDouble()).toInt()

                    if (event.eventTime - event.downTime > LONGPRESS_MIN_TIME && maxLen < mLongPressMaxDist) {
                        val nMaxDist = getMaxDist(mTouchPoints)
                        if (nMaxDist < mLongPressMaxDist) {
                            if (!mIsLongPress) {
                                mIsLongPress = true
                                onLongPress(mDownPoint)
                            }
                        }
                    }

                    if (!mIsScrolling && mIsScrollStartPoint) {
                        if (mRect3.contains(x, y)) {
                            mScrollDirect = UP_TO_DOWN

                            mScrollDirect = if (lenX > lenY) {
                                if (x > mDownPoint.x) {
                                    LEFT_TO_RIGHT
                                } else {
                                    RIGHT_TO_LEFT
                                }
                            } else {
                                if (y > mDownPoint.y) {
                                    UP_TO_DOWN
                                } else {
                                    DOWN_TO_UP
                                }
                            }

                            onScrollBegin(mScrollDirect)
                            mIsScrolling = true
                        }
                    }
                    if (mIsScrolling) {
                        onScrolling(mScrollDirect, Point(x, y))
                    }
                }
                interceptEvent = false
            }

            MotionEvent.ACTION_UP -> {
                updateMessageIconStatus()
                mTouchPoints.add(Point(x, y))

                val lenX = abs((x - mDownPoint.x).toDouble()).toInt()
                val lenY = abs((y - mDownPoint.y).toDouble()).toInt()
                val maxLen = max(lenX.toDouble(), lenY.toDouble()).toInt()

                if (mIsScrolling) {
                    if (maxLen > mFitMinDist && event.eventTime - event.downTime < FLIT_MAX_TIME) {
                        onScrollFinish(mScrollDirect)
                        interceptEvent = true
                    } else {
                        if (maxLen < mScrollMinDist) {
                            onScrollCancel(mScrollDirect)
                            interceptEvent = true
                        } else {
                            val nDistX = getLastFivePointXDist(mTouchPoints)
                            val nDistY = getLastFivePointYDist(mTouchPoints)
                            if ((mScrollDirect == LEFT_TO_RIGHT && nDistX > 0) ||
                                (mScrollDirect == RIGHT_TO_LEFT && nDistX < 0) ||
                                (mScrollDirect == UP_TO_DOWN && nDistY > 0) ||
                                (mScrollDirect == DOWN_TO_UP && nDistY < 0)
                            ) {
                                onScrollFinish(mScrollDirect)
                                interceptEvent = true
                            } else {
                                onScrollCancel(mScrollDirect)
                                interceptEvent = true
                            }
                        }
                    }
                } else {
                    if (maxLen > mFitMinDist && event.eventTime - event.downTime < FLIT_MAX_TIME) {
                        if (lenX > lenY) {
                            if (x > mDownPoint.x) {
                                onFlit(LEFT_TO_RIGHT)
                                interceptEvent = true
                            } else {
                                onFlit(RIGHT_TO_LEFT)
                                interceptEvent = true
                            }
                        } else {
                            if (y > mDownPoint.y) {
                                onFlit(UP_TO_DOWN)
                                interceptEvent = true
                            } else {
                                onFlit(DOWN_TO_UP)
                                interceptEvent = true
                            }
                        }
                    } else {
                        if (!mIsLongPress) {
                            val nMaxDist = getMaxDist(mTouchPoints)

                            if (nMaxDist < mClickMaxDist) {
                                if (event.eventTime - mLastClickTime < DOUBLECLICK_MAX_TIME) {
                                    if (max(
                                            abs((x - mLastDownPoint.x).toDouble()),
                                            abs((y - mLastDownPoint.y).toDouble())
                                        ) < mDoubleClickMaxDist
                                    ) {
                                        mLastClickTime = -1
                                        onDoubleClick(Point(x, y))
                                    } else {
                                        mLastClickTime = event.eventTime
                                        mLastDownPoint[mDownPoint.x] = mDownPoint.y
                                        onClick(Point(x, y))
                                    }
                                } else {
                                    mLastClickTime = event.eventTime
                                    mLastDownPoint[mDownPoint.x] = mDownPoint.y
                                    onClick(Point(x, y))
                                }
                            } else {
                                onUnknown(Point(x, y))
                            }
                        }
                    }
                }
            }

            MotionEvent.ACTION_CANCEL -> if (mIsScrolling) {
                mIsScrolling = false
                onScrollCancel(mScrollDirect)
            }
        }

        return interceptEvent
    }

    fun onUnknown(pt: Point?) {
    }

    fun onClick(pt: Point?) {
    }

    fun onDoubleClick(pt: Point?) {
    }

    fun onLongPress(pt: Point?) {
    }

    fun onScrollBegin(direct: Int) {
        updateMessageIconStatus()
    }

    fun onScrolling(direct: Int, pt: Point) {
        if (mShutDirect == direct) {
            if (direct == UP_TO_DOWN) {
                this.y = pt.y.toFloat()
            } else if (direct == DOWN_TO_UP) {
//                this.animate().translationY(mHeight - pt.y);
                animate().translationY((pt.y - mHeight).toFloat())
                //            } else if (direct == LEFT_TO_RIGHT) {
//                this.animate().translationX(mWidth);
            }
        }
    }


    fun onScrollFinish(direct: Int) {
        if (mShutDirect == direct) {
            animate().setDuration(FLIT_ANIMATION_TIME.toLong())

            if (direct == UP_TO_DOWN) {
                updateViewWhenDownToUp()
                animate().translationY(mHeight.toFloat())
                upToDown()
            } else if (direct == DOWN_TO_UP) {
                animate().translationY(-mHeight.toFloat())
                downToUp()
            } else if (direct == LEFT_TO_RIGHT) {
                Log.e("Mars11113333", "MLGestureView.RIGHT_TO_LEFT: === 8")
                animate().translationX(mWidth.toFloat())
            }
        }
    }

    protected abstract fun downToUp()

    protected abstract fun upToDown()

    fun onScrollCancel(direct: Int) {
        animate().setDuration(60)
        animate().translationY(0f)
    }

    fun onFlit(direct: Int) {
        if (mShutDirect == direct) {
            animate().setDuration(FLIT_ANIMATION_TIME.toLong())
            if (direct == UP_TO_DOWN) {
                updateViewWhenDownToUp()
                animate().translationY(mHeight.toFloat())
                upToDown()
            } else if (direct == DOWN_TO_UP) {
                updateViewWhenDownToUp()
                animate().translationY(-mHeight.toFloat())
                downToUp()
            } else if (direct == LEFT_TO_RIGHT) {
                Log.e("Mars11113333", "MLGestureView.RIGHT_TO_LEFT: === 7")
                animate().setDuration(60)
                animate().translationX(mWidth.toFloat())
            }
        }
    }

    private fun updateViewWhenDownToUp() {
//        DownToUpUpdateCallback.getInstance().setDownToUpUpdate();
    }

    protected open fun updateMessageIconStatus() {
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val nSpace = (min(w.toDouble(), h.toDouble()) / 10).toInt()

        mWidth = w
        mHeight = h

        mClickMaxDist = nSpace * 2
        mFitMinDist = (min(w.toDouble(), h.toDouble()) / 4).toInt()
        mDoubleClickMaxDist = nSpace * 2
        mLongPressMaxDist = nSpace * 2
        mScrollMinDist = (min(w.toDouble(), h.toDouble()) / 2).toInt()

        mRect1[0, 0, w] = h
        mRect2[nSpace, nSpace, w - nSpace] = h - nSpace
        mRect3[nSpace * 2, nSpace * 2, w - nSpace * 2] = h - nSpace * 2
    }

    protected fun setWatchDesktopStatus(status: Boolean) {
        val PREFERENCE_KEY_IN_HOME_SCREEN = "watch_status_in_home_screen"
        if (status) {
            Settings.Global.putInt(mContext!!.contentResolver, PREFERENCE_KEY_IN_HOME_SCREEN, 1)
        } else {
            Settings.Global.putInt(mContext!!.contentResolver, PREFERENCE_KEY_IN_HOME_SCREEN, 0)
        }
    }


    companion object {
        const val UP_TO_DOWN: Int = 1
        const val LEFT_TO_RIGHT: Int = 2
        const val DOWN_TO_UP: Int = 3
        const val RIGHT_TO_LEFT: Int = 4
        const val SCROLL_LAST_POINT_NUM: Int = 2

        private const val LONGPRESS_MIN_TIME = 800
        private const val FLIT_MAX_TIME = 500
        private const val DOUBLECLICK_MAX_TIME = 600
    }
}
