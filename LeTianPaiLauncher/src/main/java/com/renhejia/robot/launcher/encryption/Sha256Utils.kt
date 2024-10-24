package com.renhejia.robot.launcher.encryption

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Sha256Utils {
    /**
     * sha256加密
     *
     * @param str 要加密的字符串
     * @return 加密后的字符串
     */
    fun getSha256Str(str: String): String {
        val messageDigest: MessageDigest
        var encodeStr = ""
        try {
            messageDigest = MessageDigest.getInstance("SHA-256")
            messageDigest.update(str.toByteArray(StandardCharsets.UTF_8))
            encodeStr = byte2Hex(messageDigest.digest())
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return encodeStr
    }

    /**
     * sha256加密 将byte转为16进制
     *
     * @param bytes 字节码
     * @return 加密后的字符串
     */
    private fun byte2Hex(bytes: ByteArray): String {
        val stringBuilder = StringBuilder()
        var temp: String
        for (aByte in bytes) {
            temp = Integer.toHexString(aByte.toInt() and 0xFF)
            if (temp.length == 1) {
                //1得到一位的进行补0操作
                stringBuilder.append("0")
            }
            stringBuilder.append(temp)
        }
        return stringBuilder.toString()
    }
}
