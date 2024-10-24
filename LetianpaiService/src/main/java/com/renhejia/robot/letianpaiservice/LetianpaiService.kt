package com.renhejia.robot.letianpaiservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.RemoteCallbackList
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.letianpai.robot.letianpaiservice.LtpAppCmdCallback
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class LetianpaiService : Service() {
    private var robotStatus = 0
    private val ROBOT_OTA_STATUS = 1
    private val RemoteCallbackList: RemoteCallbackList<LtpCommandCallback?> =
        RemoteCallbackList<LtpCommandCallback?>()
    private val ltpLongConnectCallback: RemoteCallbackList<LtpLongConnectCallback?> =
        RemoteCallbackList<LtpLongConnectCallback?>()
    private val ltpMcuCommandCallback: RemoteCallbackList<LtpMcuCommandCallback?> =
        RemoteCallbackList<LtpMcuCommandCallback?>()

    //
    private val ltpAudioEffectCallback: RemoteCallbackList<LtpAudioEffectCallback?> =
        RemoteCallbackList<LtpAudioEffectCallback?>()
    private val ltpExpressionCallback: RemoteCallbackList<LtpExpressionCallback?> =
        RemoteCallbackList<LtpExpressionCallback?>()
    private val ltpAppCmdCallback: RemoteCallbackList<LtpAppCmdCallback?> =
        RemoteCallbackList<LtpAppCmdCallback?>()
    private val ltpRobotStatusCallback: RemoteCallbackList<LtpRobotStatusCallback?> =
        RemoteCallbackList<LtpRobotStatusCallback?>()
    private val ltpTTSCallback: RemoteCallbackList<LtpTTSCallback?> =
        RemoteCallbackList<LtpTTSCallback?>()
    private val ltpSpeechCallback: RemoteCallbackList<LtpSpeechCallback?> =
        RemoteCallbackList<LtpSpeechCallback?>()
    private val ltpSensorResponseCallback: RemoteCallbackList<LtpSensorResponseCallback?> =
        RemoteCallbackList<LtpSensorResponseCallback?>()
    private val ltpMiCmdCallback: RemoteCallbackList<LtpMiCmdCallback?> =
        RemoteCallbackList<LtpMiCmdCallback?>()
    private val ltpIdentifyCmdCallback: RemoteCallbackList<LtpIdentifyCmdCallback?> =
        RemoteCallbackList<LtpIdentifyCmdCallback?>()
    private val ltpBleCallback: RemoteCallbackList<LtpBleCallback?> =
        RemoteCallbackList<LtpBleCallback?>()
    private val ltpBleResponseCallback: RemoteCallbackList<LtpBleResponseCallback?> =
        RemoteCallbackList<LtpBleResponseCallback?>()

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return iLetianpaiService
    }

    private val timeHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            Log.i(javaClass.getSimpleName(), "发送消息：" + msg.what)

            //            callBack(msg.what);
            super.handleMessage(msg)
        }
    }

    private val mLock: Lock = ReentrantLock()
    private val mMcuLock: Lock = ReentrantLock()
    private val mAeLock: Lock = ReentrantLock()
    private val mAppLock: Lock = ReentrantLock()
    private val mExpressionLock: Lock = ReentrantLock()
    private val mLongConnectLock: Lock = ReentrantLock()
    private val mStatusLock: Lock = ReentrantLock()
    private val mTTSLock: Lock = ReentrantLock()
    private val mSpeechSLock: Lock = ReentrantLock()
    private val mMiLock: Lock = ReentrantLock()
    private val mIdentifyLock: Lock = ReentrantLock()
    private val mSensorLock: Lock = ReentrantLock()
    private val mBleLock: Lock = ReentrantLock()
    private val mBleResponseLock: Lock = ReentrantLock()
    private val iLetianpaiService: ILetianpaiService.Stub = object : Stub() {
        @get:kotlin.Throws(RemoteException::class)
        @set:kotlin.Throws(RemoteException::class)
        var robotStatus: Int
            set(status) {
                field = status
                setLTPRobotStatus(field)
                // TODO 分发命令回调
            }

        @kotlin.Throws(RemoteException::class)
        override fun setCommand(ltpCommand: LtpCommand?) {
            if (ltpCommand == null) {
                Log.i("letianpai_server", "ltpCommand is null")
            } else {
                Log.i("letianpai_server", "ltpCommand is not null")
            }
            if (robotStatus == ROBOT_OTA_STATUS) {
            } else {
                responseCommand(ltpCommand)
            }
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerCallback(cc: LtpCommandCallback?) {
            RemoteCallbackList.register(cc)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterCallback(cc: LtpCommandCallback?) {
            RemoteCallbackList.unregister(cc)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setLongConnectCommand(command: String, data: String) {
            responseLongConnectCommand(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerLCCallback(lcCallback: LtpLongConnectCallback?) {
            ltpLongConnectCallback.register(lcCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterLCCallback(lcCallback: LtpLongConnectCallback?) {
            ltpLongConnectCallback.unregister(lcCallback)
        }


        @kotlin.Throws(RemoteException::class)
        override fun setMcuCommand(command: String, data: String) {
            responseMcuCommand(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerMcuCmdCallback(mcuCallback: LtpMcuCommandCallback?) {
            ltpMcuCommandCallback.register(mcuCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterMcuCmdCallback(mcuCallback: LtpMcuCommandCallback?) {
            ltpMcuCommandCallback.unregister(mcuCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setAudioEffect(command: String, data: String) {
            Log.e("letianpai_sound0", "==== lettianpaiservice_1")
            responseAudioEffectCmd(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerAudioEffectCallback(aeCallback: LtpAudioEffectCallback?) {
            ltpAudioEffectCallback.register(aeCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterAudioEffectCallback(aeCallback: LtpAudioEffectCallback?) {
            ltpAudioEffectCallback.unregister(aeCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setExpression(command: String, data: String) {
            onExpressionChange(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerExpressionCallback(expressionCallback: LtpExpressionCallback?) {
            ltpExpressionCallback.register(expressionCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterExpressionCallback(expressionCallback: LtpExpressionCallback?) {
            ltpExpressionCallback.unregister(expressionCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setAppCmd(command: String, data: String) {
            setAppCommand(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerAppCmdCallback(appCallback: LtpAppCmdCallback?) {
            ltpAppCmdCallback.register(appCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterAppCmdCallback(appCallback: LtpAppCmdCallback?) {
            ltpAppCmdCallback.unregister(appCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setRobotStatusCmd(command: String, data: String) {
            setRobotStatusCmds(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerRobotStatusCallback(statusCallback: LtpRobotStatusCallback?) {
            ltpRobotStatusCallback.register(statusCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterRobotStatusCallback(statusCallback: LtpRobotStatusCallback?) {
            ltpRobotStatusCallback.unregister(statusCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setTTS(command: String, data: String) {
            setRobotTTS(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerTTSCallback(ttsCallback: LtpTTSCallback?) {
            ltpTTSCallback.register(ttsCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterTTSCallback(ttsCallback: LtpTTSCallback?) {
            ltpTTSCallback.unregister(ttsCallback)
        }


        @kotlin.Throws(RemoteException::class)
        override fun setSpeechCmd(command: String, data: String) {
            setSpeechCmds(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerSpeechCallback(speechCallback: LtpSpeechCallback?) {
            ltpSpeechCallback.register(speechCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterSpeechCallback(speechCallback: LtpSpeechCallback?) {
            ltpSpeechCallback.unregister(speechCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setSensorResponse(command: String, data: String) {
            responseSensorCommand(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerSensorResponseCallback(sensorCallback: LtpSensorResponseCallback?) {
            ltpSensorResponseCallback.register(sensorCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterSensorResponseCallback(speechCallback: LtpSensorResponseCallback?) {
            ltpSensorResponseCallback.unregister(speechCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setMiCmd(command: String, data: String) {
            responseMiCmd(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerMiCmdResponseCallback(miCmdCallback: LtpMiCmdCallback?) {
            ltpMiCmdCallback.register(miCmdCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterMiCmdResponseCallback(miCmdCallback: LtpMiCmdCallback?) {
            ltpMiCmdCallback.unregister(miCmdCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setIdentifyCmd(command: String, data: String) {
            responseIdentifyCmd(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerIdentifyCmdCallback(identifyCmdCallback: LtpIdentifyCmdCallback?) {
            ltpIdentifyCmdCallback.register(identifyCmdCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterIdentifyCmdCallback(identifyCmdCallback: LtpIdentifyCmdCallback?) {
            ltpIdentifyCmdCallback.unregister(identifyCmdCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setBleCmd(command: String, data: String, isNeedResponse: Boolean) {
            responseBleCmd(command, data, isNeedResponse)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerBleCmdCallback(bleCallback: LtpBleCallback?) {
            ltpBleCallback.register(bleCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterBleCmdCallback(bleCallback: LtpBleCallback?) {
            ltpBleCallback.unregister(bleCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun setBleResponse(command: String, data: String) {
            responseBleResponse(command, data)
        }

        @kotlin.Throws(RemoteException::class)
        override fun registerBleResponseCallback(bleResponseCallback: LtpBleResponseCallback?) {
            ltpBleResponseCallback.register(bleResponseCallback)
        }

        @kotlin.Throws(RemoteException::class)
        override fun unregisterBleResponseCmdCallback(bleResponseCallback: LtpBleResponseCallback?) {
            ltpBleResponseCallback.unregister(bleResponseCallback)
        }
    }

    private fun responseBleResponse(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mBleResponseLock.lock()
        try {
            val N = ltpBleResponseCallback.beginBroadcast()
            if (N > 0) {
                for (i in 0 until N) {
                    try {
                        if (ltpBleResponseCallback.getBroadcastItem(i) != null) {
                            ltpBleResponseCallback.getBroadcastItem(i)
                                .onBleCmdResponsReceived(command, data)
                        }
                        Log.e(
                            TAG,
                            "responseBleResponse:=== count:$N  current:$i"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "responseBleResponse:  异常$i"
                        )
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpBleResponseCallback.finishBroadcast()
            mBleResponseLock.unlock()
        }
    }

    private fun responseBleCmd(command: String, data: String, isNeedResponse: Boolean) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mBleLock.lock()
        try {
            val N = ltpBleCallback.beginBroadcast()
            if (N > 0) {
                for (i in 0 until N) {
                    try {
                        if (ltpBleCallback.getBroadcastItem(i) != null) {
                            ltpBleCallback.getBroadcastItem(i)
                                .onBleCmdReceived(command, data, isNeedResponse)
                        }
                        Log.e(
                            TAG,
                            "responseBleCmd:=== count:$N  current:$i"
                        )
                    } catch (e: Exception) {
                        Log.e(
                            TAG,
                            "responseBleCmd:  异常$i"
                        )
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpBleCallback.finishBroadcast()
            mBleLock.unlock()
        }
    }

    private fun responseIdentifyCmd(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mIdentifyLock.lock()
        try {
            val N = ltpIdentifyCmdCallback.beginBroadcast()
            if (N > 0) {
                for (i in 0 until N) {
                    try {
                        if (ltpIdentifyCmdCallback.getBroadcastItem(i) != null) {
                            ltpIdentifyCmdCallback.getBroadcastItem(i)
                                .onIdentifyCommandReceived(command, data)
                        }
                        Log.e(
                            TAG,
                            "responseMiCmd:=== count:$N  current:$i"
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "responseMiCmd:  异常$i")
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpIdentifyCmdCallback.finishBroadcast()
            mIdentifyLock.unlock()
        }
    }

    private fun responseMiCmd(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mMiLock.lock()
        try {
            val N = ltpMiCmdCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                try {
                    if (ltpMiCmdCallback.getBroadcastItem(i) != null) {
                        ltpMiCmdCallback.getBroadcastItem(i).onMiCommandReceived(command, data)
                    }
                    Log.e(
                        TAG,
                        "responseMiCmd:=== count:$N  current:$i"
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "responseMiCmd:  异常$i")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpMiCmdCallback.finishBroadcast()
            mMiLock.unlock()
        }
    }

    private fun setSpeechCmds(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mSpeechSLock.lock()
        try {
            val N = ltpSpeechCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                try {
                    if (ltpSpeechCallback.getBroadcastItem(i) != null) {
                        ltpSpeechCallback.getBroadcastItem(i).onSpeechCommandReceived(command, data)
                    }
                    Log.e(
                        TAG,
                        "setSpeechCmds:=== count:$N  current:$i"
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "setSpeechCmds:  异常$i")
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpSpeechCallback.finishBroadcast()
            mSpeechSLock.unlock()
        }
    }

    private fun setRobotTTS(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mTTSLock.lock()
        try {
            val N = ltpTTSCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpTTSCallback.getBroadcastItem(i) != null) {
                    ltpTTSCallback.getBroadcastItem(i).onTTSCommand(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpTTSCallback.finishBroadcast()
            mTTSLock.unlock()
        }
    }

    private fun setRobotStatusCmds(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mStatusLock.lock()
        try {
            val N = ltpRobotStatusCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpRobotStatusCallback.getBroadcastItem(i) != null) {
                    ltpRobotStatusCallback.getBroadcastItem(i).onRobotStatusChanged(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpRobotStatusCallback.finishBroadcast()
            mStatusLock.unlock()
        }
    }


    //    private void setAppCommand(String command, String data) {
    //        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
    //            return;
    //        }
    //        mAppLock.lock();
    //        try {
    //            int N = ltpAppCmdCallback.beginBroadcast();
    //            if (N > 0){
    //                for (int i = 0; i < N; i++) {
    //                    Log.d("-------", "--" + N);
    //                    Log.d("-------", "--" + i);
    //                    if (ltpAppCmdCallback.getBroadcastItem(i) != null) {
    //                        ltpAppCmdCallback.getBroadcastItem(i).onAppCommandReceived(command, data);
    //                    }
    //                }
    //            }
    //
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        } finally {
    //            ltpAppCmdCallback.finishBroadcast();
    //            mAppLock.unlock();
    //        }
    //
    //    }
    private fun setAppCommand(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        synchronized(this) {
            try {
                val N = ltpAppCmdCallback.beginBroadcast()
                if (N > 0) {
                    for (i in 0 until N) {
                        Log.d("-------", "--$N")
                        Log.d("-------", "--$i")
                        if (ltpAppCmdCallback.getBroadcastItem(i) != null) {
                            ltpAppCmdCallback.getBroadcastItem(i)
                                .onAppCommandReceived(command, data)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                ltpAppCmdCallback.finishBroadcast()
            }
        }
    }

    private fun onExpressionChange(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mExpressionLock.lock()
        try {
            val N = ltpExpressionCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpExpressionCallback.getBroadcastItem(i) != null) {
                    ltpExpressionCallback.getBroadcastItem(i).onExpressionChanged(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpExpressionCallback.finishBroadcast()
            mExpressionLock.unlock()
        }
    }

    private fun responseAudioEffectCmd(command: String, data: String) {
        Log.e("letianpai_sound0", "==== lettianpaiservice_2")
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        Log.e("letianpai_sound0", "==== lettianpaiservice_3")
        mAeLock.lock()
        try {
            val N = ltpAudioEffectCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                Log.e("letianpai_sound0", "==== lettianpaiservice_4")
                if (ltpAudioEffectCallback.getBroadcastItem(i) != null) {
                    ltpAudioEffectCallback.getBroadcastItem(i).onAudioEffectCommand(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            Log.e("letianpai_sound0", "==== lettianpaiservice_5")
            ltpAudioEffectCallback.finishBroadcast()
            mAeLock.unlock()
        }
    }

    private fun responseMcuCommand(command: String, data: String) {
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mMcuLock.lock()
        try {
            val N = ltpMcuCommandCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpMcuCommandCallback.getBroadcastItem(i) != null) {
                    ltpMcuCommandCallback.getBroadcastItem(i).onMcuCommandCommand(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpMcuCommandCallback.finishBroadcast()
            mMcuLock.unlock()
        }
    }

    private fun setLTPRobotStatus(robotStatus: Int) {
        mLock.lock()

        try {
            val N = RemoteCallbackList.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (RemoteCallbackList.getBroadcastItem(i) != null) {
                    RemoteCallbackList.getBroadcastItem(i).onRobotStatusChanged(robotStatus)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            RemoteCallbackList.finishBroadcast()
            mLock.unlock()
        }
    }

    private fun responseCommand(ltpCommand: LtpCommand?) {
        mLock.lock()
        try {
            val N = RemoteCallbackList.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (RemoteCallbackList.getBroadcastItem(i) != null) {
                    RemoteCallbackList.getBroadcastItem(i).onCommandReceived(ltpCommand)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            RemoteCallbackList.finishBroadcast()
            mLock.unlock()
        }
    }

    private fun responseLongConnectCommand(command: String, data: String) {
        Log.d("<<<<", "responseLongConnectCommand: command--$command-----data::$data")
        if (TextUtils.isEmpty(command) || (TextUtils.isEmpty(data))) {
            return
        }
        mLongConnectLock.lock()
        try {
            val N = ltpLongConnectCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpLongConnectCallback.getBroadcastItem(i) != null) {
                    ltpLongConnectCallback.getBroadcastItem(i).onLongConnectCommand(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpLongConnectCallback.finishBroadcast()
            mLongConnectLock.unlock()
        }
    }

    private fun responseSensorCommand(command: String, data: String) {
        if (TextUtils.isEmpty(command)) {
            return
        }
        mSensorLock.lock()
        try {
            val N = ltpSensorResponseCallback.beginBroadcast()
            if (N == 0) {
                return
            }
            for (i in 0 until N) {
                if (ltpSensorResponseCallback.getBroadcastItem(i) != null) {
                    ltpSensorResponseCallback.getBroadcastItem(i).onSensorResponse(command, data)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            ltpSensorResponseCallback.finishBroadcast()
            mSensorLock.unlock()
        }
    }

    companion object {
        private const val TAG = "LetianpaiService"
    }
}
