package com.renhejia.robot.expression.face

import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

/**
 * 动画配置解析器
 *
 * @author liujunbin
 */
class AnimationConfigParser {
    /**
     * 解析xml
     *
     * @param is
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun parse(`is`: InputStream?): List<FaceFrame?>? {
        val factory = SAXParserFactory.newInstance()
        val parser = factory.newSAXParser()
        val handler = FrameHandler()
        parser.parse(`is`, handler)
        return handler.getFrames()
    }
    companion object {
        private const val STEP_MODE_SCALE = 0x1
        private const val STEP_MODE_ALPHA = 0x2
        private const val STEP_MODE_ROTATE = 0x3
    }

    private inner class FrameHandler : DefaultHandler() {
        private var frames: MutableList<FaceFrame?>? = null
        private var frame: FaceFrame? = null
        private var subFrame: FaceFrame.SubFrame? = null
        private var duration = 0
        private var stepMode = 0

        //返回解析后得到的Frame对象集合  
        fun getFrames(): List<FaceFrame?>? {
            return frames
        }

        @Throws(SAXException::class)
        override fun startDocument() {
            super.startDocument()
            frames = ArrayList()
        }

        @Throws(SAXException::class)
        override fun startElement(
            uri: String,
            localName: String,
            qName: String,
            attributes: Attributes
        ) {
            super.startElement(uri, localName, qName, attributes)

            when (localName) {
                "animation" -> {
                    duration = attributes.getValue("duration").toInt()
                }
                "frame" -> {
                    // 逐帧动画的一帧
                    frame = FaceFrame()
                    frame!!.setImageName(attributes.getValue("drawable"))
                    val frames = attributes.getValue("frames").toInt()
                    frame!!.duration = (frames * duration)
                    frame!!.frames = (frames)
                }
                "sub-frame" -> {
                    // 其中一个子帧
                    subFrame = FaceFrame.SubFrame()
                    subFrame!!.drawableName = (attributes.getValue("drawable"))
                }
                "scale" -> {
                    // 添加缩放的参数
                    stepMode = Companion.STEP_MODE_SCALE
                }
                "alpha" -> {
                    // 添加透明度的参数
                    stepMode = Companion.STEP_MODE_ALPHA
                }
                "rotate" -> {
                    stepMode = Companion.STEP_MODE_ROTATE
                    //            } else if ("step".equals(localName)) {
        //            	float range = Float.parseFloat(attributes.getValue("range"));
        //            	float ratio = Float.parseFloat(attributes.getValue("ratio"));
        //            	addStep(stepMode, range, ratio);
                }
                "step" -> {
                    val x = attributes.getValue("x").toFloat()
                    val y = attributes.getValue("y").toFloat()
                    val ratio = attributes.getValue("ratio").toFloat()
                    addStep(stepMode, Scale(x, y), ratio)
                }
                "point" -> {
                    val x = attributes.getValue("x").toInt()
                    val y = attributes.getValue("y").toInt()
                    val ratio = attributes.getValue("ratio").toFloat()
                    subFrame!!.addPivotStep(x, y, ratio)
                }
            }
        }

        /**
         * 添加一个渐变的步骤
         * @param mode
         * @param range
         * @param ratio
         */
        fun addStep(mode: Int, range: Scale, ratio: Float) {
            when (mode) {
                Companion.STEP_MODE_SCALE ->                // 缩放
//        		subFrame.addScaleStep(range, ratio);
                    subFrame!!.addScalesStep(range, ratio)
            }
        }

        @Throws(SAXException::class)
        override fun endElement(uri: String, localName: String, qName: String) {
            super.endElement(uri, localName, qName)
            if ("frame" == localName) {
                frames!!.add(frame)
            } else if ("sub-frame" == localName) {
                frame!!.addSubFrame(subFrame)
            }
        }


    }
}
