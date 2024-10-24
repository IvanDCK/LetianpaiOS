package com.renhejia.robot.gesturefactory.manager

import android.content.Context
import java.lang.ref.WeakReference

/**
 * 解析管理器
 * @author liujunbin
 */
class ParserManager(private val mContext: Context) {
    companion object {
        private var parserManager: WeakReference<ParserManager>? = null

        fun getInstance(context: Context): ParserManager {
            var localParserManager = parserManager?.get()
            if (localParserManager == null) {
                synchronized(ParserManager::class.java) {
                    localParserManager = parserManager?.get()
                    if (localParserManager == null) {
                        localParserManager = ParserManager(context.applicationContext)
                        parserManager = WeakReference(localParserManager) // Wrap in WeakReference
                    }
                }
            }
            return localParserManager!!
        }

        private fun getGestureById(gestureId: Int) {
        }
    }
}
