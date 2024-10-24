package com.renhejia.robot.launcher.standbymode

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * 待机模式
 * @author liujunbin
 */
class StandbyView : RelativeLayout {
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
