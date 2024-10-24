package com.rhj.message

class MessageMediaListBean : MessageBean() {
    var count: Int = 0
    var widgetName: String? = null

    var list: List<MessageMusicBean> = ArrayList()

    override fun toString(): String {
        return "MessageMediaListBean{" +
                "count=" + count +
                ", widgetName='" + widgetName + '\'' +
                ", list=" + list +
                '}'
    }
}
