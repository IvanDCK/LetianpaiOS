package com.renhejia.robot.expression.face

import android.animation.TypeEvaluator

/**
 * 渐变的Point
 *
 * @author liujunbin
 */
class FaceScaleEvaluator : TypeEvaluator<FaceScale> {
    override fun evaluate(fraction: Float, startValue: FaceScale, endValue: FaceScale): FaceScale {
        val x = (startValue.x + fraction * (endValue.x - startValue.x)).toInt().toFloat()
        val y = (startValue.y + fraction * (endValue.y - startValue.y)).toInt().toFloat()
        return FaceScale(x, y)
    } //	@Override
    //	public Point evaluate(float fraction, Point startValue, Point endValue) {
    //		int x = (int) (startValue.x + fraction * (endValue.x - startValue.x));
    //		int y = (int) (startValue.y + fraction * (endValue.y - startValue.y));
    //		return new Point(x, y);
    //	}
}
