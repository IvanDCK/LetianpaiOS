package com.renhejia.robot.commandlib.consts

/**
 * @author liujunbin
 */
object MCUCommandConsts {
    /**
     * ======================================== 命令类型 ========================================
     */
    /**
     * 表情
     */
    const val COMMAND_TYPE_FACE: String = "controlFace"

    const val COMMAND_TYPE_SHOW_TIME: String = "showTIme"

    /**
     * 动作控制
     */
    const val COMMAND_TYPE_MOTION: String = "controlMotion"

    /**
     * 声音控制
     */
    const val COMMAND_TYPE_SOUND: String = "controlSound"

    /**
     * 天线控制
     */
    const val COMMAND_TYPE_ANTENNA_MOTION: String = "controlAntennaMotion"

    /**
     * 天线光控制
     */
    const val COMMAND_TYPE_ANTENNA_LIGHT: String = "controlAntennaLight"

    /**
     * 电源控制
     */
    const val COMMAND_TYPE_POWER_CONTROL: String = "powerControl"

    /**
     * 天线光控制
     */
    const val COMMAND_TYPE_ANTENNA_LIGHT_VALUE_ON: String = "on"
    const val COMMAND_TYPE_ANTENNA_LIGHT_VALUE_OFF: String = "off"

    /**
     * Video call
     */
    const val COMMAND_TYPE_TRTC: String = "trtc"
    const val COMMAND_TYPE_UPDATE_WAKEUP_CONFIG: String = "updateAwakeConfig"

    /**
     * 天线光控制
     */
    const val COMMAND_TYPE_OPEN_MCU: String = "open_mcu"

    const val COMMAND_TYPE_CLOSE_MCU: String = "close_mcu"

    /**
     *
     */
    const val COMMAND_VALUE_ANTENNA_MOTION: String = "turn"

    /**
     *
     */
    const val COMMAND_VALUE_ANTENNA_MOTION_DIY: String = "diy"


    /**
     * ======================================== 声音类型 ================ start ========================
     */
    /**
     * 失落
     */
    const val COMMAND_VALUE_SOUND_LOSE: String = "lose"

    /**
     * 生气
     */
    const val COMMAND_VALUE_SOUND_ANGRY: String = "angry"

    /**
     * 搞怪
     */
    const val COMMAND_VALUE_SOUND_FUNNY: String = "funny"

    /**
     * 愤怒
     */
    const val COMMAND_VALUE_SOUND_ANGER: String = "anger"

    /**
     * 哭泣
     */
    const val COMMAND_VALUE_SOUND_CRY: String = "cry"

    /**
     * 撒娇
     */
    const val COMMAND_VALUE_SOUND_SPOILED: String = "spoiledChild"

    /**
     * 开心
     */
    const val COMMAND_VALUE_SOUND_HAPPY: String = "happy"

    /**
     * 苦笑
     */
    const val COMMAND_VALUE_SOUND_WRY_SMILE: String = "wrySmile"

    /**
     * 伤心
     */
    const val COMMAND_VALUE_SOUND_SAD: String = "sad"


    const val COMMAND_VALUE_SOUND_AVOIDANCE: String = "avoidance"
    const val COMMAND_VALUE_SOUND_CLICK: String = "click"
    const val COMMAND_VALUE_SOUND_MISTAKE: String = "mistake"
    const val COMMAND_VALUE_SOUND_FINISH: String = "finish"
    const val COMMAND_VALUE_SOUND_FIND_PEOPLE: String = "findPerson"
    const val COMMAND_VALUE_SOUND_CLOCK: String = "clock"
    const val COMMAND_VALUE_SOUND_SHUT_DOWN: String = "shutdown"
    const val COMMAND_VALUE_SOUND_STARTUP: String = "startUp"
    const val COMMAND_VALUE_SOUND_LOW_POWER: String = "lowPower"
    const val COMMAND_VALUE_SOUND_CHARGE: String = "charge"
    const val COMMAND_VALUE_SOUND_MAINTOFACE: String = "mainToFace"
    const val COMMAND_VALUE_SOUND_DIZZINESS: String = "dizziness"
    const val COMMAND_VALUE_SOUND_WAKE: String = "wake"
    const val COMMAND_VALUE_SOUND_SLEEP: String = "sleep"
    const val COMMAND_VALUE_SOUND_PANT: String = "pant"
    const val COMMAND_VALUE_SOUND_TIRED: String = "tired"
    const val COMMAND_VALUE_SOUND_FATIGUE: String = "fatigue"
    const val COMMAND_VALUE_SOUND_SHY: String = "shy"
    const val COMMAND_VALUE_SOUND_SURPRISE: String = "surprise"
    const val COMMAND_VALUE_SOUND_FEAR: String = "fear"
    const val COMMAND_VALUE_SOUND_PANIC: String = "panic"
    const val COMMAND_VALUE_SOUND_CONFUSE: String = "confuse"
    const val COMMAND_VALUE_SOUND_DISGUSTED: String = "disgusted"
    const val COMMAND_VALUE_SOUND_HATE: String = "hate"
    const val COMMAND_VALUE_SOUND_COMFORTABLE: String = "comfortable"
    const val COMMAND_VALUE_SOUND_SNEAKPEEK: String = "sneakPeek"
    const val COMMAND_VALUE_SOUND_RESPOND: String = "respond"


    /**
     * ======================================== 声音类型 ================ end ========================
     */
    /**
     * ======================================== 音效类型 ================ start ========================
     */
    /**
     * 生日
     */
    const val COMMAND_VALUE_SOUND_BIRTHDAY: String = "birthday"

    const val COMMAND_AUDIO_TURN_AROUND: String = "audio_turn_around"

    const val COMMAND_SET_APP_MODE: String = "set_app_mode"
    const val COMMAND_SET_SHOW_TIME: String = "showTime"

    /**
     *
     */
    const val COMMAND_SHOW_TEXT: String = "show_text"

    /**
     *
     */
    const val COMMAND_HIDE_TEXT: String = "hide_text"

    /**
     * 锁定全局状态栏文字控制
     */
    const val COMMAND_LOCK_CONTROL_TEXT: String = "lock_control_text"

    /**
     * 全局状态栏文字
     */
    const val COMMAND_LOCK_CONTROL_FULL_TEXT: String = "lock_control_full_text"


    /**
     * ======================================== 音效类型 ================ end ========================
     */
    /**
     * ======================================== 表情类型 ========================================
     */
    /**
     * 苦笑
     */
    const val COMMAND_VALUE_FACE_WRY_SMILE: String = "wrySmile"

    /**
     * 生气
     */
    const val COMMAND_VALUE_FACE_ANGRY: String = "angry"

    /**
     * 伤心
     */
    const val COMMAND_VALUE_FACE_SAD: String = "sad"

    /**
     * 愤怒
     */
    const val COMMAND_VALUE_FACE_ANGER: String = "anger"

    /**
     * 无聊
     */
    const val COMMAND_VALUE_FACE_BORED: String = "bored"

    /**
     * 兴奋
     */
    const val COMMAND_VALUE_FACE_EXCITING: String = "exciting"

    /**
     * 哭泣
     */
    const val COMMAND_VALUE_FACE_CRY: String = "cry"

    /**
     * 失落
     */
    const val COMMAND_VALUE_FACE_LOSE: String = "lose"

    /**
     * 高兴
     */
    const val COMMAND_VALUE_FACE_HAPPY: String = "happy"

    /**
     * 高兴
     */
    const val COMMAND_VALUE_FACE_STAND: String = "stand_yellow"

    /**
     * 生日
     */
    const val COMMAND_VALUE_BIRTHDAY: String = "HappyBirthday_Loop"

    /**
     * 生日
     */
    const val COMMAND_VALUE_BIRTHDAY2: String = "birthday2"

    const val COMMAND_VALUE_COMMONWINK: String = "commonWink"
    const val COMMAND_VALUE_BIG_SMALL_EYE: String = "BigSmallEye"
    const val COMMAND_VALUE_SINGLE_BLINK: String = "singleBlink"
    const val COMMAND_VALUE_PEEP: String = "peep"
    const val COMMAND_VALUE_SQUINT: String = "squint"
    const val COMMAND_VALUE_SINGLE_EXPECT: String = "expect"
    const val COMMAND_VALUE_GLANCE_LEFT_RIGHT: String = "glanceLeftRight"
    const val COMMAND_VALUE_SQUINT_RIGHT_UP: String = "squintRightUp"
    const val COMMAND_VALUE_SQUINT_LEFT_UP: String = "squintLfetUp"
    const val COMMAND_VALUE_SQUINT_RIGHT_DOWN: String = "squintRightDown"
    const val COMMAND_VALUE_SQUINT_LEFT_DOWN: String = "squintLeftDown"
    const val COMMAND_VALUE_SQUINT_LOOK_DOWN: String = "lookDown"
    const val COMMAND_VALUE_CONFUSE: String = "confuse"
    const val COMMAND_VALUE_SHAKE_HEAD: String = "shakeHead"

    const val COMMAND_VALUE_THINK: String = "think"
    const val COMMAND_VALUE_BORED: String = "bored"
    const val COMMAND_VALUE_LISTEN_LEFT: String = "listenLeft"
    const val COMMAND_VALUE_LISTEN_RIGHT: String = "listenRight"
    const val COMMAND_VALUE_LISTEN_HAPPY: String = "happy"
    const val COMMAND_VALUE_BIG_LAUGH: String = "bigLaugh"
    const val COMMAND_VALUE_EXCITING: String = "exciting"
    const val COMMAND_VALUE_LOVE: String = "h0027"
    const val COMMAND_VALUE_WRONGED: String = "wronged"
    const val COMMAND_VALUE_FROWN: String = "frown"
    const val COMMAND_VALUE_LOSE: String = "lose"
    const val COMMAND_VALUE_SAD: String = "sad"
    const val COMMAND_VALUE_CRY: String = "cry"
    const val COMMAND_VALUE_ASHAMED: String = "ashamed"
    const val COMMAND_VALUE_UNWEL: String = "unwell"

    const val COMMAND_VALUE_PAIN: String = "pain"
    const val COMMAND_VALUE_SORRY: String = "sorry"
    const val COMMAND_VALUE_FURIOUS: String = "furious"
    const val COMMAND_VALUE_RESIST: String = "resist"
    const val COMMAND_VALUE_SHUT_UP: String = "shutUp"
    const val COMMAND_VALUE_ENVY: String = "envy"
    const val COMMAND_VALUE_ARROGNT: String = "arrogant"
    const val COMMAND_VALUE_ANGER: String = "anger"
    const val COMMAND_VALUE_SUPRISE: String = "surprise"
    const val COMMAND_VALUE_BARBIQ: String = "barbieQ"
    const val COMMAND_VALUE_SUSPECT: String = "suspect"
    const val COMMAND_VALUE_FEAR: String = "fear"
    const val COMMAND_VALUE_PANTING: String = "panting"
    const val COMMAND_VALUE_YAWN: String = "yawn"
    const val COMMAND_VALUE_WEAK_UP: String = "wakeUp"
    const val COMMAND_VALUE_SLEEP: String = "sleep"
    const val COMMAND_VALUE_SLEEP_OPEN_EYE: String = "sleepOpenEye"
    const val COMMAND_VALUE_DIZZINESS: String = "dizziness"
    const val COMMAND_VALUE_TREMBINE: String = "trembline"
    const val COMMAND_VALUE_CLOCK: String = "clock"
    const val COMMAND_VALUE_PHOTO: String = "photo"
    const val COMMAND_VALUE_WUNAI: String = "haveNoChoice"


    /**
     * ======================================== 动作类型 ========================================
     */
    /**
     * 向前
     */
    const val COMMAND_VALUE_MOTION_FORWARD: String = "forward"

    /**
     * 向后
     */
    const val COMMAND_VALUE_MOTION_BACKEND: String = "backend"

    /**
     * 向左
     */
    const val COMMAND_VALUE_MOTION_LEFT: String = "left"

    /**
     * 向右
     */
    const val COMMAND_VALUE_MOTION_RIGHT: String = "right"

    /**
     * 向左转
     */
    const val COMMAND_VALUE_MOTION_LEFT_ROUND: String = "leftRound"

    /**
     * 向右转
     */
    const val COMMAND_VALUE_MOTION_RIGHT_ROUND: String = "rightRound"

    /**
     * 回正
     */
    const val COMMAND_VALUE_MOTION_SET_STRAIGHT: String = "setStraight"

    /**
     * 转圈
     */
    const val COMMAND_VALUE_MOTION_TURN_ROUND: String = "turnRound"

    /**
     * 撒娇
     */
    const val COMMAND_VALUE_MOTION_PETTISH: String = "pettish"

    /**
     * 生气
     */
    const val COMMAND_VALUE_MOTION_ANGRY: String = "angry"

    /**
     * 奔跑
     */
    const val COMMAND_VALUE_MOTION_RUN: String = "run"

    /**
     * 奔跑
     */
    const val COMMAND_VALUE_MOTION_CHEERS: String = "cheers"

    /**
     * 疲惫
     */
    const val COMMAND_VALUE_MOTION_TRIED: String = "tried"

    /**
     * 抖腿
     */
    const val COMMAND_VALUE_MOTION_SHAKE_LEG: String = "shakeLeg"

    /**
     * ======================================== 动作类型 ========================================
     */
    /**
     * 天线灯开
     */
    const val COMMAND_VALUE_ANTENNA_LIGHT_ON: String = "on"

    /**
     * 天线灯关
     */
    const val COMMAND_VALUE_ANTENNA_LIGHT_OFF: String = "off"

    /**
     * 天线闪烁
     */
    const val COMMAND_VALUE_ANTENNA_LIGHT_TWINKLE: String = "twinkle"

    /**
     * 天线闪烁
     */
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_RED: Int = 1
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_GREEN: Int = 2
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLUE: Int = 3
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_ORANGE: Int = 4
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_WHITE: Int = 5
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_YELLOW: Int = 6
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_PURPLE: Int = 7
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_CYAN: Int = 8
    const val COMMAND_VALUE_ANTENNA_LIGHT_COLOR_BLACK: Int = 9


    /**
     * 天线旋转
     */
    const val COMMAND_VALUE_ANTENNA_MOTION_3: Int = 3
}
