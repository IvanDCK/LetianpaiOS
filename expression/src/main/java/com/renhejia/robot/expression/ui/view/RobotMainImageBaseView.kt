package com.renhejia.robot.expression.ui.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.renhejia.robot.commandlib.consts.RobotExpressionConsts
import com.renhejia.robot.expression.R

/**
 * 机器表情基类
 * @author liujunbin
 */
abstract class RobotMainImageBaseView : RelativeLayout, RobotExpressionConsts {
    protected var mContext: Context? = null
    private var animation: AnimationDrawable? = null
    private var playingAnimation: AnimationDrawable? = null
    private var ivExpression: ImageView? = null
    private var tvExpression: TextView? = null
    private val animationNull: Animation? = null
    private var topView: View? = null
    private var bottomView: View? = null
    private var topWeight = 1.0f
    private var bottomWeight = 1.0f
    private var desContent = "你好 乐天派"


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
            mContext, R.layout.robot_main_image_view,
            this
        )

        initView()
        updateViewData()
        topView!!.layoutParams = LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT,
            LayoutParams.WRAP_CONTENT,
            topWeight
        )
        bottomView!!.layoutParams = LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT,
            LayoutParams.WRAP_CONTENT,
            bottomWeight
        )
        if (animation != null) {
            animation!!.isOneShot = false
            ivExpression!!.background = animation
            animation!!.start()
        }
    }

    /**
     * 设置文字，设置 动画，设置文字位置
     */
    protected abstract fun updateViewData()

    private fun initView() {
        ivExpression = findViewById(R.id.iv_expression)
        tvExpression = findViewById(R.id.tv_expression)
        topView = findViewById(R.id.top_view)
        bottomView = findViewById(R.id.bottom_view)


        tvExpression!!.text = desContent

        //animation = (AnimationDrawable) getResources().getDrawable(R.drawable.blue_hole_animlist);
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

    protected fun setAnimation(anim: AnimationDrawable?) {
        this.animation = anim
    }

    /**
     * 设置描述文案
     * @param des
     */
    protected fun setDesContent(des: String) {
        this.desContent = des
    }

    protected fun setViewWeight(tVWeight: Float, bVWeight: Float) {
        this.topWeight = tVWeight
        this.bottomWeight = bVWeight
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
