package com.renhejia.robot.launcher.displaymode

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * @author liujunbin
 */
class DisplayViewpagerAdapter(views: MutableList<View>) :
    PagerAdapter() {
    private var displayViews: MutableList<View> = ArrayList()

    init {
        this.displayViews = views
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if ((displayViews.size > 0) && (position < displayViews.size)) {
            container.removeView(displayViews[position])
        }
    }

    /*
     val count: Int
        get() = displayViews.size
     */

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return displayViews.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = displayViews[position % displayViews.size]
        val parent: ViewGroup = view.parent as ViewGroup
        if (parent != null) {
            parent.removeView(view)
        }
        container.addView(view)
        return view
    }


    fun refresh(appList: MutableList<View>) {
        this.displayViews = appList
        for (i in displayViews.indices) {
            displayViews[i] = appList[i]
        }
        notifyDataSetChanged()
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}










