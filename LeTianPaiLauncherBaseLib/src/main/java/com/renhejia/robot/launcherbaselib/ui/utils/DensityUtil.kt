package com.renhejia.robot.launcherbaselib.ui.utils

import android.content.Context
import android.util.DisplayMetrics

object DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }


    //获取屏幕density
    fun getScreenDensity(context: Context): Float {
        val m: DisplayMetrics = context.resources.displayMetrics
        return m.density
    }


    /****
     * 获取屏幕宽度
     * @param context
     */
    fun getScreenWidthDimension(context: Context): Int {
        val m: DisplayMetrics = context.resources.displayMetrics
        return m.widthPixels
    }


    /****
     * 获取屏幕高度
     * @param context
     */
    fun getScreenHeightDimension(context: Context): Int {
        val m: DisplayMetrics = context.resources.displayMetrics
        return m.heightPixels
    }
}

