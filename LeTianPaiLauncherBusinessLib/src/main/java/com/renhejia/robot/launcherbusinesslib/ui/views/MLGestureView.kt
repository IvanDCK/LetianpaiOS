package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MLGestureView : View {
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
    private var width = 0
    private var height = 0

    private val mRect1 = Rect(0, 0, 0, 0)
    private val mRect2 = Rect(0, 0, 0, 0)
    private val mRect3 = Rect(0, 0, 0, 0)

    //    private MLMainView mDelegate;
    private var mDelegate: AbstractMainView? = null

    private val mTouchPoints = ArrayList<Point>()

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context?) {
        this.setBackgroundColor(0x00000000)
    }

    fun setDelegate(delegate: AbstractMainView?) {
        mDelegate = delegate
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("view_test", "MotionEvent.ACTION_DOWN: X: $x Y: $y")
                mIsLongPress = false
                mIsScrolling = false
                mDownPoint[x] = y
                mIsScrollStartPoint = (mRect1.contains(x, y) && !mRect2.contains(x, y))
                mTouchPoints.clear()

                mTouchPoints.add(Point(x, y))
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                Log.e("view_test", "MotionEvent.ACTION_MOVE: X: $x Y: $y")
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

            MotionEvent.ACTION_UP -> {
                Log.e("view_test", "MotionEvent.ACTION_UP: X: $x Y: $y")
                mTouchPoints.add(Point(x, y))

                val lenX = abs((x - mDownPoint.x).toDouble()).toInt()
                val lenY = abs((y - mDownPoint.y).toDouble()).toInt()
                val maxLen = max(lenX.toDouble(), lenY.toDouble()).toInt()

                if (mIsScrolling) {
                    if (maxLen > mFitMinDist && event.eventTime - event.downTime < FLIT_MAX_TIME) {
                        onScrollFinish(mScrollDirect)
                    } else {
                        if (maxLen < mScrollMinDist) {
                            onScrollCancel(mScrollDirect)
                        } else {
                            val nDistX = getLastFivePointXDist(mTouchPoints)
                            val nDistY = getLastFivePointYDist(mTouchPoints)
                            if ((mScrollDirect == LEFT_TO_RIGHT && nDistX > 0) ||
                                (mScrollDirect == RIGHT_TO_LEFT && nDistX < 0) ||
                                (mScrollDirect == UP_TO_DOWN && nDistY > 0) ||
                                (mScrollDirect == DOWN_TO_UP && nDistY < 0)
                            ) {
                                onScrollFinish(mScrollDirect)
                            } else {
                                onScrollCancel(mScrollDirect)
                            }
                        }
                    }
                } else {
                    if (maxLen > mFitMinDist && event.eventTime - event.downTime < FLIT_MAX_TIME) {
                        if (lenX > lenY) {
                            if (x > mDownPoint.x) {
                                onFlit(LEFT_TO_RIGHT)
                            } else {
                                onFlit(RIGHT_TO_LEFT)
                            }
                        } else {
                            if (y > mDownPoint.y) {
                                onFlit(UP_TO_DOWN)
                            } else {
                                onFlit(DOWN_TO_UP)
                            }

                            //                            if (y > mDownPoint.y) {
//                                int yy = (width * 2 / 3);
//                                LogUtils.logi("view_test1", "yy : " + yy);
//                                LogUtils.logi("view_test1", "x0 : " + x);
//                                if (x > yy) {
//                                    onFlit(UP_TO_DOWN_RIGHT);
//                                } else {
//                                    onFlit(UP_TO_DOWN);
//                                }
//
//                            } else {
//                                onFlit(DOWN_TO_UP);
//                            }
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
        return true
    }

    fun onUnknown(pt: Point?) {
        mDelegate!!.onUnknown(pt)
    }

    fun onClick(pt: Point?) {
        mDelegate!!.onClick(pt)
    }

    fun onDoubleClick(pt: Point?) {
        mDelegate!!.onDoubleClick(pt)
    }

    fun onLongPress(pt: Point?) {
        mDelegate!!.onLongPress(pt)
    }

    fun onScrollBegin(direct: Int) {
        Log.e("view_test1", "onScrollBegin ==== 1")
        mDelegate!!.onScrollBegin(direct)
    }

    fun onScrolling(direct: Int, pt: Point) {
        mDelegate!!.onScrolling(direct, pt)
    }

    fun onScrollFinish(direct: Int) {
        mDelegate!!.onScrollFinish(direct)
    }

    fun onScrollCancel(direct: Int) {
        mDelegate!!.onScrollCancel(direct)
    }

    fun onFlit(direct: Int) {
        mDelegate!!.onFlit(direct)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = w
        height = h
        val nSpace = (min(w.toDouble(), h.toDouble()) / 10).toInt()

        mClickMaxDist = nSpace * 2
        mFitMinDist = (min(w.toDouble(), h.toDouble()) / 4).toInt()
        mDoubleClickMaxDist = nSpace * 2
        mLongPressMaxDist = nSpace * 2
        mScrollMinDist = (min(w.toDouble(), h.toDouble()) / 2).toInt()

        //        LogUtils.logi("view_test","nSpace: "+ nSpace);
//        LogUtils.logi("view_test","w: "+ w);
//        LogUtils.logi("view_test","h: "+ h);
//        LogUtils.logi("view_test","mClickMaxDist: "+ mClickMaxDist);
//        LogUtils.logi("view_test","mFitMinDist: "+ mFitMinDist);
//        LogUtils.logi("view_test","mDoubleClickMaxDist: "+ mDoubleClickMaxDist);
//        LogUtils.logi("view_test","mLongPressMaxDist: "+ mLongPressMaxDist);
//        LogUtils.logi("view_test","mScrollMinDist: "+ mScrollMinDist);
        mRect1[0, 0, w] = h
        mRect2[nSpace, nSpace, w - nSpace] = h - nSpace
        mRect3[nSpace * 2, nSpace * 2, w - nSpace * 2] = h - nSpace * 2
    }

    companion object {
        private const val TAG = "MLGenstureView"

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
