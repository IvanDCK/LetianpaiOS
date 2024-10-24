package com.renhejia.robot.launcher.encryption

import java.security.MessageDigest

object MD5 {
    fun encode(string: String): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(string.toByteArray())
            val b = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0) {
                    i += 256
                }
                if (i < 16) {
                    buf.append("0")
                }
                buf.append(Integer.toHexString(i))
            }
            return buf.toString()
            // if (type) {
            // return buf.toString(); // 32
            // } else {
            // return buf.toString().substring(8, 24);// 16
            // }
        } catch (e: Exception) {
            return null
        }
    }

    fun encode(bytes: ByteArray): String? {
        try {
            val md = MessageDigest.getInstance("MD5")
            md.update(bytes)
            val b = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0) {
                    i += 256
                }
                if (i < 16) {
                    buf.append("0")
                }
                buf.append(Integer.toHexString(i))
            }
            return buf.toString()
            // if (type) {
            // return buf.toString(); // 32
            // } else {
            // return buf.toString().substring(8, 24);// 16
            // }
        } catch (e: Exception) {
            return null
        }
    }
}
