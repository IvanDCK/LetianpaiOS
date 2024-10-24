package com.renhejia.robot.expression.face

import android.graphics.Point

/**
 * 表情动画的帧
 *
 * @author liujunbin
 */
class FaceFrame {
    var drawableName: String? = null
        private set
    var duration: Int = 0
    var frames: Int = 0
    private val subFrameList: MutableList<SubFrame?> = ArrayList()

    fun setImageName(imageName: String?) {
        this.drawableName = imageName
    }

    fun addSubFrame(subFrame: SubFrame?) {
        subFrameList.add(subFrame)
    }

    fun getSubFrameList(): List<SubFrame?> {
        return subFrameList
    }

    /**
     * 是否有子帧
     *
     * @return
     */
    fun hasSubFrame(): Boolean {
        return !subFrameList.isEmpty()
    }

    class SubFrame {
        var drawableName: String? = null

        //		private List<Float> scaleRangeList = new ArrayList<Float>();
        private val scaleRangeList: MutableList<Scale> = ArrayList()
        private val scaleRatioList: MutableList<Float> = ArrayList()

        private val alphaRangeList: MutableList<Float> = ArrayList()
        private val alphaRatioList: MutableList<Float> = ArrayList()

        private val pivotRangeList: MutableList<Point> = ArrayList()
        private val pivotRatioList: MutableList<Float> = ArrayList()

        private val degreesRangeList: MutableList<Int> = ArrayList()
        private val degreesRatioList: MutableList<Float> = ArrayList()

        /**
         * 添加缩放参数
         *
         * @param range 缩放大小
         * @param ratio 百分比时间
         */
        //		public void addScaleStep(float range, float ratio) {
        //			scaleRangeList.add(range);
        //			scaleRatioList.add(ratio);
        //		}
        fun addScalesStep(range: Scale, ratio: Float) {
            scaleRangeList.add(range)
            scaleRatioList.add(ratio)
        }

        fun addAlphaStep(range: Float, ratio: Float) {
            alphaRangeList.add(range)
            alphaRatioList.add(ratio)
        }

        fun addPivotStep(x: Int, y: Int, ratio: Float) {
            pivotRangeList.add(Point(x, y))
            pivotRatioList.add(ratio)
        }

        fun addRotateStep(range: Int, ratio: Float) {
            degreesRangeList.add(range)
            degreesRatioList.add(ratio)
        }

        fun hasScaleAnimation(): Boolean {
            return !scaleRangeList.isEmpty()
        }

        val scaleRangeArray: Array<Any>
            get() = scaleRangeList.toTypedArray()

        val scaleRatioArray: FloatArray
            get() = toArray(scaleRatioList)

        fun hasAplhaAnimation(): Boolean {
            return !alphaRangeList.isEmpty()
        }

        val alphaRangeArray: Array<Any>
            get() = alphaRangeList.toTypedArray()

        val alphaRatioArray: FloatArray
            get() = toArray(alphaRatioList)

        val pivotRangeArray: Array<Any>
            get() = pivotRangeList.toTypedArray()

        val pivotRatioArray: FloatArray
            get() = toArray(pivotRatioList)

        fun hasPivotAnimation(): Boolean {
            return !pivotRangeList.isEmpty()
        }

        val rotateRangeArray: Array<Any>
            get() = degreesRangeList.toTypedArray()

        val rotateRatioArray: FloatArray
            get() = toArray(degreesRatioList)

        fun hasRotateAnimation(): Boolean {
            return !degreesRangeList.isEmpty()
        }

        private fun toArray(list: List<Float>): FloatArray {
            val result = FloatArray(list.size)
            for (i in list.indices) {
                result[i] = list[i]
            }
            return result
        }
    }
}
