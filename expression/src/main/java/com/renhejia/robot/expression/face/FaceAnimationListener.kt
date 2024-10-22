package com.renhejia.robot.expression.face

/**
 * 表情动画的监听器
 * RobotTestAnimationView
 *
 */
interface FaceAnimationListener {
    /**
     * 刷新表情
     */
    fun invalidateFace()

    /**
     * 局部刷新
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    fun invalidateFace(left: Int, top: Int, right: Int, bottom: Int)
}
