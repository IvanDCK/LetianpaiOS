package com.renhejia.robot.launcher.main.view

import android.content.Context
import android.util.AttributeSet
import com.renhejia.robot.expression.face.FaceAnimationView
import com.renhejia.robot.expression.face.FaceConsts
import com.renhejia.robot.launcher.R
import com.renhejia.robot.launcherbusinesslib.ui.views.RightView

/**
 * @author liujunbin
 */
class RobotTestAnimationView : RightView {
    private var watchMainView: RobotMainView? = null
    private var faceAnimationView: FaceAnimationView? = null
    private val animationType = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun updateData() {
    }

    constructor(view: RobotMainView?, context: Context?) : super(view, context) {
        this.watchMainView = view
    }


    override fun initView() {
        super.initView()
        inflate(mContext, R.layout.robot_test_animation_view, this)
        faceAnimationView = findViewById(R.id.face_view)
        //        initFaceListAndPlay(faceAnimationView);
        //faceAnimationView.nextAnimation();
        addListeners()
    }

    private fun addListeners() {
        faceAnimationView!!.setOnClickListener { faceAnimationView!!.nextAnimation() }
    }

    override fun downToUp() {
    }

    override fun upToDown() {
    }

    fun playAnimationView(playType: Int) {
        if (playType == FaceConsts.FACE_SLEEP) {
            playSleepAnimation(faceAnimationView!!)
        } else if (playType == FaceConsts.FACE_DEEP_SLEEP) {
            playDeepSleepAnimation(faceAnimationView!!)
        }
    }

    fun stopAnimationView() {
        if (faceAnimationView != null) {
            faceAnimationView!!.release()
        }
    }

    private fun playSleepAnimation(view: FaceAnimationView) {
        view.addFrameAnimation(FaceConsts.FACE_SLEEP, FaceConsts.FACE_FOLDER_SLEEP)
        view.changeAnimation(FaceConsts.FACE_SLEEP)
    }

    private fun playDeepSleepAnimation(view: FaceAnimationView) {
        view.addFrameAnimation(FaceConsts.FACE_DEEP_SLEEP, FaceConsts.FACE_FOLDER_DEEP_SLEEP)
        view.changeAnimation(FaceConsts.FACE_DEEP_SLEEP)
    }
}
