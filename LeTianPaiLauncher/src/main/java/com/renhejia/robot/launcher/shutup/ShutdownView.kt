package com.renhejia.robot.launcher.shutup

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.renhejia.robot.launcher.R

/**
 * 关机界面
 *
 */
class ShutdownView : RelativeLayout {
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
        inflate(context, R.layout.robot_shutup_view, this)
    }
}
