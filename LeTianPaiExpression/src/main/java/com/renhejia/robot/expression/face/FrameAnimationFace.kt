package com.renhejia.robot.expression.face

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Rect
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import java.io.InputStream
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.Volatile

/**
 * 逐帧动画，从asset中加载动画资源，逐帧播放
 *
 * @author liujunbin
 */
class FrameAnimationFace(mContext: Context?) : BaseAbstractFace(mContext) {
    private var animation: AnimationDrawable? = null
    private var mView: ImageView? = null
    private var animationName: String? = null
    private var mConfigParser: AnimationConfigParser? = null
    private var mCache: AnimationLruCache? = null
    private var hasSubFrame = false
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    private val mDirtyRect = Rect()

    @Volatile
    private var needInvalidate = true

    init {
        mConfigParser = AnimationConfigParser()
        mCache = AnimationLruCache.Companion.getInstance(mContext)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawFilter = PaintFlagsDrawFilter(
            0, Paint.ANTI_ALIAS_FLAG
                    or Paint.FILTER_BITMAP_FLAG
        )
    }

    override fun init() {
    }

    @SuppressLint("NewApi")
    fun init(view: ImageView?, animationName: String?) {
        this.mView = view
        this.animationName = animationName
    }

    override fun load() {
        animation = AnimationDrawable()
        val frameList = parserFrameList() ?: return

        // 只是一帧的时候，逐帧动画只播放一次，子帧动画不停的重复播放
        val onShot = frameList.size == 1
        hasSubFrame = false

        for (f in frameList) {
            val path: String =
                BaseAbstractFace.Companion.ANIMATION_PATH + animationName + "/" + f!!.drawableName
            // 逐帧动画中的一帧
            val frameDrawable = mCache!!.getDrawable(path)
            if (frameDrawable != null) {
                frameDrawable.duration = f.duration
                frameDrawable.frames = f.frames
                frameDrawable.isOneShot = onShot
                frameDrawable.clearSubFrame()
                // 自帧中，有多种元素
                for (subFrame in f.getSubFrameList()) {
                    val subPath: String =
                        BaseAbstractFace.Companion.ANIMATION_PATH + animationName + "/" + subFrame!!.drawableName
                    val subDrawable = mCache!!.getDrawable(subPath)
                    if (subDrawable != null) {
                        if (subFrame != null) {
                            frameDrawable.addSubFrame(subDrawable, subFrame)
                        }
                        hasSubFrame = true
                    }
                }
                frameDrawable.setFace(this)
                animation!!.addFrame(frameDrawable, f.duration)
            }
        }
        animation!!.isOneShot = onShot
        mView!!.setImageDrawable(animation)
    }

    /**
     * 解析xml
     *
     * @return
     */
    private fun parserFrameList(): List<FaceFrame?>? {
        val context = mView!!.context
        val `is`: InputStream
        try {
            `is` =
                context.assets.open(BaseAbstractFace.Companion.ANIMATION_PATH + animationName + "/config.xml")
            return mConfigParser!!.parse(`is`)
        } catch (e: Exception) {
//			KTExceptionPrinter.printStackTrace(e);
            e.printStackTrace()
        }
        return null
    }

    override fun start() {
        // 判断是否是纯逐帧动画
        if (hasSubFrame) {
            startTimer()
        } else { // 没有子帧，则不需要间隔刷新
            stopTimer()
        }
        animation!!.start()
    }

    private fun startTimer() {
        var needSchedule = false
        if (mTimer == null) {
            mTimer = Timer()
            needSchedule = true
        }
        if (mTimerTask == null) {
            mTimerTask = object : TimerTask() {
                override fun run() {
                    if (needInvalidate) {
                        needInvalidate = false
                        invalidateFace()
                    }
                }
            }
        }
        if (needSchedule && mTimer != null && mTimerTask != null) {
            mTimer!!.schedule(mTimerTask, 0, 50)
        }
    }

    private fun stopTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
        if (mTimerTask != null) {
            mTimerTask!!.cancel()
            mTimerTask = null
        }
    }

    /**
     * 更新刷新脏区
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    fun updateDirtyRect(left: Int, top: Int, right: Int, bottom: Int) {
        mDirtyRect[left, top, right] = bottom
    }

    /**
     * 是否需要刷新
     *
     * @param need
     */
    fun needInvalidate(need: Boolean) {
        needInvalidate = need
    }

    override fun stop() {
        animation!!.stop()
    }

    @SuppressLint("NewApi")
    override fun release() {
        animation = null
        //		mView.setImageDrawable(null);
        stopTimer()
    }
}
