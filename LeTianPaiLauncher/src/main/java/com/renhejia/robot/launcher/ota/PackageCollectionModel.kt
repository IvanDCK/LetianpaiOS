package com.renhejia.robot.launcher.ota

import com.google.gson.annotations.SerializedName

class PackageCollectionModel {
    @SerializedName("rom_package")
    var romPackage: PackageModel? = null

    @SerializedName("mcu_a_package")
    var mcuPackage: PackageModel? = null


    override fun toString(): String {
        return """
            ${romPackage.toString()}
            
            ${mcuPackage.toString()}
            """.trimIndent()
    }
}
