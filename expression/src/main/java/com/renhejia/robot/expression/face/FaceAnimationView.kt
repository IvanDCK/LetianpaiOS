package com.renhejia.robot.expression.face

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.util.ArrayMap
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView

/**
 * 表情绘制的View
 *
 * @author liujunbin
 */
//public class FaceAnimationView extends androidx.appcompat.widget.AppCompatImageView {
class FaceAnimationView : AppCompatImageView {
    interface OnFaceAnimationChangedListener {
        /**
         * Called when FaceAnimationView change face animation.
         *
         * @param v The FaceAnimationView that face animation was change.
         * @param faceNew new face.
         * @param faceOld old face.
         */
        fun onFaceAnimationChanged(v: FaceAnimationView?, faceNew: Int, faceOld: Int)
    }

    var currentAnimation: BaseAbstractFace? = null
        private set
    private val animationMap = ArrayMap<Int, BaseAbstractFace>()
    private var mCurrentIndex = 0

    /**
     * 获取当前正在播放的表情
     *
     * @return
     */
    var currentFace: Int = DEFAULT_FACE
        private set

    private var mOnFaceAnimationChangedListener: OnFaceAnimationChangedListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context) : super(context)

    fun clearAnimationList() {
        animationMap.clear()
    }

    /**
     * 添加逐帧动画
     */
    fun addFrameAnimation(faceType: Int, dirName: String) {
        animationMap[faceType] = createFrameAnimation(dirName)
    }

    private fun createFrameAnimation(name: String): FrameAnimationFace {
        val a = FrameAnimationFace(
            context
        )
        a.init(this, name)
        a.faceAnimationListener = faceAnimationListener
        return a
    }

    fun changeAnimation(faceType: Int) {
        var faceType = faceType
        Log.e("error_Launcher_initView", "changeAnimation ======== 0 ======")
        if (!animationMap.containsKey(faceType)) {
            faceType = FaceConsts.FACE_CHANGTAI
        }

        val faceOld = currentFace

        currentFace = faceType
        changeAnimation(animationMap[faceType])

        if (null != mOnFaceAnimationChangedListener) {
            mOnFaceAnimationChangedListener!!.onFaceAnimationChanged(this, faceType, faceOld)
        }
    }

    val isCurrentDefaultFace: Boolean
        /**
         * 是否是默认表情
         *
         * @return
         */
        get() = currentFace == DEFAULT_FACE

    /**
     * 返回到默认表情
     */
    fun changeToDefaultFace() {
        changeAnimation(DEFAULT_FACE)
    }

    /**
     * 下一个动画
     */
    fun nextAnimation() {
        mCurrentIndex++
        if (mCurrentIndex == animationMap.size) {
            mCurrentIndex = 0
        }
        val key = animationMap.keyAt(mCurrentIndex)
        changeAnimation(key)
    }

    private fun changeAnimation(face: BaseAbstractFace?) {
        Log.e("error_Launcher_initView", "changeAnimation ======== 1 ======")
        if (currentAnimation === face) {
            return
        }
        var before: BaseAbstractFace? = null
        if (currentAnimation != null) {
            currentAnimation!!.stop()
            before = currentAnimation
            //			mCurrentAnimation.release();
        }
        currentAnimation = face
        currentAnimation!!.load()
        currentAnimation!!.start()
        Log.e("error_Launcher_initView", "changeAnimation ========2 ======")
        before?.release()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawFilter = PaintFlagsDrawFilter(
            0, Paint.ANTI_ALIAS_FLAG
                    or Paint.FILTER_BITMAP_FLAG
        )
        super.onDraw(canvas)
        if (currentAnimation != null) {
            currentAnimation!!.draw(canvas)
        }
    }

    private val faceAnimationListener: FaceAnimationListener = object : FaceAnimationListener {
        override fun invalidateFace() {
            postInvalidate()
        }

        override fun invalidateFace(left: Int, top: Int, right: Int, bottom: Int) {
            postInvalidate(left, top, right, bottom)
        }
    }

    fun release() {
        if (currentAnimation != null) {
            currentAnimation!!.release()
        }
    }

    fun start() {
        if (currentAnimation != null) {
            currentAnimation!!.start()
        }
    }

    /**
     * 设置表情变化监听器
     * @param listener
     */
    fun setOnFaceAnimationChangedListener(listener: OnFaceAnimationChangedListener?) {
        this.mOnFaceAnimationChangedListener = listener
    }

    companion object {
        private const val DEFAULT_FACE = FaceConsts.FACE_CHANGTAI
    }
}
