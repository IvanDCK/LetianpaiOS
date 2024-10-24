package com.renhejia.robot.expression.face

import android.animation.TypeEvaluator
import android.graphics.Point

/**
 * 渐变的Point
 *
 * @author liujunbin
 */
class FacePointEvaluator : TypeEvaluator<Point> {
    override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
        val x = (startValue.x + fraction * (endValue.x - startValue.x)).toInt()
        val y = (startValue.y + fraction * (endValue.y - startValue.y)).toInt()
        return Point(x, y)
    }
}
