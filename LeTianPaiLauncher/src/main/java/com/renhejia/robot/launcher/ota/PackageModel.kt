package com.renhejia.robot.launcher.ota

import com.google.gson.annotations.SerializedName

class PackageModel {
    @SerializedName("is_update")
    var isUpdate: String? = null

    @SerializedName("rom_package_url")
    private var url: String? = null

    @SerializedName("version")
    var version: String? = null

    @SerializedName("byte_size")
    var byteSize: Long = 0

    @SerializedName("md5")
    var md5: String? = null

    @SerializedName("update_time")
    var updateTime: Long = 0

    @SerializedName("upgrade_desc")
    var updateDesc: String? = null


    fun setUrl(url: String?) {
        this.url = url
    }

    fun getUrl(): String {
        val replaced = url!!.replace("\u0026", "&")
        return replaced
    }


    override fun toString(): String {
        val sb = StringBuffer()
        sb.append("    data: {\n")
        sb.append("        isUpdate: ").append(isUpdate).append("\n")
        sb.append("        url: ").append(url).append("\n")
        sb.append("        version: ").append(version).append("\n")
        sb.append("        size: ").append(byteSize).append("\n")
        sb.append("        sign: ").append(md5).append("\n")
        sb.append("    }\n")
        return sb.toString()
    }
}
