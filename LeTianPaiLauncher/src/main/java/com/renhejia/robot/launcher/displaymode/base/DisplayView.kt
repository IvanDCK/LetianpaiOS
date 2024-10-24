package com.renhejia.robot.launcher.displaymode.base

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

/**
 * 显示模式
 */
class DisplayView : ViewPager {
    constructor( context: Context?) : super(context!!)

    constructor( context: Context?,  attrs: AttributeSet?) : super(context!!, attrs)
}
