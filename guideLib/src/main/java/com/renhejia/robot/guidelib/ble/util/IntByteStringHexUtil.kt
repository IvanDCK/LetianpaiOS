package com.renhejia.robot.guidelib.ble.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder

/**
 *
 */
object IntByteStringHexUtil {
    /**
     * 将一个整形化为十六进制，并以字符串的形式返回
     */
    private val hexArray =
        arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")

    fun intToHexStr(n: Int): String {
        var n = n
        if (n < 0) {
            n = n + 256
        }
        val d1 = n / 16
        val d2 = n % 16
        return hexArray[d1] + hexArray[d2]
    }

    fun hexStrToInt(str: String): Int {
        return str.toInt(16)
    }

    fun hexStrToLong(str: String): Long {
        return str.toLong(16)
    }

    //单个字符转成十六进制
    fun oneStrToHexStr(string: String): String {
        if (string == "") {
            return "00"
        }

        var n = string.toInt()

        if (n < 0) {
            n = n + 256
        }
        val d1 = n / 16
        val d2 = n % 16
        return hexArray[d1] + hexArray[d2]
    }

    //将字符串中的每个字符转成十六进制
    fun strToHexStr(str: String): String {
        var hexStr = ""

        val chars = str.toCharArray()
        for (i in chars.indices) {
            hexStr += Integer.toHexString(chars[i].code)
        }

        return hexStr
    }

    fun strToHexStrTemp(str: String): String {
        var hexStr = ""

        val chars = str.toCharArray()
        for (i in chars.indices) {
            var temStr = Integer.toHexString(chars[i].code)
            if (temStr.length == 1) {
                temStr = "0$temStr"
            }
            hexStr += temStr
        }

        return hexStr
    }

    /**
     * 十六进制 转换成ASCII字符串
     * @param hex
     * @return
     */
    fun hex2Str(hex: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < hex.length - 1) {
            val h = hex.substring(i, (i + 2))
            if (h == "00") {
                i += 2
                continue
            }
            val decimal = h.toInt(16)
            sb.append(decimal.toChar())
            i += 2
        }
        return sb.toString()
    }

    fun utf8ToStr(utf8: String): String? {
        var utf8 = utf8
        var strUTF8: String? = null
        try {
            val s1 = utf8.replace("(.{2})".toRegex(), "$1%")
            utf8 = "%" + s1.substring(0, s1.length - 1)
            strUTF8 = URLDecoder.decode(utf8, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return strUTF8
    }

    //将字符串中的每个字符的ASCII转成十六进制
    fun strASCIIToHexStr(str: String): String {
        var hexStr = ""

        val chars = str.toCharArray()
        for (i in chars.indices) {
            val number = chars[i].code
            hexStr += intToHexStr(number)
        }

        return hexStr
    }

    fun hexStrToCharStr(str: String?): String? {
        if (str == null) {
            return null
        }
        if (str.length == 0) {
            return ""
        }
        val sb = StringBuilder()
        val charArray = CharArray(str.length / 2)
        for (i in charArray.indices) {
            val subStr = str.substring(2 * i, 2 * i + 2)
            val c = subStr.toInt(16) as Char
            sb.append(c)
        }
        return sb.toString()
    }

    fun hexStrToByteArray(str: String?): ByteArray? {
        if (str == null) {
            return null
        }
        if (str.length == 0) {
            return ByteArray(0)
        }
        val byteArray = ByteArray(str.length / 2)
        for (i in byteArray.indices) {
            val subStr = str.substring(2 * i, 2 * i + 2)
            byteArray[i] = (subStr.toInt(16) as Byte)
        }
        return byteArray
    }

    fun byteArrayToHexStr(byteArray: ByteArray?): String? {
        if (byteArray == null) {
            return null
        }
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(byteArray.size * 2)
        for (j in byteArray.indices) {
            val v = byteArray[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun StrToAddHexStr(strings: Array<String>): String {
        var all = "0".toLong(16)
        for (i in strings.indices) {
            val one = strings[i].toLong(16)
            all = all + one
        }
        return java.lang.Long.toHexString(256 - (all % 256))
    }

    fun <T> concatAll(first: Array<T>, vararg rest: Array<T>): Array<T?> {
        var totalLength = first.size
        for (array in rest) {
            totalLength += array.size
        }
        val result = first.copyOf(totalLength)
        var offset = first.size
        for (array in rest) {
            System.arraycopy(array, 0, result, offset, array.size)
            offset += array.size
        }
        return result
    }

    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    fun hexStr2Str(hexStr: String): String {
        val str = "0123456789ABCDEF"
        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var n: Int
        for (i in bytes.indices) {
            n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])
            bytes[i] = (n and 0xff).toByte()
        }
        return String(bytes)
    }

    fun intToHexString(n: Int): String {
        var s = Integer.toHexString(n)
        if (s.length % 2 != 0) {
            s = "0$s"
        }
        return s
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(hexStrToByteArray("55AA0100010001").contentToString())
    }
}