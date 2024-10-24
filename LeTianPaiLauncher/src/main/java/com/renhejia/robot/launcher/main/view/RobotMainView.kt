package com.renhejia.robot.launcher.main.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import com.renhejia.robot.guidelib.qrcode.QRCodeView
import com.renhejia.robot.launcherbusinesslib.ui.views.AbstractMainView

class RobotMainView : AbstractMainView {
    //    private RobotTopView robotTopView;
    private var qrCodeView: QRCodeView? = null
    private var robotBottomView: RobotBottomView? = null
    private var robotLeftView: RobotLeftView? = null
    private val robotRightView: RobotRightView? = null

    //    private RobotTestAnimationView robotTestAnimationView;
    private var robotDesktopView: RobotDesktopView? = null

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

    override fun responseChangedSpineSkin(skinPath: String?) {
    }

    override fun fillWatchViews() {
        robotDesktopView = RobotDesktopView(mContext)
        //        robotTopView = new RobotTopView(mContext);
        qrCodeView = QRCodeView(mContext)
        robotBottomView = RobotBottomView(mContext)
        robotLeftView = RobotLeftView(mContext)

        //        robotRightView = new RobotRightView(mContext);
//        robotTestAnimationView = new RobotTestAnimationView(mContext);
        setDesktopView(robotDesktopView)
        //        setTopView(robotTopView);
        setTopView(qrCodeView)
        setBottomView(robotBottomView)
        setLeftView(robotLeftView)
        setRightView(robotRightView)


        //        setRightView(robotTestAnimationView);
    }

    override fun responseOnClick(pt: Point?) {
        robotDesktopView!!.onClick(pt)
    }

    override fun responseLongPressOnWatchView() {
        //TODO 预留切换页面入口
    }

    override fun updateData() {
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
//            if (QSettings.isDisableMode(mContext)) {
            if (false) {
                showClassLimitDialog()
                //                return true;
                return false
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}
