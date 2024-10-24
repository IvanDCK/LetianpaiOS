package com.renhejia.robot.launcher.main.view

import android.content.Context
import android.util.AttributeSet
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcherbusinesslib.ui.views.AbstractMainView
import com.renhejia.robot.launcherbusinesslib.ui.views.BottomView

class RobotBottomView : BottomView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(view: AbstractMainView?, context: Context?) : super(view, context)

    override fun initView() {
        super.initView()
        setShutDirect(UP_TO_DOWN)
        inflate(mContext, R.layout.robot_bottom_view, this)
    }
}
