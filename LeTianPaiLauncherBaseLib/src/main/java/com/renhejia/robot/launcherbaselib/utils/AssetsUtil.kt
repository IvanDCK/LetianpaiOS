package com.renhejia.robot.launcherbaselib.utils

import android.content.Context
import java.io.InputStream

object AssetsUtil {
    fun getFile(context: Context, fileName: String): ByteArray? {
        try {
            val `in`: InputStream = context.resources.assets.open(fileName)
            // 获取文件的字节数
            val length: Int = `in`.available()
            // 创建byte数组
            val buffer: ByteArray = ByteArray(length)
            // 将文件中的数据读到byte数组中
            `in`.read(buffer)
            `in`.close()
            return buffer
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
