package com.renhejia.robot.expression.face

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.view.animation.LinearInterpolator
import java.io.InputStream

class PropertyFrameDrawable(res: Resources?, `is`: InputStream?) :
    BitmapDrawable(res, `is`) {
    private var hasSubFrame = false
    private var face: BaseAbstractFace? = null
    var duration: Int = 0

    /**
     * 是否只是播放一次，不循环
     */
    var isOneShot: Boolean = true

    /**
     * 一共这个动画需要播放的帧数
     */
    var frames: Int = 1

    private val subFrameList: MutableList<SubFrameDrawable> = ArrayList()

    override fun draw(canvas: Canvas) {
        try {
            super.draw(canvas)
            if (!hasSubFrame) {
                return
            }
            if (subFrameList.size > 0 && !subFrameList[0].isRunning) {
                startSubFrameDraw()
            }
            for (sub in subFrameList) {
                sub.draw(canvas)
            }
        } catch (e: Exception) {
        }
    }

    override fun invalidateSelf() {
        super.invalidateSelf()
    }

    private fun startSubFrameDraw() {
        for (sub in subFrameList) {
            sub.start()
        }
    }

    /**
     * 单一选项的时候没有动画
     *
     * @param range
     * @param ratio
     * @return
     */
    private fun hasNoAnimator(range: Array<Any>, ratio: FloatArray): Boolean {
        return range.size == 1 && ratio.size == 1 && ratio[0] == 1f
    }

    /**
     * 添加一个子帧
     *
     * @param drawable 子帧的图片
     * @param subFrame 子帧的信息
     */
    fun addSubFrame(drawable: PropertyFrameDrawable, subFrame: FaceFrame.SubFrame) {
        val subDrawable: SubFrameDrawable = SubFrameDrawable()
        subDrawable.setDrawable(drawable)
        // 移动
        if (subFrame.hasPivotAnimation()) {
            val range = subFrame.pivotRangeArray
            val ratio = subFrame.pivotRatioArray
            if (hasNoAnimator(range, ratio!!)) {
                // 如果没有动画
                subDrawable.setPiovt(range[0] as Point)
            } else {
                subDrawable.initPivotAnimation(range, ratio)
            }
        }
        // 透明度变化
        if (subFrame.hasAplhaAnimation()) {
            val range = subFrame.alphaRangeArray
            val ratio = subFrame.alphaRatioArray
            if (hasNoAnimator(range, ratio!!)) {
                subDrawable.alpha = (range[0] as Float)
            } else {
                subDrawable.initAlphaAnimation(range, ratio)
            }
        }
        // 缩放
        if (subFrame.hasScaleAnimation()) {
            val range = subFrame.scaleRangeArray
            val ratio = subFrame.scaleRatioArray
            subDrawable.initScaleAnimation(range, ratio!!)
        }
        // 旋转
        if (subFrame.hasRotateAnimation()) {
            val range = subFrame.rotateRangeArray
            val ratio = subFrame.rotateRatioArray
            subDrawable.initRotateAnimation(range, ratio!!)
        }
        // 名字
        subDrawable.name = subFrame.drawableName
        subFrameList.add(subDrawable)
        hasSubFrame = true
    }

    fun clearSubFrame() {
        subFrameList.clear()
    }

    fun setFace(face: BaseAbstractFace?) {
        this.face = face
    }

    val frameBitmap: Bitmap
        get() = super.getBitmap()

    /**
     * 需要刷新
     */
    private fun needInvalidate() {
        (face as FrameAnimationFace).needInvalidate(true)
    }

    /**
     * 子帧Drawable，里面包括属性动画
     *
     * @author
     */
    @Suppress("unused")
    private inner class SubFrameDrawable {
        var name: String? = null
        private var drawable: PropertyFrameDrawable? = null
        var alpha: Float = 1f
            set(alpha) {
                field = alpha
                mPaint.alpha = (alpha * 255).toInt()
            }
        var scale: Float = 1f
        private var degrees = 0 // 旋转角度
        private var height = 0 // 图片高度
        private var width = 0 // 图片宽度
        private var piovt: Point? = Point()
        private val matrix = Matrix()

        private var alphaAnimation: ValueAnimator? = null
        private var scaleAnimation: ValueAnimator? = null
        private var piovtAnimation: ValueAnimator? = null
        private var degreesAnimation: ValueAnimator? = null

        // 动画集合
        private val items: MutableList<Animator> = ArrayList(4)
        private val bouncer = AnimatorSet()
        private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        init {
            bouncer.setDuration(duration.toLong())
        }

        fun initAlphaAnimation(range: Array<Any>, ratio: FloatArray) {
            val holder = createValuesHolder("alpha", range, ratio)
            alphaAnimation =
                ValueAnimator.ofPropertyValuesHolder(holder) //.ofObject(new FaceFloatEvaluator(ratio), range);
            alphaAnimation!!.setDuration(duration.toLong())
            //			alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            if (isOneShot) {
                alphaAnimation!!.repeatCount = ValueAnimator.INFINITE
            }
            alphaAnimation!!.interpolator = LinearInterpolator()
            alphaAnimation!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
                val a = animation.animatedValue as Float
                setAlpha(a.toInt())
                needInvalidate()
            })
            items.add(alphaAnimation!!)
        }

        fun initScaleAnimation(range: Array<Any>, ratio: FloatArray) {
            val holder = createValuesHolder("scale", range, ratio)
            scaleAnimation =
                ValueAnimator.ofPropertyValuesHolder(holder) //.ofObject(new FaceFloatEvaluator(ratio), range);
            scaleAnimation!!.duration = (duration.toLong())
            if (isOneShot) {
                scaleAnimation!!.repeatCount = ValueAnimator.INFINITE
            }
            scaleAnimation!!.interpolator = LinearInterpolator()
            scaleAnimation!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
                scale = animation.animatedValue as Float
                updateMatrix()
                needInvalidate()
            })
            items.add(scaleAnimation!!)
        }

        fun initPivotAnimation(range: Array<Any>, ratio: FloatArray) {
            val holder = createValuesHolder("pivot", range, ratio)
            piovtAnimation =
                ValueAnimator.ofPropertyValuesHolder(holder) //.ofObject(new FacePointEvaluator(ratio), range);
            piovtAnimation!!.setEvaluator(FacePointEvaluator())
            piovtAnimation!!.setDuration(duration.toLong())
            if (isOneShot) {
                piovtAnimation!!.repeatCount = ValueAnimator.INFINITE
            }
            piovtAnimation!!.interpolator = LinearInterpolator()
            piovtAnimation!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
                piovt = animation.animatedValue as Point
                updateMatrix()
                needInvalidate()
            })
            items.add(piovtAnimation!!)
        }

        fun initRotateAnimation(range: Array<Any>, ratio: FloatArray) {
            val holder = createValuesHolder("degrees", range, ratio)
            degreesAnimation = ValueAnimator.ofPropertyValuesHolder(holder)
            degreesAnimation!!.setDuration(duration.toLong())
            if (isOneShot) {
                degreesAnimation!!.repeatCount = ValueAnimator.INFINITE
            }
            degreesAnimation!!.interpolator = LinearInterpolator()
            degreesAnimation!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation ->
                degrees = animation.animatedValue as Int
                updateMatrix()
                needInvalidate()
            })
            items.add(degreesAnimation!!)
        }

        fun createValuesHolder(
            propertyName: String?,
            range: Array<Any>,
            ratio: FloatArray
        ): PropertyValuesHolder {
            val keyFrames = arrayOfNulls<Keyframe>(ratio.size)
            for (i in ratio.indices) {
                if (range[i] is Float) {
                    keyFrames[i] = Keyframe.ofFloat(
                        ratio[i],
                        (range[i] as Float?)!!
                    )
                } else if (range[i] is Int) {
                    keyFrames[i] = Keyframe.ofInt(
                        ratio[i],
                        (range[i] as Int?)!!
                    )
                } else {
                    keyFrames[i] = Keyframe.ofObject(ratio[i], range[i])
                }
            }
            return PropertyValuesHolder.ofKeyframe(propertyName, *keyFrames)
        }

        /**
         * 根据中心点，计算出来偏移量
         * @param
         */
        fun updateMatrix() {
            val px = width / 2
            val py = height / 2
            //			matrix.setScale(scale, scale, px, py);
            matrix.setScale(1f, scale, px.toFloat(), py.toFloat())
            val dx = piovt!!.x - px
            val dy = piovt!!.y - py
            matrix.postTranslate(dx.toFloat(), dy.toFloat())
            matrix.postRotate(degrees.toFloat(), piovt!!.x.toFloat(), piovt!!.y.toFloat())
        }

        fun draw(canvas: Canvas) {
            val bitmap = drawable!!.frameBitmap
            canvas.drawBitmap(bitmap, matrix, mPaint)
        }

        fun start() {
            if (isRunning) {
                return
            }
            for (anim in items) {
                anim.start()
            }
            //			bouncer.playTogether(items);
//			bouncer.start();
        }

        val isRunning: Boolean
            get() {
                for (anim in items) {
                    if (anim.isRunning) {
                        return true
                    }
                }
                return false
            }

        fun getDrawable(): PropertyFrameDrawable? {
            return drawable
        }

        fun setDrawable(drawable: PropertyFrameDrawable) {
            this.drawable = drawable
            val bitmap = drawable.bitmap
            width = bitmap.width
            height = bitmap.height
            //			width = height;
//			rate = height/width;
//			Log.e("robot","rate: "+ rate);
        }

        fun getPiovt(): Point? {
            return piovt
        }

        fun setPiovt(piovt: Point?) {
            this.piovt = piovt
            updateMatrix()
        }
    }
}
