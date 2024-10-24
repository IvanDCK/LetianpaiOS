package com.renhejia.robot.expression.face

import android.content.Context
import android.graphics.Canvas

/**
 * 表情的抽象基类
 *
 * @author liujunbin
 */
abstract class BaseAbstractFace(mContext: Context?) {
    protected var mContext: Context? = null

    var faceAnimationListener: FaceAnimationListener? = null

    abstract fun draw(canvas: Canvas)

    abstract fun release()

    abstract fun start()

    abstract fun stop()

    abstract fun init()

    abstract fun load()

    init {
        this.mContext = mContext
    }

    /**
     * 刷新表情
     */
    protected fun invalidateFace() {
        if (faceAnimationListener != null) {
            faceAnimationListener!!.invalidateFace()
        }
    }

    companion object {
        const val ANIMATION_PATH: String = "annimation/"
    }
}
