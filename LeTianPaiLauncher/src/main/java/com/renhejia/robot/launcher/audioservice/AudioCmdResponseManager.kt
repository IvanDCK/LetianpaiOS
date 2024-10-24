package com.renhejia.robot.launcher.audioservice

import android.content.Context
import android.os.RemoteException
import com.renhejia.robot.commandlib.consts.MCUCommandConsts
import com.renhejia.robot.commandlib.log.LogUtils.logd
import com.renhejia.robot.commandlib.parser.motion.Motion
import com.renhejia.robot.gesturefactory.manager.GestureManager
import com.renhejia.robot.gesturefactory.parser.GestureData
import com.renhejia.robot.launcher.audioservice.parser.AudioCommand
import com.renhejia.robot.launcher.dispatch.gesture.GestureCallback
import com.renhejia.robot.letianpaiservice.ILetianpaiService
import java.lang.ref.WeakReference

/**
 * 语音命令执行单元
 *
 * @author liujunbin
 */
class AudioCmdResponseManager private constructor(context: Context) {
    private var mContext: Context? = null

    private fun init(context: Context) {
        this.mContext = context
    }

    var i: Int = 0

    init {
        init(context)
    }

    fun responseGesture(gestureData: GestureData?, iLetianpaiService: ILetianpaiService?) {
        responseGestureData(mContext, gestureData, iLetianpaiService)
    }


    fun responseGestures(gesture: String, iLetianpaiService: ILetianpaiService?) {
        val list = GestureManager.getInstance(mContext!!).getRobotGesture(gesture)
        responseGestures(list!!, -1, iLetianpaiService)

        // LogUtils.logd("AudioCmdResponseManager", "responseGestures: 执行的姿态:" + list.size() + "   " + list);
//         if ((list != null) && (list.size() > 0)) {
//             i = 0;
//             Timer mTimer = new Timer();
//             TimerTask mTimerTask = new TimerTask() {
//                 @Override
//                 public void run() {
//                     if (i < list.size()) {
//                         Log.e("letianpai_12346", "========= 1 =========== i1: " + i);
//                         responseGestureData(mContext, list.get(i), iLetianpaiService);
//                         i += 1;
//                         Log.e("letianpai_12346", "========= 1 =========== i2: " + i);
//                     } else if (i >= list.size()) {
//                         Log.e("letianpai_12346", "========= 1 =========== i3: " + i);
//                         i = 0;
//                         // TODO 回到显示模式
// //                        ExpressionChangeCallback.getInstance().showDisplayView(null);
// //                        GestureCallback.getInstance().setGesture(GestureConsts.GESTURE_DEMO);
// //                        GestureCallback.getInstance().setGesture(GestureConsts.GESTURE_TEST0);
// //                        ModeChangeCallback.getInstance().setModeChange(ViewModeConsts.VM_AUTO_MODE);
// //                         ModeChangeCallback.getInstance().setModeChange(ViewModeConsts.VM_DISPLAY_MODE);
// //                        RobotModeManager.getInstance(mContext).switchRobotMode(ViewModeConsts.VM_DISPLAY_MODE, 1);
// //                        ModeChangeCallback.getInstance().setModeChange(ViewModeConsts.VM_AUTO_PLAY_MODE);
// //                        ModeChangeCallback.getInstance().setModeChange(ViewModeConsts.VM_AUTO_PLAY_MODE);
// //                        RobotModeManager.getInstance(mContext).switchToPreviousRobotMode();
//                         mTimer.cancel();
//                     }
//                 }
//             };
//             if ((i < list.size()) && list.get(i).getInterval() != 0) {
//                 mTimer.schedule(mTimerTask, 0, list.get(i).getInterval());
//             } else {
//                 mTimer.schedule(mTimerTask, 0, 2000);
//             }
//
//         }
    }

    fun responseGestures(gesture: String, gestureId: Int, iLetianpaiService: ILetianpaiService?) {
        logd(
            "AudioCmdResponseManager",
            "responseGestures: gesture=$gesture gestureId: $gestureId"
        )
        val list = GestureManager.getInstance(mContext!!).getRobotGesture(gesture)
        responseGestures(list!!, gestureId, iLetianpaiService)

        // if ((list != null) && (list.size() > 0)) {
        //     Timer mTimer = new Timer();
        //     TimerTask mTimerTask = new TimerTask() {
        //         @Override
        //         public void run() {
        //             if (i < list.size()) {
        //                 responseGestureData(mContext, list.get(i), iLetianpaiService);
        //                 i += 1;
        //             } else if (i == list.size()) {
        //                 i = 0;
        //                 // TODO 回到显示模式
        //                 Log.e("letianpai12345678", "switchToPreviousPlayMode_setGesturesComplete: ");
        //                 GestureCallback.getInstance().setGesturesComplete(gesture, gestureId);
        //                 mTimer.cancel();
        //             }
        //         }
        //     };
        //     if ((i < list.size()) && list.get(i).getInterval() != 0) {
        //         mTimer.schedule(mTimerTask, 0, list.get(i).getInterval());
        //     } else {
        //         mTimer.schedule(mTimerTask, 0, 2000);
        //     }
        //
        // }
    }

    fun responseGestures(
        list: ArrayList<GestureData>,
        taskId: Int,
        iLetianpaiService: ILetianpaiService?
    ) {
        GestureDataThreadExecutor.instance.execute {
            logd(
                "AudioCmdResponseManager",
                "run start: taskId:$taskId"
            )
            for (gestureData in list) {
                responseGesture(gestureData, iLetianpaiService)
                try {
                    if (gestureData.interval == 0L) {
                        Thread.sleep(2000)
                    } else {
                        Thread.sleep(gestureData.interval)
                    }
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
            }
            logd(
                "AudioCmdResponseManager",
                "run end: taskId:$taskId"
            )
            GestureCallback.instance.setGesturesComplete("list", taskId)
        }
    }

    companion object {
        private var instance: WeakReference<AudioCmdResponseManager>? = null
        @JvmStatic
        fun getInstance(context: Context): AudioCmdResponseManager {
            var localInstance = instance?.get()
            if (localInstance == null) {
                synchronized(AudioCmdResponseManager::class.java) {
                    localInstance = instance?.get()
                    if (localInstance == null) {
                        localInstance = AudioCmdResponseManager(context.applicationContext)
                        instance = WeakReference(localInstance)
                    }
                }
            }
            return localInstance!!
        }

        @JvmStatic
        fun commandResponse(
            context: Context?,
            commandFrom: String?,
            commandType: String,
            commandData: Any,
            iLetianpaiService: ILetianpaiService?
        ) {
            logd(
                "AudioCmdResponseManager",
                "commandResponse: $commandFrom commandType $commandType commandData  $commandData"
            )
            if (commandFrom == null) {
                return
            }

            //        if (commandType.equals(Const.RhjController.move)) {
//            responseMove(commandData, iLetianpaiService);
//
//        } else if (commandType.equals(Const.RhjController.congraturationBirthday)) {
//            if (instance == null) {
//                instance = new AudioCmdResponseManager(context.getApplicationContext());
//            }
//            AudioCmdResponseManager.getInstance(instance.mContext).responseGestures(GestureConsts.GESTURE_BIRTHDAY, iLetianpaiService);
//
//        } else if (commandType.equals(Const.DUIController.ShutDown)) {
//            Log.e("letianpai_app", "========== 1 ========= viewName: ");
//            ExpressionChangeCallback.getInstance().showShutDown();
//
////            SystemFunctionUtil.shutdownRobot(context);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    SystemFunctionUtil.shutdownRobot(context);
//                }
//            }, 2000);
//
//
//        } else if (commandType.equals(Const.RhjController.turn)) {
//
//
//        } else if (commandType.equals(MCUCommandConsts.COMMAND_TYPE_SOUND)) {
//
//        } else if (commandType.equals(MCUCommandConsts.COMMAND_AUDIO_TURN_AROUND)) {
//
//            turnDirection(iLetianpaiService, ATCmdConsts.AT_STR_MOVEW_LOCAL_ROUND_LEFT, (int) commandData);
//        }
        }

        private fun turnDirection(
            iLetianpaiService: ILetianpaiService,
            direction: String,
            number: Int
        ) {
            try {
//            iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, (new Motion(direction, number)).toString()));
                iLetianpaiService.setMcuCommand(
                    MCUCommandConsts.COMMAND_TYPE_MOTION,
                    Motion(direction, number).toString()
                )
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        /**
         * @param commandData
         * @param iLetianpaiService
         */
        fun responseMove(commandData: Any?, iLetianpaiService: ILetianpaiService) {
            if (commandData is AudioCommand) {
                var direction = commandData.direction
                val number = commandData.number
                if (number != null && direction != null) {
                    when (direction) {
                        "前" -> {
                            direction = MCUCommandConsts.COMMAND_VALUE_MOTION_FORWARD
                        }
                        "后" -> {
                            direction = MCUCommandConsts.COMMAND_VALUE_MOTION_BACKEND
                        }
                        "左" -> {
                            direction = MCUCommandConsts.COMMAND_VALUE_MOTION_BACKEND
                        }
                        "右" -> {
                            direction = MCUCommandConsts.COMMAND_VALUE_MOTION_BACKEND
                        }
                    }
                    val numberInt = number.toInt()
                    try {
//                    iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, (new Motion(direction, numberInt)).toString()));
                        iLetianpaiService.setMcuCommand(
                            MCUCommandConsts.COMMAND_TYPE_MOTION,
                            Motion(direction, numberInt).toString()
                        )
                    } catch (e: RemoteException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /*
    public void responseGestures(ArrayList<GestureData> list, int taskId, ILetianpaiService iLetianpaiService) {

        if ((list != null) && (list.size() > 0)) {
            i=0;
            Timer mTimer = new Timer();
            TimerTask mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (i < list.size()) {
                        responseGestureData(mContext, list.get(i), iLetianpaiService);
                        i += 1;
                    } else if (i == list.size()) {
                        i = 0;
                        // TODO 回到显示模式
                        // ExpressionChangeCallback.getInstance().showDisplayView(null);
                        LogUtils.logd("AudioCmdResponseManager", "run: 执行完成: "+taskId);
                        GestureCallback.getInstance().setGesturesComplete("list", taskId);

                        mTimer.cancel();
                    }
                }
            };
            if ((i < list.size()) && list.get(i).getInterval() != 0) {
                mTimer.schedule(mTimerTask, 0, list.get(i).getInterval());
            } else {
                mTimer.schedule(mTimerTask, 0, 2000);
            }

        }

    }*/
        fun responseGestureData(
            context: Context?,
            gestureData: GestureData?,
            iLetianpaiService: ILetianpaiService?
        ) {
            logGestureData(gestureData)

            if (gestureData == null) {
                return
            }

            //        try {
//            if (gestureData.getTtsInfo() != null) {
//                //响应单元在Launcher
//            }
//            if (gestureData.getExpression() != null) {
//                //响应单元在Launcher
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_FACE, ((gestureData.getExpression()).toString())));
//               // iLetianpaiService.setExpression(MCUCommandConsts.COMMAND_TYPE_FACE, (gestureData.getExpression()).toString());
//            }
//            if (gestureData.getAntennalight() != null) {
//                //响应单元在MCUservice
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_ANTENNA_LIGHT, ((gestureData.getAntennalight()).toString())));
//                iLetianpaiService.setMcuCommand(MCUCommandConsts.COMMAND_TYPE_ANTENNA_LIGHT, (gestureData.getAntennalight()).toString());
//            }
//            if (gestureData.getSoundEffects() != null) {
//                //响应单元在AudioService
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_SOUND, ((gestureData.getSoundEffects()).toString())));
//                iLetianpaiService.setAudioEffect(MCUCommandConsts.COMMAND_TYPE_SOUND,(gestureData.getSoundEffects()).toString());
//            }
//            if (gestureData.getFootAction() != null) {
//                //响应单元在MCUservice
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, (gestureData.getFootAction()).toString()));
//                iLetianpaiService.setMcuCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, (gestureData.getFootAction()).toString());
//            } else {
//                Motion motion = new Motion();
//                //0会立即停止当前的动作
//                //motion.setNumber(0);
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, motion.toString()));
//                iLetianpaiService.setMcuCommand(MCUCommandConsts.COMMAND_TYPE_MOTION, motion.toString());
//            }
//            if (gestureData.getEarAction() != null) {
//                //响应单元在MCUservice
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_ANTENNA_MOTION, (gestureData.getEarAction()).toString()));
//                iLetianpaiService.setMcuCommand(MCUCommandConsts.COMMAND_TYPE_ANTENNA_MOTION, (gestureData.getEarAction()).toString());
//            } else {
//                //天线
////                AntennaMotion antennaMotion=new AntennaMotion("sturn");
////                iLetianpaiService.setCommand(new LtpCommand(MCUCommandConsts.COMMAND_TYPE_ANTENNA_MOTION, (gestureData.getEarAction()).toString()));
//            }
        }

        private fun logGestureData(gestureData: GestureData?) {
//        String log = "";
//        if (gestureData.getFootAction() != null && !gestureData.getFootAction().getMotion().isEmpty()) {
//            log += "   动作:" + gestureData.getFootAction().getDesc();
//        }
//        if (gestureData.getExpression() != null && !gestureData.getExpression().getFace().isEmpty()) {
//            log += "   表情：" + gestureData.getExpression().getDesc();
//        }
//        if (gestureData.getSoundEffects() != null && !gestureData.getSoundEffects().getSound().isEmpty()) {
//            log += "   声音：" + gestureData.getSoundEffects().getDesc();
//        }
//        if (gestureData.getEarAction() != null && !gestureData.getEarAction().getAntenna_motion().isEmpty()) {
//            log += "   耳朵：" + gestureData.getEarAction().getAntenna_motion().isEmpty();
//        }
//
//        if (gestureData.getAntennalight() != null && !gestureData.getAntennalight().getAntenna_light().isEmpty()) {
//            log += "    天线" + gestureData.getAntennalight().getAntenna_light();
//        }
//
            logd(
                "AudioCmdResponseManager",
                "解析给实际执行单元 $gestureData"
            )
        }
    }
}
