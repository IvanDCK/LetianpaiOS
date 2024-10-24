package com.renhejia.robot.launcher.displaymode.base

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.renhejia.robot.launcher.R

/**
 * 显示模式基类
 *
 * @author liujunbin
 */
open class DisplayModeBaseView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    protected fun initView(context: Context?) {
        inflate(context, R.layout.robot_test_animation_view, this)
    }
}
