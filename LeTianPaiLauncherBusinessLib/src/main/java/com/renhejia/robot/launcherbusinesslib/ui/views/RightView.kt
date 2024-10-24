package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.util.AttributeSet

abstract class RightView : BaseView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(view: AbstractMainView?, context: Context?) : super(context)

    abstract fun updateData()

    override fun initView() {
        super.initView()
        setShutDirect(BaseView.Companion.LEFT_TO_RIGHT)
    }
}
