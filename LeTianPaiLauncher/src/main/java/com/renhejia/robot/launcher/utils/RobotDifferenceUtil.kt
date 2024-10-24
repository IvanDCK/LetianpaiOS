package com.renhejia.robot.launcher.utils

import android.util.Log
import java.util.Locale

/**
 * Launcher 差异化管理器
 * @author liujunbin
 */
object RobotDifferenceUtil {
    private const val PRC_LAUNGUAGE = "zh"

    val isUseOverseaLayout: Boolean
        /**
         * ZxingView是否使用海外版布局
         *
         *
         * @return
         */
        get() = false

    val isChinese: Boolean
        get() {
            val language = Locale.getDefault().language
            Log.e("isChinese", "isChinese: $language")
            return if (language == PRC_LAUNGUAGE) {
                true
            } else {
                false
            }
        }

    val isDisplayIconButton: Boolean
        /**
         * 是否显示海外版图标
         * @return
         */
        get() = isUseOverseaLayout && (!isChinese)
}
