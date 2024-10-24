package com.renhejia.robot.guidelib.ble.ancs

import java.nio.ByteBuffer
import java.util.Locale

/**
 * Created by hxsmart on 2018/3/22.
 */
object StringTools {
    /**
     * @param b      字节对象
     * @param offset 字节位移
     * @param count  字节数
     * @return hexString字符串
     */
    fun Bytes2HexString(b: ByteArray, offset: Int, count: Int): String? {
        if (offset > b.size) {
            return null
        }
        if (offset + count > b.size) {
            return null
        }
        val ret = StringBuilder()
        for (i in 0 until count) {
            var hex = Integer.toHexString(b[offset + i].toInt() and 0xFF)
            if (hex.length == 1) {
                hex = "0$hex"
            }
            ret.append(hex.uppercase(Locale.getDefault()))
        }
        return ret.toString()
    }

    /*左右空格都去掉*/
    fun trim(str: String?): String? {
        return if (str == null || str == "") {
            str
        } else {
            //return leftTrim(rightTrim(str));
            str.replace("^[　 ]+|[　 ]+$".toRegex(), "")
        }
    }

    /*去左空格*/
    fun leftTrim(str: String?): String? {
        return if (str == null || str == "") {
            str
        } else {
            str.replace("^[　 ]+".toRegex(), "")
        }
    }

    /*去右空格*/
    fun rightTrim(str: String?): String? {
        return if (str == null || str == "") {
            str
        } else {
            str.replace("[　 ]+$".toRegex(), "")
        }
    }

    fun fetchByteBuffer(
        byteBuffer: ByteBuffer,
        des: ByteArray,
        offset: Int,
        len: Int,
        delLen: Int
    ): Boolean {
        val bytesLength = byteBuffer.position()
        val inputBuffer = byteBuffer.array()

        if (bytesLength < len + offset || bytesLength < delLen) return false
        System.arraycopy(inputBuffer, offset, des, 0, len)
        byteBuffer.position(delLen)
        byteBuffer.compact()
        byteBuffer.position(bytesLength - delLen)
        return true
    }
}
