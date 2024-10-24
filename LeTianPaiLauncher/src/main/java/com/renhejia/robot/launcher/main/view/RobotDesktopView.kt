package com.renhejia.robot.launcher.main.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import com.renhejia.robot.display.SpineSkinView
import com.renhejia.robot.expression.ui.view.RobotExpressionView
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcherbusinesslib.ui.views.DesktopView

class RobotDesktopView : DesktopView {
    private var mContext: Context? = null
    private var mSpineSkinView: SpineSkinView? = null
    private val expressionView: RobotExpressionView? = null

    //    private static String SKIN_PATH = "skin/skin_101";
    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    override fun unregisterActivationReceiver() {
    }

    private fun init(context: Context?) {
        this.mContext = context
        inflate(mContext, R.layout.robot_desktop_view, this)
        initView()
        resizeView()
        loadClockView()
    }

    private fun resizeView() {
    }

    private fun initView() {
        mSpineSkinView = findViewById(R.id.ssv_view)
        //        expressionView = findViewById(R.id.expression_view);
    }

    private fun loadClockView() {
//        mSpineSkinView.loadSkin(SKIN_PATH);
    }


    fun onClick(pt: Point?) {
    }
}
