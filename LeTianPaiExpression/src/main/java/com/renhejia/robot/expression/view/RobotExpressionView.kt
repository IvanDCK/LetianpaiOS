package com.renhejia.robot.expression.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.ImageView
import android.widget.RelativeLayout
import com.renhejia.robot.expression.R
import com.renhejia.robot.expression.utils.RobotExpressionConsts

/**
 * 机器人表情
 * @author liujunbin
 */
class RobotExpressionView : RelativeLayout, RobotExpressionConsts {
    private var mContext: Context? = null
    private var animation: AnimationDrawable? = null
    private var playingAnimation: AnimationDrawable? = null
    private var ivExpression: ImageView? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context
        inflate(
            mContext, R.layout.robot_expression_view,
            this
        )
        initView()
        if (animation != null) {
            animation!!.isOneShot = false
            ivExpression!!.background = animation
            animation!!.start()
        }
    }

    private fun initView() {
        ivExpression = findViewById(R.id.iv_expression)
        animation =
            resources.getDrawable(R.drawable.excited_expression_animlist) as AnimationDrawable
        val set = AnimationSet(true)
        set.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }

    fun playingAnimation(animationType: Int, isPlayOnce: Boolean) {
        if (animationType == RobotExpressionConsts.ROBOT_EXPRESSION_1) {
            playingAnimation = animation
        }

        //TODO 添加对应的表情的初始化
        if (playingAnimation != null) {
            if (!isPlayOnce) {
                animation!!.isOneShot = false
            }
            playingAnimation!!.start()
        }
    }

    fun stopAnimation() {
        if (playingAnimation != null) {
            playingAnimation!!.stop()
        }
    }
}
