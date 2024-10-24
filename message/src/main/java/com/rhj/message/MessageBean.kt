package com.rhj.message

open class MessageBean {
    open var message_type: Int = 0

    companion object {
        const val TYPE_OUTPUT: Int = 7
        const val TYPE_INPUT: Int = 1
        const val TYPE_WIDGET_CONTENT: Int = 2
        const val TYPE_WIDGET_LIST: Int = 3
        const val TYPE_WIDGET_WEB: Int = 4

        /**
         * 音乐类的消息回调，如歌曲信息
         */
        const val TYPE_WIDGET_MEDIA: Int = 5
        const val TYPE_WIDGET_WEATHER: Int = 6
    }
}
