package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.util.AttributeSet

open class LeftView : BaseView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun downToUp() {
    }

    override fun upToDown() {
    }

    constructor(view: AbstractMainView?, context: Context?) : super(context)

    open fun hideView() {
    }

    override fun initView() {
        super.initView()
        setShutDirect(BaseView.Companion.RIGHT_TO_LEFT)
    }
}
