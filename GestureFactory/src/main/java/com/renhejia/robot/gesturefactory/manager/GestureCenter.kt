package com.renhejia.robot.gesturefactory.manager

import com.renhejia.robot.commandlib.consts.ATCmdConsts
import com.renhejia.robot.commandlib.consts.MCUCommandConsts
import com.renhejia.robot.commandlib.consts.RobotExpressionConsts
import com.renhejia.robot.commandlib.consts.SoundEffect
import com.renhejia.robot.commandlib.parser.antennalight.AntennaLight
import com.renhejia.robot.commandlib.parser.antennamotion.AntennaMotion
import com.renhejia.robot.commandlib.parser.face.Face
import com.renhejia.robot.commandlib.parser.motion.Motion
import com.renhejia.robot.commandlib.parser.sound.Sound
import com.renhejia.robot.commandlib.parser.tts.Tts
import com.renhejia.robot.gesturefactory.parser.GestureData
import java.util.Random

/**
 * 姿态中心
 *
 * @author liujunbin
 */
class GestureCenter {
    private val expressionList = ArrayList<Face>()
    private val soundList = ArrayList<Sound>()
    private val motionList = ArrayList<Motion>()
    private val lightList = ArrayList<AntennaLight>()
    private val earList = ArrayList<AntennaMotion>()
    private val lightStatusList = ArrayList<AntennaLight>()
    private var random: Random? = null

    private fun initData() {
        random = Random()
    }


    companion object {
        //TODO 需要定义TTS的播放是结束之后菜播放动画
        /**
         * @return
         */
        fun demoGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0020")
            data.earAction = AntennaMotion(3)
            data.footAction = Motion(MCUCommandConsts.COMMAND_VALUE_MOTION_FORWARD, 2)
            data.soundEffects = Sound(MCUCommandConsts.COMMAND_VALUE_SOUND_HAPPY)
            list.add(data)
            return list
        }

        fun resetStandGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.footAction = Motion(MCUCommandConsts.COMMAND_VALUE_MOTION_SET_STRAIGHT, 1)
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_STOP)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            ) //TODO  更新为蓝色,亮3秒
            data.interval = 500
            list.add(data)
            return list
        }

        /**
         * 正常 normal
         *
         * @return
         */
        fun normalGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.expression =
                Face(RobotExpressionConsts.L_FACE_MAIN_IMAGE)
            list.add(data)
            //TODO 待添加
            return list
        }

        fun commonGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0020")
            data.expressionTime = 2
            data.interval = 500
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_PUZZLE)

            list.add(data)
            val data2 = GestureData()
            data2.expression = Face("h0019")
            data2.expressionTime = 2
            data2.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_PUZZLE2) //左顾右盼音效是什么？
            data.interval = 500

            list.add(data2)

            //TODO 待添加
            return list
        }

        fun clockGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0008")
            data.interval = (1000 * 60).toLong()
            //        data.setFootAction(new Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_SWAYING_UP_AND_DOWN1, 17,35));
            data.footAction = Motion(null, 17, 35)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE
            ) //TODO  更新为蓝色,亮3秒
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_CLOCK)
            list.add(data)

            //TODO 待添加
            return list
        }

        fun countdownGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.interval = (1000 * 60).toLong()
            //        data.setFootAction(new Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_SWAYING_UP_AND_DOWN1, 17,35));
            data.footAction = Motion(null, 17, 35)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE
            ) //TODO  更新为蓝色,亮3秒
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_CLOCK)
            list.add(data)

            //TODO 待添加
            return list
        }

        fun standGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0020")
            data.expressionTime = 2
            data.interval = 3000
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_PUZZLE)
            list.add(data)
            return list
        }

        /**
         * 通用随机
         *
         * @return
         */
        fun common01GestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0063")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_CROSS_RIGHT_FOOT, 1)
            data.interval = 500
            list.add(data)

            val data1 = GestureData()
            data1.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.interval = 500
            list.add(data1)

            val data2 = GestureData()
            data2.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_CROSS_LEFT_FOOT, 1)
            data.interval = 500
            list.add(data2)

            val data3 = GestureData()
            data3.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.interval = 500
            list.add(data3)
            //TODO 待添加
            return list
        }

        /**
         * 通用随机
         *
         * @return
         */
        fun commonStandGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0063")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_34, 1)
            data.interval = 500
            list.add(data)


            //TODO 待添加
            return list
        }

        /**
         * pair
         *
         * @return
         */
        fun pairGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_COMFORTABLE)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_ORANGE
            )
            data.interval = 4000
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            )
            data1.interval = 1000
            list.add(data)

            //TODO 待添加
            return list
        }

        /**
         * 开机  startup
         *
         * @return
         */
        fun startupGestureData2(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //展示主表情
            data.expression = Face("h0055")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_FORWARD, 1)
            data.interval = 3000
            list.add(data)

            val data1 = GestureData()
            data1.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_BACK, 1)
            data.interval = 3000
            list.add(data1)

            val data2 = GestureData()
            data2.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_CRAB_STEP_LEFT, 1)
            data2.interval = 2000
            list.add(data2)

            val data3 = GestureData()
            data3.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_CRAB_STEP_RIGHT, 1)
            data3.interval = 2000
            list.add(data3)

            //TODO 待添加
            return list
        }

        /**
         * 关机
         *
         * @return
         */
        fun shutdownGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0055")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_SHUTDOWN)
            list.add(data)
            //TODO 待添加
            return list
        }

        /**
         * 低电量
         *
         * @return
         */
        fun batteryLowerGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED
            ) //TODO 颜色为红色
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_BATTERY_LOW)
            data.interval = 2000
            //TODO 待添加
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            ) //TODO 颜色为红色
            //TODO 待添加
            list.add(data1)
            return list
        }


        fun startChargingGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_START_CHARGE)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE
            ) //TODO  更新为蓝色,亮3秒
            data.soundEffects = Sound("a0044")
            data.interval = 3000
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            )
            data1.interval = 1000
            list.add(data1)
            //TODO 待添加
            return list
        }

        fun birthdayGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression =
                Face(MCUCommandConsts.COMMAND_VALUE_BIRTHDAY)
            data.earAction = AntennaMotion(3)
            data.footAction = Motion(MCUCommandConsts.COMMAND_VALUE_MOTION_FORWARD, 2)
            data.soundEffects = Sound(MCUCommandConsts.COMMAND_VALUE_SOUND_BIRTHDAY)
            data.ttsInfo = Tts("小乐祝你生日快乐! ")
            data.interval = 14000

            list.add(data)
            return list
        }

        fun volumeUpGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("44squintLfetUp")
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_FINISH)
            data.interval = 3000
            list.add(data)
            //TODO 待添加
            return list
        }

        fun volumeDownGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0007")
            data.soundEffects = Sound(SoundEffect.COMMAND_FUNCTION_FINISH)
            data.interval = 3000
            list.add(data)
            //TODO 待添加
            return list
        }

        fun scanGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()

            val data = GestureData()
            data.expression = Face("h0019")
            data.expressionTime = 3
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_TURN_RIGHT, 2)
            data.interval = 4000

            list.add(data)

            val data2 = GestureData()
            data2.expression = Face("h0045")
            data2.expressionTime = 1
            data.interval = 4000

            list.add(data2)

            val data3 = GestureData()
            data3.expression = Face("h0036")
            data3.expressionTime = 3
            data3.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_TURN_LEFT, 2)
            data3.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_FOUND_PEOPLE)
            data.interval = 4000

            list.add(data3)

            val data4 = GestureData()
            data4.expression = Face("h0042")
            data4.expressionTime = 1
            data.interval = 4000

            list.add(data4)

            val data5 = GestureData()
            data5.expression = Face("h0042") //左顾右盼表情
            data5.expressionTime = 3
            data5.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_TURN_RIGHT, 2)
            data5.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_PUZZLE)
            data5.interval = 4000
            list.add(data5)
            //TODO 待添加
            return list
        }

        fun foundOwnerGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0006")
            data.expressionTime = 2
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_LEG, 1)
            data.interval = 2000
            list.add(data)

            val data1 = GestureData()
            data1.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_LEG, 1)
            data1.interval = 2000
            list.add(data1)

            val data2 = GestureData()
            data2.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_LEG, 1)
            data2.interval = 2000
            list.add(data2)

            val data3 = GestureData()
            data3.expression =
                Face(MCUCommandConsts.COMMAND_VALUE_LOVE)
            data3.earAction = AntennaMotion(3)
            data1.interval = 2000
            list.add(data3)

            //TODO 待添加
            return list
        }

        private val foundPeopleMotion = intArrayOf(18, 23)
        private val foundPeopleFace = arrayOf("h0020", "h0015", "h0036")
        private val foundPeopleSound = arrayOf("a0051", "a0095")

        fun foundPeoGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face(
                getRandomString(foundPeopleFace)
            )
            data.footAction =
                Motion(getRandomMotion(foundPeopleMotion))
            data.soundEffects =
                Sound(getRandomString(foundPeopleSound))
            data.earAction = AntennaMotion(3)
            data.interval = 3000
            list.add(data)
            return list
        }

        private val foundNoPeopleMotion = intArrayOf(19, 43)
        private val foundNoPeopleFace = arrayOf("h0022", "h0040")
        private val foundNoPeopleSound = arrayOf("a0036", "a0078")

        fun foundNoPeoGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face(
                getRandomString(foundNoPeopleFace)
            )
            data.footAction =
                Motion(getRandomMotion(foundNoPeopleMotion))
            data.soundEffects =
                Sound(getRandomString(foundNoPeopleSound))
            //        data.setEarAction(new AntennaMotion(3));
            data.interval = 3000
            list.add(data)
            return list
        }


        /**
         * @return
         */
        fun robotFoundPeoGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            var motionList = ArrayList<Motion?>()
            val random = Random()
            var expressionList = ArrayList<Face?>()
            expressionList = initExpression2(expressionList)
            //TODO 需要更换 MontionList
            motionList = initMotionList(motionList)
            val data = GestureData()
            data.expression = getRandomExpression(random, expressionList)
            data.footAction = getRandomFoot(random, motionList)
            //        data.setFootAction(new Motion(MCUCommandConsts.COMMAND_VALUE_MOTION_LEFT_ROUND,1));
            data.expressionTime = 1
            data.interval = 2000
            list.add(data)

            val data1 = GestureData()
            data1.footAction = Motion(MCUCommandConsts.COMMAND_VALUE_MOTION_RIGHT_ROUND, 1)
            data1.interval = 2000
            list.add(data1)
            //TODO 待添加
            return list
        }

        /**
         * @return
         */
        fun robotStandGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            var motionList = ArrayList<Motion?>()
            val random = Random()
            var expressionList = ArrayList<Face?>()
            expressionList = initExpression3(expressionList)
            motionList = initMotionList2(motionList)

            val data = GestureData()
            data.expression = getRandomExpression(random, expressionList)
            data.footAction = getRandomFoot(random, motionList)
            data.expressionTime = 1
            data.interval = 2000
            list.add(data)

            //TODO 待添加
            return list
        }

        /**
         * 待机
         * standby
         *
         * @return
         */
        fun standByGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAND, 1)
            data.soundEffects = Sound("a0050")
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_WHITE
            )
            data.interval = 1000
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            )
            data1.interval = 1000
            list.add(data1)

            //TODO 待添加
            return list
        }

        /**
         * 唤醒(未完成)
         * assistant
         *
         * @return
         */
        fun assistantGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0015")
            data.soundEffects = Sound("a0025")
            data.expressionTime = 1
            data.interval = 2000
            list.add(data)

            val data1 = GestureData()
            data1.expression = Face("h0015")
            data1.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_LEG, 1)
            data1.interval = 2000
            list.add(data1)

            //TODO 待添加
            return list
        }


        private val wakeupListenFace = arrayOf("h0015", "h0019", "h0037", "h0038", "h0059")

        /**
         * 唤醒监听中
         *
         * @return
         */
        fun wakeupListenGesture(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val gestureData = GestureData()
            gestureData.expression = Face(
                wakeupListenFace[getRandomIndex(
                    wakeupListenFace.size
                )]
            )
            //停止音效播放
            gestureData.soundEffects = Sound("stop")
            val motion = Motion()
            motion.number = 0
            gestureData.footAction = motion
            list.add(gestureData)
            return list
        }

        fun wakeupUnderstandGesture(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val gestureData = GestureData()
            gestureData.expression = Face("h0050")
            val motion = Motion()
            motion.number = 34
            gestureData.footAction = motion
            list.add(gestureData)
            return list
        }

        fun wakeupSpeakGesture(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val gestureData = GestureData()
            gestureData.expression = Face("h0006")
            val motion = Motion()
            motion.number = 25
            gestureData.footAction = motion
            list.add(gestureData)
            return list
        }

        //落地
        private val fallGroundFace = arrayOf("h0054", "h0003", "h0029", "h0011")
        private val fallGroundSound = arrayOf("a0083", "a0076", "a0064", "a0068", "a0078")
        private val fallGroundMotion = intArrayOf(33, 34, 1, 2, 49, 60)

        val fallGroundGesture: ArrayList<GestureData>
            /**
             * 落地姿态
             */
            get() {
                val list = ArrayList<GestureData>()
                val gestureData = GestureData()
                gestureData.expression = Face(
                    fallGroundFace[getRandomIndex(fallGroundFace.size)]
                )
                gestureData.soundEffects = Sound(
                    fallGroundSound[getRandomIndex(fallGroundSound.size)]
                )
                val downMotion = Motion()
                downMotion.number = fallGroundMotion[getRandomIndex(fallGroundMotion.size)]
                gestureData.footAction = downMotion
                // AntennaMotion antennaMotion = new AntennaMotion(fallEar[getRandomIndex(fallEar.length)]);
                // AntennaLight light = new AntennaLight("on", getRandomIndex(9));
                // gestureData.setAntennalight(light);
                // gestureData.setEarAction(antennaMotion);
                gestureData.interval = 2000
                list.add(gestureData)
                return list
            }

        // 倒下
        private val fallDownFace = arrayOf("h0001", "h0005", "h0011", "h0016", "h0033", "h0052")
        private val fallDownSound = arrayOf("a0003", "a0006", "a0098", "a0086", "a0082", "a0020")
        private val fallDownMotion = intArrayOf(49, 59, 17, 48)

        val fallDownGesture: ArrayList<GestureData>
            /**
             * 倒下姿态
             */
            get() {
                val list = ArrayList<GestureData>()
                val gestureData = GestureData()
                gestureData.expression = Face(
                    fallDownFace[getRandomIndex(fallDownFace.size)]
                )
                gestureData.soundEffects = Sound(
                    fallDownSound[getRandomIndex(fallDownSound.size)]
                )
                val downMotion = Motion()
                downMotion.number = fallDownMotion[getRandomIndex(fallDownMotion.size)]
                gestureData.footAction = downMotion
                // AntennaMotion antennaMotion = new AntennaMotion(fallEar[getRandomIndex(fallEar.length)]);
                // AntennaLight light = new AntennaLight("on", getRandomIndex(9));
                // gestureData.setAntennalight(light);
                // gestureData.setEarAction(antennaMotion);
                gestureData.interval = 2000
                list.add(gestureData)
                return list
            }

        // 单击
        private val tapFace =
            arrayOf("h0009", "h0028", "h0022", "h0023", "h0024", "h0025", "h0045", "h0043")
        private val tapSound =
            arrayOf("a0016", "a0023", "a0028", "a0051", "a0053", "a0071", "a0074", "a0092")
        private val tapMotion = intArrayOf(31, 32, 37, 38, 44, 45, 46, 47)

        val tapGesture: ArrayList<GestureData>
            /**
             * 单击姿态
             */
            get() {
                val list = ArrayList<GestureData>()
                val gestureData = GestureData()
                gestureData.expression = Face(
                    tapFace[getRandomIndex(tapFace.size)]
                )
                gestureData.soundEffects = Sound(
                    tapSound[getRandomIndex(
                        tapSound.size
                    )]
                )
                val downMotion = Motion()
                downMotion.number = tapMotion[getRandomIndex(tapMotion.size)]
                gestureData.footAction = downMotion
                // AntennaMotion antennaMotion = new AntennaMotion(fallEar[getRandomIndex(fallEar.length)]);
                val light =
                    AntennaLight("on", getRandomIndex(9))
                gestureData.antennalight = light
                // gestureData.setEarAction(antennaMotion);
                gestureData.interval = 3000
                list.add(gestureData)
                return list
            }

        // 双击
        private val doubleTapFace = arrayOf("h0034", "h0052", "h0001", "h0011")
        private val doubleTapSound = arrayOf("a0005", "a0020", "a0034", "a0065")
        private val doubleTapMotion = intArrayOf(17, 49, 55, 56, 1, 2)

        /**
         * 双击姿态
         */
        fun getdoubleTapGesture(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val gestureData = GestureData()
            gestureData.expression = Face(
                doubleTapFace[getRandomIndex(
                    doubleTapFace.size
                )]
            )
            gestureData.soundEffects = Sound(
                doubleTapSound[getRandomIndex(doubleTapSound.size)]
            )
            val downMotion = Motion()
            downMotion.number = doubleTapMotion[getRandomIndex(
                doubleTapMotion.size
            )]
            gestureData.footAction = downMotion
            // AntennaMotion antennaMotion = new AntennaMotion(fallEar[getRandomIndex(fallEar.length)]);
            val light = AntennaLight("on", getRandomIndex(9))
            gestureData.antennalight = light
            // gestureData.setEarAction(antennaMotion);
            gestureData.interval = 3000
            list.add(gestureData)
            return list
        }

        // 长按
        private val longPressFace = arrayOf("h0054", "h0056", "h0010", "h0052", "h0001")
        private val longPressSound = arrayOf("a0034", "a0049", "a0055", "a0077", "a0083")
        private val longPressMotion = intArrayOf(50, 58, 48, 19)

        val longPressGesture: ArrayList<GestureData>
            /**
             * 长按姿态
             */
            get() {
                val list = ArrayList<GestureData>()
                val gestureData = GestureData()
                gestureData.expression = Face(
                    longPressFace[getRandomIndex(longPressFace.size)]
                )
                gestureData.soundEffects = Sound(
                    longPressSound[getRandomIndex(longPressSound.size)]
                )
                val downMotion = Motion()
                downMotion.number = longPressMotion[getRandomIndex(longPressMotion.size)]
                gestureData.footAction = downMotion
                // AntennaMotion antennaMotion = new AntennaMotion(fallEar[getRandomIndex(fallEar.length)]);
                val light =
                    AntennaLight("on", getRandomIndex(9))
                gestureData.antennalight = light
                // gestureData.setEarAction(antennaMotion);
                gestureData.interval = 3000
                list.add(gestureData)
                return list
            }

        private val danglingFace = arrayOf("h0052")
        private val danglingMotion = intArrayOf(17, 49, 43)

        /**
         * 悬空 (天线一直转)
         * assistant
         *
         * @return
         */
        fun danglingGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = GestureData()
            //        data1.setFootAction(new Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_FOOT1, 1));
            val motion = Motion()
            motion.number = danglingMotion[getRandomIndex(
                danglingMotion.size
            )]
            data1.footAction = motion
            data1.expression = Face(
                danglingFace[getRandomIndex(
                    danglingFace.size
                )]
            )
            data1.soundEffects = Sound("a0076")
            data1.earAction = AntennaMotion(3)

            val light = AntennaLight("on", getRandomIndex(9))
            data1.antennalight = light

            data1.interval = 3000
            list.add(data1)

            //TODO 待添加
            return list
        }

        fun danglingStopGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = GestureData()
            //        data1.setFootAction(new Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_FOOT1, 1));
            val motion = Motion()
            motion.number = 34
            motion.stepNum = 4
            data1.footAction = motion
            data1.expression = Face("h0027")
            data1.soundEffects = Sound("click")
            val antennaMotion = AntennaMotion(3)
            data1.earAction = antennaMotion
            data1.interval = 1000
            list.add(data1)
            return list
        }

        /**
         * 放跌落表情随机其中之一
         */
        private val fallFace = arrayOf("h0016", "h0011", "h0028", "h0001", "h0049")

        /**
         * 放跌落声音随机其中之一
         */
        private val fallSound = arrayOf("a0003", "a0012", "a0007", "a0019")

        /**
         * 防跌落天线随机其中之一
         */
        private val fallEar = intArrayOf(1, 2, 3)

        private val fallGesture: GestureData
            /**
             * 防跌落统一表情与声音等
             *
             * @return
             */
            get() {
                val gestureData = GestureData()
                gestureData.expression = Face(
                    fallFace[getRandomIndex(fallFace.size)]
                )
                gestureData.soundEffects = Sound(
                    fallSound[getRandomIndex(fallSound.size)]
                )
                val antennaMotion = AntennaMotion(
                    fallEar[getRandomIndex(fallEar.size)]
                )
                val light =
                    AntennaLight("on", getRandomIndex(9))
                gestureData.antennalight = light
                gestureData.earAction = antennaMotion
                gestureData.interval = 2000
                return gestureData
            }

        /**
         * 防跌落前进不调整动作，可以调整其他
         */
        fun fallForwardGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = fallGesture
            val motion = Motion()
            motion.number = 63
            motion.stepNum = 3
            data1.footAction = motion

            list.add(data1)
            return list
        }

        /**
         * 防跌落后退不调整动作，可以调整其他
         */
        fun fallBackendGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = fallGesture
            val motion = Motion()
            motion.number = 64
            motion.stepNum = 3
            data1.footAction = motion
            list.add(data1)
            return list
        }

        /**
         * 防跌落走左不调整动作，可以调整其他
         */
        fun fallLeftGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = fallGesture
            val motion = Motion()
            motion.number = 6
            motion.stepNum = 3
            data1.footAction = motion
            list.add(data1)
            return list
        }

        /**
         * 防跌落右走不调整动作，可以调整其他
         */
        fun fallRightGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data1 = fallGesture
            val motion = Motion()
            motion.number = 5
            motion.stepNum = 3
            data1.footAction = motion
            list.add(data1)
            return list
        }

        /**
         * 防跌落
         * assistant
         *
         * @return
         */
        fun fallPreventionGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0016")
            data.expressionTime = 1
            data.soundEffects = Sound(SoundEffect.COMMAND_VALUE_SOUND_FEAR)
            val antennaMotion = AntennaMotion(3)
            antennaMotion.speed = 60
            antennaMotion.angle = 90
            data.earAction = antennaMotion
            data.interval = 2000
            list.add(data)

            //TODO 待添加
            return list
        }

        /**
         * 落地
         * assistant
         *
         * @return
         */
        fun whereaboutsGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()

            val data = GestureData()
            data.expression = Face("h0029")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_LEG, 1)
            data.interval = 1500
            list.add(data)

            val data1 = GestureData()
            data1.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_LEG1, 1)
            data1.interval = 1000
            list.add(data1)

            val data2 = GestureData()
            data2.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_LEG, 1)
            data2.interval = 1000
            list.add(data2)

            //TODO 待添加
            return list
        }


        fun autoDisplayGestureData(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()


            val data3 = GestureData()
            data3.expression = Face("h0027")
            data3.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED
            )
            data3.interval = 1000
            list.add(data3)

            val data1 = GestureData()
            data1.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            )
            data1.interval = 1000
            list.add(data1)

            return list
        }

        private val face6To8 = arrayOf("h0069", "h0067", "h0068")
        private val face8To10 = arrayOf("h0066", "h0075")
        private val face10To12 = arrayOf("h0071", "h0070")
        private val face12To14 = arrayOf("h0074")
        private val face14To16 = arrayOf("h0073")
        private val face16To18 = arrayOf("h0064")
        private val face18To20 = arrayOf("h0072")
        private val face20To22 = arrayOf("h0065", "h0071")
        private val face22To6 = arrayOf("h0004", "h0039", "h0055")

        /**
         * 24小时展示的
         *
         * @return
         */
        fun hourGestureData(hour: Int): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            var faceName: String? = null
            var movieDuration = 3
            when (hour) {
                6, 7 -> faceName = getRandomString(face6To8)
                8, 9 -> faceName = getRandomString(face8To10)
                10, 11 -> faceName = getRandomString(face10To12)
                12, 13 -> faceName = getRandomString(face12To14)
                14, 15 -> faceName = getRandomString(face14To16)
                16, 17 -> faceName = getRandomString(face16To18)
                18, 19 -> faceName = getRandomString(face18To20)
                20, 21 -> faceName = getRandomString(face20To22)
                22, 23, 24, 0, 1, 2, 3, 4, 5 -> faceName = getRandomString(face22To6)
            }

            when (faceName) {
                "h0064" -> movieDuration = 12
                "h0065" -> movieDuration = 13
                "h0066" -> movieDuration = 17
                "h0067" -> movieDuration = 10
                "h0068" -> movieDuration = 21
                "h0069" -> movieDuration = 12
                "h0070" -> movieDuration = 6
                "h0071" -> movieDuration = 10
                "h0072" -> movieDuration = 13
                "h0073" -> movieDuration = 10
                "h0074" -> movieDuration = 9
                "h0075" -> movieDuration = 13
                "h0004" -> movieDuration = 4
                "h0039" -> movieDuration = 11
                "h0055" -> movieDuration = 3
            }
            data.expression = Face(faceName!!)
            data.footAction = Motion("null", 0)
            data.interval = ((movieDuration - 6) * 1000).toLong()
            data.antennalight = AntennaLight("on", getRandomIndex(9))
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight("on", getRandomIndex(9))
            data1.interval = 2000
            list.add(data1)
            val data2 = GestureData()
            data2.antennalight = AntennaLight("on", getRandomIndex(9))
            val antennaMotion = AntennaMotion(3)
            antennaMotion.speed = 600
            antennaMotion.angle = 45
            data2.earAction = antennaMotion
            data2.interval = 2000
            list.add(data2)
            val data3 = GestureData()
            data3.antennalight = AntennaLight("on", getRandomIndex(9))
            data3.interval = 2000
            list.add(data3)

            return list
        }

        fun hourGestureDataWithName(faceName: String): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            var movieDuration = 3

            when (faceName) {
                "h0064" -> movieDuration = 12
                "h0065" -> movieDuration = 13
                "h0066" -> movieDuration = 17
                "h004cloud" -> movieDuration = 10
                "h0068" -> movieDuration = 21
                "h0069" -> movieDuration = 12
                "h0070" -> movieDuration = 6
                "h0071" -> movieDuration = 10
                "h0072" -> movieDuration = 13
                "h0073" -> movieDuration = 10
                "h0074" -> movieDuration = 9
                "h0075" -> movieDuration = 13
                "h0004" -> movieDuration = 4
                "h0039" -> movieDuration = 11
                "h0055" -> movieDuration = 3
            }
            data.expression = Face(faceName)
            data.footAction = Motion("null", 0)
            data.interval = ((movieDuration - 6) * 1000).toLong()
            data.antennalight = AntennaLight("on", getRandomIndex(9))
            list.add(data)

            val data1 = GestureData()
            data1.antennalight = AntennaLight("on", getRandomIndex(9))
            data1.interval = 2000
            data1.footAction = Motion("null", 34)
            list.add(data1)
            val data2 = GestureData()
            data2.antennalight = AntennaLight("on", getRandomIndex(9))
            data2.earAction = AntennaMotion(3)
            data2.interval = 2000
            list.add(data2)
            val data3 = GestureData()
            data3.antennalight = AntennaLight("on", getRandomIndex(9))
            //        data3.setSoundEffects(new Sound(""));
            data3.interval = 2000
            list.add(data3)

            return list
        }

        val allHour: ArrayList<GestureData>
            get() {
                val list = ArrayList<GestureData>()
                val allHourFace = arrayOf(
                    "h0064", "h0065", "h0066", "h004cloud", "h0068", "h0069", "h0070",
                    "h0071", "h0072", "h0073", "h0074", "h0075"
                )
                for (i in allHourFace.indices) {
                    list.addAll(hourGestureDataWithName(allHourFace[i]))
                }
                return list
            }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        fun test_GestureData1(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //        data.setExpression(new Face(MCUCommandConsts.COMMAND_VALUE_FACE_ANGRY));
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAMP_LEFT_FOOT1, 2)
            data.interval = 3000
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED
            )
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData2(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //        data.setExpression(new Face(MCUCommandConsts.COMMAND_VALUE_FACE_SAD));
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_STAMP_RIGHT_FOOT1, 2)
            data.interval = 3000
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_GREEN
            )
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData3(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0001")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_LEANING1, 2)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE
            )
            data.interval = 3000
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData4(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("55wronged")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_LEANING1, 2)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_ORANGE
            )
            data.interval = 3000
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData5(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //        data.setExpression(new Face(MCUCommandConsts.COMMAND_VALUE_FACE_BORED));
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_FOOT1, 2)
            data.interval = 3000
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_WHITE
            )
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }


        fun test_GestureData6(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //        data.setExpression(new Face(MCUCommandConsts.COMMAND_VALUE_FACE_EXCITING));
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_FOOT1, 2)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_YELLOW
            )
            data.interval = 3000
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData7(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0010")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_CROSS_RIGHT_FOOT1, 2)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_PURPLE
            )
            data.interval = 3000
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData8(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            //        data.setExpression(new Face(MCUCommandConsts.COMMAND_VALUE_FACE_LOSE));
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_CROSS_LEFT_FOOT1, 2)
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_CYAN
            )
            data.interval = 3000
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }

        fun test_GestureData0(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val data = GestureData()
            data.expression = Face("h0027")
            data.footAction = Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_SWAYING_UP_AND_DOWN1, 2)
            data.interval = 2000
            data.antennalight = AntennaLight(
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK
            )
            //        data.setSoundEffects(new Sound(SoundEffect.COMMAND_VALUE_SOUND_BASHFUL));
            list.add(data)

            //TODO 待添加
            return list
        }


        private fun initStatusLight(lightStatusList: ArrayList<AntennaLight>): ArrayList<AntennaLight> {
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_OFF,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_GREEN
                )
            )
            return lightStatusList
        }

        private fun initEarList(earList: ArrayList<AntennaMotion?>): ArrayList<AntennaMotion?> {
            earList.add(AntennaMotion(3))
            earList.add(null)
            return earList
        }

        private fun intLightList(lightStatusList: ArrayList<AntennaLight>): ArrayList<AntennaLight> {
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_GREEN
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_ORANGE
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_WHITE
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_YELLOW
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_PURPLE
                )
            )
            lightStatusList.add(
                AntennaLight(
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_ON,
                    MCUCommandConsts.COMMAND_VALUE_ANTENNA_LIGHT_COLOR_CYAN
                )
            )
            return lightStatusList
        }

        //Done
        private fun initMotionList(motionList: ArrayList<Motion?>): ArrayList<Motion?> {
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_HEAD1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_SWINGS_FROM_SIDE_TO_SIDE1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_STAND_AT_EASE1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_LEFT_FOOT1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_RIGHT_FOOT1, 1))
            return motionList
        }

        //Done 找人前
        private fun initMotionList2(motionList: ArrayList<Motion?>): ArrayList<Motion?> {
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_29, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_30, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_31, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_HEAD1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_CROSS_LEFT_FOOT1, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_SHAKE_SWINGS_FROM_SIDE_TO_SIDE1, 1))
            return motionList
        }

        //Done random
        private fun initMotionList3(motionList: ArrayList<Motion?>): ArrayList<Motion?> {
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_34, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_54, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_48, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_49, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_50, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_31, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_32, 1))

            return motionList
        }

        //Done
        private fun initMotionList4(motionList: ArrayList<Motion>): ArrayList<Motion> {
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_39, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_40, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_41, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_42, 1))
            motionList.add(Motion(ATCmdConsts.AT_STR_MOVEW_31, 1))
            return motionList
        }

        //Done
        private fun initSoundList(soundList: ArrayList<Sound>): ArrayList<Sound> {
            soundList.add(Sound(SoundEffect.COMMAND_VALUE_SOUND_RESPOND))
            soundList.add(Sound(SoundEffect.COMMAND_VALUE_SOUND_HAPPY))
            soundList.add(Sound(SoundEffect.COMMAND_VALUE_SOUND_PANIC))
            soundList.add(Sound(SoundEffect.COMMAND_VALUE_SOUND_SURPRISE))
            soundList.add(Sound(SoundEffect.COMMAND_VALUE_SOUND_TIRED))
            return soundList
        }


        private fun initExpression1(expressionList: ArrayList<Face?>): ArrayList<Face?> {
//        expressionList.add(new Face(MCUCommandConsts.COMMAND_VALUE_EXCITING));
            expressionList.add(Face("h0033"))
            expressionList.add(Face("h0015"))
            expressionList.add(Face("h0041"))
            expressionList.add(Face("h0030"))


            return expressionList
        }

        private fun initExpression2(expressionList: ArrayList<Face?>): ArrayList<Face?> {
            expressionList.add(Face("h0021"))
            expressionList.add(Face("h0011"))
            expressionList.add(Face("h0017"))
            expressionList.add(Face("h0044"))

            return expressionList
        }

        private fun initExpression3(expressionList: ArrayList<Face?>): ArrayList<Face?> {
            expressionList.add(Face("h0044"))
            expressionList.add(Face("h0042"))
            expressionList.add(Face("h0025"))
            expressionList.add(Face("h0024"))
            expressionList.add(Face("h0019"))

            return expressionList
        }

        private fun getRandomFoot(random: Random, motionList: ArrayList<Motion?>): Motion? {
            val randNum = random.nextInt(motionList.size)
            val motion = motionList[randNum]
            return motionList[randNum]
        }

        private fun getRandomSoundEffect(random: Random, soundList: ArrayList<Sound>): Sound {
            val randNum = random.nextInt(soundList.size)
            return soundList[randNum]
        }

        private fun getRandomExpression(random: Random, expressionList: ArrayList<Face?>): Face? {
            val randNum = random.nextInt(expressionList.size)
            return expressionList[randNum]
        }


        private fun getRandomLight(
            random: Random,
            lightList: ArrayList<AntennaLight>
        ): AntennaLight {
            val randNum = random.nextInt(lightList.size)
            return lightList[randNum]
        }

        private fun getRandomEarList(
            random: Random,
            earList: ArrayList<AntennaMotion?>
        ): AntennaMotion? {
            val randNum = random.nextInt(earList.size)
            return earList[randNum]
        }

        val randomGesture: ArrayList<GestureData>
            get() {
                val list = ArrayList<GestureData>()
                val random = Random()
                var expressionList =
                    ArrayList<Face?>()
                var soundList = ArrayList<Sound>()
                var motionList = ArrayList<Motion?>()
                var lightList =
                    ArrayList<AntennaLight>()
                val earList =
                    ArrayList<AntennaMotion?>()
                var lightStatusList =
                    ArrayList<AntennaLight>()

                earList.add(AntennaMotion(3))
                earList.add(null)
                expressionList = initExpression1(expressionList)
                motionList = initMotionList3(motionList)
                soundList = initSoundList(soundList)
                lightList = intLightList(lightList)

                lightStatusList = initStatusLight(lightStatusList)

                val data = GestureData()
                data.expression = getRandomExpression(
                    random,
                    expressionList
                )
                data.footAction = getRandomFoot(random, motionList)
                data.antennalight = getRandomLight(random, lightList)
                data.soundEffects = getRandomSoundEffect(
                    random,
                    soundList
                )
                data.earAction = getRandomEarList(random, earList)
                data.interval = 2500
                list.add(data)
                return list
            }

        fun youPinGestures(): ArrayList<GestureData> {
            val list = ArrayList<GestureData>()
            val gestureData10 = GestureData()
            gestureData10.ttsInfo =
                Tts("小米的朋友们，大家好，我是乐天派！很高兴认识你们！")
            gestureData10.expression = Face("h0027")
            gestureData10.interval = 5000
            val gestureData20 = GestureData()
            gestureData20.ttsInfo =
                Tts("这还是我第一次离开乐天派的办公室见到这么多的好朋友，有点害羞~")
            gestureData20.expression = Face("h0037")
            gestureData20.interval = 5000
            val gestureData30 = GestureData()
            gestureData30.ttsInfo =
                Tts("下面我给大家做个自我介绍吧")
            gestureData30.expression = Face("h0025")
            gestureData30.interval = 5000
            val gestureData40 = GestureData()
            gestureData40.ttsInfo = Tts("我能歌善舞")
            gestureData40.expression = Face("h0043")
            gestureData40.interval = 3000
            val gestureData50 = GestureData()
            gestureData50.ttsInfo =
                Tts("我可以提醒你的日程：叮~~欣然姐，今年我们有约会哦，3点不见不散")
            gestureData50.expression = Face("h0008")
            gestureData50.interval = 5000
            val gestureData60 = GestureData()
            gestureData60.ttsInfo =
                Tts("如果你爽约了，我会不高兴，但是你可以摸摸我的头~快来安慰我吧")
            gestureData60.expression = Face("h0011")
            gestureData60.interval = 5000
            val gestureData70 = GestureData()
            gestureData70.ttsInfo =
                Tts("除此之外，我还能做好多事情")
            gestureData70.expression = Face("h0023")
            gestureData70.interval = 5000
            val gestureData80 = GestureData()
            gestureData80.ttsInfo =
                Tts("我接入了米家APP，可以和智能家居联动")
            gestureData80.expression = Face("h0047")
            gestureData80.interval = 5000
            val gestureData90 = GestureData()
            gestureData90.ttsInfo =
                Tts("以上就是我的自我介绍，希望你们喜欢我，谢谢！")
            gestureData90.expression = Face("h0027")
            gestureData90.interval = 5000

            list.add(gestureData10)
            list.add(gestureData20)
            list.add(gestureData30)
            list.add(gestureData40)
            list.add(gestureData50)
            list.add(gestureData60)
            list.add(gestureData70)
            list.add(gestureData80)
            list.add(gestureData90)

            return list
        }

        private fun getRandomIndex(length: Int): Int {
            val r = Random()
            return r.nextInt(length)
        }

        private fun getRandomString(group: Array<String>): String {
            val r = Random()
            return group[r.nextInt(group.size)]
        }

        private fun getRandomMotion(group: IntArray): Int {
            val r = Random()
            return group[r.nextInt(group.size)]
        }
    }
}
