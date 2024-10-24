package com.renhejia.robot.launcherbaselib.facedetect

import android.content.Context

/**
 * 人脸识别
 */
class FaceDetectManager(private val mContext: Context) {
    fun detectObject(type: Int, `object`: Any?): String? {
        return null
    }

    companion object {
        private var instance: FaceDetectManager? = null
        fun getInstance(context: Context): FaceDetectManager {
            synchronized(FaceDetectManager::class.java) {
                if (instance == null) {
                    instance = FaceDetectManager(context.applicationContext)
                }
                return instance!!
            }
        }
    }
}