package com.renhejia.robot.launcher.main.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.renhejia.robot.expression.ui.view.RobotExpressionView
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcherbusinesslib.ui.views.LeftView

//import com.renhejia.robot.expression.view.RobotExpressionView;
/**
 * @author liujunbin
 */
class RobotLeftView : LeftView {
    private var watchMainView: RobotMainView? = null
    private var robotExpressionView: RobotExpressionView? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(view: RobotMainView?, context: Context?) : super(view, context) {
        this.watchMainView = view
    }

    override fun initView() {
        super.initView()
        inflate(mContext, R.layout.robot_left_view, this)
        robotExpressionView = findViewById(R.id.expression_view)
    }


    fun onHide() {
        Log.d("VoiceServiceView", "=== onHide()")

        //        if (game.getScreen() != null) {
//            ((BaseScreen) game.getScreen()).setViewShowing(false);
//            ((BaseScreen) game.getScreen()).onHide();
//        }
    }


    fun hideLeftView() {
        watchMainView!!.onFlitVoiceService()
    }


    override fun hideView() {
        super.hideView()
        watchMainView!!.onFlitVoiceService()
    }
}
