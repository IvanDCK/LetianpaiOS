package com.renhejia.robot.gesturefactory.manager

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.renhejia.robot.gesturefactory.parser.gesture.Gesture
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * 姿态解析器
 * @author liujunbin
 */
class GestureResPool(private val mContext: Context) {
    private val mAssetManager: AssetManager = mContext.resources.assets
    var skinPath: String = ""
    private val gson = Gson()
    private var gestureData: Gesture? = null

    private fun getJson(fileName: String, context: Context): String {
        val stringBuilder = StringBuilder()
        var fullname = fileName
        if (skinPath.length > 0) {
            fullname = skinPath + "/" + fileName
        }

        try {
            val assetManager = context.assets
            val bf = BufferedReader(
                InputStreamReader(
                    assetManager.open(fullname)
                )
            )
            var line: String?
            while ((bf.readLine().also { line = it }) != null) {
                stringBuilder.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    fun createGesture(pathName: String): Gesture? {
        skinPath = pathName
        val jsonString = getJson("gesture.json", mContext)
        gestureData = gson.fromJson(
            jsonString,
            Gesture::class.java
        )
        return gestureData
    }

    fun isValidSpineSkin(spineSkin: String): Boolean {
        val mSkinList: Array<String>
        val JSON_FILE_NAME = "skin_list.json"
        val jsonString = getSkinListJson(JSON_FILE_NAME, mContext)
        mSkinList = gson.fromJson(jsonString, Array<String>::class.java)
        for (i in mSkinList.indices) {
            if (mSkinList[i] == spineSkin) {
                return true
            }
        }
        return false
    }


    companion object {
        fun getSkinListJson(fileName: String, context: Context): String {
            val stringBuilder = StringBuilder()
            val fullname = fileName

            try {
                val assetManager = context.assets
                val bf = BufferedReader(
                    InputStreamReader(
                        assetManager.open(fullname)
                    )
                )
                var line: String?
                while ((bf.readLine().also { line = it }) != null) {
                    stringBuilder.append(line)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return stringBuilder.toString()
        }


        fun getGestureList(context: Context): Array<String> {
            val mSkinList: Array<String>
            val JSON_FILE_NAME = "gesture/gesture_list.json"
            val jsonString = getSkinListJson(JSON_FILE_NAME, context)
            mSkinList = Gson().fromJson(jsonString, Array<String>::class.java)
            return mSkinList
        }
    }
}
