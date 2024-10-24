package com.renhejia.robot.launcherbaselib.ui.utils

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
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * Created by liujunbin
 */
object ViewUtils {
    /**
     * @param context
     * @param view
     */
    fun resizeLinearViewSize(context: Activity, view: View) {
        val screenWidth: Int = context.windowManager.defaultDisplay.width
        val bannerlayout: LinearLayout.LayoutParams =
            (view.layoutParams) as LinearLayout.LayoutParams
        bannerlayout.width = screenWidth * 2 / 3

        //        bannerlayout.height = screenWidth * height / width;
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param context
     * @param view
     * @param width
     * @param height
     */
    fun resizeLinearLayoutViewSize0(context: Activity?, view: View, width: Int, height: Int) {
        val bannerlayout: LinearLayout.LayoutParams =
            (view.layoutParams) as LinearLayout.LayoutParams
        bannerlayout.width = width
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeLinearLayoutViewSize(
        view: View,
        width: Int,
        height: Int,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val bannerlayout: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bannerlayout.width = width
        bannerlayout.height = height
        bannerlayout.setMargins(left, top, right, bottom)
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeLinearLayoutViewSize(view: LinearLayout, width: Int, height: Int) {
        val bannerlayout: LinearLayout.LayoutParams =
            (view.layoutParams) as LinearLayout.LayoutParams
        bannerlayout.width = width
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeGridLayoutViewSize(view: LinearLayout, width: Int, height: Int) {
//        GridLayout.LayoutParams bannerlayout = (GridLayout.LayoutParams) (view.getLayoutParams());
        val bannerlayout: GridLayout.LayoutParams = GridLayout.LayoutParams()
        bannerlayout.width = width
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     */
    fun resizeGridLayoutViewSize(view: LinearLayout, width: Int) {
        val bannerlayout: GridLayout.LayoutParams = GridLayout.LayoutParams()
        bannerlayout.width = width
        view.setLayoutParams(bannerlayout)
    }


    /**
     * @param view
     * @param height
     */
    fun resizeLinearLayoutViewSize(view: View, height: Int) {
        val bannerlayout: LinearLayout.LayoutParams =
            (view.layoutParams) as LinearLayout.LayoutParams
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

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
     * @param width
     * @param height
     */
    fun resizeRelativeLayoutViewSize0(view: View, width: Int, height: Int) {
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
     * @param width
     * @param height
     */
    fun resizeRelativeLayoutViewSize0(view: View, width: Int, height: Int, left: Int, top: Int) {
        val bannerlayout: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bannerlayout.width = width
        bannerlayout.height = height
        bannerlayout.setMargins(left, top, 0, 0)
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeLinearLayoutViewSize0(view: View, width: Int, height: Int, left: Int, top: Int) {
        val bannerlayout: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bannerlayout.width = width
        bannerlayout.height = height
        bannerlayout.setMargins(left, top, 0, 0)
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeLinearLayoutViewSize(view: View, width: Int, height: Int) {
        val bannerlayout: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        bannerlayout.width = width
        bannerlayout.height = height
        view.setLayoutParams(bannerlayout)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeViewSize(view: View, width: Int, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        params.width = width
        view.setLayoutParams(params)
    }

    /**
     * @param view
     */
    fun resizeRelativeLayoutViewSize(view: RelativeLayout, size: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = size
        params.width = size
        view.setLayoutParams(params)
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
     */
    fun resizeRelativeLayoutViewWidthSize(view: RelativeLayout, width: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.width = width
        view.setLayoutParams(params)
    }

    /**
     * @param view
     */
    fun resizeLinearLayoutViewHeightSize(view: LinearLayout, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        view.setLayoutParams(params)
    }

    /**
     * @param view
     */
    fun resizeLinearLayoutViewHeightSize1(view: LinearLayout, height: Int) {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(view.getLayoutParams())
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
     * @param view
     * @param width
     * @param height
     */
    fun resizeTextViewSize(view: TextView, width: Int, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        params.width = width
        view.setLayoutParams(params)
    }

    /**
     * @param view
     * @param height
     */
    fun resizeTextViewSize(view: TextView, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        view.setLayoutParams(params)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeImageViewSize(view: ImageView, width: Int, height: Int, left: Int, right: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        params.width = width
        view.setLayoutParams(params)
    }

    /**
     * @param view
     * @param width
     * @param height
     */
    fun resizeButtonViewSize(view: Button, width: Int, height: Int) {
        val params: ViewGroup.LayoutParams = view.getLayoutParams()
        params.height = height
        params.width = width
        view.setLayoutParams(params)
    }


    /**
     * 重置dialogView 大小
     *
     * @param width
     * @param height
     */
    fun resizeViewSize(context: Context, window: Window, width: Int, height: Int) {
        val manager: WindowManager = (context as Activity).getWindowManager()
        val params: WindowManager.LayoutParams = window.getAttributes()
        window.setGravity(Gravity.CENTER)
        val display: Display = manager.getDefaultDisplay()
        params.width = width
        params.height = height
        window.setAttributes(params)
    }

    /**
     * 重置dialogView 所在View短边大小
     *
     * @param width
     * @param height
     */
    fun getAnswerDialogDisplaySize(
        context: Context,
        window: Window,
        width: Float,
        height: Float
    ): Int {
        var shortSize: Int = 0
        val manager: WindowManager = (context as Activity).windowManager
        val params: WindowManager.LayoutParams = window.attributes
        window.setGravity(Gravity.CENTER)
        val display: Display = manager.getDefaultDisplay()
        val displayWidth: Int = (display.getWidth() * width).toInt()
        val displayHeight: Int = (display.getHeight() * height).toInt()
        if (displayWidth > displayHeight) {
            shortSize = displayHeight
        } else {
            shortSize = displayWidth
        }
        return shortSize
    }

    /**
     * 重置dialogView 所在View短边大小
     *
     */
    fun getViewWidthSize(context: Context, window: Window): Int {
        val shortSize: Int = 0
        val manager: WindowManager = (context as Activity).windowManager
        val params: WindowManager.LayoutParams = window.attributes
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
        val manager: WindowManager = (context as Activity).windowManager
        val params: WindowManager.LayoutParams = window.attributes
        window.setGravity(Gravity.CENTER)
        val display: Display = manager.getDefaultDisplay()
        val displayWidth: Int = (display.getWidth())

        //        LogUtils.k("displayWidth: "+ displayWidth);
//        LogUtils.k("displayHeight: "+ displayHeight);
        return (display.getHeight())
    }


    //    /**
    //     *
    //     * @param context
    //     * @param view
    //     */
    //    public static void resizeViewSize(Activity context, View view) {
    //        int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
    //        RelativeLayout.LayoutParams bannerlayout = (RelativeLayout.LayoutParams) (view.getLayoutParams());
    //        bannerlayout.width = screenWidth * 2 / 3;
    //
    //        view.setLayoutParams(bannerlayout);
    //
    //    }
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

    /**
     * 填充titlebar
     */
    fun resizeStatusAndTitlebar(context: Context, relativeLayout: RelativeLayout) {
        val height: Int = DensityUtil.dip2px(context, 55f)
        resizeStatusAndTitlebar(context, relativeLayout, height)
    }


    fun moveRelateLayout(left: Int, top: Int, relativeLayout: ViewGroup) {
        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(0, top, 0, 0)
        relativeLayout.setLayoutParams(lp)
    }

    fun moveLinearLayout(left: Int, top: Int, linearLayout: LinearLayout) {
        val lp: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(linearLayout.getLayoutParams())
        lp.setMargins(0, top, 0, 0)
        linearLayout.setLayoutParams(lp)
    }

    fun moveLinearLayout0(left: Int, top: Int, linearLayout: LinearLayout) {
        val lp: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        lp.setMargins(0, top, 0, 0)
        linearLayout.setLayoutParams(lp)
    }

    fun moveLinearImageView(left: Int, top: Int, imageView: ImageView) {
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(imageView.getLayoutParams())
        lp.setMargins(0, top, 0, 0)
        imageView.setLayoutParams(lp)
    }

    fun moveRelativeImageView(left: Int, top: Int, imageView: ImageView) {
        val lp: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(imageView.getLayoutParams())
        lp.setMargins(left, top, 0, 0)
        imageView.setLayoutParams(lp)
    }
}
