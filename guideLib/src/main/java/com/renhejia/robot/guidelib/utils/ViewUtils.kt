package com.renhejia.robot.guidelib.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout

/**
 * Created by liujunbin
 */
object ViewUtils {
    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeRelativeLayoutViewSize(view: View, width: Int, height: Int) {
        val bannerlayout: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bannerlayout.width = width
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     */
    fun resizeRelativeLayoutViewHeightSize(view: RelativeLayout, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        view.setLayoutParams(params)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeImageViewSize(view: ImageView, width: Int, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        params.width = width
        view.setLayoutParams(params)
    }


    /**
     * 重置dialogView 所在View短边大小
     *
     */
    fun getViewWidthSize(context: Context, window: Window): Int {
        val shortSize: Int = 0
        val manager: WindowManager = (context as Activity).getWindowManager()
        val params: WindowManager.LayoutParams = window.getAttributes()
        window.setGravity(Gravity.CENTER)
        val display: Display = manager.getDefaultDisplay()
        val displayHeight: Int = (display.getHeight())

        //        LogUtils.k("displayWidth: "+ displayWidth);
//        LogUtils.k("displayHeight: "+ displayHeight);
        return (display.getWidth())
    }

    /**
     * 获取View高度
     *
     */
    fun getViewHeightSize(context: Context, window: Window): Int {
        val shortSize: Int = 0
        val manager: WindowManager = (context as Activity).getWindowManager()
        val params: WindowManager.LayoutParams = window.getAttributes()
        window.setGravity(Gravity.CENTER)
        val display: Display = manager.getDefaultDisplay()
        val displayWidth: Int = (display.getWidth())

        //        LogUtils.k("displayWidth: "+ displayWidth);
//        LogUtils.k("displayHeight: "+ displayHeight);
        return (display.getHeight())
    }

    /**
     * 填充titlebar
     * @param window
     */
    fun fillTitleBar(window: Window) {
        window.requestFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.getDecorView().setSystemUiVisibility(
                (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            )
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
            window.setNavigationBarColor(Color.TRANSPARENT)
        }
    }

    /**
     * 获取statusbar高度
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight2: Int = -1
        try {
            val clazz: Class<*> = Class.forName("com.android.internal.R\$dimen")
            val `object`: Any = clazz.newInstance()
            val height: Int = clazz.getField("status_bar_height")
                .get(`object`).toString().toInt()
            statusBarHeight2 = context.getResources().getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return statusBarHeight2
    }

    /**
     * 填充titlebar
     */
    fun resizeStatusAndTitlebar(context: Context, relativeLayout: RelativeLayout, viewHeight: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val statusBarHeight: Int = getStatusBarHeight(context)

            resizeRelativeLayoutViewHeightSize(relativeLayout, viewHeight + statusBarHeight)
        } else {
            resizeRelativeLayoutViewHeightSize(relativeLayout, viewHeight)
        }
    }
}
