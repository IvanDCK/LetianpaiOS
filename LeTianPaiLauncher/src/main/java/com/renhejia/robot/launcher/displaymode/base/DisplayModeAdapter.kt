package com.renhejia.robot.launcher.displaymode.base

import android.view.View
import androidx.viewpager.widget.PagerAdapter

/**
 * 显示模式adapter
 *
 * @author liujunbin
 */
class DisplayModeAdapter : PagerAdapter() {
    private val displayModeViews: List<View>? = null

    /*
      val count: Int
        get() = 0

    override fun isViewFromObject(@NonNull view: View?, @NonNull `object`: Any?): Boolean {
        return false
    }
     */


    override fun getCount(): Int {
        return displayModeViews!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
        //return false
    }
}
