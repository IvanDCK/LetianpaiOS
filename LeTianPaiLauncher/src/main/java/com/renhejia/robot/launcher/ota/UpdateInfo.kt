package com.renhejia.robot.launcher.ota

/**
 * Created by binzo on 2018/4/2.
 */
class UpdateInfo {
    val code: Int? = null
    val data: Data? = null
    val msg: String? = null

    override fun toString(): String {
        val sb = StringBuffer()
        sb.append("data: {\n")
        sb.append("    code: ").append(code).append("\n")
        sb.append("    msg: ").append(msg).append("\n")
        if (data == null) sb.append("    data: null")
        else sb.append(data.toString())
        sb.append("}\n")
        return sb.toString()
    }
}
