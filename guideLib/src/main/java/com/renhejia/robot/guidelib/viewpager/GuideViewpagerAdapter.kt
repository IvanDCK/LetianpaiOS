package com.renhejia.robot.guidelib.viewpager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.PagerAdapter.POSITION_NONE

/**
 * @author liujunbin
 */
class GuideViewpagerAdapter(private val mContext: Context?, views: MutableList<View>) :
    PagerAdapter() {
    private var guideViews: MutableList<View> = ArrayList()

    init {
        this.guideViews = views
    }

    override fun getCount(): Int {
        return guideViews.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (position < guideViews.size) {
            val view: View = guideViews.get(position)
            container.addView(view)
            return view
        }
        return Any()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if ((guideViews.size > 0) && (position < guideViews.size)) {
            container.removeView(guideViews.get(position))
        }
    }


    fun refresh(appList: MutableList<View>) {
        this.guideViews = appList
        for (i in guideViews.indices) {
            guideViews.set(i, appList.get(i))
        }
        notifyDataSetChanged()
    }


    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}










