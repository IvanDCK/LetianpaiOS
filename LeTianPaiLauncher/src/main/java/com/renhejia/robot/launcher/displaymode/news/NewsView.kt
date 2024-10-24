package com.renhejia.robot.launcher.displaymode.news

import android.content.Context
import android.util.AttributeSet
import com.renhejia.robot.launcher.displaymode.base.DisplayModeBaseView

/**
 * 新闻界面
 *
 * @author liujunbin
 */
class NewsView : DisplayModeBaseView {
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
