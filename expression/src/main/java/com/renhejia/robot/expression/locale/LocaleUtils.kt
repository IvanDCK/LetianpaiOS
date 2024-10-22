package com.renhejia.robot.expression.locale

import java.util.Locale

object LocaleUtils {
    private const val PRC_LAUNGUAGE = "zh"
    val isChinese: Boolean
        get() {
            val language = Locale.getDefault().language
            return if (language == PRC_LAUNGUAGE) {
                true
            } else {
                false
            }
        }
}
