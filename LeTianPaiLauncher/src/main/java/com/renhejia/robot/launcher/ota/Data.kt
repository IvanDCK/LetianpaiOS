package com.renhejia.robot.launcher.ota

import com.google.gson.annotations.SerializedName

/**
 * Created by binzo on 2018/4/2.
 */
class Data {
    @SerializedName("whole_package_url")
    var wholeUrl: String? = null

    @SerializedName("md5")
    var md5: String? = null

    @SerializedName("upgrade_desc")
    var updateDesc: String? = null

    @SerializedName("byte_size")
    var byteSize: Long = 0

    @SerializedName("update_time")
    var updateTime: Long = 0

    @SerializedName("package_collection")
    var packageCollectionModel: PackageCollectionModel? = null

    @SerializedName("version")
    var version: String? = null
}
