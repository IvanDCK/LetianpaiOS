package com.renhejia.robot.launcher.displaymode.custom

import android.content.Context
import android.util.AttributeSet
import com.renhejia.robot.launcher.displaymode.base.DisplayModeBaseView

/**
 * 自定义View
 *
 * @author liujunbin
 */
class CustomView : DisplayModeBaseView {
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
