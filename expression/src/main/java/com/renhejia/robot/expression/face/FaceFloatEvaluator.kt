package com.renhejia.robot.expression.face

import android.animation.TypeEvaluator

/**
 * 表情部分，分段值域的分布计算
 *
 * @author liujunbin
 */
class FaceFloatEvaluator : TypeEvaluator<Number> {
    override fun evaluate(fraction: Float, startValue: Number, endValue: Number): Float {
        val startFloat = startValue.toFloat()
        val endFloat = endValue.toFloat()
        return startFloat + fraction * (endFloat - startFloat)
    }
}
