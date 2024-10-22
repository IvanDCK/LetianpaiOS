package com.renhejia.robot.display

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.google.gson.Gson
import com.renhejia.robot.commandlib.log.LogUtils.logi
import com.renhejia.robot.display.manager.RobotSkinJsonConversionTools.getSpineClockSkin
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class SpineSkinResPool(private val mContext: Context) {
    private val mAssetManager: AssetManager = mContext.resources.assets
    private var mPathName: String? = ""
    private val gson = Gson()
    private var skin: RobotClockSkin? = null


    private fun getJson(fileName: String, context: Context): String? {
        if (mPathName!!.length > 0 && mPathName!!.startsWith("/")) {
            var jsonStr: String? = null
            try {
                val yourFile = File("$mPathName/$fileName")
                val stream = FileInputStream(yourFile)

                try {
                    val fc = stream.channel
                    val bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size())

                    jsonStr = Charset.defaultCharset().decode(bb).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    stream.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
            }
            return jsonStr
        } else {
            val stringBuilder = StringBuilder()
            var fullname = fileName
            if (mPathName!!.length > 0) {
                fullname = "$mPathName/$fileName"
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
    }

    fun createSkin(pathName: String?): RobotClockSkin {
        reset()

        setSkinPath(pathName)

        val jsonString = getJson("skin.json", mContext)
        //        SpineClockSkin skin = gson.fromJson(jsonString, SpineClockSkin.class);
        skin = getSpineClockSkin(jsonString)

        skin!!.resPool = this

        return skin!!
    }

    fun isValidSpineSkin(spineSkin: String): Boolean {
        logi("Clock_Skin", "spineSkin--1: $spineSkin")
        val mSkinList: Array<String>
        val JSON_FILE_NAME = "skin_list.json"
        val jsonString = getSkinListJson(JSON_FILE_NAME, mContext)
        mSkinList = gson.fromJson(jsonString, Array<String>::class.java)
        for (i in mSkinList.indices) {
            if (mSkinList[i] == spineSkin) {
                return true
            }
        }

        //        List<MineClockSkinItem> installedSkinList = PersonalDbOperator.getInstance(mContext).getMineClockSkinList();
//        if (installedSkinList == null ){
//            LogUtils.logi("Clock_Skin","installedSkinList is null !");
//            return false;
//        }
//        if (installedSkinList.size() == 0){
//            LogUtils.logi("Clock_Skin","installedSkinList.size: == 0 ");
//            return false;
//        }
//        for (int j = 0; j< installedSkinList.size();j++){
//            if (installedSkinList.get(j) != null && installedSkinList.get(j).getSkinPath() != null){
//                LogUtils.logi("Clock_Skin","mPathName00--2: installedSkinList.get(j).getSkinPath()"+ installedSkinList.get(j).getSkinPath());
//                if (installedSkinList.get(j).getSkinPath().equals(spineSkin)){
//                    return true;
//                }
//            }
//        }
        return false
    }

    fun isValidCustomSkin(spineSkin: String?): Boolean {
//        LogUtils.logi("Clock_Skin","spineSkin--1: "+ spineSkin);
//        String JSON_FILE_NAME = "skin_list.json";
//        List<MineClockSkinItem> installedSkinList = PersonalDbOperator.getInstance(mContext).getMineClockSkinList();
//        if (installedSkinList == null ){
//            LogUtils.logi("Clock_Skin","installedSkinList is null !");
//            return false;
//        }
//        if (installedSkinList.size() == 0){
//            LogUtils.logi("Clock_Skin","installedSkinList.size: == 0 ");
//            return false;
//        }
//        for (int j = 0; j< installedSkinList.size();j++){
//            if (installedSkinList.get(j) != null && installedSkinList.get(j).getSkinPath() != null){
//                LogUtils.logi("Clock_Skin","mPathName00--2: installedSkinList.get(j).getSkinPath()"+ installedSkinList.get(j).getSkinPath());
//                if (installedSkinList.get(j).getSkinPath().equals(spineSkin)){
//                    return true;
//                }
//            }
//        }
        return false
    }

    fun setSkinPath(pathName: String?) {
        mPathName = pathName
    }

    fun getSkinPath(): String? {
        return mPathName
    }


    fun reset() {
        mapBitmap.clear()
    }

    var mapBitmap: HashMap<String, Bitmap?> = HashMap()

    fun getBitmap(fileName: String): Bitmap? {
        return getBitmap(fileName, 1.0f, 1.0f)
    }

    fun getBitmap(fileName: String, xRadio: Float, yRadio: Float): Bitmap? {
        val key = "$xRadio-$yRadio-$fileName"

        if (mapBitmap.containsKey(key)) {
            return mapBitmap[key]
        } else {
            var bitmap: Bitmap? = null
            var orig_bitmap: Bitmap? = null

            if (mPathName!!.length > 0 && mPathName!!.startsWith("/")) {
                val fullName = "$mPathName/$fileName"
                orig_bitmap = BitmapFactory.decodeFile(fullName)
            } else {
                if (mPathName!!.length > 0) {
                    val fullName = "$mPathName/$fileName"
                    orig_bitmap = loadBitmapFromAssetFilename(fullName)
                } else {
                    orig_bitmap = loadBitmapFromAssetFilename(fileName)
                }
            }

            if (orig_bitmap != null) {
                val matrix = Matrix()
                matrix.postScale(xRadio, yRadio)
                bitmap = Bitmap.createBitmap(
                    orig_bitmap,
                    0,
                    0,
                    orig_bitmap.width,
                    orig_bitmap.height,
                    matrix,
                    true
                )

                mapBitmap[key] = bitmap
            }
            return bitmap
        }
    }


    private fun loadBitmapFromAssetFilename(filename: String): Bitmap? {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null

        try {
            inputStream = mAssetManager.open(filename)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return bitmap
    }

    fun fillMapBitmap(key: String, bitmap: Bitmap?) {
        val key1 = 1.0f.toString() + "-" + 1.0f + "-" + key
        mapBitmap[key1] = bitmap
    }

    companion object {
        private fun getSkinListJson(fileName: String, context: Context): String {
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


        fun getSkinList(context: Context): List<String> {
            val mSkinList: Array<String>
            val JSON_FILE_NAME = "skin_list.json"
            val jsonString = getSkinListJson(JSON_FILE_NAME, context)
            mSkinList = Gson().fromJson(jsonString, Array<String>::class.java)

            return mSkinList.toList()
        }

        fun getDisplayList(context: Context): List<String> {
            val mSkinList: Array<String>
            val JSON_FILE_NAME = "display/display_list.json"
            val jsonString = getSkinListJson(JSON_FILE_NAME, context)
            mSkinList = Gson().fromJson(jsonString, Array<String>::class.java)
            return mSkinList.toList()
        }
    }
}
