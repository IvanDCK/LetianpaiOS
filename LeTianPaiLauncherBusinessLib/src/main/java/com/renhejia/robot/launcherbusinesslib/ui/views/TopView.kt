package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.util.AttributeSet

open class TopView : BaseView {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(view: AbstractMainView?, context: Context?) : super(context)

    override fun initView() {
        super.initView()
        setShutDirect(BaseView.Companion.DOWN_TO_UP)
    }

    override fun downToUp() {
        setWatchDesktopStatus(true)
    }

    override fun upToDown() {
        setWatchDesktopStatus(false)
    }
}
