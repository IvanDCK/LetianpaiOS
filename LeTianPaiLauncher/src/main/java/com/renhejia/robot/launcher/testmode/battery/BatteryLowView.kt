package com.renhejia.robot.launcher.testmode.battery

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * 低东提醒
 */
class BatteryLowView : RelativeLayout {
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)
}
