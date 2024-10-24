package com.renhejia.robot.launcherbusinesslib.ui.views

import android.content.Context
import android.util.AttributeSet

open class BottomView : BaseView {
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
        setWatchDesktopStatus(true)
    }

    constructor(view: AbstractMainView?, context: Context?) : super(context)

    override fun updateMessageIconStatus() {
        super.updateMessageIconStatus()
    }
}
